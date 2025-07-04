package com.example.blood_donation.service;

import com.example.blood_donation.entity.Option;
import com.example.blood_donation.entity.Question;
import com.example.blood_donation.repositoty.OptionRepository;
import com.example.blood_donation.repositoty.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    public Question create(Question question) {
        return questionRepository.save(question);
    }

    public Question update(Long id, Question updated) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        q.setContent(updated.getContent());
        q.setType(updated.getType());
        return questionRepository.save(q);
    }

    public void delete(Long id) {
        questionRepository.deleteById(id);
    }

    public List<Option> getOptions(Long questionId) {
        return optionRepository.findByQuestionId(questionId);
    }

    public Option createOption(Long questionId, Option option) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        option.setQuestion(q);
        return optionRepository.save(option);
    }

    public Option updateOption(Long optionId, Option updated) {
        Option o = optionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Option not found"));
        o.setLabel(updated.getLabel());
        o.setRequiresText(updated.getRequiresText());
        return optionRepository.save(o);
    }

    public void deleteOption(Long optionId) {
        optionRepository.deleteById(optionId);
    }
}

