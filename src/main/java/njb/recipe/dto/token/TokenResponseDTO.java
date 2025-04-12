package njb.recipe.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TokenResponseDTO {
    private String accessToken;
    private String accessExpireTime;
    private Long accessExpireTimeEpoch;
    private String refreshToken;
    private String refreshExpireTime;
    private Long refreshExpireTimeEpoch;
}
