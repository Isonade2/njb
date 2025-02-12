package njb.recipe.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TokenRequestDTO {
    private String accessToken;
    private String refreshToken;
}
