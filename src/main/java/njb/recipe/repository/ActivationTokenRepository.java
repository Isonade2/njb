package njb.recipe.repository;

import njb.recipe.entity.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {
}
