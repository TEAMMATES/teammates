package teammates.sqllogic.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;
import teammates.storage.sqlapi.FeedbackQuestionsDb;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * Handles operations related to feedback questions.
 *
 * @see FeedbackQuestion
 * @see FeedbackQuestionsDb
 */
public final class FeedbackQuestionsLogic {

    private static final Logger log = Logger.getLogger();

    private static final FeedbackQuestionsLogic instance = new FeedbackQuestionsLogic();
    private FeedbackQuestionsDb fqDb;

    private FeedbackQuestionsLogic() {
        // prevent initialization
    }

    public static FeedbackQuestionsLogic inst() {
        return instance;
    }

    void initLogicDependencies(FeedbackQuestionsDb fqDb) {
        this.fqDb = fqDb;
    }

    /**
     * Creates a new feedback question.
     *
     * @return the created question
     * @throws InvalidParametersException if the question is invalid
     */
    public FeedbackQuestion createFeedbackQuestion(FeedbackQuestion feedbackQuestion) throws InvalidParametersException {
        assert feedbackQuestion != null;

        if (!feedbackQuestion.isValid()) {
            throw new InvalidParametersException(feedbackQuestion.getInvalidityInfo());
        }

        List<FeedbackQuestion> questionsBefore = getFeedbackQuestionsForSession(feedbackQuestion.getFeedbackSession());

        FeedbackQuestion createdQuestion = fqDb.createFeedbackQuestion(feedbackQuestion);

        adjustQuestionNumbers(questionsBefore.size() + 1, createdQuestion.getQuestionNumber(), questionsBefore);
        return createdQuestion;
    }

    /**
     * Gets an feedback question by feedback question id.
     * @param id of feedback question.
     * @return the specified feedback question.
     */
    public FeedbackQuestion getFeedbackQuestion(UUID id) {
        return fqDb.getFeedbackQuestion(id);
    }

    /**
     * Gets a {@link List} of every FeedbackQuestion in the given session.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForSession(FeedbackSession feedbackSession) {

        List<FeedbackQuestion> questions = fqDb.getFeedbackQuestionsForSession(feedbackSession.getId());
        questions.sort(null);

        // check whether the question numbers are consistent
        if (questions.size() > 1 && !areQuestionNumbersConsistent(questions)) {
            log.severe(feedbackSession.getCourse().getId() + ": " + feedbackSession.getName()
                    + " has invalid question numbers");
        }

        return questions;
    }

    /**
     * Checks if there are any questions for the given session that instructors can view/submit.
     */
    public boolean hasFeedbackQuestionsForInstructors(List<FeedbackQuestion> fqs, boolean isCreator) {
        boolean hasQuestions = hasFeedbackQuestionsForGiverType(fqs, FeedbackParticipantType.INSTRUCTORS);
        if (hasQuestions) {
            return true;
        }

        if (isCreator) {
            hasQuestions = hasFeedbackQuestionsForGiverType(fqs, FeedbackParticipantType.SELF);
        }

        return hasQuestions;
    }

    /**
     * Checks if there are any questions for the given session that students can view/submit.
     */
    public boolean hasFeedbackQuestionsForStudents(List<FeedbackQuestion> fqs) {
        return hasFeedbackQuestionsForGiverType(fqs, FeedbackParticipantType.STUDENTS)
                || hasFeedbackQuestionsForGiverType(fqs, FeedbackParticipantType.TEAMS);
    }

    /**
     * Checks if there is any feedback questions in a session in a course for the given giver type.
     */
    public boolean hasFeedbackQuestionsForGiverType(
            List<FeedbackQuestion> feedbackQuestions, FeedbackParticipantType giverType) {
        assert feedbackQuestions != null;
        assert giverType != null;

        for (FeedbackQuestion fq : feedbackQuestions) {
            if (fq.getGiverType() == giverType) {
                return true;
            }
        }
        return false;
    }

    // TODO can be removed once we are sure that question numbers will be consistent
    private boolean areQuestionNumbersConsistent(List<FeedbackQuestion> questions) {
        Set<Integer> questionNumbersInSession = new HashSet<>();
        for (FeedbackQuestion question : questions) {
            if (!questionNumbersInSession.add(question.getQuestionNumber())) {
                return false;
            }
        }

        for (int i = 1; i <= questions.size(); i++) {
            if (!questionNumbersInSession.contains(i)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Adjust questions between the old and new number,
     * if the new number is smaller, then shift up (increase qn#) all questions in between.
     * if the new number is bigger, then shift down(decrease qn#) all questions in between.
     */
    private void adjustQuestionNumbers(int oldQuestionNumber, int newQuestionNumber, List<FeedbackQuestion> questions) {
        if (oldQuestionNumber > newQuestionNumber && oldQuestionNumber >= 1) {
            for (int i = oldQuestionNumber - 1; i >= newQuestionNumber; i--) {
                FeedbackQuestion question = questions.get(i - 1);
                question.setQuestionNumber(question.getQuestionNumber() + 1);
            }
        } else if (oldQuestionNumber < newQuestionNumber && oldQuestionNumber < questions.size()) {
            for (int i = oldQuestionNumber + 1; i <= newQuestionNumber; i++) {
                FeedbackQuestion question = questions.get(i - 1);
                question.setQuestionNumber(question.getQuestionNumber() - 1);
            }
        }
    }
}
