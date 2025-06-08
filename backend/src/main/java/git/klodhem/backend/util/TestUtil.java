package git.klodhem.backend.util;

import git.klodhem.backend.model.Answer;
import git.klodhem.backend.model.Question;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TestUtil {

    public List<Question> parseTextToQuestions(String text) {
        List<Question> questions = new ArrayList<>();
        String[] questionTexts = text.substring(6).split("Type: ");
        for (String questionText : questionTexts) {
            Question question = new Question();
            int countCorrectAnswers = 0;
            String[] lines = questionText.split("\n");
            if (lines[0].equals("multiple"))
                question.setType(TypeTest.MULTIPLE);
            else question.setType(TypeTest.SINGLE);

            question.setText(lines[1].substring(10));

            String[] answers = lines[2].substring(10, lines[2].length() - 2).split("\\|");
            List<Answer> answerList = new ArrayList<>();
            for (String answer : answers) {
                Answer answerObject = new Answer();
                int ind = answer.lastIndexOf("·");
                answerObject.setText(answer.substring(0, ind));
                if (answer.substring(ind + 1).equals("true")) {
                    answerObject.setCorrect(true);
                    countCorrectAnswers++;
                }

                answerList.add(answerObject);
            }
            Collections.shuffle(answerList);
            question.setCountCorrectAnswers(countCorrectAnswers);
            question.setAnswers(answerList);
            questions.add(question);
        }
        return questions;
    }
}
