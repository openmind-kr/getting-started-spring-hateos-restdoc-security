package kr.openmind.restapi.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "application-security")
public class ApplicationSecurityProperties {

    private String defaultClientId;
    private String defaultClientSecret;
}
