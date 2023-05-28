package world.meta.sns.aaaaaa.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import world.meta.sns.aaaaaa.member.entity.Member;
import world.meta.sns.aaaaaa.member.repository.MemberRepository;
import world.meta.sns.aaaaaa.security.vo.PrincipalDetailsVO;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member foundMember = memberRepository.findMemberByEmail(email);

        if (foundMember == null) {
            log.info("[loadUserByUsername] [email: {}] 회원을 찾을 수 없습니다.", email); // 만약 비밀번호가 틀린거라면?
            return null;
        }

        return new PrincipalDetailsVO(foundMember);
    }

}
