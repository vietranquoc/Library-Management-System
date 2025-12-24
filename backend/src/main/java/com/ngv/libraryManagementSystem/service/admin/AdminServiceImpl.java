package com.ngv.libraryManagementSystem.service.admin;

import com.ngv.libraryManagementSystem.dto.request.CreateBookRequest;
import com.ngv.libraryManagementSystem.dto.request.CreateCategoryRequest;
import com.ngv.libraryManagementSystem.dto.request.CreateStaffRequest;
import com.ngv.libraryManagementSystem.dto.response.AuthorSimpleResponse;
import com.ngv.libraryManagementSystem.dto.response.BookResponse;
import com.ngv.libraryManagementSystem.dto.response.CategorySimpleResponse;
import com.ngv.libraryManagementSystem.dto.response.StaffSimpleResponse;
import com.ngv.libraryManagementSystem.entity.*;
import com.ngv.libraryManagementSystem.enums.MemberStatusEnum;
import com.ngv.libraryManagementSystem.enums.RoleEnum;
import com.ngv.libraryManagementSystem.exception.BadRequestException;
import com.ngv.libraryManagementSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void createCategory(CreateCategoryRequest request) {
        CategoryEntity category = new CategoryEntity();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BadRequestException("ISBN đã tồn tại");
        }

        CategoryEntity category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy thể loại với id: " + request.getCategoryId()));

        BookEntity book = new BookEntity();
        book.setTitle(request.getTitle());
        book.setPublicationYear(request.getPublicationYear());
        book.setIsbn(request.getIsbn());
        book.setQuantity(request.getQuantity());
        book.setDescription(request.getDescription());
        book.setImage(request.getImage());
        book.setCategory(category);

        // Xử lý danh sách tác giả (cả theo id và theo tên)
        Set<AuthorEntity> authors = null;

        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            authors = authorRepository.findAllById(request.getAuthorIds())
                    .stream()
                    .collect(Collectors.toSet());
            if (authors.isEmpty()) {
                throw new BadRequestException("Không tìm thấy tác giả với danh sách id đã cung cấp");
            }
        }

        if (request.getAuthorNames() != null && !request.getAuthorNames().isEmpty()) {
            if (authors == null) {
                authors = new java.util.HashSet<>();
            }
            for (String rawName : request.getAuthorNames()) {
                if (rawName == null) continue;
                String name = rawName.trim();
                if (name.isEmpty()) continue;

                AuthorEntity author = authorRepository.findByNameIgnoreCase(name)
                        .orElseGet(() -> {
                            AuthorEntity a = new AuthorEntity();
                            a.setName(name);
                            return authorRepository.save(a);
                        });
                authors.add(author);
            }
        }

        if (authors != null && !authors.isEmpty()) {
            book.setAuthors(authors);
        }

        BookEntity saved = bookRepository.save(book);
        return mapToBookResponse(saved);
    }

    @Override
    @Transactional
    public Long createStaff(CreateStaffRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Tên đăng nhập đã tồn tại");
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email đã được sử dụng");
        }

        if (memberRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Số điện thoại đã được sử dụng");
        }

        MemberEntity member = new MemberEntity();
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setEmail(request.getEmail());
        member.setPhone(request.getPhone());
        member.setJoinDate(LocalDate.now());
        member.setStatus(MemberStatusEnum.ACTIVE);
        memberRepository.save(member);

        RoleEntity staffRole = roleRepository.findByName(RoleEnum.ROLE_STAFF.name());
        if (staffRole == null) {
            throw new BadRequestException("Không tìm thấy role STAFF trong hệ thống");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMember(member);
        user.setRoles(Set.of(staffRole));
        userRepository.save(user);

        return user.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySimpleResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategorySimpleResponse(c.getId(), c.getName(), c.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorSimpleResponse> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(a -> new AuthorSimpleResponse(a.getId(), a.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffSimpleResponse> getAllStaffs() {
        RoleEntity staffRole = roleRepository.findByName(RoleEnum.ROLE_STAFF.name());
        if (staffRole == null) {
            throw new BadRequestException("Không tìm thấy role STAFF trong hệ thống");
        }

        return userRepository.findByRolesContaining(staffRole).stream()
                .map(user -> {
                    MemberEntity m = user.getMember();
                    return new StaffSimpleResponse(
                            user.getId(),
                            user.getUsername(),
                            m != null ? m.getFirstName() : null,
                            m != null ? m.getLastName() : null,
                            m != null ? m.getEmail() : null,
                            m != null ? m.getPhone() : null,
                            m != null ? m.getStatus() : null
                    );
                })
                .collect(Collectors.toList());
    }

    private BookResponse mapToBookResponse(BookEntity book) {
        int totalCopies = book.getQuantity() != null ? book.getQuantity() : 0;
        // Đếm số lượng sách đang được mượn (chưa trả)
        long activeLoans = loanRepository.countByBookIdAndReturnedDateIsNull(book.getId());
        // Số sách còn sẵn = tổng số - số đang mượn
        int availableCopies = Math.max(0, totalCopies - (int) activeLoans);
        
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .category(book.getCategory() != null ?
                        BookResponse.CategoryInfo.builder()
                                .id(book.getCategory().getId())
                                .name(book.getCategory().getName())
                                .build() : null)
                .authors(book.getAuthors() != null ?
                        book.getAuthors().stream()
                                .map(author -> BookResponse.AuthorInfo.builder()
                                        .id(author.getId())
                                        .name(author.getName())
                                        .build())
                                .collect(Collectors.toSet()) : null)
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();
    }
}


