package com.ngv.libraryManagementSystem.service.fine;

import com.ngv.libraryManagementSystem.dto.request.PayFineRequest;
import com.ngv.libraryManagementSystem.dto.response.FineResponse;
import com.ngv.libraryManagementSystem.entity.FineEntity;
import com.ngv.libraryManagementSystem.entity.LoanEntity;
import com.ngv.libraryManagementSystem.entity.MemberEntity;
import com.ngv.libraryManagementSystem.repository.FineRepository;
import com.ngv.libraryManagementSystem.repository.LoanRepository;
import com.ngv.libraryManagementSystem.repository.MemberRepository;
import com.ngv.libraryManagementSystem.service.config.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {

    private final FineRepository fineRepository;
    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final ConfigService configService;

    @Override
    @Transactional
    public void checkAndIssueFine(LoanEntity loan) {
        if (loan.getReturnedDate() == null) {
            return; // Book not returned yet
        }

        // Check if fine already exists
        if (fineRepository.findByLoan(loan).isPresent()) {
            return;
        }

        LocalDate dueDate = loan.getDueDate();
        LocalDate returnedDate = loan.getReturnedDate();

        if (returnedDate.isAfter(dueDate)) {
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, returnedDate);
            // Fine is calculated for each day overdue - sử dụng giá trị từ config
            BigDecimal finePerDay = configService.getFinePerDay();
            BigDecimal fineAmount = finePerDay.multiply(BigDecimal.valueOf(daysOverdue));

            FineEntity fine = new FineEntity();
            fine.setLoan(loan);
            fine.setMember(loan.getMember());
            fine.setAmount(fineAmount);
            fine.setIssueDate(LocalDate.now());
            fine.setPaid(false);

            fineRepository.save(fine);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FineResponse> getMyFines(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return fineRepository.findByMember(member).stream()
                .map(this::mapToFineResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FineResponse payFine(PayFineRequest request, Long memberId) {
        FineEntity fine = fineRepository.findById(request.getFineId())
                .orElseThrow(() -> new RuntimeException("Fine not found with id: " + request.getFineId()));

        if (!fine.getMember().getId().equals(memberId)) {
            throw new RuntimeException("You can only pay your own fines");
        }

        if (fine.getPaid()) {
            throw new RuntimeException("Fine has already been paid");
        }

        fine.setPaid(true);
        FineEntity savedFine = fineRepository.save(fine);
        return mapToFineResponse(savedFine);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FineResponse> getAllFines() {
        return fineRepository.findAll().stream()
                .map(this::mapToFineResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void processOverdueLoans() {
        List<LoanEntity> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());

        for (LoanEntity loan : overdueLoans) {
            if (loan.getReturnedDate() == null) {
                // Book is still not returned and overdue
                // Issue fine if not already issued
                if (fineRepository.findByLoan(loan).isEmpty()) {
                    long daysOverdue = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());
                    // Fine is calculated for each day overdue - sử dụng giá trị từ config
                    BigDecimal finePerDay = configService.getFinePerDay();
                    BigDecimal fineAmount = finePerDay.multiply(BigDecimal.valueOf(daysOverdue));

                    FineEntity fine = new FineEntity();
                    fine.setLoan(loan);
                    fine.setMember(loan.getMember());
                    fine.setAmount(fineAmount);
                    fine.setIssueDate(LocalDate.now());
                    fine.setPaid(false);

                    fineRepository.save(fine);
                }
            }
        }
    }

    private FineResponse mapToFineResponse(FineEntity fine) {
        return FineResponse.builder()
                .id(fine.getId())
                .amount(fine.getAmount())
                .paid(fine.getPaid())
                .issueDate(fine.getIssueDate())
                .member(fine.getMember() != null ?
                        FineResponse.MemberInfo.builder()
                                .id(fine.getMember().getId())
                                .firstName(fine.getMember().getFirstName())
                                .lastName(fine.getMember().getLastName())
                                .email(fine.getMember().getEmail())
                                .build() : null)
                .loan(fine.getLoan() != null ?
                        FineResponse.LoanInfo.builder()
                                .id(fine.getLoan().getId())
                                .loanDate(fine.getLoan().getLoanDate())
                                .dueDate(fine.getLoan().getDueDate())
                                .bookTitle(fine.getLoan().getBook() != null ?
                                        fine.getLoan().getBook().getTitle() : null)
                                .build() : null)
                .build();
    }
}

