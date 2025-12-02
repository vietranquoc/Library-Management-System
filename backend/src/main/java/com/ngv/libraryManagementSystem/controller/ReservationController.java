package com.ngv.libraryManagementSystem.controller;

import com.ngv.libraryManagementSystem.dto.request.ReservationRequest;
import com.ngv.libraryManagementSystem.dto.response.ApiResponse;
import com.ngv.libraryManagementSystem.dto.response.ReservationResponse;
import com.ngv.libraryManagementSystem.service.reservation.ReservationService;
import com.ngv.libraryManagementSystem.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<ReservationResponse>> createReservation(@Valid @RequestBody ReservationRequest request) {
        Long memberId = userService.getCurrentMemberId();
        ReservationResponse reservation = reservationService.createReservation(request, memberId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đặt giữ sách thành công", reservation));
    }

    @GetMapping("/my-reservations")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getMyReservations() {
        Long memberId = userService.getCurrentMemberId();
        List<ReservationResponse> reservations = reservationService.getMyReservations(memberId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách đặt giữ sách thành công", reservations));
    }

    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationResponse>>> getReservationsByBookId(@PathVariable Long bookId) {
        List<ReservationResponse> reservations = reservationService.getReservationsByBookId(bookId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách đặt giữ sách thành công", reservations));
    }

    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<Void>> cancelReservation(@PathVariable Long reservationId) {
        Long memberId = userService.getCurrentMemberId();
        reservationService.cancelReservation(reservationId, memberId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Hủy đặt giữ sách thành công", null));
    }
}

