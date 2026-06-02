package com.cinebook.controller;

import com.cinebook.dto.request.SupportTicketRequest;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.dto.response.FaqResponse;
import com.cinebook.dto.response.SupportTicketResponse;
import com.cinebook.service.SupportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @GetMapping("/api/faqs")
    public ApiResponse<List<FaqResponse>> faqs() {
        return ApiResponse.success("FAQs fetched successfully", supportService.faqs());
    }

    @GetMapping("/api/faqs/search")
    public ApiResponse<List<FaqResponse>> searchFaqs(@RequestParam("q") String keyword) {
        return ApiResponse.success("FAQs searched successfully", supportService.searchFaqs(keyword));
    }

    @GetMapping("/api/help/topics")
    public ApiResponse<List<Map<String, Object>>> topics() {
        return ApiResponse.success("Help topics fetched successfully", supportService.topics());
    }

    @GetMapping("/api/help/topics/{topicId}")
    public ApiResponse<Map<String, Object>> topic(@PathVariable Long topicId) {
        return ApiResponse.success("Help topic fetched successfully", supportService.topic(topicId));
    }

    @PostMapping("/api/support/tickets")
    public ApiResponse<SupportTicketResponse> createTicket(@Valid @RequestBody SupportTicketRequest request) {
        return ApiResponse.success("Support ticket created successfully", supportService.createTicket(request));
    }

    @GetMapping("/api/support/tickets")
    public ApiResponse<List<SupportTicketResponse>> tickets() {
        return ApiResponse.success("Support tickets fetched successfully", supportService.tickets());
    }

    @GetMapping("/api/support/tickets/{ticketId}")
    public ApiResponse<SupportTicketResponse> ticket(@PathVariable Long ticketId) {
        return ApiResponse.success("Support ticket fetched successfully", supportService.ticket(ticketId));
    }

    @GetMapping("/api/support/contact")
    public ApiResponse<Map<String, String>> contact() {
        return ApiResponse.success("Support contact fetched successfully", supportService.contact());
    }
}
