package world.meta.sns.security.provider;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes; // oauth2User.getAttributes()

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        Map<String, String> kakaoAccountMap = (Map<String, String>) attributes.get("kakao_account");

        if (kakaoAccountMap.isEmpty()) {
            return null;
        }

        return kakaoAccountMap.get("email");
    }

    @Override
    public String getName() {
        Map<String, String> propertiesMap = (Map<String, String>) attributes.get("properties");

        if (propertiesMap.isEmpty()) {
            return null;
        }

        return propertiesMap.get("nickname");
    }

}
