package com.URLROUTER.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.URLROUTER.entity.UrlMapping;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, Long> {

	Optional<UrlMapping> findByShortKey(String shortKey);
}
