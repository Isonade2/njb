package njb.recipe.repository;

import njb.recipe.entity.Category;
import njb.recipe.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByRefrigeratorId(Long refrigeratorId); // 냉장고 ID로 재료 조회

    @Query("SELECT i FROM Ingredient i WHERE i.refrigerator.id = :refrigeratorId AND (:categoryId IS NULL OR i.category.id = :categoryId)")
    List<Ingredient> findByRefrigeratorIdAndCategoryId(@Param("refrigeratorId") Long refrigeratorId, @Param("categoryId") Long categoryId);
}
