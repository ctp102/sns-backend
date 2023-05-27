package world.meta.sns.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@RequiredArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private final Long accessLength;
    private final Long refreshLength;
    private final String accessSecretKey;  // 액세스 토큰 생성 시 필요한 Secret Key
    private final String refreshSecretKey; // 리프레시 토큰 생성 시 필요한 Secret Key

}
