package njb.recipe.domain.member.repository;

import njb.recipe.domain.member.entity.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, String> {
}
