package world.meta.sns.api.security.core.userdetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import world.meta.sns.api.common.enums.ErrorResponseCodes;
import world.meta.sns.api.exception.CustomUnauthorizedException;
import world.meta.sns.core.member.entity.Member;
import world.meta.sns.core.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member foundMember = memberRepository.findMemberByEmail(email);

        if (foundMember == null) {
            log.info("[loadUserByUsername] [email: {}] 존재하지 않는 회원입니다.", email); // 만약 비밀번호가 틀리면 --> '401, 자격 증명에 실패하였습니다'라는 메시지가 반환됨
            throw new CustomUnauthorizedException(ErrorResponseCodes.MEMBER_UNAUTHORIZED.getNumber(), ErrorResponseCodes.MEMBER_UNAUTHORIZED.getMessage());
        }

        return new PrincipalDetails(foundMember);
    }

}
