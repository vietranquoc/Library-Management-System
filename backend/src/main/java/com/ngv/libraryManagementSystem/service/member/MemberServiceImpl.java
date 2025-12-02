package com.ngv.libraryManagementSystem.service.member;

import com.ngv.libraryManagementSystem.dto.response.MemberResponse;
import com.ngv.libraryManagementSystem.entity.MemberEntity;
import com.ngv.libraryManagementSystem.repository.LoanRepository;
import com.ngv.libraryManagementSystem.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberById(Long id) {
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        return mapToMemberResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMyProfile(Long memberId) {
        return getMemberById(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::mapToMemberResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembersByBookId(Long bookId) {
        // Get all members who have borrowed this book
        List<MemberEntity> members = loanRepository.findByBookId(bookId).stream()
                .map(loan -> loan.getMember())
                .distinct()
                .collect(Collectors.toList());
        
        return members.stream()
                .map(this::mapToMemberResponse)
                .collect(Collectors.toList());
    }

    private MemberResponse mapToMemberResponse(MemberEntity member) {
        return MemberResponse.builder()
                .id(member.getId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .joinDate(member.getJoinDate())
                .status(member.getStatus())
                .address(member.getAddress() != null ?
                        MemberResponse.AddressInfo.builder()
                                .id(member.getAddress().getId())
                                .street(member.getAddress().getStreet())
                                .city(member.getAddress().getCity())
                                .state(member.getAddress().getState())
                                .zipCode(member.getAddress().getZipCode())
                                .build() : null)
                .loans(member.getLoans() != null ?
                        member.getLoans().stream()
                                .map(loan -> MemberResponse.LoanInfo.builder()
                                        .id(loan.getId())
                                        .loanDate(loan.getLoanDate())
                                        .dueDate(loan.getDueDate())
                                        .returnedDate(loan.getReturnedDate())
                                        .bookTitle(loan.getBookCopy().getBook() != null ?
                                                loan.getBookCopy().getBook().getTitle() : null)
                                        .bookCopyBarCode(loan.getBookCopy().getBarCode())
                                        .build())
                                .collect(Collectors.toList()) : null)
                .reservations(member.getReservations() != null ?
                        member.getReservations().stream()
                                .map(reservation -> MemberResponse.ReservationInfo.builder()
                                        .id(reservation.getId())
                                        .reservationDate(reservation.getReservationDate())
                                        .notified(reservation.getNotified())
                                        .bookTitle(reservation.getBookCopy().getBook() != null ?
                                                reservation.getBookCopy().getBook().getTitle() : null)
                                        .build())
                                .collect(Collectors.toList()) : null)
                .fines(member.getFines() != null ?
                        member.getFines().stream()
                                .map(fine -> MemberResponse.FineInfo.builder()
                                        .id(fine.getId())
                                        .amount(fine.getAmount())
                                        .paid(fine.getPaid())
                                        .issueDate(fine.getIssueDate())
                                        .build())
                                .collect(Collectors.toList()) : null)
                .build();
    }
}

