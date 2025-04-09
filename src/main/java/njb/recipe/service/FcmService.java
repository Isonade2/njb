package njb.recipe.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;

import njb.recipe.dto.token.FcmNotificationRequestDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FcmService {
    // fcm에게 푸시 전송

    private final FirebaseMessaging firebaseMessaging;

    // 생성자를 통해 FirebaseMessaging 주입
    public FcmService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public ResponseEntity<String> sendNotification(FcmNotificationRequestDTO request) {
        // 메시지 구성
        Message message = Message.builder()
            .setToken(request.getFcmToken())
            .setNotification(Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .build())
            .build();

        try {
            // 메시지 전송
            String response = firebaseMessaging.send(message);
            log.info("Fcm 푸시 전송 성공: {}", response);
            return ResponseEntity.ok("Fcm 푸시 전송 성공: " + response);
        } catch (FirebaseMessagingException e) {
            String errorMessage = "Fcm 푸시 전송 실패: " + e.getMessage();
            if (e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                errorMessage = "유효하지 않은 FCM 토큰: " + request.getFcmToken();
                log.error(errorMessage);
                return ResponseEntity.status(400).body(errorMessage);
            }
            log.error(errorMessage, e);
            return ResponseEntity.status(500).body(errorMessage);
        } catch (Exception e) {
            String errorMessage = "Fcm 푸시 전송 실패: " + e.getMessage();
            log.error(errorMessage, e);
            return ResponseEntity.status(500).body(errorMessage);
        }
    }
}
