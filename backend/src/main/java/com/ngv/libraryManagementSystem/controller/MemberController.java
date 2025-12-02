package com.ngv.libraryManagementSystem.controller;

import com.ngv.libraryManagementSystem.dto.response.ApiResponse;
import com.ngv.libraryManagementSystem.dto.response.MemberResponse;
import com.ngv.libraryManagementSystem.service.member.MemberService;
import com.ngv.libraryManagementSystem.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final UserService userService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyProfile() {
        Long memberId = userService.getCurrentMemberId();
        MemberResponse member = memberService.getMyProfile(memberId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin thành viên thành công", member));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(@PathVariable Long id) {
        MemberResponse member = memberService.getMemberById(id);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin thành viên thành công", member));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getAllMembers() {
        List<MemberResponse> members = memberService.getAllMembers();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách thành viên thành công", members));
    }

    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getMembersByBookId(@PathVariable Long bookId) {
        List<MemberResponse> members = memberService.getMembersByBookId(bookId);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách thành viên mượn sách thành công", members));
    }
}

