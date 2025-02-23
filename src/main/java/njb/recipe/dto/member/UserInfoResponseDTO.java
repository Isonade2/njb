package njb.recipe.dto.member;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponseDTO {
    private String email;
    private String nickname;


    public static UserInfoResponseDTO of(String email, String nickname) {
        return UserInfoResponseDTO.builder()
                .email(email)
                .nickname(nickname)
                .build();
    }
}
