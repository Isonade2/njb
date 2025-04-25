package njb.recipe.service;

import njb.recipe.entity.Ingredient;
import njb.recipe.repository.IngredientRepository;
import org.springframework.transaction.annotation.Transactional;
import njb.recipe.dto.refri.RefrigeratorRequestDTO;
import njb.recipe.dto.refri.RefrigeratorResponseDTO;
import njb.recipe.entity.Category;
import njb.recipe.entity.Member;
import njb.recipe.entity.Refrigerator;
import njb.recipe.repository.CategoryRepository;
import njb.recipe.repository.RefrigeratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RefrigeratorService {

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private S3Service s3Service;

     // 모든 카테고리 조회
     public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 냉장고 생성
    public void createRefrigerator(RefrigeratorRequestDTO refrigeratorDTO, String memberId) {
        // DTO에서 값을 가져와서 Refrigerator 엔티티 생성
        Refrigerator refrigerator = Refrigerator.builder()
                .name(refrigeratorDTO.getName())
                .photoUrl(refrigeratorDTO.getPhotoUrl())
                .description(refrigeratorDTO.getDescription())
                .member(new Member(Long.parseLong(memberId))) // Member 객체 설정
                .build();

        // 냉장고 저장
        refrigeratorRepository.save(refrigerator); // 냉장고 저장
    }

    // 냉장고 목록 조회
    public List<RefrigeratorResponseDTO> getRefrigeratorsByMemberId(String memberId, String sort, String direction) {
        // 유효한 정렬 기준인지 확인
        if (!"name".equals(sort) && !"createdAt".equals(sort)) {
            sort = "name"; // 기본값 설정
        }

        // 유효한 정렬 방향인지 확인
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException e) {
            sortDirection = Sort.Direction.ASC; // 기본값 설정
        }

        Sort sortOrder = Sort.by(sortDirection, sort); // 정렬 기준과 방향 설정
        List<Refrigerator> refrigerators = refrigeratorRepository.findByMemberId(Long.parseLong(memberId), sortOrder);

        // Refrigerator 엔티티를 DTO로 변환
        return refrigerators.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    // 냉장고 조회
    public Optional<RefrigeratorResponseDTO> getRefrigeratorById(Long id, Long memberId) {
        Optional<Refrigerator> refrigerator = refrigeratorRepository.findById(id);
        return refrigerator.filter(r -> r.getMember().getId().equals(memberId))
                          .map(this::convertToResponseDTO); // 소유자 확인 후 DTO로 변환
    }

    // DTO 변환 메서드
    private RefrigeratorResponseDTO convertToResponseDTO(Refrigerator refrigerator) {
        return RefrigeratorResponseDTO.builder()
                .id(refrigerator.getId())
                .name(refrigerator.getName())
                .photoUrl(refrigerator.getPhotoUrl())
                .description(refrigerator.getDescription())
                .build();
    }

    @Transactional
    public boolean updateRefrigerator(Long id, RefrigeratorRequestDTO refrigeratorDTO, Long memberId) {
        Optional<Refrigerator> optionalRefrigerator = refrigeratorRepository.findById(id);

        if (optionalRefrigerator.isPresent() && optionalRefrigerator.get().getMember().getId().equals(memberId)) {
            Refrigerator refrigerator = optionalRefrigerator.get();

            String oldPhotoUrl = refrigerator.getPhotoUrl();  // 기존 이미지 URL
            String newPhotoUrl = refrigeratorDTO.getPhotoUrl();  // 새 이미지 URL

            // 🔥 이미지가 수정되었을 때 (기존 이미지가 있고, 새 URL이 다를 때)
            if (oldPhotoUrl != null && !oldPhotoUrl.isBlank() && !oldPhotoUrl.equals(newPhotoUrl)) {
                s3Service.deleteFile(oldPhotoUrl);  // 기존 이미지 삭제
            }

            // 🔥 필드 업데이트
            refrigerator.setName(refrigeratorDTO.getName());
            refrigerator.setPhotoUrl(newPhotoUrl);  // 새 이미지 URL (새로 추가되거나, null일 수 있음)
            refrigerator.setDescription(refrigeratorDTO.getDescription());

            refrigeratorRepository.save(refrigerator);
            return true; // 수정 성공
        }

        return false; // 냉장고가 없거나 소유자가 다를 경우
    }

    // 냉장고 삭제
    @Transactional
    public boolean deleteRefrigerator(Long id, Long memberId) {
        Optional<Refrigerator> optionalRefrigerator = refrigeratorRepository.findById(id);

        if (optionalRefrigerator.isPresent() && optionalRefrigerator.get().getMember().getId().equals(memberId)) {
            Refrigerator refrigerator = optionalRefrigerator.get();

            // 1. 재료 리스트 조회
            List<Ingredient> ingredients = ingredientRepository.findByRefrigeratorId(refrigerator.getId());

            // 2. 재료 이미지 삭제
            for (Ingredient ingredient : ingredients) {
                if (ingredient.getPhotoUrl() != null) {
                    s3Service.deleteFile(ingredient.getPhotoUrl());
                }
            }

            // 3. 냉장고 이미지 삭제 (있다면)
            if (refrigerator.getPhotoUrl() != null) {
                s3Service.deleteFile(refrigerator.getPhotoUrl());
            }

            // 4. 냉장고 삭제 (Cascade로 재료도 삭제)
            refrigeratorRepository.deleteById(id);
            return true;
        }
        return false;
    }


}
