package com.triplea.triplea.controller;


import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.notice.NoticeRequest;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.service.NoticeService;
import com.triplea.triplea.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "관리자")
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserService userService;

    private final NoticeService noticeService;

    @Operation(summary = "인증번호 발급")
    @PostMapping("/admin/email")
    public ResponseEntity<?> adminEmail(@Parameter(hidden = true) @AuthenticationPrincipal MyUserDetails myUserDetails){

        userService.adminEmail(myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>("메일 발송 성공"));
    }

    @Operation(summary = "이메일 인증 확인")
    @PostMapping("/admin/email/verify")
    public ResponseEntity<?> adminEmailVerified(@RequestBody @Valid UserRequest.EmailVerify request, Errors errors) {

        return ResponseEntity.ok().body(new ResponseDTO<>(userService.adminEmailVerified(request)));
    }

    @Operation(summary = "유저 리스트")
    @GetMapping("/admin/user/list")
    public ResponseEntity<?> userList() {

        return ResponseEntity.ok().body(new ResponseDTO<>(userService.userList()));
    }

    @Operation(summary = "유저 권한 설정")
    @PostMapping("/admin/user/role")
    public ResponseEntity<?> userList(@RequestBody @Valid UserRequest.ChangeRole request, Errors errors) {
        userService.changeRole(request);
        return ResponseEntity.ok().body(new ResponseDTO<>("권한 설정 성공"));
    }

    @Operation(summary = "유저 탈퇴")
    @PostMapping("/admin/user/delete/{id}")
    public ResponseEntity<?> userDelete(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().body(new ResponseDTO<>("탈퇴처리 성공"));
    }

    @Operation(summary = "유저 수 조회")
    @GetMapping("/admin/user/list/length")
    public ResponseEntity<?> userListLength() {

        return ResponseEntity.ok().body(new ResponseDTO<>(userService.userListLength()));
    }

    @Operation(summary = "유저 검색")
    @GetMapping("/admin/user/list/search")
    public ResponseEntity<?> userListSearch(@RequestParam("type") String type, @RequestParam("content")String content) {
        UserRequest.UserSearch request = UserRequest.UserSearch.builder().type(type).content(content).build();
        return ResponseEntity.ok().body(new ResponseDTO<>(userService.userSearchList(request)));
    }


    // 공지사항 작성
    @Operation(summary = "공지사항 작성")
    @PostMapping("/admin/notice/save")
    public ResponseEntity<?> save(@RequestBody @Valid NoticeRequest.Save save, Errors errors, HttpServletRequest request) {

        noticeService.noticeSave(save);
        return ResponseEntity.ok().body(new ResponseDTO<>("공지사항 작성완료"));
    }

    // 공지사항 수정
    @Operation(summary = "공지사항 수정")
    @PostMapping("/admin/notice/update")
    public ResponseEntity<?> save(@RequestBody @Valid NoticeRequest.Update update, Errors errors, HttpServletRequest request) {

        noticeService.noticeUpdate(update);
        return ResponseEntity.ok().body(new ResponseDTO<>("공지사항 수정완료"));
    }

    // 공지사항 수정
    @Operation(summary = "공지사항 삭제")
    @PostMapping("/admin/notice/delete/{id}")
    public ResponseEntity<?> save(@PathVariable("id")Long id) {

        noticeService.noticeDelete(id);
        return ResponseEntity.ok().body(new ResponseDTO<>("공지사항 삭제완료"));
    }

    // 공지사항 리스트 조회
    @Operation(summary = "공지사항 리스트 조회")
    @GetMapping("/auth/notice/list")
    public ResponseEntity<?> getList() {

        return ResponseEntity.ok().body(new ResponseDTO<>(noticeService.getNoticeList()));
    }

    // 공지사항 리스트 조회
    @Operation(summary = "공지사항 상세 조회")
    @GetMapping("/auth/notice/detail/{id}")
    public ResponseEntity<?> getDetail(@PathVariable("id")Long id) {

        return ResponseEntity.ok().body(new ResponseDTO<>(noticeService.getNotice(id)));
    }

    // 공지사항 리스트 조회
    @Operation(summary = "파일 업로드")
    @PostMapping("/admin/notice/upload")
    public ResponseEntity<?> fileUpload(MultipartFile file) {

        return ResponseEntity.ok().body(new ResponseDTO<>(noticeService.fileUpload(file)));
    }
}
