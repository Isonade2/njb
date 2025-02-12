package njb.recipe.dto.member;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import njb.recipe.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@Builder
@AllArgsConstructor
public class MemberRequestDTO {
    private String email;
    private String password;
    private String nickname;


    public Member toEntity(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build();
    }
}
