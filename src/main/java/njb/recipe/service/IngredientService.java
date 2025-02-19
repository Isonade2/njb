package njb.recipe.service;

import njb.recipe.dto.refri.IngredientRequestDTO;
import njb.recipe.dto.refri.IngredientResponseDTO;
import njb.recipe.entity.Ingredient;
import njb.recipe.entity.Refrigerator;
import njb.recipe.repository.IngredientRepository;
import njb.recipe.repository.RefrigeratorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RefrigeratorRepository refrigeratorRepository; // 냉장고 리포지토리 주입

    public List<IngredientResponseDTO> createIngredients(List<IngredientRequestDTO> ingredientRequestDTOs) {
        return ingredientRequestDTOs.stream()
                .map(this::createIngredient) // 각 재료를 추가하고 DTO로 변환
                .collect(Collectors.toList());
    }

    private IngredientResponseDTO createIngredient(IngredientRequestDTO ingredientRequestDTO) {
        // 냉장고 ID로 냉장고 객체를 조회
        Refrigerator refrigerator = refrigeratorRepository.findById(ingredientRequestDTO.getRefrigeratorId())
                .orElseThrow(() -> new IllegalArgumentException("해당 냉장고가 존재하지 않습니다."));

        // 재료 객체 생성
        Ingredient ingredient = Ingredient.builder()
                .refrigerator(refrigerator) // 냉장고 설정
                .name(ingredientRequestDTO.getName())
                .photoUrl(ingredientRequestDTO.getPhotoUrl())
                .quantity(ingredientRequestDTO.getQuantity())
                .category(ingredientRequestDTO.getCategory())
                .expirationDate(ingredientRequestDTO.getExpirationDate())
                .build();

        // 재료 저장
        Ingredient savedIngredient = ingredientRepository.save(ingredient);

        // DTO로 변환하여 반환
        return IngredientResponseDTO.builder()
                .id(savedIngredient.getId())
                .refrigeratorId(refrigerator.getId())
                .name(savedIngredient.getName())
                .photoUrl(savedIngredient.getPhotoUrl())
                .quantity(savedIngredient.getQuantity())
                .category(savedIngredient.getCategory())
                .registrationDate(savedIngredient.getRegistrationDate())
                .expirationDate(savedIngredient.getExpirationDate())
                .build();
    }
}