package com.ngv.libraryManagementSystem.service.reservation;

import com.ngv.libraryManagementSystem.dto.request.ReservationRequest;
import com.ngv.libraryManagementSystem.dto.response.ReservationResponse;
import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request, Long memberId);
    List<ReservationResponse> getMyReservations(Long memberId);
    List<ReservationResponse> getReservationsByBookId(Long bookId);
    void cancelReservation(Long reservationId, Long memberId);
    void checkAndNotifyReservations(Long bookId);
}

