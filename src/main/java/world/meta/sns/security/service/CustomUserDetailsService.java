package world.meta.sns.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import world.meta.sns.entity.Member;
import world.meta.sns.repository.member.MemberRepository;
import world.meta.sns.security.enums.OAuth2ProviderTypes;
import world.meta.sns.security.enums.RoleTypes;
import world.meta.sns.security.provider.*;
import world.meta.sns.service.security.PrincipalDetails;

import java.util.Map;
import java.util.UUID;

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

        return new PrincipalDetails(foundMember);
    }

}
