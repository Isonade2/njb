package njb.recipe.dto.token;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TokenRequestDTO {
    private String accessToken;
    @NotBlank
    private String refreshToken;
}
