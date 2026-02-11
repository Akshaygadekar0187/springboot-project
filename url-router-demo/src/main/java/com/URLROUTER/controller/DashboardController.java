package com.URLROUTER.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.URLROUTER.entity.UrlMapping;
import com.URLROUTER.service.UrlService;

@Controller
public class DashboardController {

	private final UrlService service;

	public DashboardController(UrlService service) {
		this.service = service;
	}

	@GetMapping("/")
	public String dashboard(Model model) {
		model.addAttribute("urls", service.getAll());
		model.addAttribute("newUrl", new UrlMapping());
		return "dashboard";
	}

	@PostMapping("/add")
	public String addUrl(@ModelAttribute UrlMapping url) {

		service.save(url);
		return "redirect:/";
	}
	
	@PostMapping("/delete/{id}")
	public String delete(@PathVariable Long id) {
	    service.delete(id);
	    return "redirect:/";
	}

	@PostMapping("/update")
	public String update(@ModelAttribute UrlMapping url) {
	    service.save(url);
	    return "redirect:/";
	}

}
