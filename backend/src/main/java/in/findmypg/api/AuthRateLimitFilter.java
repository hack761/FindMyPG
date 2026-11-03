package in.findmypg.api;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
class AuthRateLimitFilter extends OncePerRequestFilter {
    private static final long WINDOW_MS=60_000; private static final int LIMIT=10;
    private final ConcurrentHashMap<String,Window> attempts=new ConcurrentHashMap<>();
    @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException{
        String path=request.getRequestURI();if(!path.equals("/api/auth/login")&&!path.equals("/api/auth/register")){chain.doFilter(request,response);return;}
        long now=Instant.now().toEpochMilli();String key=request.getRemoteAddr()+":"+path;Window window=attempts.compute(key,(k,current)->current==null||now-current.startedAt>=WINDOW_MS?new Window(now,1):new Window(current.startedAt,current.count+1));
        if(attempts.size()>20_000)attempts.entrySet().removeIf(e->now-e.getValue().startedAt>=WINDOW_MS);
        if(window.count>LIMIT){response.setStatus(429);response.setContentType("application/json");response.setHeader("Retry-After","60");response.getWriter().write("{\"message\":\"Too many attempts. Please wait a minute and try again.\"}");return;}
        chain.doFilter(request,response);
    }
    private record Window(long startedAt,int count){}
}
