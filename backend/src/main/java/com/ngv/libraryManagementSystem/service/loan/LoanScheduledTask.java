package com.ngv.libraryManagementSystem.service.loan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanScheduledTask {

    private final LoanService loanService;

    /**
     * Tự động kiểm tra và cập nhật trạng thái các loan quá hạn
     * Chạy mỗi ngày lúc 00:00 (nửa đêm)
     */
    @Scheduled(cron = "0 * * * * ?")
    public void checkAndUpdateOverdueLoans() {
        log.info("Bắt đầu kiểm tra và cập nhật các loan quá hạn...");
        try {
            loanService.updateOverdueLoans();
            log.info("Hoàn thành kiểm tra và cập nhật các loan quá hạn");
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra và cập nhật các loan quá hạn: {}", e.getMessage(), e);
        }
    }
}

