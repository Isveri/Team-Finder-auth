package com.evi.teamfinderauth.security.jwt;


import com.evi.teamfinderauth.domain.User;
import com.evi.teamfinderauth.repository.UserRepository;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import static org.apache.logging.log4j.util.Strings.isEmpty;

@RequiredArgsConstructor
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain)
            throws ServletException, IOException {

        boolean isTokenParam = false;
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isEmpty(header) || !header.startsWith("Bearer ")) {
            isTokenParam = true;
            if(request.getParameter("token")==null){
                chain.doFilter(request, response);
                return;
            }
        }
        String token = isTokenParam ? request.getParameter("token") : header.split(" ")[1].trim();

        if (!jwtTokenUtil.validate(token)) {
            chain.doFilter(request, response);
            return;
        }


        User user = userRepository.findByUsername(jwtTokenUtil.getUsername(token))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setToken(token);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}