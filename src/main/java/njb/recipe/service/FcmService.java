package njb.recipe.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;

import njb.recipe.dto.token.FcmNotificationRequestDTO;
import njb.recipe.entity.FcmToken;
import njb.recipe.entity.Member;
import njb.recipe.repository.FcmTokenRepository;
import njb.recipe.repository.MemberRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {
    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;
    private final MemberRepository memberRepository;
    private static final int MAX_TOKENS_PER_USER = 3;

    @Transactional
    public void updateFcmToken(String memberId, String newToken) {
        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 이미 존재하는 토큰인지 확인
        fcmTokenRepository.findByToken(newToken)
                .ifPresent(token -> {
                    if (!token.getMember().getId().equals(member.getId())) {
                        fcmTokenRepository.delete(token); // 다른 사용자의 토큰이면 삭제
                    }
                });

        // 현재 토큰 개수 확인
        long tokenCount = fcmTokenRepository.countByMemberId(member.getId());

        if (tokenCount >= MAX_TOKENS_PER_USER) {
            // 가장 오래된 토큰 삭제
            List<FcmToken> oldestTokens = fcmTokenRepository.findOldestTokensByMemberId(member.getId());
            if (!oldestTokens.isEmpty()) {
                fcmTokenRepository.delete(oldestTokens.get(0));
            }
        }

        // 새 토큰 저장
        FcmToken fcmToken = FcmToken.builder()
                .member(member)
                .token(newToken)
                .build();
        fcmTokenRepository.save(fcmToken);
    }

    public ResponseEntity<String> sendNotification(FcmNotificationRequestDTO request) {
        try {
            // 토큰 존재 여부 확인
            FcmToken fcmToken = fcmTokenRepository.findByToken(request.getFcmToken())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid FCM token"));

            Message message = Message.builder()
                    .setToken(request.getFcmToken())
                    .setNotification(Notification.builder()
                            .setTitle(request.getTitle())
                            .setBody(request.getBody())
                            .build())
                    .build();

            String response = firebaseMessaging.send(message);
            log.info("Fcm 푸시 전송 성공: {}", response);
            return ResponseEntity.ok("Fcm 푸시 전송 성공: " + response);

        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                // 유효하지 않은 토큰 자동 삭제
                fcmTokenRepository.findByToken(request.getFcmToken())
                        .ifPresent(token -> {
                            fcmTokenRepository.delete(token);
                            log.info("Invalid FCM token removed: {}", request.getFcmToken());
                        });
                return ResponseEntity.status(400).body("유효하지 않은 FCM 토큰: " + request.getFcmToken());
            }
            log.error("Fcm 푸시 전송 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Fcm 푸시 전송 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("Fcm 푸시 전송 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Fcm 푸시 전송 실패: " + e.getMessage());
        }
    }
}
