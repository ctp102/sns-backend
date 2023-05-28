package world.meta.sns.api.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import world.meta.sns.api.security.enums.OAuth2ProviderTypes;
import world.meta.sns.api.security.enums.RoleTypes;
import world.meta.sns.api.security.provider.*;
import world.meta.sns.api.security.vo.PrincipalDetailsVO;
import world.meta.sns.core.member.entity.Member;
import world.meta.sns.core.member.repository.MemberRepository;

import java.util.Map;
import java.util.UUID;

/**
 * 1. FilterChainProxy를 차례대로 실행하는 도중 OAuth2LoginAuthenticationFilter에 걸렸을 때 동작된다.
 * 2. 이후 ProviderManager 객체를 거쳐 OAuth2LoginAuthenticationProvider 객체의 loadUser 메서드를 실행하면 지금 이 메서드가 실행된다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2.0 Provider: {}", oAuth2UserRequest.getClientRegistration().getRegistrationId());

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest); // 여기서 AuthenticationPrincipalArgumentResolver 동작하나..?

        String oAuth2Provider = oAuth2UserRequest.getClientRegistration().getRegistrationId(); // ex) google, facebook

        OAuth2ProviderTypes oAuth2ProviderTypes = extractProviderTypes(oAuth2Provider);
        OAuth2UserInfo oAuth2UserInfo = createOAuth2UserInfo(oAuth2User, oAuth2ProviderTypes);

        // oauth2.0 인증을 완료한 회원들에 대한 회원가입 강제 진행
        String providerId   = oAuth2UserInfo.getProviderId();
        String email        = oAuth2UserInfo.getEmail();
        String password     = passwordEncoder.encode(UUID.randomUUID().toString()); // OAuth2.0에서는 비밀번호를 사용하지 않으므로 랜덤 비밀번호 저장
        String role         = RoleTypes.ROLE_USER.name();

        Member foundMember = memberRepository.findMemberByEmail(email);
        if (foundMember == null) {
            Member member = Member.builder()
                    .email(email)
                    .password(password)
                    .role(role)
                    .provider(oAuth2Provider)
                    .providerId(providerId)
                    .build();
            memberRepository.save(member);

            return new PrincipalDetailsVO(member, oAuth2User.getAttributes());
        }

        return new PrincipalDetailsVO(foundMember, oAuth2User.getAttributes());
    }

    public OAuth2UserInfo createOAuth2UserInfo(OAuth2User oAuth2User, OAuth2ProviderTypes oAuth2ProviderTypes) {
        switch (oAuth2ProviderTypes) {
            case GOOGLE -> {
                log.info("구글 로그인 요청");
                return new GoogleUserInfo(oAuth2User.getAttributes());
            }
            case FACEBOOK -> {
                log.info("페이스북 로그인 요청");
                return new FacebookUserInfo(oAuth2User.getAttributes());
            }
            case NAVER -> {
                log.info("네이버 로그인 요청");
                return new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
            }
            case KAKAO -> {
                log.info("카카오 로그인 요청");
                return new KakaoUserInfo(oAuth2User.getAttributes());
            }
            default -> {
                log.error("[createOAuth2UserInfo]: {}, 해당되는 OAuth2ProviderType이 존재하지 않습니다.", oAuth2ProviderTypes);
                return null;
            }
        }
    }

    public OAuth2ProviderTypes extractProviderTypes(String oAuth2Provider) {
        return OAuth2ProviderTypes.valueOf(oAuth2Provider.toUpperCase());
    }

}
