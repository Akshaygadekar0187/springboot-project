package com.URLROUTER.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RedirectService {

	private final Map<String, String> urlMap = new HashMap<>();

	public RedirectService(@Value("${app.redirect.google}") String google,
			@Value("${app.redirect.github}") String github,
			@Value("${app.redirect.stackoverflow}") String stackoverflow) {
		urlMap.put("google", google);
		urlMap.put("github", github);
		urlMap.put("stackoverflow", stackoverflow);
	}

	public String getUrl(String key) {
		return urlMap.getOrDefault(key, "https://example.com");
	}
}
