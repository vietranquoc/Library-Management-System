package com.ngv.libraryManagementSystem.service.reservation;

import com.ngv.libraryManagementSystem.dto.request.ReservationRequest;
import com.ngv.libraryManagementSystem.dto.response.ReservationResponse;
import com.ngv.libraryManagementSystem.entity.BookEntity;
import com.ngv.libraryManagementSystem.entity.MemberEntity;
import com.ngv.libraryManagementSystem.entity.ReservationEntity;
import com.ngv.libraryManagementSystem.enums.ReservationStatusEnum;
import com.ngv.libraryManagementSystem.exception.BadRequestException;
import com.ngv.libraryManagementSystem.repository.BookRepository;
import com.ngv.libraryManagementSystem.repository.LoanRepository;
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
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final MailService mailService;

    @Override
    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, Long memberId) {
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        BookEntity book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + request.getBookId()));

        // Kiểm tra còn bản để mượn không: dựa trên quantity - số loan đang active
        long activeLoans = loanRepository.countByBookIdAndReturnedDateIsNull(book.getId());
        if (book.getQuantity() == null || book.getQuantity() <= activeLoans) {
            // Không còn bản để mượn => được phép tạo reservation
        } else {
            throw new BadRequestException("Sách đang còn bản để mượn trực tiếp, không cần đặt chỗ");
        }

        // Check trùng reservation theo book
        if (reservationRepository.findByMemberAndBook(member, book).isPresent()) {
            throw new BadRequestException("Bạn đã đặt chỗ sách này rồi");
        }

        ReservationEntity reservation = new ReservationEntity();
        reservation.setMember(member);
        reservation.setBook(book);
        reservation.setReservationDate(LocalDate.now());
        reservation.setNotified(false);
        reservation.setStatus(ReservationStatusEnum.PENDING);

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

        // Kiểm tra còn bản trống: quantity - activeLoans
        long activeLoans = loanRepository.countByBookIdAndReturnedDateIsNull(bookId);

        if (!pendingReservations.isEmpty()) {
            BookEntity book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));

            int quantity = book.getQuantity() != null ? book.getQuantity() : 0;
            if (activeLoans < quantity) {
                // Có bản trống -> notify người đầu queue
                ReservationEntity firstReservation = pendingReservations.get(0);
                MemberEntity member = firstReservation.getMember();

                String subject = "Sách đã sẵn sàng: " + book.getTitle();
                String content = "Xin chào " + member.getFirstName() + " " + member.getLastName() + ",\n\n" +
                        "Cuốn sách bạn đã đặt chỗ hiện đã sẵn sàng để mượn.\n\n" +
                        "Sách: " + book.getTitle() + "\n" +
                        "Ngày đặt: " + firstReservation.getReservationDate() + "\n\n" +
                        "Vui lòng đến thư viện để mượn.\n";

                mailService.sendMail(member.getEmail(), subject, content);
                firstReservation.setNotified(true);
                reservationRepository.save(firstReservation);
            }
        }
    }

    private ReservationResponse mapToReservationResponse(ReservationEntity reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .reservationDate(reservation.getReservationDate())
                .notified(reservation.getNotified())
                .book(reservation.getBook() != null ?
                        ReservationResponse.BookInfo.builder()
                                .id(reservation.getBook().getId())
                                .title(reservation.getBook().getTitle())
                                .isbn(reservation.getBook().getIsbn())
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

