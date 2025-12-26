package com.ngv.libraryManagementSystem.service.loan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanScheduledTask implements ApplicationListener<ApplicationReadyEvent> {

    private final LoanService loanService;

    /**
     * Kiểm tra và cập nhật các loan quá hạn ngay khi ứng dụng khởi động
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Ứng dụng đã sẵn sàng. Bắt đầu kiểm tra các loan quá hạn khi khởi động...");
        try {
            loanService.updateOverdueLoans();
            log.info("Hoàn thành kiểm tra các loan quá hạn khi khởi động");
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra các loan quá hạn khi khởi động: {}", e.getMessage(), e);
        }
    }

    /**
     * Tự động kiểm tra và cập nhật trạng thái các loan quá hạn
     * Chạy mỗi ngày lúc 00:00 (nửa đêm)
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkAndUpdateOverdueLoans() {
        log.info("Bắt đầu kiểm tra và cập nhật các loan quá hạn (scheduled)...");
        try {
            loanService.updateOverdueLoans();
            log.info("Hoàn thành kiểm tra và cập nhật các loan quá hạn (scheduled)");
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra và cập nhật các loan quá hạn: {}", e.getMessage(), e);
        }
    }
}

