package njb.recipe.repository;


import njb.recipe.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByValue(String value);
    List<RefreshToken> findAllById(Long id);

    Optional<RefreshToken> findByDeviceInfoAndValue(String deviceInfo, String value);

}
