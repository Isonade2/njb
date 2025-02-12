package njb.recipe.dto.member;


import lombok.Data;
import njb.recipe.entity.JoinType;
import njb.recipe.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class SignupRequestDTO {
    private String email;
    private String password;
    private String nickname;
    private String tel;
    private String imageUrl;


    public Member toEntity(PasswordEncoder passwordEncoder){
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .joinType(JoinType.LOCAL)
                .activated(false)
                .build();
    }
}
