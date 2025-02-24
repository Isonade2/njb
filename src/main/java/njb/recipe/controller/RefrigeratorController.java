package njb.recipe.controller;

import njb.recipe.dto.refri.RefrigeratorRequestDTO;
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
            @RequestBody RefrigeratorRequestDTO refrigeratorDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // 냉장고 생성 요청을 서비스에 위임
        refrigeratorService.createRefrigerator(refrigeratorDTO, userDetails.getMemberId());

        // 201 Created 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 냉장고 목록 조회
    @GetMapping
    public ResponseEntity<List<RefrigeratorResponseDTO>> getRefrigeratorsByMemberId(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        // 사용자 ID로 냉장고 목록 조회 요청을 서비스에 위임
        List<RefrigeratorResponseDTO> responseDTOs = refrigeratorService.getRefrigeratorsByMemberId(userDetails.getMemberId());

        return ResponseEntity.ok(responseDTOs);
    }
    // 냉장고 조회
    @GetMapping("/{refrigeratorId}")
    public ResponseEntity<RefrigeratorResponseDTO> getRefrigeratorById(
            @PathVariable(name = "refrigeratorId") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        String memberId = userDetails.getMemberId(); // JWT에서 member_id 추출
        Optional<RefrigeratorResponseDTO> responseDTO = refrigeratorService.getRefrigeratorById(id, Long.parseLong(memberId));

        return responseDTO.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // 냉장고 수정
    @PutMapping("/{refrigeratorId}")
    public ResponseEntity<Void> updateRefrigerator(
            @PathVariable(name = "refrigeratorId") Long id,
            @RequestBody RefrigeratorRequestDTO refrigeratorDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        String memberId = userDetails.getMemberId(); // JWT에서 member_id 추출
        boolean isUpdated = refrigeratorService.updateRefrigerator(id, refrigeratorDTO, Long.parseLong(memberId));

        return isUpdated ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
   // 냉장고 삭제
   @DeleteMapping("/{refrigeratorId}")
   public ResponseEntity<Void> deleteRefrigerator(
           @PathVariable(name = "refrigeratorId") Long id,
           @AuthenticationPrincipal CustomUserDetails userDetails) {
       
       String memberId = userDetails.getMemberId(); // JWT에서 member_id 추출
       boolean isDeleted = refrigeratorService.deleteRefrigerator(id, Long.parseLong(memberId));

       return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
   }
}
