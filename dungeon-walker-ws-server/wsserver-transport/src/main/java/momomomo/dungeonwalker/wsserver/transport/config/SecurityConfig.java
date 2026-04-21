package momomomo.dungeonwalker.wsserver.transport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableWebSocketSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(final PasswordEncoder encoder) {
        final var user1 =
                User.builder()
                        .username("user1")
                        .password(encoder.encode("password1"))
                        .roles("USER")
                        .build();

        final var user2 =
                User.builder()
                        .username("user2")
                        .password(encoder.encode("password2"))
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user1, user2);
    }

    /**
     * See:
     * <a href="https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html">
     * Spring WebSocket Security
     * </a>
     */
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(
            final MessageMatcherDelegatingAuthorizationManager.Builder messages
    ) {
        return messages
                .simpDestMatchers("/ws-endpoint/**").authenticated()
                .anyMessage().authenticated()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(basic -> basic // HttpBasicConfigurer allows access to the WebSocket
                        .realmName("dungeon-walker-ws-server"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator", "/actuator/**").permitAll()
                        .requestMatchers("/ws-endpoint", "/ws-endpoint/**").hasRole("USER")
                        .anyRequest().authenticated())
                .build();
    }

}
