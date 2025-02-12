package njb.recipe.repository;

import njb.recipe.entity.Refrigerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long> {
    List<Refrigerator> findByMemberId(Long memberId); // memberId로 냉장고 목록 조회
}
