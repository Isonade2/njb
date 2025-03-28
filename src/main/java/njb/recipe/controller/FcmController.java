package njb.recipe.controller;

import njb.recipe.dto.token.FcmTokenRequestDTO;
import njb.recipe.global.jwt.CustomUserDetails;
import njb.recipe.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fcm")
public class FcmController {

    private final MemberService memberService;

    public FcmController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PutMapping("/token")
    public ResponseEntity<String> updateFcmToken(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody FcmTokenRequestDTO fcmToken) {
        String memberId = userDetails.getMemberId();
        memberService.updateFcmToken(memberId, fcmToken);
        return ResponseEntity.ok("FCM token updated successfully");
    }
}
