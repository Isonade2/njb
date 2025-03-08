package njb.recipe.global.oauth2;


import jakarta.security.auth.message.AuthException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import njb.recipe.entity.Member;

import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserInfo {
    private String name;
    private String email;
    private String profile;

    /**
     * OAuth2UserInfo 클래스는 OAuth2 인증 공급자로부터 받은 사용자 정보를 담는 데이터 클래스입니다.
     */
    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes){
        return switch (registrationId) {
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> {
                try {
                    throw new AuthException("지원하지 않는 OAuth2 공급자입니다.");
                } catch (AuthException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    /**
     * Google OAuth2 공급자로부터 받은 사용자 정보를 기반으로 OAuth2UserInfo 객체를 생성합니다.
     * @param attributes Google OAuth2 공급자로부터 받은 사용자 정보
     * @return OAuth2UserInfo 객체
     */
    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .build();
    }

    /**
     * Kakao OAuth2 공급자로부터 받은 사용자 정보를 기반으로 OAuth2UserInfo 객체를 생성합니다.
     * @param attributes Kakao OAuth2 공급자로부터 받은 사용자 정보
     * @return OAuth2UserInfo 객체
     */
    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return OAuth2UserInfo.builder()
                .name((String) profile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .profile((String) profile.get("profile_image_url"))
                .build();
    }

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .nickname(name)
                .build();
    }
}
