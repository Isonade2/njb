package njb.recipe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import njb.recipe.dto.ApiResponseDTO;
import njb.recipe.dto.ResponseUtils;
import njb.recipe.dto.member.UserInfoResponseDTO;
import njb.recipe.global.jwt.CustomUserDetails;
import njb.recipe.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class MemberController {
    private final MemberService memberService;


    @GetMapping("/userinfo")
    public ResponseEntity<ApiResponseDTO<UserInfoResponseDTO>> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails){
        log.info("/userinfo");
        log.info("userDetails: {}", userDetails);
        String memberId = userDetails.getMemberId();
        UserInfoResponseDTO userInfo = memberService.getUserInfo(memberId);

        return ResponseEntity.ok(ResponseUtils.success(userInfo, "유저 정보 조회"));
    }
}
