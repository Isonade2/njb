package njb.recipe.domain.member.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import njb.recipe.domain.member.entity.Member;

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
