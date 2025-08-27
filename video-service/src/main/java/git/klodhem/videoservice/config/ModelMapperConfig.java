package git.klodhem.videoservice.config;

import git.klodhem.common.dto.model.AnswerDTO;
import git.klodhem.videoservice.model.Answer;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.typeMap(Answer.class, AnswerDTO.class)
                .addMappings(m -> {
                    m.map(Answer::getAnswerId, AnswerDTO::setAnswerId);
                    m.map(Answer::getText, AnswerDTO::setText);
                    m.map(Answer::isCorrect, AnswerDTO::setCorrect);
                });

        return mapper;
    }
}
