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
}