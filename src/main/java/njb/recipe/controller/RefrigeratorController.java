package njb.recipe.controller;

import njb.recipe.dto.refri.RefrigeratorRequestDTO;
import njb.recipe.dto.refri.RefrigeratorResponseDTO;
import njb.recipe.dto.ApiResponseDTO;
import njb.recipe.dto.ResponseUtils;
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
@RequestMapping("/refrigerators")
public class RefrigeratorController {

    @Autowired
    private RefrigeratorService refrigeratorService;

    // 냉장고 생성
    @PostMapping
    public ResponseEntity<ApiResponseDTO<Void>> createRefrigerator(
            @RequestBody RefrigeratorRequestDTO refrigeratorDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        refrigeratorService.createRefrigerator(refrigeratorDTO, userDetails.getMemberId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtils.success("냉장고가 성공적으로 생성되었습니다."));
    }

    // 냉장고 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<RefrigeratorResponseDTO>>> getRefrigeratorsByMemberId(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "sort", required = false, defaultValue = "name") String sort,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction) {
        
        List<RefrigeratorResponseDTO> responseDTOs = refrigeratorService.getRefrigeratorsByMemberId(userDetails.getMemberId(), sort, direction);
        return ResponseEntity.ok(ResponseUtils.success(responseDTOs, "냉장고 목록 조회 성공"));
    }

    // 냉장고 조회
    @GetMapping("/{refrigeratorId}")
    public ResponseEntity<ApiResponseDTO<RefrigeratorResponseDTO>> getRefrigeratorById(
            @PathVariable(name = "refrigeratorId") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        String memberId = userDetails.getMemberId();
        Optional<RefrigeratorResponseDTO> responseDTO = refrigeratorService.getRefrigeratorById(id, Long.parseLong(memberId));

        return responseDTO.map(dto -> ResponseEntity.ok(ResponseUtils.success(dto, "냉장고 조회 성공")))
                          .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                          .body(ResponseUtils.fail("냉장고를 찾을 수 없습니다.")));
    }

    // 냉장고 수정
    @PutMapping("/{refrigeratorId}")
    public ResponseEntity<ApiResponseDTO<Void>> updateRefrigerator(
            @PathVariable(name = "refrigeratorId") Long id,
            @RequestBody RefrigeratorRequestDTO refrigeratorDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        String memberId = userDetails.getMemberId();
        boolean isUpdated = refrigeratorService.updateRefrigerator(id, refrigeratorDTO, Long.parseLong(memberId));

        return isUpdated ? ResponseEntity.ok(ResponseUtils.success("냉장고가 성공적으로 수정되었습니다."))
                         : ResponseEntity.status(HttpStatus.NOT_FOUND)
                         .body(ResponseUtils.fail("냉장고를 찾을 수 없습니다."));
    }

    // 냉장고 삭제
    @DeleteMapping("/{refrigeratorId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteRefrigerator(
            @PathVariable(name = "refrigeratorId") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        String memberId = userDetails.getMemberId();
        boolean isDeleted = refrigeratorService.deleteRefrigerator(id, Long.parseLong(memberId));

        if (isDeleted) {
            return ResponseEntity.ok(ResponseUtils.success(null, "냉장고가 성공적으로 삭제되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtils.fail("냉장고를 찾을 수 없습니다."));
        }
    }
}
