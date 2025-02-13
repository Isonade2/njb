package njb.recipe.service;

import njb.recipe.entity.Refrigerator;
import njb.recipe.repository.RefrigeratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RefrigeratorService {

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

    // 냉장고 생성
    public Refrigerator createRefrigerator(Refrigerator refrigerator) {
        return refrigeratorRepository.save(refrigerator); // 냉장고 저장
    }

    // 냉장고 조회
    public Optional<Refrigerator> getRefrigeratorById(Long id, Long memberId) {
        Optional<Refrigerator> refrigerator = refrigeratorRepository.findById(id);
        return refrigerator.filter(r -> r.getMember().getId().equals(memberId)); // 소유자 확인
    }

    // 냉장고 목록 조회
    public List<Refrigerator> getRefrigeratorsByMemberId(Long memberId) {
        return refrigeratorRepository.findByMemberId(memberId); // memberId로 냉장고 목록 조회
    }

    // 냉장고 수정
    public boolean updateRefrigerator(Long id, Refrigerator updatedRefrigerator, Long memberId) {
        Optional<Refrigerator> optionalRefrigerator = refrigeratorRepository.findById(id);
        if (optionalRefrigerator.isPresent() && optionalRefrigerator.get().getMember().getId().equals(memberId)) {
            Refrigerator refrigerator = optionalRefrigerator.get();
            refrigerator.setName(updatedRefrigerator.getName());
            refrigerator.setPhotoUrl(updatedRefrigerator.getPhotoUrl());
            refrigerator.setDescription(updatedRefrigerator.getDescription());
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
