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

    // 특정 냉장고의 재료 조회 (카테고리 선택적)
    public List<IngredientResponseDTO> getIngredientsByRefrigeratorId(Long refrigeratorId, String category) {
        List<Ingredient> ingredients;
        if (category != null && !category.isEmpty()) {
            ingredients = ingredientRepository.findByRefrigeratorIdAndCategory(refrigeratorId, category);
        } else {
            ingredients = ingredientRepository.findByRefrigeratorId(refrigeratorId);
        }
        
        return ingredients.stream()
                .map(ingredient -> IngredientResponseDTO.builder()
                        .id(ingredient.getId())
                        .refrigeratorId(ingredient.getRefrigerator().getId())
                        .name(ingredient.getName())
                        .photoUrl(ingredient.getPhotoUrl())
                        .quantity(ingredient.getQuantity())
                        .category(ingredient.getCategory())
                        .registrationDate(ingredient.getRegistrationDate())
                        .expirationDate(ingredient.getExpirationDate())
                        .build())
                .collect(Collectors.toList());
    }

    // 단일 재료 수정
    public IngredientResponseDTO updateIngredient(Long id, IngredientRequestDTO ingredientRequestDTO) {
        // 재료 ID로 재료 객체를 조회
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 재료가 존재하지 않습니다."));

        // 수정할 내용 업데이트
        ingredient.setName(ingredientRequestDTO.getName());
        ingredient.setPhotoUrl(ingredientRequestDTO.getPhotoUrl());
        ingredient.setQuantity(ingredientRequestDTO.getQuantity());
        ingredient.setCategory(ingredientRequestDTO.getCategory());
        ingredient.setExpirationDate(ingredientRequestDTO.getExpirationDate());

        // 재료 저장
        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

        // DTO로 변환하여 반환
        return IngredientResponseDTO.builder()
                .id(updatedIngredient.getId())
                .refrigeratorId(updatedIngredient.getRefrigerator().getId())
                .name(updatedIngredient.getName())
                .photoUrl(updatedIngredient.getPhotoUrl())
                .quantity(updatedIngredient.getQuantity())
                .category(updatedIngredient.getCategory())
                .registrationDate(updatedIngredient.getRegistrationDate())
                .expirationDate(updatedIngredient.getExpirationDate())
                .build();
    }

    // 단일 재료 조회
    public IngredientResponseDTO getIngredientById(Long id) {
        // 재료 ID로 재료 객체를 조회
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 재료가 존재하지 않습니다."));

        // DTO로 변환하여 반환
        return IngredientResponseDTO.builder()
                .id(ingredient.getId())
                .refrigeratorId(ingredient.getRefrigerator().getId())
                .name(ingredient.getName())
                .photoUrl(ingredient.getPhotoUrl())
                .quantity(ingredient.getQuantity())
                .category(ingredient.getCategory())
                .registrationDate(ingredient.getRegistrationDate())
                .expirationDate(ingredient.getExpirationDate())
                .build();
    }

    // 다중 재료 삭제
    public void deleteIngredients(List<Long> ingredientIds) {
        for (Long id : ingredientIds) {
            ingredientRepository.deleteById(id);
        }
    }
}