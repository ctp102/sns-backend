package world.meta.sns.api.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class CoreConfig {

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

//    @Bean
//    public MappingJackson2HttpMessageConverter jsonHttpMessageConverter(ObjectMapper objectMapper) {
//        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter(objectMapper());
//        jsonHttpMessageConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/json"));
//        jsonHttpMessageConverter.setPrefixJson(false);
//
//        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
//        stringConverter.setWriteAcceptCharset(false);
//
//        ByteArrayHttpMessageConverter byteArrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
//
//        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
//        formHttpMessageConverter.setCharset(StandardCharsets.UTF_8);
//
//        List<HttpMessageConverter<?>> httpMessageConverters = new ArrayList<>();
//        httpMessageConverters.add(jsonHttpMessageConverter);
//        httpMessageConverters.add(stringConverter);
//        httpMessageConverters.add(byteArrayHttpMessageConverter);
//        httpMessageConverters.add(formHttpMessageConverter);
//
//        return jsonHttpMessageConverter;
//    }

}
