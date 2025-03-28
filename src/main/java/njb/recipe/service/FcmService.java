package njb.recipe.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import njb.recipe.dto.token.FcmNotificationRequestDTO;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FcmService {
    // fcm에게 푸시 전송

    public void sendNotification(FcmNotificationRequestDTO request) {
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
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Fcm 푸시 전송 성공: {}", response);
        } catch (Exception e) {
            log.error("Fcm 푸시 전송 실패: {}", e.getMessage(), e);
        }
    }
}
