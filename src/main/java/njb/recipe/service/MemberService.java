package njb.recipe.service;

import lombok.RequiredArgsConstructor;
import njb.recipe.dto.member.UserInfoResponseDTO;
import njb.recipe.entity.Member;
import njb.recipe.global.jwt.TokenProvider;
import njb.recipe.handler.exception.UserIdNotFountException;
import njb.recipe.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    public UserInfoResponseDTO getUserInfo(String memberId){
        long mId = Long.parseLong(memberId);
        Member member = memberRepository.findById(mId)
                .orElseThrow(() -> new UserIdNotFountException("User Not Found"));

        return UserInfoResponseDTO.of(member.getEmail(), member.getNickname());



    }
}
