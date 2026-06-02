package com.cinebook.service;

import com.cinebook.dto.request.SupportTicketRequest;
import com.cinebook.dto.response.FaqResponse;
import com.cinebook.dto.response.SupportTicketResponse;

import java.util.List;
import java.util.Map;

public interface SupportService {
    List<FaqResponse> faqs();

    List<FaqResponse> searchFaqs(String keyword);

    List<Map<String, Object>> topics();

    Map<String, Object> topic(Long topicId);

    SupportTicketResponse createTicket(SupportTicketRequest request);

    List<SupportTicketResponse> tickets();

    SupportTicketResponse ticket(Long ticketId);

    Map<String, String> contact();
}
