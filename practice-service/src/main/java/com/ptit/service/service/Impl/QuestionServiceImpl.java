package com.ptit.service.service.Impl;

import com.ptit.service.dto.QuestionDTO;
import com.ptit.service.entity.MultipleChoiceOption;
import com.ptit.service.entity.Question;
import com.ptit.service.entity.enums.QuestionType;
import com.ptit.service.repository.QuestionRepository;
import com.ptit.service.response.QuestionResponse;
import com.ptit.service.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Override
    public Page<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @Override
    public Question updateQuestion(Question question) {
        Question existingQuestion = findById(question.getId());
        existingQuestion.setContent(question.getContent());
        existingQuestion.setType(question.getType());
        existingQuestion.setOptions(question.getOptions());
        existingQuestion.setScore(question.getScore());
        return questionRepository.save(existingQuestion);
    }

    @Override
    public void delete(Long id) {
        Question existingQuestion = findById(id);
        questionRepository.delete(existingQuestion);
    }

    @Override
    public Question findById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    @Override
    @Transactional
    public Question createMultipleChoiceQuestion(QuestionDTO dto) {
        Question question = new Question();
        question.setType(QuestionType.MULTIPLE_CHOICE);
        question.setContent(dto.getContent());

        List<MultipleChoiceOption> options = dto.getOptions().stream()
                .map(optionDTO -> {
                    MultipleChoiceOption option = new MultipleChoiceOption();
                    option.setQuestion(question);
                    option.setOption(optionDTO.getOption());
                    option.setContent(optionDTO.getContent());
                    option.setCorrect(optionDTO.isCorrect());
                    return option;
                })
                .collect(Collectors.toList());

        question.setOptions(options);
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public Question createEssayQuestion(QuestionDTO dto) {
        Question question = new Question();
        question.setType(QuestionType.ESSAY);
        question.setContent(dto.getContent());
        return questionRepository.save(question);
    }

    @Override
    @Transactional
    public int importQuestionsFromExcel(MultipartFile file) throws IOException {
        List<Question> questions = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            // Process Multiple Choice Questions
            Sheet mcSheet = workbook.getSheetAt(0);
            for (int i = 1; i <= mcSheet.getLastRowNum(); i++) {
                Row row = mcSheet.getRow(i);
                if (row == null) continue;

                Question question = new Question();
                question.setType(QuestionType.MULTIPLE_CHOICE);
                question.setContent(getCellValueAsString(row.getCell(1)));

                // Set options
                List<MultipleChoiceOption> options = new ArrayList<>();
                for (int j = 2; j <= 5; j++) {
                    MultipleChoiceOption option = new MultipleChoiceOption();
                    option.setQuestion(question);
                    option.setOption(String.valueOf((char) ('A' + (j - 2)))); // A, B, C, D
                    option.setContent(getCellValueAsString(row.getCell(j)));
                    option.setCorrect(getCellValueAsString(row.getCell(6)).equals(String.valueOf((char) ('A' + (j - 2)))));
                    options.add(option);
                }
                question.setOptions(options);

                // Set score
                question.setScore(getCellValueAsDouble(row.getCell(7)));

                questions.add(question);
            }

            // Process Essay Questions
            Sheet essaySheet = workbook.getSheetAt(1);
            for (int i = 1; i <= essaySheet.getLastRowNum(); i++) {
                Row row = essaySheet.getRow(i);
                if (row == null) continue;

                Question question = new Question();
                question.setType(QuestionType.ESSAY);
                question.setContent(getCellValueAsString(row.getCell(1)));
                question.setScore(getCellValueAsDouble(row.getCell(3)));

                questions.add(question);
            }
        }

        // Save all questions
        questionRepository.saveAll(questions);
        return questions.size();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return "";
        }
    }

    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return 0.0;
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                return Double.parseDouble(cell.getStringCellValue());
            default:
                return 0.0;
        }
    }
} 