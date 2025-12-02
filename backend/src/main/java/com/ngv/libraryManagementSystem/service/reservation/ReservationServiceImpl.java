package com.ngv.libraryManagementSystem.service.reservation;

import com.ngv.libraryManagementSystem.dto.request.ReservationRequest;
import com.ngv.libraryManagementSystem.dto.response.ReservationResponse;
import com.ngv.libraryManagementSystem.entity.BookCopyEntity;
import com.ngv.libraryManagementSystem.entity.BookEntity;
import com.ngv.libraryManagementSystem.entity.MemberEntity;
import com.ngv.libraryManagementSystem.entity.ReservationEntity;
import com.ngv.libraryManagementSystem.repository.BookCopyRepository;
import com.ngv.libraryManagementSystem.repository.BookRepository;
import com.ngv.libraryManagementSystem.repository.MemberRepository;
import com.ngv.libraryManagementSystem.repository.ReservationRepository;
import com.ngv.libraryManagementSystem.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final MemberRepository memberRepository;
    private final MailService mailService;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        BookEntity book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + request.getBookId()));

        // Check if there are any available copies
        List<BookCopyEntity> availableCopies = bookCopyRepository.findAvailableCopiesByBookId(request.getBookId());
        if (!availableCopies.isEmpty()) {
            throw new RuntimeException("Book is available. You can borrow it directly.");
        }

        // Check if member already has a reservation for this book
        List<BookCopyEntity> bookCopies = bookCopyRepository.findByBook(book);
        if (bookCopies.isEmpty()) {
            throw new RuntimeException("No copies of this book exist in the library");
        }

        // Check if member already has a reservation for any copy of this book
        for (BookCopyEntity copy : bookCopies) {
            if (reservationRepository.findByMemberAndBookCopy(member, copy).isPresent()) {
                throw new RuntimeException("You already have a reservation for this book");
            }
        }

        // Use the first copy (or we could implement a queue system)
        BookCopyEntity bookCopy = bookCopies.get(0);

        ReservationEntity reservation = new ReservationEntity();
        reservation.setMember(member);
        reservation.setBookCopy(bookCopy);
        reservation.setReservationDate(LocalDate.now());
        reservation.setNotified(false);

        ReservationEntity savedReservation = reservationRepository.save(reservation);
        return mapToReservationResponse(savedReservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return reservationRepository.findByMember(member).stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByBookId(Long bookId) {
        return reservationRepository.findByBookIdOrderByDateAsc(bookId).stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelReservation(Long reservationId, Long memberId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

        if (!reservation.getMember().getId().equals(memberId)) {
            throw new RuntimeException("You can only cancel your own reservations");
        }

        reservationRepository.delete(reservation);
    }

    @Override
    @Transactional
    public void checkAndNotifyReservations(Long bookId) {
        // Find pending reservations for this book
        List<ReservationEntity> pendingReservations = reservationRepository.findPendingReservationsByBookId(bookId);

        // Check if there are available copies
        List<BookCopyEntity> availableCopies = bookCopyRepository.findAvailableCopiesByBookId(bookId);

        if (!availableCopies.isEmpty() && !pendingReservations.isEmpty()) {
            // Notify the first person in the reservation queue
            ReservationEntity firstReservation = pendingReservations.get(0);
            MemberEntity member = firstReservation.getMember();

            String subject = "Book Available: " + firstReservation.getBookCopy().getBook().getTitle();
            String content = "Dear " + member.getFirstName() + " " + member.getLastName() + ",\n\n" +
                    "The book you reserved is now available. Please visit the library to borrow it.\n\n" +
                    "Book: " + firstReservation.getBookCopy().getBook().getTitle() + "\n" +
                    "Reservation Date: " + firstReservation.getReservationDate() + "\n\n" +
                    "Thank you!";

            mailService.sendMail(member.getEmail(), subject, content);
            firstReservation.setNotified(true);
            reservationRepository.save(firstReservation);
        }
    }

    private ReservationResponse mapToReservationResponse(ReservationEntity reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .reservationDate(reservation.getReservationDate())
                .notified(reservation.getNotified())
                .book(reservation.getBookCopy().getBook() != null ?
                        ReservationResponse.BookInfo.builder()
                                .id(reservation.getBookCopy().getBook().getId())
                                .title(reservation.getBookCopy().getBook().getTitle())
                                .isbn(reservation.getBookCopy().getBook().getIsbn())
                                .build() : null)
                .member(reservation.getMember() != null ?
                        ReservationResponse.MemberInfo.builder()
                                .id(reservation.getMember().getId())
                                .firstName(reservation.getMember().getFirstName())
                                .lastName(reservation.getMember().getLastName())
                                .email(reservation.getMember().getEmail())
                                .build() : null)
                .build();
    }
}

