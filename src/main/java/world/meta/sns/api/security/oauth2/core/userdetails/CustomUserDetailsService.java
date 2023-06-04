package world.meta.sns.api.security.oauth2.core.userdetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import world.meta.sns.api.common.enums.ErrorResponseCodes;
import world.meta.sns.api.exception.CustomNotFoundException;
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

        // TODO: [2023-06-04] 예외를 ControllerAdvice가 잡지 못하는 이슈
        if (foundMember == null) {
            log.info("[loadUserByUsername] [email: {}] 존재하지 않는 회원입니다.", email); // 만약 비밀번호가 틀린거라면?
            throw new CustomNotFoundException(ErrorResponseCodes.MEMBER_NOT_FOUND.getNumber(), ErrorResponseCodes.MEMBER_NOT_FOUND.getMessage());
        }

        return new PrincipalDetails(foundMember);
    }

}
