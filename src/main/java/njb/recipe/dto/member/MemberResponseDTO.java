package njb.recipe.dto.member;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import njb.recipe.entity.Member;

@Data
@AllArgsConstructor
@Builder
public class MemberResponseDTO {
    private String email;
    private String nickname;


    public static MemberResponseDTO of(Member member) {
        return MemberResponseDTO.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .build();
    }


}
