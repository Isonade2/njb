package njb.recipe.controller;

import njb.recipe.dto.refri.IngredientRequestDTO;
import njb.recipe.dto.refri.IngredientResponseDTO;
import njb.recipe.entity.Ingredient;
import njb.recipe.entity.Refrigerator;
import njb.recipe.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<IngredientResponseDTO>> createIngredients(@RequestBody List<IngredientRequestDTO> ingredientRequestDTOs) {
        List<IngredientResponseDTO> createdIngredients = ingredientService.createIngredients(ingredientRequestDTOs);
        return ResponseEntity.status(201).body(createdIngredients); // 201 Created 응답 반환
    }

    // 특정 냉장고의 재료 조회 (카테고리 선택적)
    @GetMapping("/{refrigeratorId}")
    public ResponseEntity<List<IngredientResponseDTO>> getIngredientsByRefrigeratorId(
            @PathVariable(name = "refrigeratorId") Long refrigeratorId,
            @RequestParam(name = "category", required = false) String category) {
        List<IngredientResponseDTO> ingredients = ingredientService.getIngredientsByRefrigeratorId(refrigeratorId, category);
        return ResponseEntity.ok(ingredients); // 200 OK 응답 반환
    }

    // 단일 재료 조회
    @GetMapping("/ingredient/{ingredientId}")
    public ResponseEntity<IngredientResponseDTO> getIngredientById(@PathVariable(name = "ingredientId") Long ingredientId) {
        IngredientResponseDTO ingredient = ingredientService.getIngredientById(ingredientId);
        return ResponseEntity.ok(ingredient); // 200 OK 응답 반환
    }

    // 단일 재료 수정
    @PutMapping("/{ingredientId}")
    public ResponseEntity<IngredientResponseDTO> updateIngredient(
            @PathVariable(name = "ingredientId") Long id,
            @RequestBody IngredientRequestDTO ingredientRequestDTO) {
        IngredientResponseDTO updatedIngredient = ingredientService.updateIngredient(id, ingredientRequestDTO);
        return ResponseEntity.ok(updatedIngredient); // 200 OK 응답 반환
    }

    // 다중 재료 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteIngredients(@RequestBody List<Long> ingredientIds) {
        ingredientService.deleteIngredients(ingredientIds);
        return ResponseEntity.noContent().build(); // 204 No Content 응답 반환
    }
}