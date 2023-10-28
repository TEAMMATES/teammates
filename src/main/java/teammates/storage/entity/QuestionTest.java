package teammates.storage.entity;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
public class QuestionTest {
    public void testSetQuestionTextValid() {
        // test a string not exceeds max length
        String validText = "This is a valid question text.";
        Question question = new Question();
        question.setQuestionText(validText);
        // assertEquals(validText, question.getQuestionText());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetQuestionTextTooLong() {
        // test a string which exceeds the max length
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Question.MAX_QUESTION_TEXT_LENGTH + 1; i++) {
            sb.append("a");
        }
        String tooLongText = sb.toString();

        Question question = new Question();
        question.setQuestionText(tooLongText);
    }

    @Test
    public void testSetQuestionTextNull() {
        // test an empty string
        Question question = new Question();
        question.setQuestionText(null);
        // assertEquals(null, question.getQuestionText());
    }
}
