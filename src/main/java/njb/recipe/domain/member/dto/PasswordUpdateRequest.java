package njb.recipe.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordUpdateRequest(
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String token
) {
}
