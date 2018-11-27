package kr.openmind.restapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId("restapi");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.anonymous().and()
            .exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler()).and()
            .authorizeRequests()
            .mvcMatchers(HttpMethod.POST, "/api/**").authenticated()
            .mvcMatchers(HttpMethod.PUT, "/api/**").authenticated();
    }
}
