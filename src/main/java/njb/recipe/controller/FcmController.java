package njb.recipe.controller;

import njb.recipe.dto.token.FcmNotificationRequestDTO;
import njb.recipe.dto.token.FcmTokenRequestDTO;
import njb.recipe.global.jwt.CustomUserDetails;
import njb.recipe.service.FcmService;
import njb.recipe.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fcm")
public class FcmController {

    private final MemberService memberService;
    private final FcmService fcmService;

    public FcmController(MemberService memberService, FcmService fcmService) {
        this.memberService = memberService;
        this.fcmService = fcmService;
    }

    @PutMapping("/token")
    public ResponseEntity<String> updateFcmToken(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody FcmTokenRequestDTO fcmToken) {
        String memberId = userDetails.getMemberId();
        memberService.updateFcmToken(memberId, fcmToken);
        return ResponseEntity.ok("FCM token updated successfully");
    }

    @PostMapping("/send-test-notification")
    public ResponseEntity<String> sendTestNotification(@RequestBody FcmNotificationRequestDTO request) {
        return fcmService.sendNotification(request);
    }
}
