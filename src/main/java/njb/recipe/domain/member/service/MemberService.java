package njb.recipe.domain.member.service;

import lombok.RequiredArgsConstructor;
import njb.recipe.domain.member.dto.PasswordUpdateRequest;
import njb.recipe.domain.member.dto.UserInfoResponseDTO;
import njb.recipe.domain.member.entity.PasswordResetToken;
import njb.recipe.domain.member.repository.PasswordResetTokenRepository;
import njb.recipe.dto.token.FcmTokenRequestDTO;
import njb.recipe.domain.member.entity.Member;
import njb.recipe.handler.exception.MemberException;
import njb.recipe.handler.exception.UserIdNotFountException;
import njb.recipe.domain.member.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordResetTokenRepository redisRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final EmailService emailService;

    public UserInfoResponseDTO getUserInfo(String memberId){
        long mId = Long.parseLong(memberId);
        Member member = memberRepository.findById(mId)
                .orElseThrow(() -> new UserIdNotFountException("User Not Found"));

        return UserInfoResponseDTO.of(member.getEmail(), member.getNickname());
    }

    //fcm 토큰 업데이트
    @Transactional
    public void updateFcmToken(String memberId, FcmTokenRequestDTO fcmToken) {
        long mId = Long.parseLong(memberId);
        Member member = memberRepository.findById(mId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID: " + memberId));
        member.updateFcmToken(fcmToken.getFcmToken());
        memberRepository.save(member);
    }

    // 비밀번호 초기화 메일 발송
    @Transactional
    public void createPasswordResetToken(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberException.MemberNotFoundException::new);

        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = PasswordResetToken.builder()
                .id(token)
                .memberId(member.getId())
                .build();

        redisRepo.save(prt);
        emailService.sendPRTEmail(email, token);
    }

    public void updatePassword(String email, String newPassword, String token) {
        PasswordResetToken prt = redisRepo.findById(token).orElseThrow(MemberException.MemberPasswordResetTokenNotFoundException::new);
        Member member = memberRepository.findById(prt.getMemberId())
                .orElseThrow(MemberException.MemberNotFoundException::new);
        member.updatePassword(newPassword);
        memberRepository.save(member);
        redisRepo.delete(prt);
    }
}
