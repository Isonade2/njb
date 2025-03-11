package njb.recipe.global.jwt;

import lombok.RequiredArgsConstructor;
import njb.recipe.entity.Member;
import njb.recipe.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("Invalid Email or Password"));

        if(!member.isActivated()){
            throw new UsernameNotFoundException("InActivated User");
        }

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getRole()));

        //List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(member.getAuthorities().toString()));

        return new User(member.getEmail(), member.getPassword(), authorities);
    }
}
