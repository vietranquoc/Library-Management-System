package com.ngv.libraryManagementSystem.service.loan;

import com.ngv.libraryManagementSystem.dto.request.LoanRequest;
import com.ngv.libraryManagementSystem.dto.request.ReturnBookRequest;
import com.ngv.libraryManagementSystem.dto.response.LoanResponse;
import java.util.List;

public interface LoanService {
    LoanResponse borrowBook(LoanRequest request, Long memberId);
    LoanResponse returnBook(ReturnBookRequest request, Long memberId);
    List<LoanResponse> getMyLoans(Long memberId);
    List<LoanResponse> getLoansByBookId(Long bookId);
    List<LoanResponse> getMemberLoans(Long memberId);
}

