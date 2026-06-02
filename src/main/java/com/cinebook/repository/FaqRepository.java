package com.cinebook.repository;

import com.cinebook.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByQuestionContainingIgnoreCaseOrAnswerContainingIgnoreCase(String question, String answer);

    List<Faq> findByTopicIgnoreCase(String topic);
}
