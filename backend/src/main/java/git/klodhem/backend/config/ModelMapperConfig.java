package git.klodhem.backend.config;

import git.klodhem.backend.dto.AnswerDTO;
import git.klodhem.backend.model.Answer;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        Converter<Answer, Boolean> forceFalse = new Converter<Answer, Boolean>() {
            @Override
            public Boolean convert(MappingContext<Answer, Boolean> context) {
                return false;
            }
        };

        mapper.typeMap(Answer.class, AnswerDTO.class)
                .addMappings(m -> {
                    m.map(Answer::getAnswerId, AnswerDTO::setAnswerId);
                    m.map(Answer::getText,     AnswerDTO::setText);
                    m.using(forceFalse)
                            .map(Answer::isCorrect, AnswerDTO::setCorrect);
                });

        return mapper;
    }
}
