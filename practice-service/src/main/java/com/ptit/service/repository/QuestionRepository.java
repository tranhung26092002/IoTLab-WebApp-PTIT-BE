package com.ptit.service.repository;

import com.ptit.service.entity.Question;
import com.ptit.service.entity.enums.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByType(QuestionType type);

    @Query("SELECT q FROM Question q WHERE q.type = :type ORDER BY RANDOM() LIMIT :limit")
    List<Question> findRandomQuestionsByType(@Param("type") QuestionType type, @Param("limit") int limit);
}