package njb.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import njb.recipe.entity.FcmToken;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    
     // 토큰 중복 확인용 
     boolean existsByToken(String token);

     // 특정 멤버의 모든 토큰 조회 
     List<FcmToken> findByMemberOrderByCreatedAtAsc(Member member);
 
     // 가장 오래된 토큰 1개 조회
     Optional<FcmToken> findFirstByMemberOrderByCreatedAtAsc(Member member);
 
     // 멤버의 토큰 개수만 조회 
     long countByMember(Member member);
 
     // 로그아웃 시 또는 수동 삭제용(푸시 실패 응답 시에도)
     void deleteByToken(String token);
} 