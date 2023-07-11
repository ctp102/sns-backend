package world.meta.sns.api.security.core.userdetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import world.meta.sns.api.exception.CustomUnauthorizedException;
import world.meta.sns.core.member.entity.Member;
import world.meta.sns.core.member.repository.MemberRepository;

import java.util.Objects;

import static world.meta.sns.api.common.enums.ErrorResponseCodes.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member foundMember = memberRepository.findMemberByEmail(email);

        if (Objects.isNull(foundMember)) {
            log.info("[loadUserByUsername] [email: {}] 존재하지 않는 회원입니다.", email);
            throw new CustomUnauthorizedException(MEMBER_NOT_EXISTS.getNumber(), MEMBER_NOT_EXISTS.getMessage());
        }

        return new PrincipalDetails(foundMember);
    }

}
