package teammates.logic.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.api.FeedbackQuestionsDb;

/**
 * Handles operations related to feedback questions.
 *
 * @see FeedbackQuestionAttributes
 * @see FeedbackQuestionsDb
 */
public final class FeedbackQuestionsLogic {

    private static final Logger log = Logger.getLogger();

    private static FeedbackQuestionsLogic instance = new FeedbackQuestionsLogic();

    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    private FeedbackQuestionsLogic() {
        // prevent initialization
    }

    public static FeedbackQuestionsLogic inst() {
        return instance;
    }

    public void createFeedbackQuestion(FeedbackQuestionAttributes fqa)
            throws InvalidParametersException {

        String feedbackSessionName = fqa.feedbackSessionName;
        String courseId = fqa.courseId;
        List<FeedbackQuestionAttributes> questions = null;

        try {
            questions = getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        } catch (EntityDoesNotExistException e) {
            Assumption.fail("Session disappeared.");
        }
        if (fqa.questionNumber < 0) {
            fqa.questionNumber = questions.size() + 1;
        }
        adjustQuestionNumbers(questions.size() + 1, fqa.questionNumber, questions);
        createFeedbackQuestionNoIntegrityCheck(fqa, fqa.questionNumber);
    }

    /**
     * Used for creating initial questions only.
     * Does not check if feedback session exists.
     * Does not check if question number supplied is valid(does not check for clashes, or make adjustments)
     */
    public FeedbackQuestionAttributes createFeedbackQuestionNoIntegrityCheck(
            FeedbackQuestionAttributes fqa, int questionNumber) throws InvalidParametersException {
        fqa.questionNumber = questionNumber;
        fqa.removeIrrelevantVisibilityOptions();
        return fqDb.createFeedbackQuestionWithoutExistenceCheck(fqa);
    }

    public FeedbackQuestionAttributes copyFeedbackQuestion(
            String feedbackQuestionId, String feedbackSessionName, String courseId, String instructorEmail)
            throws InvalidParametersException {

        FeedbackQuestionAttributes question = getFeedbackQuestion(feedbackQuestionId);
        question.feedbackSessionName = feedbackSessionName;
        question.courseId = courseId;
        question.creatorEmail = instructorEmail;
        question.questionNumber = -1;
        question.setId(null);

        createFeedbackQuestion(question);

        return question;
    }

    /**
     * Gets a single question corresponding to the given parameters. <br><br>
     * <b>Note:</b><br>
     * *    This method should only be used if the question already exists in the<br>
     * datastore and has an ID already generated.
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(String feedbackQuestionId) {
        return fqDb.getFeedbackQuestion(feedbackQuestionId);
    }

    /**
     * Gets a single question corresponding to the given parameters.
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(
            String feedbackSessionName,
            String courseId,
            int questionNumber) {
        return fqDb.getFeedbackQuestion(feedbackSessionName,
                courseId, questionNumber);
    }

    /**
     * Gets a {@link List} of every FeedbackQuestion in the given session.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForSession(
            String feedbackSessionName, String courseId) throws EntityDoesNotExistException {

        if (fsLogic.getFeedbackSession(feedbackSessionName, courseId) == null) {
            throw new EntityDoesNotExistException(
                    "Trying to get questions for a feedback session that does not exist.");
        }
        List<FeedbackQuestionAttributes> questions =
                fqDb.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        questions.sort(null);

        if (questions.size() > 1 && !areQuestionNumbersConsistent(questions)) {
            log.severe(courseId + ": " + feedbackSessionName + " has invalid question numbers");
        }

        return questions;
    }

    // TODO can be removed once we are sure that question numbers will be consistent
    private boolean areQuestionNumbersConsistent(List<FeedbackQuestionAttributes> questions) {
        Set<Integer> questionNumbersInSession = new HashSet<>();
        for (FeedbackQuestionAttributes question : questions) {
            if (!questionNumbersInSession.add(question.questionNumber)) {
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
     *  Gets a {@link List} of every FeedbackQuestion that the instructor can copy.
     */
    public List<FeedbackQuestionAttributes> getCopiableFeedbackQuestionsForInstructor(String googleId)
            throws EntityDoesNotExistException {

        List<FeedbackQuestionAttributes> copiableQuestions = new ArrayList<>();
        List<CourseAttributes> courses = coursesLogic.getCoursesForInstructor(googleId);
        for (CourseAttributes course : courses) {
            List<FeedbackSessionAttributes> sessions = fsLogic.getFeedbackSessionsForCourse(course.getId());
            for (FeedbackSessionAttributes session : sessions) {
                List<FeedbackQuestionAttributes> questions =
                        getFeedbackQuestionsForSession(session.getFeedbackSessionName(), course.getId());
                copiableQuestions.addAll(questions);
            }
        }

        copiableQuestions.sort(Comparator.comparing((FeedbackQuestionAttributes question) -> question.courseId)
                .thenComparing(question -> question.feedbackSessionName)
                .thenComparing(question -> question.getQuestionDetails().getQuestionTypeDisplayName())
                .thenComparing(question -> question.getQuestionDetails().getQuestionText()));

        return copiableQuestions;
    }

    /**
     * Gets a {@code List} of all questions for the given session for an
     * instructor to view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForInstructor(
            String feedbackSessionName, String courseId, String userEmail)
            throws EntityDoesNotExistException {

        if (fsLogic.getFeedbackSession(feedbackSessionName, courseId) == null) {
            throw new EntityDoesNotExistException(
                    "Trying to get questions for a feedback session that does not exist.");
        }

        if (fsLogic.isCreatorOfSession(feedbackSessionName, courseId, userEmail)) {
            return getFeedbackQuestionsForCreatorInstructor(feedbackSessionName, courseId);
        }

        List<FeedbackQuestionAttributes> questions = new ArrayList<>();

        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, userEmail);
        boolean isInstructor = instructor != null;

        if (isInstructor) {
            questions.addAll(fqDb.getFeedbackQuestionsForGiverType(
                            feedbackSessionName, courseId, FeedbackParticipantType.INSTRUCTORS));
        }
        questions.sort(null);
        return questions;
    }

    /**
     * Gets a {@code List} of all questions for the list of questions that an
     * instructor who is the creator of the course can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForCreatorInstructor(
                                    String feedbackSessionName, String courseId)
                    throws EntityDoesNotExistException {

        FeedbackSessionAttributes fsa = fsLogic.getFeedbackSession(feedbackSessionName, courseId);
        if (fsa == null) {
            throw new EntityDoesNotExistException(
                    "Trying to get questions for a feedback session that does not exist.");
        }

        return getFeedbackQuestionsForCreatorInstructor(fsa);
    }

    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForCreatorInstructor(
                                    FeedbackSessionAttributes fsa) {

        List<FeedbackQuestionAttributes> questions = new ArrayList<>();

        String feedbackSessionName = fsa.getFeedbackSessionName();
        String courseId = fsa.getCourseId();

        questions.addAll(fqDb.getFeedbackQuestionsForGiverType(
                                       feedbackSessionName, courseId, FeedbackParticipantType.INSTRUCTORS));

        // Return all self (creator) questions
        questions.addAll(fqDb.getFeedbackQuestionsForGiverType(feedbackSessionName,
                courseId, FeedbackParticipantType.SELF));

        questions.sort(null);
        return questions;
    }

    /**
     * Filters through the given list of questions and returns a {@code List} of
     * questions that an instructor can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForInstructor(
            List<FeedbackQuestionAttributes> allQuestions, boolean isCreator) {

        List<FeedbackQuestionAttributes> questions = new ArrayList<>();

        for (FeedbackQuestionAttributes question : allQuestions) {
            if (question.giverType == FeedbackParticipantType.INSTRUCTORS
                    || question.giverType == FeedbackParticipantType.SELF && isCreator) {
                questions.add(question);
            }
        }

        return questions;
    }

    /**
     * Gets a {@code List} of all questions for the given session that
     * students can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForStudents(
            String feedbackSessionName, String courseId) {

        List<FeedbackQuestionAttributes> questions = new ArrayList<>();

        questions.addAll(
                fqDb.getFeedbackQuestionsForGiverType(
                        feedbackSessionName, courseId, FeedbackParticipantType.STUDENTS));
        questions.addAll(
                fqDb.getFeedbackQuestionsForGiverType(
                        feedbackSessionName, courseId, FeedbackParticipantType.TEAMS));

        questions.sort(null);
        return questions;
    }

    /**
     * Filters through the given list of questions and returns a {@code List} of
     * questions that students can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForStudents(
            List<FeedbackQuestionAttributes> allQuestions) {

        List<FeedbackQuestionAttributes> questions = new ArrayList<>();

        for (FeedbackQuestionAttributes question : allQuestions) {
            if (question.giverType == FeedbackParticipantType.STUDENTS
                    || question.giverType == FeedbackParticipantType.TEAMS) {
                questions.add(question);
            }
        }

        return questions;
    }

    public Map<String, String> getRecipientsForQuestion(FeedbackQuestionAttributes question, String giver)
            throws EntityDoesNotExistException {

        InstructorAttributes instructorGiver = instructorsLogic.getInstructorForEmail(question.courseId, giver);
        StudentAttributes studentGiver = studentsLogic.getStudentForEmail(question.courseId, giver);

        return getRecipientsForQuestion(question, giver, instructorGiver, studentGiver);
    }

    public Map<String, String> getRecipientsForQuestion(
            FeedbackQuestionAttributes question, String giver,
            InstructorAttributes instructorGiver, StudentAttributes studentGiver)
                    throws EntityDoesNotExistException {

        Map<String, String> recipients = new HashMap<>();

        FeedbackParticipantType recipientType = question.recipientType;

        String giverTeam = getGiverTeam(giver, instructorGiver, studentGiver);

        switch (recipientType) {
        case SELF:
            if (question.giverType == FeedbackParticipantType.TEAMS) {
                recipients.put(studentGiver.team, studentGiver.team);
            } else {
                recipients.put(giver, Const.USER_NAME_FOR_SELF);
            }
            break;
        case STUDENTS:
            List<StudentAttributes> studentsInCourse = studentsLogic.getStudentsForCourse(question.courseId);
            for (StudentAttributes student : studentsInCourse) {
                // Ensure student does not evaluate himself
                if (!giver.equals(student.email)) {
                    recipients.put(student.email, student.name);
                }
            }
            break;
        case INSTRUCTORS:
            List<InstructorAttributes> instructorsInCourse = instructorsLogic.getInstructorsForCourse(question.courseId);
            for (InstructorAttributes instr : instructorsInCourse) {
                // Ensure instructor does not evaluate himself
                if (!giver.equals(instr.email)) {
                    recipients.put(instr.email, instr.name);
                }
            }
            break;
        case TEAMS:
            List<TeamDetailsBundle> teams = coursesLogic.getTeamsForCourse(question.courseId);
            for (TeamDetailsBundle team : teams) {
                // Ensure student('s team) does not evaluate own team.
                if (!giverTeam.equals(team.name)) {
                    // recipientEmail doubles as team name in this case.
                    recipients.put(team.name, team.name);
                }
            }
            break;
        case OWN_TEAM:
            recipients.put(giverTeam, giverTeam);
            break;
        case OWN_TEAM_MEMBERS:
            List<StudentAttributes> students = studentsLogic.getStudentsForTeam(giverTeam, question.courseId);
            for (StudentAttributes student : students) {
                if (!student.email.equals(giver)) {
                    recipients.put(student.email, student.name);
                }
            }
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            List<StudentAttributes> teamMembers = studentsLogic.getStudentsForTeam(giverTeam, question.courseId);
            for (StudentAttributes student : teamMembers) {
                // accepts self feedback too
                recipients.put(student.email, student.name);
            }
            break;
        case NONE:
            recipients.put(Const.GENERAL_QUESTION, Const.GENERAL_QUESTION);
            break;
        default:
            break;
        }
        return recipients;
    }

    private String getGiverTeam(String defaultTeam, InstructorAttributes instructorGiver,
            StudentAttributes studentGiver) {
        String giverTeam = defaultTeam;
        boolean isStudentGiver = studentGiver != null;
        boolean isInstructorGiver = instructorGiver != null;
        if (isStudentGiver) {
            giverTeam = studentGiver.team;
        } else if (isInstructorGiver) {
            giverTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
        }
        return giverTeam;
    }

    public boolean areThereResponsesForQuestion(String feedbackQuestionId) {
        return !frLogic.getFeedbackResponsesForQuestionWithinRange(feedbackQuestionId, 1)
                       .isEmpty();
    }

    public boolean isQuestionFullyAnsweredByUser(FeedbackQuestionAttributes question, String email)
            throws EntityDoesNotExistException {

        int numberOfResponsesGiven =
                frLogic.getFeedbackResponsesFromGiverForQuestion(question.getId(), email).size();
        int numberOfResponsesNeeded =
                question.numberOfEntitiesToGiveFeedbackTo;

        if (numberOfResponsesNeeded == Const.MAX_POSSIBLE_RECIPIENTS) {
            numberOfResponsesNeeded = getRecipientsForQuestion(question, email).size();
        }

        return numberOfResponsesGiven >= numberOfResponsesNeeded;
    }

    /**
     * Updates the feedback question number, shifts other questions up/down
     * depending on the change.
     */
    public void updateFeedbackQuestionNumber(FeedbackQuestionAttributes newQuestion)
            throws InvalidParametersException, EntityDoesNotExistException {

        FeedbackQuestionAttributes oldQuestion =
                fqDb.getFeedbackQuestion(newQuestion.getId());

        if (oldQuestion == null) {
            throw new EntityDoesNotExistException("Trying to update a feedback question that does not exist.");
        }

        int oldQuestionNumber = oldQuestion.questionNumber;
        int newQuestionNumber = newQuestion.questionNumber;
        String feedbackSessionName = oldQuestion.feedbackSessionName;
        String courseId = oldQuestion.courseId;
        List<FeedbackQuestionAttributes> questions = null;

        try {
            questions = getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        } catch (EntityDoesNotExistException e) {
            Assumption.fail("Session disappeared.");
        }

        adjustQuestionNumbers(oldQuestionNumber, newQuestionNumber, questions);
        updateFeedbackQuestion(newQuestion);
    }

    /**
     * Adjust questions between the old and new number,
     * if the new number is smaller, then shift up (increase qn#) all questions in between.
     * if the new number is bigger, then shift down(decrease qn#) all questions in between.
     */
    private void adjustQuestionNumbers(int oldQuestionNumber,
            int newQuestionNumber, List<FeedbackQuestionAttributes> questions) {
        if (oldQuestionNumber > newQuestionNumber && oldQuestionNumber >= 1) {
            for (int i = oldQuestionNumber - 1; i >= newQuestionNumber; i--) {
                FeedbackQuestionAttributes question = questions.get(i - 1);
                question.questionNumber += 1;
                updateFeedbackQuestionWithoutResponseRateUpdate(question);
            }
        } else if (oldQuestionNumber < newQuestionNumber && oldQuestionNumber < questions.size()) {
            for (int i = oldQuestionNumber + 1; i <= newQuestionNumber; i++) {
                FeedbackQuestionAttributes question = questions.get(i - 1);
                question.questionNumber -= 1;
                updateFeedbackQuestionWithoutResponseRateUpdate(question);
            }
        }
    }

    /**
     * Updates the feedback question. For each attribute in
     * {@code newAttributes}, the existing value is preserved if the attribute
     * is null (due to 'keep existing' policy). Existing responses for the
     * question are automatically deleted if giverType/recipientType are
     * changed, or if the response visibility is increased. However, the
     * response rate of the feedback session is not updated.<br>
     * Precondition: <br>
     * {@code newAttributes} is not {@code null}
     */
    private void updateFeedbackQuestionWithoutResponseRateUpdate(FeedbackQuestionAttributes newAttributes) {
        try {
            updateFeedbackQuestion(newAttributes, false);
        } catch (InvalidParametersException e) {
            Assumption.fail("Invalid question.");
        } catch (EntityDoesNotExistException e) {
            Assumption.fail("Question disappeared.");
        }
    }

    /**
     * Updates the feedback question. For each attribute in
     * {@code newAttributes}, the existing value is preserved if the attribute
     * is null (due to 'keep existing' policy). Existing responses for the
     * question are automatically deleted and the response rate of the feedback
     * session is updated if giverType/recipientType are changed, or if the
     * response visibility is increased.<br>
     * Precondition: <br>
     * {@code newAttributes} is not {@code null}
     */
    public void updateFeedbackQuestion(FeedbackQuestionAttributes newAttributes)
            throws InvalidParametersException, EntityDoesNotExistException {

        updateFeedbackQuestion(newAttributes, true);
    }

    private void updateFeedbackQuestion(FeedbackQuestionAttributes newAttributes, boolean hasResponseRateUpdate)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackQuestionAttributes oldQuestion = null;
        if (newAttributes.getId() == null) {
            oldQuestion = fqDb.getFeedbackQuestion(newAttributes.feedbackSessionName,
                    newAttributes.courseId, newAttributes.questionNumber);
        } else {
            oldQuestion = fqDb.getFeedbackQuestion(newAttributes.getId());
        }

        if (oldQuestion == null) {
            throw new EntityDoesNotExistException(
                    "Trying to update a feedback question that does not exist.");
        }

        if (oldQuestion.areResponseDeletionsRequiredForChanges(newAttributes)) {
            frLogic.deleteFeedbackResponsesForQuestionAndCascade(oldQuestion.getId(), hasResponseRateUpdate);
        }

        oldQuestion.updateValues(newAttributes);
        newAttributes.removeIrrelevantVisibilityOptions();
        fqDb.updateFeedbackQuestion(newAttributes);
    }

    public void deleteFeedbackQuestionsForSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {
        List<FeedbackQuestionAttributes> questions =
                getFeedbackQuestionsForSession(feedbackSessionName, courseId);

        for (FeedbackQuestionAttributes question : questions) {
            deleteFeedbackQuestionCascadeWithoutResponseRateUpdate(question.getId());
        }

    }

    /**
     * Deletes a question by its auto-generated ID. <br>
     * Cascade the deletion of all existing responses for the question and then
     * shifts larger question numbers down by one to preserve number order. The
     * response rate of the feedback session is not updated.
     *
     * <p>Silently fails if question does not exist.
     */
    private void deleteFeedbackQuestionCascadeWithoutResponseRateUpdate(String feedbackQuestionId) {
        FeedbackQuestionAttributes questionToDeleteById =
                        getFeedbackQuestion(feedbackQuestionId);

        if (questionToDeleteById == null) {
            log.warning("Trying to delete question that does not exist: " + feedbackQuestionId);
        } else {
            deleteFeedbackQuestionCascade(questionToDeleteById.feedbackSessionName,
                                            questionToDeleteById.courseId,
                                            questionToDeleteById.questionNumber, false);
        }
    }

    /**
     * Deletes a question by its auto-generated ID. <br>
     * Cascade the deletion of all existing responses for the question and then
     * shifts larger question numbers down by one to preserve number order. The
     * response rate of the feedback session is updated accordingly.
     *
     * <p>Silently fail if question does not exist.
     */
    public void deleteFeedbackQuestionCascade(String feedbackQuestionId) {
        FeedbackQuestionAttributes questionToDeleteById =
                        getFeedbackQuestion(feedbackQuestionId);

        if (questionToDeleteById == null) {
            log.warning("Trying to delete question that does not exist: " + feedbackQuestionId);
        } else {
            deleteFeedbackQuestionCascade(questionToDeleteById.feedbackSessionName,
                                            questionToDeleteById.courseId,
                                            questionToDeleteById.questionNumber, true);
        }
    }

    /**
     * Deletes all feedback questions in all sessions of the course specified. This is
     * a non-cascade delete. The responses to the questions and the comments of these responses
     * should be handled.
     *
     */
    public void deleteFeedbackQuestionsForCourse(String courseId) {
        fqDb.deleteFeedbackQuestionsForCourse(courseId);
    }

    /**
     * Deletes a question.<br> Question is identified by it's question number, and
     * the feedback session name and course ID of the question.<br>
     * Can be used when the question ID is unknown. <br>
     * Cascade the deletion of all existing responses for the question and then
     * shifts larger question numbers down by one to preserve number order.
     */
    private void deleteFeedbackQuestionCascade(
            String feedbackSessionName, String courseId, int questionNumber, boolean hasResponseRateUpdate) {

        FeedbackQuestionAttributes questionToDelete =
                getFeedbackQuestion(feedbackSessionName, courseId, questionNumber);

        if (questionToDelete == null) {
            return; // Silently fail if question does not exist.
        }
        // Cascade delete responses for question.
        frLogic.deleteFeedbackResponsesForQuestionAndCascade(questionToDelete.getId(), hasResponseRateUpdate);

        List<FeedbackQuestionAttributes> questionsToShiftQnNumber = null;
        try {
            questionsToShiftQnNumber = getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        } catch (EntityDoesNotExistException e) {
            Assumption.fail("Session disappeared.");
        }

        fqDb.deleteEntity(questionToDelete);

        if (questionToDelete.questionNumber < questionsToShiftQnNumber.size()) {
            shiftQuestionNumbersDown(questionToDelete.questionNumber, questionsToShiftQnNumber);
        }
    }

    // Shifts all question numbers after questionNumberToShiftFrom down by one.
    private void shiftQuestionNumbersDown(int questionNumberToShiftFrom,
            List<FeedbackQuestionAttributes> questionsToShift) {
        for (FeedbackQuestionAttributes question : questionsToShift) {
            if (question.questionNumber > questionNumberToShiftFrom) {
                question.questionNumber -= 1;
                updateFeedbackQuestionWithoutResponseRateUpdate(question);
            }
        }
    }

    /*
     * Removes questions with no recipients.
     */
    public List<FeedbackQuestionAttributes> getQuestionsWithRecipients(
            List<FeedbackQuestionAttributes> questions, String giver)
            throws EntityDoesNotExistException {
        List<FeedbackQuestionAttributes> questionsWithRecipients = new ArrayList<>();
        for (FeedbackQuestionAttributes question : questions) {
            int numRecipients = question.numberOfEntitiesToGiveFeedbackTo;
            if (numRecipients == Const.MAX_POSSIBLE_RECIPIENTS) {
                numRecipients = this.getRecipientsForQuestion(question, giver)
                        .size();
            }
            if (numRecipients > 0) {
                questionsWithRecipients.add(question);
            }
        }
        return questionsWithRecipients;
    }

}
