package njb.recipe.service;

import njb.recipe.dto.refri.RefrigeratorRequestDTO;
import njb.recipe.dto.refri.RefrigeratorResponseDTO;
import njb.recipe.entity.Member;
import njb.recipe.entity.Refrigerator;
import njb.recipe.global.jwt.CustomUserDetails;
import njb.recipe.repository.RefrigeratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RefrigeratorService {

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

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
    public List<RefrigeratorResponseDTO> getRefrigeratorsByMemberId(String memberId) {
        List<Refrigerator> refrigerators = refrigeratorRepository.findByMemberId(Long.parseLong(memberId)); // memberId로 냉장고 목록 조회

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

    // 냉장고 수정
    public boolean updateRefrigerator(Long id, RefrigeratorRequestDTO refrigeratorDTO, Long memberId) {
        Optional<Refrigerator> optionalRefrigerator = refrigeratorRepository.findById(id);
        if (optionalRefrigerator.isPresent() && optionalRefrigerator.get().getMember().getId().equals(memberId)) {
            Refrigerator refrigerator = optionalRefrigerator.get();
            refrigerator.setName(refrigeratorDTO.getName());
            refrigerator.setPhotoUrl(refrigeratorDTO.getPhotoUrl());
            refrigerator.setDescription(refrigeratorDTO.getDescription());
            refrigeratorRepository.save(refrigerator);
            return true; // 수정 성공
        }
        return false; // 냉장고가 없거나 소유자가 다를 경우
    }

    // 냉장고 삭제
    public boolean deleteRefrigerator(Long id, Long memberId) {
        Optional<Refrigerator> optionalRefrigerator = refrigeratorRepository.findById(id);
        if (optionalRefrigerator.isPresent() && optionalRefrigerator.get().getMember().getId().equals(memberId)) {
            refrigeratorRepository.deleteById(id);
            return true; // 삭제 성공
        }
        return false; // 냉장고가 없거나 소유자가 다를 경우
    }
}
