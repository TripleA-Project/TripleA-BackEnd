package com.triplea.triplea.controller;


import com.triplea.triplea.core.auth.session.MyUserDetails;
import com.triplea.triplea.dto.ResponseDTO;
import com.triplea.triplea.dto.experience.ExperienceRequest;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.service.ExperienceService;
import com.triplea.triplea.service.NoticeService;
import com.triplea.triplea.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "무료체험")
@RequestMapping("/api/admin")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ExperienceController {
    private final ExperienceService experienceService;

    @Operation(summary = "무료체험 등록")
    @PostMapping("/experience/insert")
    public ResponseEntity<?> experienceInsert(@RequestBody ExperienceRequest.Insert insert,
                                                  @AuthenticationPrincipal MyUserDetails myUserDetails){
        experienceService.insertExperience(insert);
        return ResponseEntity.ok().body(new ResponseDTO<>("무료체험 등록 성공"));
    }

    @Operation(summary = "무료체험 수정")
    @PostMapping("/experience/update")
    public ResponseEntity<?> experienceUpdate(@RequestBody ExperienceRequest.Update update, Errors errors) {
        experienceService.updateExperience(update);
        return ResponseEntity.ok().body(new ResponseDTO<>("무료체험 수정 성공"));
    }

    @Operation(summary = "무료체험 삭제")
    @PostMapping("/experience/delete/{id}")
    public ResponseEntity<?> experienceDelete(@PathVariable("id")Long id, Errors errors) {
        experienceService.deleteExperience(id);
        return ResponseEntity.ok().body(new ResponseDTO<>("무료체험 수정 성공"));
    }
}
