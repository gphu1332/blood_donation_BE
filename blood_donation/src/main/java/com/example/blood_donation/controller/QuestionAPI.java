package com.example.blood_donation.controller;

import com.example.blood_donation.dto.QuestionWithOptionsDTO;
import com.example.blood_donation.entity.Option;
import com.example.blood_donation.entity.Question;
import com.example.blood_donation.service.QuestionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')")
public class QuestionAPI {

    @Autowired
    private QuestionService questionService;

    // CRUD Question
    @GetMapping
    public List<Question> getAllQuestions() { return questionService.getAllQuestions(); }

    @GetMapping("/{id}")
    public Question getQuestion(@PathVariable Long id) { return questionService.getById(id); }

    @PostMapping
    public Question createQuestion(@RequestBody Question question) { return questionService.create(question); }

    @PutMapping("/{id}")
    public Question updateQuestion(@PathVariable Long id, @RequestBody Question updated) {
        return questionService.update(id, updated);
    }

    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable Long id) { questionService.delete(id); }

    // CRUD Option
    // Lấy option của 1 câu hỏi
    @GetMapping("/{questionId}/options")
    public List<Option> getOptions(@PathVariable Long questionId) {
        return questionService.getOptions(questionId);
    }

    // Thêm option cho 1 câu hỏi
    @PostMapping("/{questionId}/options")
    public Option createOption(@PathVariable Long questionId, @RequestBody Option option) {
        return questionService.createOption(questionId, option);
    }

    // Sửa option
    @PutMapping("/options/{optionId}")
    public Option updateOption(@PathVariable Long optionId, @RequestBody Option updated) {
        return questionService.updateOption(optionId, updated);
    }

    // Xóa option
    @DeleteMapping("/options/{optionId}")
    public void deleteOption(@PathVariable Long optionId) {
        questionService.deleteOption(optionId);
    }

    @GetMapping("/with-options")
    public List<QuestionWithOptionsDTO> getAllQuestionsWithOptions() {
        List<Question> questions = questionService.getAllQuestions();
        return questions.stream().map(q -> {
            QuestionWithOptionsDTO dto = new QuestionWithOptionsDTO();
            dto.setId(q.getId());
            dto.setContent(q.getContent());
            dto.setType(q.getType());

            List<Option> options = questionService.getOptions(q.getId());
            List<QuestionWithOptionsDTO.OptionDto> optionDtos = options.stream().map(opt -> {
                QuestionWithOptionsDTO.OptionDto o = new QuestionWithOptionsDTO.OptionDto();
                o.setId(opt.getId());
                o.setLabel(opt.getLabel());
                o.setRequiresText(opt.getRequiresText());
                return o;
            }).toList();

            dto.setOptions(optionDtos);
            return dto;
        }).toList();
    }
}


