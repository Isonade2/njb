package njb.recipe.controller;

import njb.recipe.dto.refri.IngredientRequestDTO;
import njb.recipe.dto.refri.IngredientResponseDTO;
import njb.recipe.dto.ApiResponseDTO;
import njb.recipe.dto.ResponseUtils;
import njb.recipe.service.IngredientService;
import njb.recipe.global.jwt.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/refrigerators/{refrigeratorId}/ingredients")
public class IngredientController {
    @Autowired
    private IngredientService ingredientService;

 
    // 다중 재료 추가
    @PostMapping
    public ResponseEntity<ApiResponseDTO<List<IngredientResponseDTO>>> createIngredients(
            @PathVariable(name = "refrigeratorId") Long refrigeratorId,
            @RequestBody List<IngredientRequestDTO> ingredientRequestDTOs,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getMemberId();
        List<IngredientResponseDTO> createdIngredients = ingredientService.createIngredients(refrigeratorId, ingredientRequestDTOs, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.success(createdIngredients, "재료가 성공적으로 추가되었습니다."));
    }

    // 재료 리스트 조회 (카테고리 선택적, 정렬 추가)
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<IngredientResponseDTO>>> getIngredientsByRefrigeratorId(
            @PathVariable(name = "refrigeratorId") Long refrigeratorId,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @RequestParam(name = "sortField", defaultValue = "name") String sortField,
            @RequestParam(name = "sortOrder", defaultValue = "asc") String sortOrder,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        String userId = userDetails.getMemberId();
        List<IngredientResponseDTO> ingredients = ingredientService.getIngredientsByRefrigeratorId(refrigeratorId, categoryId, sortField, sortOrder, userId);
        
        return ResponseEntity.ok(ResponseUtils.success(ingredients, "재료 목록 조회 성공"));
    }

    // 단일 재료 조회
    @GetMapping("/{ingredientId}")
    public ResponseEntity<ApiResponseDTO<IngredientResponseDTO>> getIngredientById(
            @PathVariable(name = "refrigeratorId") Long refrigeratorId,
            @PathVariable(name = "ingredientId") Long ingredientId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        String userId = userDetails.getMemberId();
        IngredientResponseDTO ingredient = ingredientService.getIngredientById(refrigeratorId, ingredientId, userId);
        
        return ResponseEntity.ok(ResponseUtils.success(ingredient, "재료 조회 성공"));
    }

    // 단일 재료 수정
    @PutMapping("/{ingredientId}")
    public ResponseEntity<ApiResponseDTO<IngredientResponseDTO>> updateIngredient(
            @PathVariable(name = "refrigeratorId") Long refrigeratorId,
            @PathVariable(name = "ingredientId") Long ingredientId,
            @RequestBody IngredientRequestDTO ingredientRequestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getMemberId();
        IngredientResponseDTO updatedIngredient = ingredientService.updateIngredient(refrigeratorId, ingredientId, ingredientRequestDTO, userId);
        return ResponseEntity.ok(ResponseUtils.success(updatedIngredient, "재료가 성공적으로 수정되었습니다."));
    }

    // 다중 재료 삭제
    @DeleteMapping
    public ResponseEntity<ApiResponseDTO<Void>> deleteIngredients(
            @PathVariable(name = "refrigeratorId") Long refrigeratorId,
            @RequestBody List<Long> ingredientIds,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getMemberId();
        ingredientService.deleteIngredients(ingredientIds, userId, refrigeratorId);
        return ResponseEntity.ok(ResponseUtils.success(null, "재료가 성공적으로 삭제되었습니다."));
    }
}