package com.ngv.libraryManagementSystem.service.loan;

import com.ngv.libraryManagementSystem.dto.request.LoanRequest;
import com.ngv.libraryManagementSystem.dto.request.ReturnBookRequest;
import com.ngv.libraryManagementSystem.dto.response.LoanResponse;
import com.ngv.libraryManagementSystem.entity.BookCopyEntity;
import com.ngv.libraryManagementSystem.entity.LoanEntity;
import com.ngv.libraryManagementSystem.entity.MemberEntity;
import com.ngv.libraryManagementSystem.repository.BookCopyRepository;
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
    private final BookCopyRepository bookCopyRepository;
    private final MemberRepository memberRepository;
    private final FineService fineService;
    private final ReservationService reservationService;

    @Override
    @Transactional
    public LoanResponse borrowBook(LoanRequest request, Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        BookCopyEntity bookCopy = bookCopyRepository.findById(request.getBookCopyId())
                .orElseThrow(() -> new RuntimeException("Book copy not found with id: " + request.getBookCopyId()));

        if (!Boolean.TRUE.equals(bookCopy.getAvailable())) {
            throw new RuntimeException("Book copy is not available");
        }

        // Check if member already has an active loan for this copy
        if (loanRepository.findByBookCopyAndReturnedDateIsNull(bookCopy).isPresent()) {
            throw new RuntimeException("This book copy is already borrowed");
        }

        LoanEntity loan = new LoanEntity();
        loan.setMember(member);
        loan.setBookCopy(bookCopy);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(7));

        bookCopy.setAvailable(false);
        bookCopyRepository.save(bookCopy);

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
        loan.getBookCopy().setAvailable(true);
        bookCopyRepository.save(loan.getBookCopy());

        LoanEntity savedLoan = loanRepository.save(loan);

        // Check if fine should be issued (overdue by more than 7 days)
        if (loan.getDueDate().isBefore(LocalDate.now())) {
            fineService.checkAndIssueFine(savedLoan);
        }

        // Check for pending reservations and notify
        reservationService.checkAndNotifyReservations(savedLoan.getBookCopy().getBook().getId());

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
                .book(loan.getBookCopy().getBook() != null ?
                        LoanResponse.BookInfo.builder()
                                .id(loan.getBookCopy().getBook().getId())
                                .title(loan.getBookCopy().getBook().getTitle())
                                .isbn(loan.getBookCopy().getBook().getIsbn())
                                .build() : null)
                .member(loan.getMember() != null ?
                        LoanResponse.MemberInfo.builder()
                                .id(loan.getMember().getId())
                                .firstName(loan.getMember().getFirstName())
                                .lastName(loan.getMember().getLastName())
                                .email(loan.getMember().getEmail())
                                .build() : null)
                .bookCopyBarCode(loan.getBookCopy().getBarCode())
                .build();
    }
}

