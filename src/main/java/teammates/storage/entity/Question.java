package teammates.storage.entity;

public class Question {

    public static int MAX_QUESTION_TEXT_LENGTH = 100;
    private String questionText;

    public void setQuestionText(String questionText) {
        if (questionText != null && questionText.length() > MAX_QUESTION_TEXT_LENGTH) {
            throw new IllegalArgumentException("Question text exceeds the maximum allowed length of " + MAX_QUESTION_TEXT_LENGTH + " characters.");
        }
        this.questionText = questionText;
    }


}
