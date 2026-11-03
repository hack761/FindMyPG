package in.findmypg.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;

@RestController @RequestMapping("/api/auth")
class AuthController {
    private final AccountRepository accounts; private final PasswordEncoder passwords; private final JwtEncoder encoder;
    @Value("${app.jwt.issuer}") String issuer; @Value("${app.jwt.ttl}") Duration ttl; @Value("${app.cookie.secure:false}") boolean cookieSecure;
    AuthController(AccountRepository accounts,PasswordEncoder passwords,JwtEncoder encoder){this.accounts=accounts;this.passwords=passwords;this.encoder=encoder;}
    @PostMapping("/register") ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        if(request.role()==Role.ADMIN) return ResponseEntity.badRequest().body(Map.of("message","Admin accounts are provisioned by platform operators."));
        String email=request.email().trim().toLowerCase(Locale.ROOT);
        if(accounts.findByEmailIgnoreCase(email).isPresent()) return ResponseEntity.status(409).body(Map.of("message","An account already uses this email."));
        Account user=accounts.save(new Account(email,passwords.encode(request.password()),request.name().trim(),request.role()));
        return withSession(issue(user),201);
    }
    @PostMapping("/login") ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        Account user=accounts.findByEmailIgnoreCase(request.email().trim()).orElse(null);
        if(user==null || !user.enabled || !passwords.matches(request.password(),user.passwordHash)) return ResponseEntity.status(401).body(Map.of("message","Email or password is incorrect."));
        return withSession(issue(user),200);
    }
    @PostMapping("/logout") ResponseEntity<?> logout(){ResponseCookie cookie=ResponseCookie.from("FM_SESSION","").httpOnly(true).secure(cookieSecure).sameSite("Strict").path("/").maxAge(Duration.ZERO).build();return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE,cookie.toString()).build();}
    private Issued issue(Account u){
        Instant now=Instant.now(); JwtClaimsSet claims=JwtClaimsSet.builder().issuer(issuer).issuedAt(now).expiresAt(now.plus(ttl)).subject(u.id.toString()).claim("email",u.email).claim("name",u.name).claim("roles",List.of("ROLE_"+u.role.name())).build();
        String token=encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();
        return new Issued(new AuthResponse(Map.of("id",u.id,"name",u.name,"email",u.email,"role",u.role.name())),token);
    }
    private ResponseEntity<AuthResponse> withSession(Issued issued,int status){ResponseCookie cookie=ResponseCookie.from("FM_SESSION",issued.token()).httpOnly(true).secure(cookieSecure).sameSite("Strict").path("/").maxAge(ttl).build();return ResponseEntity.status(status).header(HttpHeaders.SET_COOKIE,cookie.toString()).body(issued.response());}
    private record Issued(AuthResponse response,String token){}
}
