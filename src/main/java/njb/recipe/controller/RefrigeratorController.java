package njb.recipe.controller;

import njb.recipe.dto.refri.RefrigeratorDTO;
import njb.recipe.dto.refri.RefrigeratorResponseDTO;
import njb.recipe.entity.Member;
import njb.recipe.entity.Refrigerator;
import njb.recipe.global.jwt.CustomUserDetails;
import njb.recipe.service.RefrigeratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/refri")
public class RefrigeratorController {

    @Autowired
    private RefrigeratorService refrigeratorService;

    // 냉장고 생성
    @PostMapping
    public ResponseEntity<Void> createRefrigerator(
            @RequestBody RefrigeratorDTO refrigeratorDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String memberId = userDetails.getMemberId(); // JWT에서 member_id 추출

        // DTO에서 값을 가져와서 Refrigerator 엔티티 생성
        Refrigerator refrigerator = Refrigerator.builder()
                .name(refrigeratorDTO.getName())
                .photoUrl(refrigeratorDTO.getPhotoUrl())
                .description(refrigeratorDTO.getDescription())
                .member(new Member(Long.parseLong(memberId))) // Member 객체 설정
                .build();

        // 냉장고 저장
        refrigeratorService.createRefrigerator(refrigerator);

        // 201 Created 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 냉장고 조회
    @GetMapping("/{id}")
    public ResponseEntity<RefrigeratorResponseDTO> getRefrigeratorById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String memberId = userDetails.getMemberId(); // JWT에서 member_id 추출
        Optional<Refrigerator> refrigerator = refrigeratorService.getRefrigeratorById(id, Long.parseLong(memberId));

        return refrigerator.map(r -> {
            RefrigeratorResponseDTO responseDTO = new RefrigeratorResponseDTO();
            responseDTO.setId(r.getId()); // ID 설정
            responseDTO.setName(r.getName());
            responseDTO.setPhotoUrl(r.getPhotoUrl());
            responseDTO.setDescription(r.getDescription());
            return ResponseEntity.ok(responseDTO);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // 냉장고 목록 조회
    @GetMapping
    public ResponseEntity<List<RefrigeratorResponseDTO>> getRefrigeratorsByMemberId(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String memberId = userDetails.getMemberId(); // JWT에서 member_id 추출
        List<Refrigerator> refrigerators = refrigeratorService.getRefrigeratorsByMemberId(Long.parseLong(memberId));

        List<RefrigeratorResponseDTO> responseDTOs = refrigerators.stream().map(r -> {
            RefrigeratorResponseDTO responseDTO = new RefrigeratorResponseDTO();
            responseDTO.setId(r.getId());
            responseDTO.setName(r.getName());
            responseDTO.setPhotoUrl(r.getPhotoUrl());
            responseDTO.setDescription(r.getDescription());
            return responseDTO;
        }).toList();

        return ResponseEntity.ok(responseDTOs);
    }

    //냉장고 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRefrigerator(
            @PathVariable Long id,
            @RequestBody RefrigeratorDTO refrigeratorDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String memberId = userDetails.getMemberId(); // JWT에서 member_id 추출

        Refrigerator updatedRefrigerator = Refrigerator.builder()
                .name(refrigeratorDTO.getName())
                .photoUrl(refrigeratorDTO.getPhotoUrl())
                .description(refrigeratorDTO.getDescription())
                .build();

        boolean isUpdated = refrigeratorService.updateRefrigerator(id, updatedRefrigerator, Long.parseLong(memberId));
        return isUpdated ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // 냉장고 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRefrigerator(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String memberId = userDetails.getMemberId(); // JWT에서 member_id 추출
        boolean isDeleted = refrigeratorService.deleteRefrigerator(id, Long.parseLong(memberId));
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
