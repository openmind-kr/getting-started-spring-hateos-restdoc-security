package kr.openmind.restapi.config;

import kr.openmind.restapi.common.ApplicationSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final String[] AUTHORIZED_GRANT_TYPES = { "password", "refresh_token" };
    private static final String[] AUTHORIZED_SCOPES = { "read", "write", "trust" };
    private static final int ACCESS_TOKEN_VALIDITY_SECONDS = (int) TimeUnit.SECONDS.convert(10, TimeUnit.MINUTES);
    private static final int REFRESH_TOKEN_VALIDITY_SECONDS = (int) TimeUnit.SECONDS.convert(1, TimeUnit.HOURS);

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private ApplicationSecurityProperties applicationSecurityProperties;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
            .withClient(applicationSecurityProperties.getDefaultClientId())
            .authorizedGrantTypes(AUTHORIZED_GRANT_TYPES)
            .scopes(AUTHORIZED_SCOPES)
            .secret(passwordEncoder.encode(applicationSecurityProperties.getDefaultClientSecret()))
            .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
            .refreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore)
            .authenticationManager(authenticationManager)
            .userDetailsService(userDetailsService);
    }
}
