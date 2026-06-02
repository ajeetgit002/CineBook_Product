package com.cinebook.service.ServiceImpl;

import com.cinebook.dto.request.SupportTicketRequest;
import com.cinebook.dto.response.FaqResponse;
import com.cinebook.dto.response.SupportTicketResponse;
import com.cinebook.entity.SupportTicket;
import com.cinebook.entity.User;
import com.cinebook.enums.SupportStatus;
import com.cinebook.exceptions.ForbiddenException;
import com.cinebook.exceptions.ResourceNotFoundException;
import com.cinebook.repository.FaqRepository;
import com.cinebook.repository.SupportTicketRepository;
import com.cinebook.security.CurrentUserService;
import com.cinebook.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {

    private final FaqRepository faqRepository;
    private final SupportTicketRepository ticketRepository;
    private final CurrentUserService currentUserService;

    @Override
    public List<FaqResponse> faqs() {
        return faqRepository.findAll().stream()
                .map(FaqResponse::from)
                .toList();
    }

    @Override
    public List<FaqResponse> searchFaqs(String keyword) {
        String query = keyword == null ? "" : keyword;
        return faqRepository.findByQuestionContainingIgnoreCaseOrAnswerContainingIgnoreCase(query, query).stream()
                .map(FaqResponse::from)
                .toList();
    }

    @Override
    public List<Map<String, Object>> topics() {
        return List.of(
                Map.of("id", 1, "name", "Bookings"),
                Map.of("id", 2, "name", "Payments"),
                Map.of("id", 3, "name", "Refunds"),
                Map.of("id", 4, "name", "Account")
        );
    }

    @Override
    public Map<String, Object> topic(Long topicId) {
        return topics().stream()
                .filter(topic -> topic.get("id").equals(topicId.intValue()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Help topic not found"));
    }

    @Override
    public SupportTicketResponse createTicket(SupportTicketRequest request) {
        User user = currentUserService.getCurrentUser();
        SupportTicket ticket = SupportTicket.builder()
                .subject(request.subject())
                .message(request.message())
                .status(SupportStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        return SupportTicketResponse.from(ticketRepository.save(ticket));
    }

    @Override
    public List<SupportTicketResponse> tickets() {
        User user = currentUserService.getCurrentUser();
        return ticketRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(SupportTicketResponse::from)
                .toList();
    }

    @Override
    public SupportTicketResponse ticket(Long ticketId) {
        User user = currentUserService.getCurrentUser();
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found"));

        boolean admin = user.getRole() != null && "ROLE_ADMIN".equals(user.getRole().getName());
        if (!admin && !ticket.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Support ticket does not belong to current user");
        }

        return SupportTicketResponse.from(ticket);
    }

    @Override
    public Map<String, String> contact() {
        return Map.of(
                "email", "support@cinebook.com",
                "phone", "+91-1800-000-000",
                "hours", "24x7"
        );
    }
}
