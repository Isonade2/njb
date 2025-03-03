package njb.recipe.repository;

import njb.recipe.entity.AiApiUsage;
import njb.recipe.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiApiUsageRepository extends JpaRepository<AiApiUsage,Long> {
    Optional<AiApiUsage> findByMemberId(Long memberId);

    Long member(Member member);
}
