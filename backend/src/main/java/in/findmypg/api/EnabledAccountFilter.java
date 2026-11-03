package in.findmypg.api;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

class EnabledAccountFilter extends OncePerRequestFilter {
    private final AccountRepository accounts;
    EnabledAccountFilter(AccountRepository accounts){this.accounts=accounts;}
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException{
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth!=null&&auth.getPrincipal() instanceof Jwt jwt){Account account=accounts.findById(Long.valueOf(jwt.getSubject())).orElse(null);if(account==null||!account.enabled){SecurityContextHolder.clearContext();response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);response.setContentType("application/json");response.getWriter().write("{\"message\":\"This account is no longer active.\"}");return;}}
        chain.doFilter(request,response);
    }
}
