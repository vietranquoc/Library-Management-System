package com.ngv.libraryManagementSystem.service.admin;

import com.ngv.libraryManagementSystem.dto.request.admin.CreateBookRequest;
import com.ngv.libraryManagementSystem.dto.request.admin.CreateCategoryRequest;
import com.ngv.libraryManagementSystem.dto.request.admin.CreateStaffRequest;
import com.ngv.libraryManagementSystem.dto.response.BookResponse;
import com.ngv.libraryManagementSystem.entity.*;
import com.ngv.libraryManagementSystem.enums.RoleEnum;
import com.ngv.libraryManagementSystem.exception.BadRequestException;
import com.ngv.libraryManagementSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        book.setCategory(category);

        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            Set<AuthorEntity> authors = authorRepository.findAllById(request.getAuthorIds())
                    .stream().collect(Collectors.toSet());
            if (authors.isEmpty()) {
                throw new BadRequestException("Không tìm thấy tác giả với danh sách id đã cung cấp");
            }
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
        member.setStatus("ACTIVE");
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

    private BookResponse mapToBookResponse(BookEntity book) {
        int totalCopies = book.getCopies() != null ? book.getCopies().size() : 0;
        int availableCopies = book.getCopies() != null ?
                (int) book.getCopies().stream().filter(copy -> Boolean.TRUE.equals(copy.getAvailable())).count() : 0;

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
                .copies(book.getCopies() != null ?
                        book.getCopies().stream()
                                .map(copy -> BookResponse.BookCopyInfo.builder()
                                        .id(copy.getId())
                                        .barCode(copy.getBarCode())
                                        .available(copy.getAvailable())
                                        .build())
                                .collect(Collectors.toList()) : null)
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();
    }
}


