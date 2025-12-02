package com.ngv.libraryManagementSystem.service.fine;

import com.ngv.libraryManagementSystem.dto.request.PayFineRequest;
import com.ngv.libraryManagementSystem.dto.response.FineResponse;
import com.ngv.libraryManagementSystem.entity.LoanEntity;
import java.util.List;

public interface FineService {
    void checkAndIssueFine(LoanEntity loan);
    List<FineResponse> getMyFines(Long memberId);
    FineResponse payFine(PayFineRequest request, Long memberId);
    List<FineResponse> getAllFines();
    void processOverdueLoans();
}

