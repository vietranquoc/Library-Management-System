package com.ngv.libraryManagementSystem.service.admin;

import com.ngv.libraryManagementSystem.dto.request.CreateBookRequest;
import com.ngv.libraryManagementSystem.dto.request.CreateCategoryRequest;
import com.ngv.libraryManagementSystem.dto.request.CreateStaffRequest;
import com.ngv.libraryManagementSystem.dto.response.*;
import com.ngv.libraryManagementSystem.enums.LoanStatusEnum;
import com.ngv.libraryManagementSystem.entity.*;
import com.ngv.libraryManagementSystem.enums.BookCopyStatusEnum;
import com.ngv.libraryManagementSystem.enums.MemberStatusEnum;
import com.ngv.libraryManagementSystem.enums.RoleEnum;
import com.ngv.libraryManagementSystem.exception.BadRequestException;
import com.ngv.libraryManagementSystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
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

        // Tạo BookCopyEntity dựa trên quantity từ request
        if (request.getQuantity() != null && request.getQuantity() > 0) {
            List<BookCopyEntity> copies = new ArrayList<>();
            for (int i = 1; i <= request.getQuantity(); i++) {
                BookCopyEntity copy = new BookCopyEntity();
                // Tạo barCode: ISBN-001, ISBN-002, ...
                copy.setBarCode(saved.getIsbn() + "-" + String.format("%03d", i));
                copy.setStatus(BookCopyStatusEnum.AVAILABLE);
                copy.setBook(saved);
                copies.add(copy);
            }
            bookCopyRepository.saveAll(copies);
        }

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

    @Override
    @Transactional(readOnly = true)
    public DashboardStatisticsResponse getDashboardStatistics() {
        // 1. Tổng số BookCopy đang AVAILABLE
        long totalBooks = bookCopyRepository.countByStatus(BookCopyStatusEnum.AVAILABLE);

        // 2. Tổng số member đang hoạt động
        long activeMembers = memberRepository.countByStatus(MemberStatusEnum.ACTIVE);

        // 3. Tổng số sách đã được mượn (status BORROWED hoặc OVERDUE)
        long totalBorrowedBooks = loanRepository.countByStatusIn(List.of(LoanStatusEnum.BORROWED, LoanStatusEnum.OVERDUE));

        // 4. Tổng số sách quá hạn
        long overdueBooks = loanRepository.countOverdueBooks(LoanStatusEnum.OVERDUE);

        // 5. Dữ liệu mượn sách theo tháng (12 tháng gần nhất)
        List<MonthlyLoanData> monthlyLoanData = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 11; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            int year = monthDate.getYear();
            int month = monthDate.getMonthValue();
            
            long borrowedCount = loanRepository.countBorrowedInMonth(LoanStatusEnum.BORROWED, year, month);
            long returnedCount = loanRepository.countReturnedInMonth(LoanStatusEnum.RETURNED, year, month);
            
            String monthLabel = String.format("Tháng %02d/%d", month, year);
            
            monthlyLoanData.add(MonthlyLoanData.builder()
                    .month(monthLabel)
                    .borrowedCount(borrowedCount)
                    .returnedCount(returnedCount)
                    .build());
        }

        // 6. Phân bổ sách theo thể loại (đếm BookCopy AVAILABLE)
        List<CategoryDistribution> categoryDistribution = new ArrayList<>();
        List<BookCopyEntity> availableCopies = bookCopyRepository.findByStatus(BookCopyStatusEnum.AVAILABLE);
        Map<String, Long> categoryCountMap = new HashMap<>();
        
        for (BookCopyEntity copy : availableCopies) {
            if (copy.getBook() != null && copy.getBook().getCategory() != null) {
                String categoryName = copy.getBook().getCategory().getName();
                categoryCountMap.put(categoryName, categoryCountMap.getOrDefault(categoryName, 0L) + 1);
            } else {
                String categoryName = "Không phân loại";
                categoryCountMap.put(categoryName, categoryCountMap.getOrDefault(categoryName, 0L) + 1);
            }
        }
        
        for (Map.Entry<String, Long> entry : categoryCountMap.entrySet()) {
            categoryDistribution.add(CategoryDistribution.builder()
                    .categoryName(entry.getKey())
                    .bookCount(entry.getValue())
                    .build());
        }

        // 7. Hoạt động mới nhất (20 hoạt động gần nhất)
        List<ActivityLog> recentActivities = new ArrayList<>();
        List<LoanEntity> recentLoans = loanRepository.findAllOrderByIdDesc().stream()
                .limit(20)
                .collect(Collectors.toList());

        for (LoanEntity loan : recentLoans) {
            String type;
            String description;
            String memberName = loan.getMember() != null 
                    ? loan.getMember().getFirstName() + " " + loan.getMember().getLastName()
                    : null;
            String bookTitle = loan.getBook() != null ? loan.getBook().getTitle() : "Unknown";

            LoanStatusEnum status = loan.getStatus();
            if (status == null) {
                continue;
            }

            switch (status) {
                case REQUESTED:
                    type = "LOAN_REQUEST";
                    description = memberName + " đã yêu cầu mượn sách \"" + bookTitle + "\"";
                    break;
                case BORROWED:
                    type = "LOAN_BORROWED";
                    description = memberName + " đã mượn sách \"" + bookTitle + "\"";
                    break;
                case RETURNED:
                    type = "LOAN_RETURNED";
                    description = memberName + " đã trả sách \"" + bookTitle + "\"";
                    break;
                case OVERDUE:
                    type = "LOAN_OVERDUE";
                    description = "Sách \"" + bookTitle + "\" của " + memberName + " đã quá hạn";
                    break;
                default:
                    continue;
            }

            // Tạo timestamp từ loanDate hoặc returnedDate
            LocalDateTime timestamp;
            if (loan.getReturnedDate() != null) {
                timestamp = loan.getReturnedDate().atStartOfDay();
            } else if (loan.getLoanDate() != null) {
                timestamp = loan.getLoanDate().atStartOfDay();
            } else {
                // Nếu không có date, dùng ngày hiện tại (cho REQUESTED)
                timestamp = LocalDateTime.now();
            }

            recentActivities.add(ActivityLog.builder()
                    .type(type)
                    .description(description)
                    .memberName(memberName)
                    .bookTitle(bookTitle)
                    .timestamp(timestamp)
                    .build());
        }

        return DashboardStatisticsResponse.builder()
                .totalBooks(totalBooks)
                .activeMembers(activeMembers)
                .totalBorrowedBooks(totalBorrowedBooks)
                .overdueBooks(overdueBooks)
                .monthlyLoanData(monthlyLoanData)
                .categoryDistribution(categoryDistribution)
                .recentActivities(recentActivities)
                .build();
    }

    private BookResponse mapToBookResponse(BookEntity book) {
        // Lấy danh sách BookCopy của sách này
        List<BookCopyEntity> copies = bookCopyRepository.findByBook(book);
        int totalCopies = copies.size();
        
        // Đếm số BookCopy có status AVAILABLE
        long availableCount = copies.stream()
                .filter(copy -> copy.getStatus() == BookCopyStatusEnum.AVAILABLE)
                .count();
        int availableCopies = (int) availableCount;
        
        // Map BookCopyInfo
        List<BookResponse.BookCopyInfo> copyInfos = copies.stream()
                .map(copy -> BookResponse.BookCopyInfo.builder()
                        .id(copy.getId())
                        .barCode(copy.getBarCode())
                        .available(copy.getStatus() == BookCopyStatusEnum.AVAILABLE)
                        .build())
                .collect(Collectors.toList());
        
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .image(book.getImage())
                .description(book.getDescription())
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
                .copies(copyInfos)
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();
    }
}


