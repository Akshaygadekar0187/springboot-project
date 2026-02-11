package com.URLROUTER.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.URLROUTER.entity.UrlMapping;
import com.URLROUTER.repository.UrlRepository;

@Service
public class UrlService {

	private final UrlRepository repo;

	public UrlService(UrlRepository repo) {
		this.repo = repo;
	}

	public String resolveUrl(String key) {

		UrlMapping mapping = repo.findByShortKey(key).orElseThrow(() -> new RuntimeException("Key not found: " + key));

		mapping.setVisitCount(mapping.getVisitCount() + 1);

		repo.save(mapping); // ← THIS IS CRITICAL

		System.out.println("Visited: " + key + " count: " + mapping.getVisitCount());

		return mapping.getOriginalUrl();
	}

	public List<UrlMapping> getAll() {
		return repo.findAll();
	}

	public UrlMapping save(UrlMapping url) {
		return repo.save(url);
	}
	
	public void delete(Long id) {
	    repo.deleteById(id);
	}

}
