package njb.recipe.repository;

import njb.recipe.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByRefrigeratorId(Long refrigeratorId); // 냉장고 ID로 재료 조회
}
