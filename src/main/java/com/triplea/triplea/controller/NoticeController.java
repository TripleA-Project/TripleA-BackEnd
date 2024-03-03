package com.triplea.triplea.controller;


import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.notice.NoticeRequest;
import com.triplea.triplea.dto.notice.NoticeResponse;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "공지사항")
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;

}
