package com.estimate.infrastructure.security;

import com.estimate.domain.port.out.AuthenticationProviderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@Profile("jwt")
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
    
    private final AuthenticationProviderPort authenticationProvider;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String jwt = getJwtFromRequest(exchange.getRequest());
        
        if (StringUtils.hasText(jwt)) {
            return authenticationProvider.validateToken(jwt)
                    .filter(isValid -> isValid)
                    .flatMap(isValid -> authenticationProvider.extractUserInfo(jwt))
                    .flatMap(userInfo -> {
                        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + userInfo.getRole()));
                        
                        var authentication = new UsernamePasswordAuthenticationToken(
                                new UserPrincipal(userInfo.getUserId(), userInfo.getEmail(), userInfo.getRole()),
                                null,
                                authorities
                        );
                        
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                    })
                    .onErrorResume(e -> {
                        log.error("Could not set user authentication in security context", e);
                        return chain.filter(exchange);
                    });
        }
        
        return chain.filter(exchange);
    }
    
    private String getJwtFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
