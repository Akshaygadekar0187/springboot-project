package com.URLROUTER.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.URLROUTER.service.RedirectService;
import com.URLROUTER.service.UrlService;
import org.springframework.core.env.Environment;

@RestController
public class RedirectController {

	private final RedirectService redirectService;
	private final UrlService urlService;
	private final Environment env;

	public RedirectController(RedirectService redirectService, UrlService urlService, Environment env) {
		this.redirectService = redirectService;
		this.urlService = urlService;
		this.env = env;
	}

	// Static config redirect
	@GetMapping("/redirect")
	public ResponseEntity<?> redirectStatic(@RequestParam String key) {
		String url = redirectService.getUrl(key);

		return ResponseEntity.status(302).header("Location", url).build();
	}

	// Database redirect
	@GetMapping("/go/{key}")
	public ResponseEntity<?> redirectDynamic(@PathVariable String key) {
		System.out.println("Controller hit with key: " + key);
		String url = urlService.resolveUrl(key);

		return ResponseEntity.status(302).header("Location", url).build();
	}

	// Instance info
	@GetMapping("/instance")
	public String instance() {
		return "Running on port: " + env.getProperty("local.server.port");
	}
}
