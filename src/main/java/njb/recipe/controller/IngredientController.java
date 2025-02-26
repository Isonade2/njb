package njb.recipe.controller;

import njb.recipe.dto.refri.IngredientRequestDTO;
import njb.recipe.dto.refri.IngredientResponseDTO;
import njb.recipe.dto.ApiResponseDTO;
import njb.recipe.dto.ResponseUtils;
import njb.recipe.entity.Ingredient;
import njb.recipe.entity.Refrigerator;
import njb.recipe.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/refri/ingredients")
public class IngredientController {
    @Autowired
    private IngredientService ingredientService;

 
    // 다중 재료 추가
    @PostMapping
    public ResponseEntity<ApiResponseDTO<List<IngredientResponseDTO>>> createIngredients(
            @RequestBody List<IngredientRequestDTO> ingredientRequestDTOs) {
        List<IngredientResponseDTO> createdIngredients = ingredientService.createIngredients(ingredientRequestDTOs);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.success(createdIngredients, "재료가 성공적으로 추가되었습니다."));
    }

    // 특정 냉장고의 재료 조회 (카테고리 선택적)
    @GetMapping("/{refrigeratorId}")
    public ResponseEntity<ApiResponseDTO<List<IngredientResponseDTO>>> getIngredientsByRefrigeratorId(
            @PathVariable(name = "refrigeratorId") Long refrigeratorId,
            @RequestParam(name = "category", required = false) String category) {
        List<IngredientResponseDTO> ingredients = ingredientService.getIngredientsByRefrigeratorId(refrigeratorId, category);
        return ResponseEntity.ok(ResponseUtils.success(ingredients, "재료 목록 조회 성공"));
    }

    // 단일 재료 조회
    @GetMapping("/ingredient/{ingredientId}")
    public ResponseEntity<ApiResponseDTO<IngredientResponseDTO>> getIngredientById(
            @PathVariable(name = "ingredientId") Long ingredientId) {
        IngredientResponseDTO ingredient = ingredientService.getIngredientById(ingredientId);
        return ResponseEntity.ok(ResponseUtils.success(ingredient, "재료 조회 성공"));
    }

    // 단일 재료 수정
    @PutMapping("/{ingredientId}")
    public ResponseEntity<ApiResponseDTO<IngredientResponseDTO>> updateIngredient(
            @PathVariable(name = "ingredientId") Long id,
            @RequestBody IngredientRequestDTO ingredientRequestDTO) {
        IngredientResponseDTO updatedIngredient = ingredientService.updateIngredient(id, ingredientRequestDTO);
        return ResponseEntity.ok(ResponseUtils.success(updatedIngredient, "재료가 성공적으로 수정되었습니다."));
    }

    // 다중 재료 삭제
    @DeleteMapping
    public ResponseEntity<ApiResponseDTO<Void>> deleteIngredients(@RequestBody List<Long> ingredientIds) {
        ingredientService.deleteIngredients(ingredientIds);
        return ResponseEntity.ok(ResponseUtils.success(null, "재료가 성공적으로 삭제되었습니다."));
    }
}