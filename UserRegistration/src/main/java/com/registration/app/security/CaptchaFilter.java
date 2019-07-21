package com.registration.app.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CaptchaFilter implements Filter {
	
	private static final Logger logger = LoggerFactory.getLogger(CaptchaFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Filter.super.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest serRequest, ServletResponse serResponse, FilterChain chain)
			throws IOException, ServletException {
		logger.info("At the CaptchaFilter filter.");
		HttpServletRequest req = (HttpServletRequest) serRequest;
		HttpServletResponse res = (HttpServletResponse) serResponse;
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String captcha = req.getParameter("captcha");
		if(null!=email && null!=password && null!=captcha) {
			String captchaSession = (String)req.getSession().getAttribute("captcha");
			if(!captcha.equals(captchaSession)) {
				req.setAttribute("captcha", "Invalid Capthcha");
				res.sendRedirect("/login?error=true");  
			}else {
				chain.doFilter(serRequest, serResponse);
			}
		}else {
			chain.doFilter(serRequest, serResponse);
		}
	}
}

