package njb.recipe.integration;

import com.google.firebase.messaging.FirebaseMessaging;

import njb.recipe.dto.token.FcmNotificationRequestDTO;
import njb.recipe.entity.Ingredient;
import njb.recipe.entity.Member;
import njb.recipe.repository.IngredientRepository;
import njb.recipe.service.ExpirationNotificationService;
import njb.recipe.service.FcmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ExpirationNotificationServiceIntegrationTest {

    @Autowired
    private ExpirationNotificationService expirationNotificationService;

    @MockBean
    private FcmService fcmService; // 모킹된 FcmService 사용

    // @Autowired
    // private FcmService fcmService; // 실제 fcmService 사용해 디바이스 알람 테스트

    @MockBean
    private IngredientRepository ingredientRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendExpirationNotifications() {
        // Given
        Member memberWithToken = new Member();
        memberWithToken.updateFcmToken("fcm token real"); // 실제 FCM 토큰 사용

        Ingredient ingredientWithToken = new Ingredient();
        ingredientWithToken.setMember(memberWithToken);
        ingredientWithToken.setName("Milk");
        ingredientWithToken.setExpirationDate(LocalDate.now().plusDays(2));

        List<Ingredient> ingredients = Arrays.asList(ingredientWithToken);

        when(ingredientRepository.findExpiringIngredients(any(LocalDate.class))).thenReturn(ingredients);

        // When
        expirationNotificationService.sendExpirationNotifications();

        // Then
        // 실제 알림 전송을 확인할 수 있는 방법이 필요
    }

    @Test
    public void testSendExpirationNotificationsWithMultipleIngredients() {
        // Given
        Member memberWithToken = new Member();
        memberWithToken.updateFcmToken("fcm token real"); // 실제 FCM 토큰 사용

        Ingredient ingredient1 = new Ingredient();
        ingredient1.setMember(memberWithToken);
        ingredient1.setName("Milk");
        ingredient1.setExpirationDate(LocalDate.now().plusDays(2));

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setMember(memberWithToken);
        ingredient2.setName("Eggs");
        ingredient2.setExpirationDate(LocalDate.now().plusDays(2));

        List<Ingredient> ingredients = Arrays.asList(ingredient1, ingredient2);

        when(ingredientRepository.findExpiringIngredients(any(LocalDate.class))).thenReturn(ingredients);

        // When
        expirationNotificationService.sendExpirationNotifications();

        // Then
        ArgumentCaptor<FcmNotificationRequestDTO> captor = ArgumentCaptor.forClass(FcmNotificationRequestDTO.class);
        verify(fcmService, times(1)).sendNotification(captor.capture());

        FcmNotificationRequestDTO capturedRequest = captor.getValue();
        assertEquals("유통기한 임박 알림", capturedRequest.getTitle());
        assertEquals("Milk, Eggs의 유통기한이 임박했습니다!", capturedRequest.getBody());
    }
}
