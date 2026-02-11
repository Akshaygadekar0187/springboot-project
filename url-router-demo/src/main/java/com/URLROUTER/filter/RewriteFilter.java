package com.URLROUTER.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RewriteFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		String uri = req.getRequestURI();

		if (uri.startsWith("/legacy/")) {

			String key = uri.substring(4);

			request.getRequestDispatcher("/redirect?key=" + key).forward(request, response);

			return;
		}

		chain.doFilter(request, response);
	}
}
