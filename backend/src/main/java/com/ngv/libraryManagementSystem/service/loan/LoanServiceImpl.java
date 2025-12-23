package com.ngv.libraryManagementSystem.service.loan;

import com.ngv.libraryManagementSystem.dto.request.LoanRequest;
import com.ngv.libraryManagementSystem.dto.request.ReturnBookRequest;
import com.ngv.libraryManagementSystem.dto.response.LoanResponse;
import com.ngv.libraryManagementSystem.entity.BookEntity;
import com.ngv.libraryManagementSystem.entity.LoanEntity;
import com.ngv.libraryManagementSystem.entity.MemberEntity;
import com.ngv.libraryManagementSystem.exception.BadRequestException;
import com.ngv.libraryManagementSystem.repository.BookRepository;
import com.ngv.libraryManagementSystem.repository.LoanRepository;
import com.ngv.libraryManagementSystem.repository.MemberRepository;
import com.ngv.libraryManagementSystem.service.reservation.ReservationService;
import com.ngv.libraryManagementSystem.service.fine.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final FineService fineService;
    private final ReservationService reservationService;

    @Override
    @Transactional
    public LoanResponse borrowBook(LoanRequest request, Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        // Với cấu trúc DB mới, LoanEntity tham chiếu trực tiếp BookEntity.
        // LoanRequest.bookCopyId hiện được hiểu là bookId.
        BookEntity book = bookRepository.findById(request.getBookCopyId())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy sách với id: " + request.getBookCopyId()));

        if (book.getQuantity() == null || book.getQuantity() <= 0) {
            throw new BadRequestException("Sách này chưa được cấu hình số lượng");
        }

        // Đếm số lượng lượt mượn hiện tại (chưa trả) của cuốn sách này
        long activeLoans = loanRepository.countByBookIdAndReturnedDateIsNull(book.getId());
        if (activeLoans >= book.getQuantity()) {
            throw new BadRequestException("Hiện không còn bản nào của sách này để mượn");
        }

        // TODO: Nếu muốn giới hạn 1 member không được mượn trùng cùng 1 sách,
        // có thể kiểm tra thêm tại đây.

        LoanEntity loan = new LoanEntity();
        loan.setMember(member);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(7));

        LoanEntity savedLoan = loanRepository.save(loan);
        return mapToLoanResponse(savedLoan);
    }

    @Override
    @Transactional
    public LoanResponse returnBook(ReturnBookRequest request, Long memberId) {
        LoanEntity loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + request.getLoanId()));

        if (!loan.getMember().getId().equals(memberId)) {
            throw new RuntimeException("You can only return your own books");
        }

        if (loan.getReturnedDate() != null) {
            throw new RuntimeException("Book has already been returned");
        }

        loan.setReturnedDate(LocalDate.now());

        LoanEntity savedLoan = loanRepository.save(loan);

        // Check if fine should be issued (overdue by more than 7 days)
        if (loan.getDueDate().isBefore(LocalDate.now())) {
            fineService.checkAndIssueFine(savedLoan);
        }

        // Check for pending reservations and notify
        reservationService.checkAndNotifyReservations(savedLoan.getBook().getId());

        return mapToLoanResponse(savedLoan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getMyLoans(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return loanRepository.findByMember(member).stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansByBookId(Long bookId) {
        return loanRepository.findByBookId(bookId).stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getMemberLoans(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return loanRepository.findByMember(member).stream()
                .map(this::mapToLoanResponse)
                .collect(Collectors.toList());
    }

    private LoanResponse mapToLoanResponse(LoanEntity loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .loanDate(loan.getLoanDate())
                .dueDate(loan.getDueDate())
                .returnedDate(loan.getReturnedDate())
                .book(loan.getBook() != null ?
                        LoanResponse.BookInfo.builder()
                                .id(loan.getBook().getId())
                                .title(loan.getBook().getTitle())
                                .isbn(loan.getBook().getIsbn())
                                .build() : null)
                .member(loan.getMember() != null ?
                        LoanResponse.MemberInfo.builder()
                                .id(loan.getMember().getId())
                                .firstName(loan.getMember().getFirstName())
                                .lastName(loan.getMember().getLastName())
                                .email(loan.getMember().getEmail())
                                .build() : null)
                .bookCopyBarCode(null)
                .build();
    }
}

