package com.cinebook.dto.response;

import com.cinebook.entity.Faq;

public record FaqResponse(
        Long id,
        String topic,
        String question,
        String answer
) {
    public static FaqResponse from(Faq faq) {
        return new FaqResponse(faq.getId(), faq.getTopic(), faq.getQuestion(), faq.getAnswer());
    }
}
