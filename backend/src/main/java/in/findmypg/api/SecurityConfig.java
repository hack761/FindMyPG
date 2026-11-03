package in.findmypg.api;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.cors.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Configuration
class SecurityConfig {
    @Value("${app.jwt.secret}") String secret;
    @Value("${app.cors.origins}") String origins;
    @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(12); }
    private SecretKey key(){ return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"); }
    @Bean JwtEncoder jwtEncoder(){ return new NimbusJwtEncoder(new ImmutableSecret<>(key())); }
    @Bean JwtDecoder jwtDecoder(){ return NimbusJwtDecoder.withSecretKey(key()).macAlgorithm(MacAlgorithm.HS256).build(); }
    @Bean BearerTokenResolver bearerTokenResolver(){return request->{String header=request.getHeader("Authorization");if(header!=null&&header.startsWith("Bearer "))return header.substring(7);if(request.getCookies()!=null)for(var c:request.getCookies())if("FM_SESSION".equals(c.getName())&&!c.getValue().isBlank())return c.getValue();return null;};}
    @Bean JwtAuthenticationConverter jwtAuthenticationConverter(){
        var converter=new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {List<String> roles=jwt.getClaimAsStringList("roles");return roles==null?List.of():roles.stream().map(role->(GrantedAuthority)new SimpleGrantedAuthority(role)).toList();});
        return converter;
    }
    @Bean SecurityFilterChain security(HttpSecurity http, JwtAuthenticationConverter converter, BearerTokenResolver resolver,AccountRepository accounts) throws Exception {
        return http.csrf(csrf->csrf.disable()).cors(Customizer.withDefaults()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->a.requestMatchers("/actuator/health", "/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/listings","/api/listings/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll().requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/owner/**").hasAnyRole("OWNER","ADMIN").anyRequest().authenticated())
            .oauth2ResourceServer(o->o.bearerTokenResolver(resolver).jwt(j->j.jwtAuthenticationConverter(converter)))
            .addFilterAfter(new EnabledAccountFilter(accounts),BearerTokenAuthenticationFilter.class).build();
    }
    @Bean CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration c=new CorsConfiguration(); c.setAllowedOrigins(Arrays.stream(origins.split(",")).map(String::trim).toList());
        c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS")); c.setAllowedHeaders(List.of("Authorization","Content-Type")); c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/**",c); return source;
    }
}
