package world.meta.sns.api.config.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@RequiredArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private Long accessLength;
    private Long refreshLength;
    private String accessSecretKey;  // 액세스 토큰 생성 시 필요한 Secret Key
    private String refreshSecretKey; // 리프레시 토큰 생성 시 필요한 Secret Key

}
