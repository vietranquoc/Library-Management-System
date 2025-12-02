package com.ngv.libraryManagementSystem.service.member;

import com.ngv.libraryManagementSystem.dto.response.MemberResponse;
import java.util.List;

public interface MemberService {
    MemberResponse getMemberById(Long id);
    MemberResponse getMyProfile(Long memberId);
    List<MemberResponse> getAllMembers();
    List<MemberResponse> getMembersByBookId(Long bookId);
}

