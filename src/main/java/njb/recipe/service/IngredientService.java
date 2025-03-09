package njb.recipe.service;

import njb.recipe.dto.refri.IngredientRequestDTO;
import njb.recipe.dto.refri.IngredientResponseDTO;
import njb.recipe.entity.Category;
import njb.recipe.entity.Ingredient;
import njb.recipe.entity.Refrigerator;
import njb.recipe.repository.CategoryRepository;
import njb.recipe.repository.IngredientRepository;
import njb.recipe.repository.RefrigeratorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

    @Autowired
    private CategoryRepository categoryRepository; 

    
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

        // 재료의 냉장고 ID가 입력받은 냉장고 ID와 일치하는지 확인
        if (!ingredient.getRefrigerator().getId().equals(refrigeratorId)) {
            throw new IllegalArgumentException("이 재료는 해당 냉장고에 속하지 않습니다.");
        }

        // 소유자 확인
        if (!ingredient.getMember().getId().toString().equals(userId)) {
            throw new IllegalArgumentException("이 재료를 수정할 권한이 없습니다.");
        }

        // 카테고리 조회
        Category category = categoryRepository.findById(ingredientRequestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

        // 재료 수정
        ingredient.setName(ingredientRequestDTO.getName());
        ingredient.setPhotoUrl(ingredientRequestDTO.getPhotoUrl());
        ingredient.setQuantity(ingredientRequestDTO.getQuantity());
        ingredient.setCategory(category); 
        ingredient.setExpirationDate(ingredientRequestDTO.getExpirationDate());

        Ingredient updatedIngredient = ingredientRepository.save(ingredient);

        return IngredientResponseDTO.builder()
                .id(updatedIngredient.getId())
                .refrigeratorId(updatedIngredient.getRefrigerator().getId())
                .name(updatedIngredient.getName())
                .photoUrl(updatedIngredient.getPhotoUrl())
                .quantity(updatedIngredient.getQuantity())
                .category(updatedIngredient.getCategory().getName()) // 카테고리 이름 반환
                .registrationDate(updatedIngredient.getRegistrationDate())
                .expirationDate(updatedIngredient.getExpirationDate())
                .build();
    }

    // 다중 재료 삭제
    public void deleteIngredients(List<Long> ingredientIds, String userId, Long refrigeratorId) {
        // 모든 재료가 유효한지 확인
        for (Long id : ingredientIds) {
            Ingredient ingredient = ingredientRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 재료가 존재하지 않습니다."));

            // 소유자 확인
            if (!ingredient.getMember().getId().toString().equals(userId)) {
                throw new IllegalArgumentException("이 재료를 삭제할 권한이 없습니다.");
            }

            // 냉장고 ID 확인
            if (!ingredient.getRefrigerator().getId().equals(refrigeratorId)) {
                throw new IllegalArgumentException("해당 냉장고에 속하지 않는 재료입니다.");
            }
        }

        // 모든 유효성 검사를 통과한 경우에만 삭제
        ingredientRepository.deleteAllById(ingredientIds);
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

        // 재료의 냉장고 ID가 입력받은 냉장고 ID와 일치하는지 확인
        if (!ingredient.getRefrigerator().getId().equals(refrigeratorId)) {
            throw new IllegalArgumentException("이 재료는 해당 냉장고에 속하지 않습니다.");
        }

        // 소유자 확인
        if (!ingredient.getMember().getId().toString().equals(userId)) {
            throw new IllegalArgumentException("이 재료를 조회할 권한이 없습니다.");
        }

        return IngredientResponseDTO.builder()
                .id(ingredient.getId())
                .refrigeratorId(ingredient.getRefrigerator().getId())
                .name(ingredient.getName())
                .photoUrl(ingredient.getPhotoUrl())
                .quantity(ingredient.getQuantity())
                .category(ingredient.getCategory().getName()) // 카테고리 이름 반환
                .registrationDate(ingredient.getRegistrationDate())
                .expirationDate(ingredient.getExpirationDate())
                .build();
    }

    // 특정 냉장고의 재료 조회 (카테고리 선택적, 정렬 추가)
    public List<IngredientResponseDTO> getIngredientsByRefrigeratorId(Long refrigeratorId, Long categoryId, String sortField, String sortOrder, String userId) {
        Refrigerator refrigerator = refrigeratorRepository.findById(refrigeratorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 냉장고가 존재하지 않습니다."));

        // 소유자 확인
        if (!refrigerator.getMember().getId().toString().equals(userId)) {
            throw new IllegalArgumentException("이 냉장고에 접근할 권한이 없습니다.");
        }

        List<Ingredient> ingredients;
        if (categoryId != null) {
            ingredients = ingredientRepository.findByRefrigeratorIdAndCategoryId(refrigeratorId, categoryId);
        } else {
            ingredients = ingredientRepository.findByRefrigeratorId(refrigeratorId);
        }

        // 정렬 설정
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        ingredients.sort((i1, i2) -> {
            int comparison = 0;
            if (sortField.equals("name")) {
                comparison = i1.getName().compareTo(i2.getName());
            } else if (sortField.equals("expirationDate")) {
                comparison = i1.getExpirationDate().compareTo(i2.getExpirationDate());
            } else if (sortField.equals("registrationDate")) {
                comparison = i1.getRegistrationDate().compareTo(i2.getRegistrationDate());
            }
            return direction.isAscending() ? comparison : -comparison;
        });

        return ingredients.stream()
                .map(ingredient -> IngredientResponseDTO.builder()
                        .id(ingredient.getId())
                        .refrigeratorId(ingredient.getRefrigerator().getId())
                        .name(ingredient.getName())
                        .photoUrl(ingredient.getPhotoUrl())
                        .quantity(ingredient.getQuantity())
                        .category(ingredient.getCategory().getName()) // 카테고리 이름 반환
                        .registrationDate(ingredient.getRegistrationDate())
                        .expirationDate(ingredient.getExpirationDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<IngredientResponseDTO> createIngredients(Long refrigeratorId, List<IngredientRequestDTO> ingredientRequestDTOs, String userId) {
        Refrigerator refrigerator = refrigeratorRepository.findById(refrigeratorId)
                .orElseThrow(() -> new IllegalArgumentException("해당 냉장고가 존재하지 않습니다."));

        // 소유자 확인
        if (!refrigerator.getMember().getId().toString().equals(userId)) {
            throw new IllegalArgumentException("이 냉장고에 접근할 권한이 없습니다.");
        }

        return ingredientRequestDTOs.stream()
                .map(dto -> {
                    // 카테고리 조회
                    Category category = categoryRepository.findById(dto.getCategoryId())
                            .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다."));

                    // 재료 생성
                    Ingredient ingredient = Ingredient.builder()
                            .refrigerator(refrigerator)
                            .name(dto.getName())
                            .photoUrl(dto.getPhotoUrl())
                            .quantity(dto.getQuantity())
                            .category(category)
                            .expirationDate(dto.getExpirationDate())
                            .member(refrigerator.getMember())
                            .build();

                    Ingredient savedIngredient = ingredientRepository.save(ingredient);

                    return IngredientResponseDTO.builder()
                            .id(savedIngredient.getId())
                            .refrigeratorId(refrigerator.getId())
                            .name(savedIngredient.getName())
                            .photoUrl(savedIngredient.getPhotoUrl())
                            .quantity(savedIngredient.getQuantity())
                            .category(savedIngredient.getCategory().getName())
                            .registrationDate(savedIngredient.getRegistrationDate())
                            .expirationDate(savedIngredient.getExpirationDate())
                            .build();
                })
                .collect(Collectors.toList());
    }
}