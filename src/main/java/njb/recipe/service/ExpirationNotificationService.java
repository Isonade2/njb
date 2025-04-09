package njb.recipe.service;

import njb.recipe.dto.token.FcmNotificationRequestDTO;
import njb.recipe.entity.Ingredient;
import njb.recipe.repository.IngredientRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class ExpirationNotificationService {
// 스프링 스케쥴러를 사용하여 유통기한이 임박한 재료에 대해 자동으로 fcm 알림을 보내는 서비스 
    private final FcmService fcmService;
    private final IngredientRepository ingredientRepository; // 재료 정보를 관리하는 리포지토리
    private static final Logger log = LoggerFactory.getLogger(ExpirationNotificationService.class);

    public ExpirationNotificationService(FcmService fcmService, IngredientRepository ingredientRepository) {
        this.fcmService = fcmService;
        this.ingredientRepository = ingredientRepository;
    }

    @Scheduled(cron = "0 0 9 * * ?") // 매일 오전 9시에 실행
    public void sendExpirationNotifications() {
        LocalDate thresholdDate = LocalDate.now().plusDays(3); // 3일 이내로 남은 재료 조회
        List<Ingredient> expiringIngredients = ingredientRepository.findExpiringIngredients(thresholdDate);

        Map<String, List<String>> userToIngredientsMap = new HashMap<>();

        for (Ingredient ingredient : expiringIngredients) {
            String fcmToken = ingredient.getUserFcmToken();
            if (fcmToken == null || fcmToken.isEmpty()) {
                log.warn("FCM 토큰이 없는 사용자: {}", ingredient.getMember().getId());
                continue; // FCM 토큰이 없으면 알림을 보내지 않음
            }

            userToIngredientsMap.computeIfAbsent(fcmToken, k -> new ArrayList<>()).add(ingredient.getName());
        }

        for (Map.Entry<String, List<String>> entry : userToIngredientsMap.entrySet()) {
            String fcmToken = entry.getKey();
            List<String> ingredientNames = entry.getValue();
            String messageBody = String.join(", ", ingredientNames) + "의 유통기한이 임박했습니다!";

            FcmNotificationRequestDTO notificationRequest = new FcmNotificationRequestDTO(
                fcmToken,
                "유통기한 임박 알림",
                messageBody
            );
            fcmService.sendNotification(notificationRequest);
        }
    }
}
