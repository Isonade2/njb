package njb.recipe.service;

import njb.recipe.dto.refri.IngredientRequestDTO;
import njb.recipe.dto.refri.IngredientResponseDTO;
import njb.recipe.entity.Ingredient;
import njb.recipe.entity.Member;
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

 

    // 다중 재료 추가
    public List<IngredientResponseDTO> createIngredients(Long refrigeratorId, List<IngredientRequestDTO> ingredientRequestDTOs, String userId) {
        Refrigerator refrigerator = refrigeratorRepository.findById(refrigeratorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 냉장고가 존재하지 않습니다."));

        // 소유자 확인
        if (!refrigerator.getMember().getId().toString().equals(userId)) {
            throw new IllegalArgumentException("이 냉장고에 접근할 권한이 없습니다.");
        }

        return ingredientRequestDTOs.stream()
                .map(requestDTO -> createIngredient(refrigerator, requestDTO))
                .collect(Collectors.toList());
    }

    private IngredientResponseDTO createIngredient(Refrigerator refrigerator, IngredientRequestDTO ingredientRequestDTO) {
        Ingredient ingredient = Ingredient.builder()
                .refrigerator(refrigerator)
                .name(ingredientRequestDTO.getName())
                .photoUrl(ingredientRequestDTO.getPhotoUrl())
                .quantity(ingredientRequestDTO.getQuantity())
                .category(ingredientRequestDTO.getCategory())
                .expirationDate(ingredientRequestDTO.getExpirationDate())
                .member(refrigerator.getMember()) // 냉장고 소유자 설정
                .build();

        Ingredient savedIngredient = ingredientRepository.save(ingredient);

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

    // 단일 재료 수정
    public IngredientResponseDTO updateIngredient(Long refrigeratorId, Long ingredientId, IngredientRequestDTO ingredientRequestDTO, String userId) {
        Refrigerator refrigerator = refrigeratorRepository.findById(refrigeratorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 냉장고가 존재하지 않습니다."));

        // 소유자 확인
        if (!refrigerator.getMember().getId().toString().equals(userId)) {
            throw new IllegalArgumentException("이 냉장고에 접근할 권한이 없습니다.");
        }

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("해당 재료가 존재하지 않습니다."));

        // 소유자 확인
        if (!ingredient.getMember().getId().toString().equals(userId)) {
            throw new IllegalArgumentException("이 재료를 수정할 권한이 없습니다.");
        }

        ingredient.setName(ingredientRequestDTO.getName());
        ingredient.setPhotoUrl(ingredientRequestDTO.getPhotoUrl());
        ingredient.setQuantity(ingredientRequestDTO.getQuantity());
        ingredient.setCategory(ingredientRequestDTO.getCategory());
        ingredient.setExpirationDate(ingredientRequestDTO.getExpirationDate());

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

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

    // 다중 재료 삭제
    public void deleteIngredients(List<Long> ingredientIds, String userId) {
        for (Long id : ingredientIds) {
            Ingredient ingredient = ingredientRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 재료가 존재하지 않습니다."));

            // 소유자 확인
            if (!ingredient.getMember().getId().toString().equals(userId)) { // Long을 String으로 변환하여 비교
                throw new IllegalArgumentException("이 재료를 삭제할 권한이 없습니다.");
            }

            ingredientRepository.deleteById(id);
        }
    }

    // 단일 재료 조회
    public IngredientResponseDTO getIngredientById(Long refrigeratorId, Long ingredientId, String userId) {
        Refrigerator refrigerator = refrigeratorRepository.findById(refrigeratorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 냉장고가 존재하지 않습니다."));

        // 소유자 확인
        if (!refrigerator.getMember().getId().toString().equals(userId)) {
            throw new IllegalArgumentException("이 냉장고에 접근할 권한이 없습니다.");
        }

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("해당 재료가 존재하지 않습니다."));

        // 소유자 확인
        if (!ingredient.getRefrigerator().getId().equals(refrigeratorId)) {
            throw new IllegalArgumentException("이 재료는 해당 냉장고에 속하지 않습니다.");
        }

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

    // 특정 냉장고의 재료 조회 (카테고리 선택적)
    public List<IngredientResponseDTO> getIngredientsByRefrigeratorId(Long refrigeratorId, String category, String userId) {
        Refrigerator refrigerator = refrigeratorRepository.findById(refrigeratorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 냉장고가 존재하지 않습니다."));

        // 소유자 확인
        if (!refrigerator.getMember().getId().toString().equals(userId)) {
            throw new IllegalArgumentException("이 냉장고에 접근할 권한이 없습니다.");
        }

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
}