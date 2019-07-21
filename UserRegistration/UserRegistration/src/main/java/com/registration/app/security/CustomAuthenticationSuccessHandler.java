package com.registration.app.security;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Authentication authentication) throws IOException, ServletException, RuntimeException {
		
		HttpSession session = httpServletRequest.getSession();
		User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		session.setAttribute("username", authUser.getUsername());
		
		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		authorities.forEach(authority -> {
			if (authority.getAuthority().equals("ADMIN")) {
				session.setAttribute("role", "ADMIN");
				try {
					httpServletResponse.sendRedirect("/admin/home");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else if (authority.getAuthority().equals("SUPER_ADMIN")) {
				session.setAttribute("role", "SUPER_ADMIN");
				try {
					httpServletResponse.sendRedirect("/superadmin/home");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

	}
}