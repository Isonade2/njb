package njb.recipe.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import njb.recipe.domain.member.dto.PasswordUpdateRequest;
import njb.recipe.domain.member.dto.PasswordfindRequest;
import njb.recipe.domain.member.dto.ApiResponseDTO;
import njb.recipe.dto.ResponseUtils;
import njb.recipe.domain.member.dto.UserInfoResponseDTO;
import njb.recipe.global.jwt.CustomUserDetails;
import njb.recipe.domain.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;


    @Operation(summary = "유저 정보 조회", description = "유저 정보를 조회합니다.",
            security = {@SecurityRequirement(name = "accessToken"), @SecurityRequirement(name = "refreshToken")})
    @GetMapping("/userinfo")
    public ResponseEntity<ApiResponseDTO<UserInfoResponseDTO>> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("/userinfo");
        log.info("userDetails: {}", userDetails);
        String memberId = userDetails.getMemberId();
        UserInfoResponseDTO userInfo = memberService.getUserInfo(memberId);

        return ResponseEntity.ok(ResponseUtils.success(userInfo, "유저 정보 조회"));
    }

    @Operation(summary = "유저 패스워드 변경", description = "유저 패스워드를 변경합니다.")
    @PostMapping("/pw/find")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordfindRequest request) {
        log.info("email: {}", request.email());
        memberService.createPasswordResetToken(request.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 패스워드 변경", description = "유저 패스워드를 변경합니다.")
    @PostMapping("/pw/update")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordUpdateRequest request) {
        log.info("pw update req: email - {}, token - {}", request.email(), request.token());
        memberService.updatePassword(request.email(), request.password(), request.token());
        return ResponseEntity.ok().build();
    }
}
