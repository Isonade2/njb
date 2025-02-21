package njb.recipe.dto.member;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import njb.recipe.entity.JoinType;
import njb.recipe.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
public class SignupRequestDTO {
    @NotBlank
    @Email()
    private String email;
    @NotBlank()
    @Size(min = 6, max = 20)
    private String password;
    @NotNull(message = "닉네임을 입력해주세요.")
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
