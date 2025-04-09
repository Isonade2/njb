package njb.recipe.service;

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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExpirationNotificationServiceTest {

    @Mock
    private FcmService fcmService; // Mock 객체로 설정

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private ExpirationNotificationService expirationNotificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendExpirationNotificationsWithRealToken() {
        // Given
        Member memberWithToken = new Member();
        memberWithToken.updateFcmToken("efWwqynX3_tAhx0iOM56-U:APA91bFcTKyivAcI8WsbUhk5pgJK4DQ-AQh9H30MHoBbRZzCrwkVBniqzenLnUb1h30Jc14OxXCKq3TAG-I_5aTQcZESPud5KHxeTL1xBeQl9PFL3xGDZzU"); // 실제 FCM 토큰 사용

        Ingredient ingredientWithToken = new Ingredient();
        ingredientWithToken.setMember(memberWithToken);
        ingredientWithToken.setName("Milk");
        ingredientWithToken.setExpirationDate(LocalDate.now().plusDays(2));

        List<Ingredient> ingredients = Arrays.asList(ingredientWithToken);

        when(ingredientRepository.findExpiringIngredients(any(LocalDate.class))).thenReturn(ingredients);

        // When
        expirationNotificationService.sendExpirationNotifications();

        // Then
        verify(fcmService, times(1)).sendNotification(any(FcmNotificationRequestDTO.class));
    }
}