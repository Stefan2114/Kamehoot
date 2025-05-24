package com.kamehoot.kamehoot_backend.controllers;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kamehoot.kamehoot_backend.services.IQuestionService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;

@RestController
public class HomeController {

    private final IQuestionService questionService;

    public HomeController(IQuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public String home(Principal principal) {
        return "Welcome " + principal.getName();
    }

    @PreAuthorize("hasAuthority('SCOPE_read')")
    @GetMapping("/secure")
    public String secure(Principal principal) {
        return "Secure endpoint";
    }

    @GetMapping("/intro-video")
    public ResponseEntity<Resource> getIntroVideo() {
        FileSystemResource video = this.questionService.getIntroVideo();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=video.mp4")
                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(video);
    }

    @GetMapping("/protocol")
    public String getProtocol(HttpServletRequest request) {
        return "Protocol: " + request.getProtocol();
    }
}
