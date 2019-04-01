package teammates.logic.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.AttributesDeletionQuery;
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

    /**
     * Creates a new feedback question.
     *
     * @return the created question
     * @throws InvalidParametersException if the question is invalid
     */
    public FeedbackQuestionAttributes createFeedbackQuestion(FeedbackQuestionAttributes fqa)
            throws InvalidParametersException {

        List<FeedbackQuestionAttributes> questionsBefore =
                getFeedbackQuestionsForSession(fqa.getFeedbackSessionName(), fqa.getCourseId());

        FeedbackQuestionAttributes createdQuestion = fqDb.putEntity(fqa);

        adjustQuestionNumbers(questionsBefore.size() + 1, createdQuestion.getQuestionNumber(), questionsBefore);
        return createdQuestion;
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
            String feedbackSessionName, String courseId) {

        List<FeedbackQuestionAttributes> questions =
                fqDb.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        questions.sort(null);

        // check whether the question numbers are consistent
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
    public List<FeedbackQuestionAttributes> getCopiableFeedbackQuestionsForInstructor(String googleId) {

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

    /**
     * Gets the recipients of a feedback question for students.
     *
     * <p>Filter out some recipients based on the setting of the course.
     */
    public Map<String, String> getRecipientsOfQuestionForStudent(
            FeedbackQuestionAttributes question, String giverEmail, String giverTeam) {
        Map<String, String> recipients = getRecipientsOfQuestion(question, giverEmail, giverTeam);

        // remove hidden instructors
        if (question.getRecipientType() == FeedbackParticipantType.INSTRUCTORS) {
            List<InstructorAttributes> instructors = instructorsLogic.getInstructorsForCourse(question.getCourseId());
            Set<String> hiddenInstructorEmails = new HashSet<>();
            for (InstructorAttributes instructorAttributes : instructors) {
                if (!instructorAttributes.isDisplayedToStudents()) {
                    hiddenInstructorEmails.add(instructorAttributes.email);
                }
            }

            for (String instructorEmail : hiddenInstructorEmails) {
                recipients.remove(instructorEmail);
            }
        }

        return recipients;
    }

    /**
     * Gets the recipients of a feedback question for instructors.
     *
     * <p>Filter out some recipients based on the privileges of the instructor.
     */
    public Map<String, String> getRecipientsOfQuestionForInstructor(FeedbackQuestionAttributes question, String giverEmail) {
        Map<String, String> recipients = getRecipientsOfQuestion(question, giverEmail, Const.USER_TEAM_FOR_INSTRUCTOR);
        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(question.getCourseId(), giverEmail);

        // TODO the below will cause slow queries when recipients are large, find a better way

        // instructor can only see students in allowed sections for him/her
        if (question.getRecipientType().equals(FeedbackParticipantType.STUDENTS)) {
            recipients.entrySet().removeIf(studentEntry -> {
                StudentAttributes student = studentsLogic.getStudentForEmail(question.getCourseId(), studentEntry.getKey());
                return !instructor.isAllowedForPrivilege(student.section, question.getFeedbackSessionName(),
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            });
        }
        // instructor can only see teams in allowed sections for him/her
        if (question.getRecipientType().equals(FeedbackParticipantType.TEAMS)) {
            recipients.entrySet().removeIf(teamEntry -> {
                String teamSection = studentsLogic.getSectionForTeam(question.getCourseId(), teamEntry.getKey());
                return !instructor.isAllowedForPrivilege(teamSection, question.getFeedbackSessionName(),
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
            });
        }

        return recipients;
    }

    /**
     * Gets the recipients of a feedback question.
     *
     * @param question the feedback question
     * @param giverEmail the email of the giver of the feedback question; In the case where the giver is a team,
     *                   this parameter can be anything as long as {@code giverTeam} is the name of the team.
     * @param giverTeam the team name of the giver of the feedback question
     * @return a map which keys are the identifiers of the recipients and values are the names of the recipients
     */
    private Map<String, String> getRecipientsOfQuestion(
            FeedbackQuestionAttributes question, String giverEmail, String giverTeam) {
        Map<String, String> recipients = new HashMap<>();

        FeedbackParticipantType recipientType = question.recipientType;

        switch (recipientType) {
        case SELF:
            if (question.giverType == FeedbackParticipantType.TEAMS) {
                recipients.put(giverTeam, giverTeam);
            } else {
                recipients.put(giverEmail, Const.USER_NAME_FOR_SELF);
            }
            break;
        case STUDENTS:
            List<StudentAttributes> studentsInCourse = studentsLogic.getStudentsForCourse(question.courseId);
            for (StudentAttributes student : studentsInCourse) {
                // Ensure student does not evaluate himself
                if (!giverEmail.equals(student.email)) {
                    recipients.put(student.email, student.name);
                }
            }
            break;
        case INSTRUCTORS:
            List<InstructorAttributes> instructorsInCourse = instructorsLogic.getInstructorsForCourse(question.courseId);
            for (InstructorAttributes instr : instructorsInCourse) {
                // Ensure instructor does not evaluate himself
                if (!giverEmail.equals(instr.email)) {
                    recipients.put(instr.email, instr.name);
                }
            }
            break;
        case TEAMS:
            List<TeamDetailsBundle> teams = null;
            try {
                teams = coursesLogic.getTeamsForCourse(question.courseId);
            } catch (EntityDoesNotExistException e) {
                Assumption.fail(e.getMessage());
            }
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
                if (!student.email.equals(giverEmail)) {
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
     * Updates a feedback question by {@code FeedbackQuestionAttributes.UpdateOptions}.
     *
     * <p>Cascade adjust the question number of questions in the same session.
     *
     * <p>Cascade adjust the existing response of the question.
     *
     * @return updated feedback question
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback question cannot be found
     */
    public FeedbackQuestionAttributes updateFeedbackQuestionCascade(FeedbackQuestionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        FeedbackQuestionAttributes oldQuestion = fqDb.getFeedbackQuestion(updateOptions.getFeedbackQuestionId());
        if (oldQuestion == null) {
            throw new EntityDoesNotExistException("Trying to update a feedback question that does not exist.");
        }

        FeedbackQuestionAttributes newQuestion = oldQuestion.getCopy();
        newQuestion.update(updateOptions);
        int oldQuestionNumber = oldQuestion.questionNumber;
        int newQuestionNumber = newQuestion.questionNumber;

        List<FeedbackQuestionAttributes> previousQuestionsInSession = new ArrayList<>();
        if (oldQuestionNumber != newQuestionNumber) {
            // get questions in session before update
            String feedbackSessionName = oldQuestion.feedbackSessionName;
            String courseId = oldQuestion.courseId;
            previousQuestionsInSession = getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        }

        // update question
        FeedbackQuestionAttributes updatedQuestion = fqDb.updateFeedbackQuestion(updateOptions);

        if (oldQuestionNumber != newQuestionNumber) {
            // shift other feedback questions (generate an empty "slot")
            adjustQuestionNumbers(oldQuestionNumber, newQuestionNumber, previousQuestionsInSession);
        }

        // adjust responses
        if (oldQuestion.areResponseDeletionsRequiredForChanges(updatedQuestion)) {
            frLogic.deleteFeedbackResponsesForQuestionCascade(oldQuestion.getId());
        }

        return updatedQuestion;
    }

    /**
     * Adjust questions between the old and new number,
     * if the new number is smaller, then shift up (increase qn#) all questions in between.
     * if the new number is bigger, then shift down(decrease qn#) all questions in between.
     */
    private void adjustQuestionNumbers(int oldQuestionNumber,
            int newQuestionNumber, List<FeedbackQuestionAttributes> questions) {
        try {
            if (oldQuestionNumber > newQuestionNumber && oldQuestionNumber >= 1) {
                for (int i = oldQuestionNumber - 1; i >= newQuestionNumber; i--) {
                    FeedbackQuestionAttributes question = questions.get(i - 1);
                    fqDb.updateFeedbackQuestion(
                            FeedbackQuestionAttributes.updateOptionsBuilder(question.getId())
                                    .withQuestionNumber(question.questionNumber + 1)
                                    .build());
                }
            } else if (oldQuestionNumber < newQuestionNumber && oldQuestionNumber < questions.size()) {
                for (int i = oldQuestionNumber + 1; i <= newQuestionNumber; i++) {
                    FeedbackQuestionAttributes question = questions.get(i - 1);
                    fqDb.updateFeedbackQuestion(
                            FeedbackQuestionAttributes.updateOptionsBuilder(question.getId())
                                    .withQuestionNumber(question.questionNumber - 1)
                                    .build());
                }
            }
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            Assumption.fail("Adjusting question number should not cause: " + e.getMessage());
        }
    }

    /**
     * Deletes a feedback question cascade its responses and comments.
     *
     * <p>Silently fail if question does not exist.
     *
     * <p>The respondent lists will also be updated due the deletion of question.
     */
    public void deleteFeedbackQuestionCascade(String feedbackQuestionId) {
        FeedbackQuestionAttributes questionToDelete =
                        getFeedbackQuestion(feedbackQuestionId);

        if (questionToDelete == null) {
            return; // Silently fail if question does not exist.
        }

        // cascade delete responses for question.
        frLogic.deleteFeedbackResponsesForQuestionCascade(questionToDelete.getId());

        List<FeedbackQuestionAttributes> questionsToShiftQnNumber =
                getFeedbackQuestionsForSession(questionToDelete.getFeedbackSessionName(), questionToDelete.getCourseId());

        // delete question
        fqDb.deleteFeedbackQuestion(feedbackQuestionId);

        // adjust question numbers
        if (questionToDelete.questionNumber < questionsToShiftQnNumber.size()) {
            shiftQuestionNumbersDown(questionToDelete.questionNumber, questionsToShiftQnNumber);
        }
    }

    /**
     * Deletes questions using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackQuestions(AttributesDeletionQuery query) {
        fqDb.deleteFeedbackQuestions(query);
    }

    // Shifts all question numbers after questionNumberToShiftFrom down by one.
    private void shiftQuestionNumbersDown(int questionNumberToShiftFrom,
            List<FeedbackQuestionAttributes> questionsToShift) {
        for (FeedbackQuestionAttributes question : questionsToShift) {
            if (question.questionNumber > questionNumberToShiftFrom) {
                try {
                    fqDb.updateFeedbackQuestion(
                            FeedbackQuestionAttributes.updateOptionsBuilder(question.getId())
                            .withQuestionNumber(question.questionNumber - 1)
                            .build());
                } catch (InvalidParametersException | EntityDoesNotExistException e) {
                    Assumption.fail("Shifting question number should not cause: " + e.getMessage());
                }
            }
        }
    }

}
