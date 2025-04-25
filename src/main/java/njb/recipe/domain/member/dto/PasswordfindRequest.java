package njb.recipe.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordfindRequest(
        @NotBlank @Email String email) {
}
