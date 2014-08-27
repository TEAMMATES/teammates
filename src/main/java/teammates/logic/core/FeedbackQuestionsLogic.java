package teammates.logic.core;

import static teammates.common.datatransfer.FeedbackParticipantType.INSTRUCTORS;
import static teammates.common.datatransfer.FeedbackParticipantType.SELF;
import static teammates.common.datatransfer.FeedbackParticipantType.STUDENTS;
import static teammates.common.datatransfer.FeedbackParticipantType.TEAMS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackAbstractQuestionDetails;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionBundle;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.api.FeedbackQuestionsDb;

public class FeedbackQuestionsLogic {
    
    @SuppressWarnings("unused")
    private static final Logger log = Utils.getLogger();

    private static FeedbackQuestionsLogic instance = null;
    
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    
    public static FeedbackQuestionsLogic inst() {
        if (instance == null)
            instance = new FeedbackQuestionsLogic();
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
        if(fqa.questionNumber < 0){
            fqa.questionNumber = questions.size() + 1;
        }
        adjustQuestionNumbers(questions.size()+1, fqa.questionNumber, questions);
        createFeedbackQuestionNoIntegrityCheck(fqa, fqa.questionNumber);
    }
    
    /**
     * Used for creating initial questions for template sessions only.
     * Does not check if feedback session exists.
     * Does not check if question number supplied is valid(does not check for clashes, or make adjustments)
     * @param fqa
     * @param questionNumber
     * @throws InvalidParametersException
     */
    public void createFeedbackQuestionNoIntegrityCheck(FeedbackQuestionAttributes fqa, int questionNumber)
            throws InvalidParametersException {
        fqa.questionNumber = questionNumber;
        fqa.removeIrrelevantVisibilityOptions();
        fqDb.createEntityWithoutExistenceCheck(fqa);
    }
    
    public FeedbackQuestionAttributes copyFeedbackQuestion(String feedbackQuestionId,
            String feedbackSessionName, String courseId, String instructorEmail)
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
        Collections.sort(questions);
        
        return questions;
    }

    /**
     *  Gets a {@link List} of every FeedbackQuestion that the instructor can copy
     */
    public List<FeedbackQuestionAttributes> getCopiableFeedbackQuestionsForInstructor(
            String googleId)
            throws EntityDoesNotExistException {
        
        List<FeedbackQuestionAttributes> copiableQuestions = new ArrayList<FeedbackQuestionAttributes>();
        List<CourseAttributes> courses = coursesLogic.getCoursesForInstructor(googleId);
        for(CourseAttributes course : courses) {
            List<FeedbackSessionAttributes> sessions = fsLogic.getFeedbackSessionsForCourse(course.id);
            for(FeedbackSessionAttributes session : sessions) {
                List<FeedbackQuestionAttributes> questions = getFeedbackQuestionsForSession(session.feedbackSessionName, course.id);
                copiableQuestions.addAll(questions);
            }
        }
        Collections.sort(copiableQuestions, new Comparator<FeedbackQuestionAttributes>(){
            @Override
            public int compare(FeedbackQuestionAttributes q1, FeedbackQuestionAttributes q2) {
                int order = q1.courseId.compareTo(q2.courseId);
                if(order != 0){
                    return order;
                }
                
                order = q1.feedbackSessionName.compareTo(q2.feedbackSessionName);
                if(order != 0){
                    return order;
                }
                
                FeedbackAbstractQuestionDetails q1Details = q1.getQuestionDetails();
                FeedbackAbstractQuestionDetails q2Details = q2.getQuestionDetails();
                
                order = q1Details.getQuestionTypeDisplayName().compareTo(q2Details.getQuestionTypeDisplayName());
                if(order != 0){
                    return order;
                }
                
                return q1Details.questionText.compareTo(q2Details.questionText);
            }
        });
        
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

        List<FeedbackQuestionAttributes> questions =
                new ArrayList<FeedbackQuestionAttributes>();
        
        // Return instructor questions if instructor.
        InstructorAttributes instructor = 
                instructorsLogic.getInstructorForEmail(courseId, userEmail);
        
        if (instructor != null) {
            questions.addAll(fqDb.getFeedbackQuestionsForGiverType(
                        feedbackSessionName, courseId, INSTRUCTORS));
        }
        
        // Return all self (creator) questions if creator.
        if (fsLogic.isCreatorOfSession(feedbackSessionName, courseId, userEmail)) {
            questions.addAll(fqDb.getFeedbackQuestionsForGiverType(feedbackSessionName,
                    courseId, SELF));
        }
        
        return questions;
    }
    
    /**
     * Gets a {@code List} of all questions for the list of questions that an
     * instructor can view/submit
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForInstructor(
            List<FeedbackQuestionAttributes> allQuestions, boolean isCreator) 
                    throws EntityDoesNotExistException {
        
        List<FeedbackQuestionAttributes> questions =
                new ArrayList<FeedbackQuestionAttributes>();
        
        for (FeedbackQuestionAttributes question : allQuestions) {
            if (question.giverType == FeedbackParticipantType.INSTRUCTORS || 
                (question.giverType == FeedbackParticipantType.SELF && isCreator) ) {
                questions.add(question);
            }
        }
        
        return questions;
    }

    /**
     * Gets a given {@code FeedbackQuestion} and its previously filled {@code FeedbackResponses}
     * for an instructor.
     * a {@link FeedbackQuestionBundle}
     */
    public FeedbackQuestionBundle getFeedbackQuestionBundleForInstructor(
            String feedbackSessionName, String courseId, String feedbackQuestionId, String userEmail)
            throws EntityDoesNotExistException {
        FeedbackSessionAttributes fs = fsLogic.getFeedbackSession(feedbackSessionName, courseId);
        if (fs == null) {
            throw new EntityDoesNotExistException("Trying to get a feedback session that does not exist.");
        }
                
        FeedbackQuestionAttributes question = fqDb
                .getFeedbackQuestion(feedbackQuestionId);
        if (question == null) {
            throw new EntityDoesNotExistException("Trying to get a feedback question that does not exist.");
        } else if (question.giverType != FeedbackParticipantType.INSTRUCTORS
                && !(question.giverType == FeedbackParticipantType.SELF 
                        && fsLogic.isCreatorOfSession(feedbackSessionName, courseId, userEmail))) {
            throw new UnauthorizedAccessException("Trying to access a question not meant for the user.");
        }

        Assumption.assertEquals(fs.courseId, question.courseId);
        Assumption.assertEquals(fs.feedbackSessionName, question.feedbackSessionName);
        
        List<FeedbackResponseAttributes> responses =
                frLogic.getFeedbackResponsesFromGiverForQuestion(
                        question.getId(), userEmail);
        Map<String, String> recipients =
                getRecipientsForQuestion(question, userEmail);
        if (question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS) {
            question.numberOfEntitiesToGiveFeedbackTo = recipients.size();
        }

        return new FeedbackQuestionBundle(fs, question, responses, recipients);
    }
    
    /**
     * Gets a {@code List} of all questions for the given session that
     * students can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForStudents(
            String feedbackSessionName, String courseId) 
                    throws EntityDoesNotExistException {

        List<FeedbackQuestionAttributes> questions =
                new ArrayList<FeedbackQuestionAttributes>();
        
        questions.addAll(
                fqDb.getFeedbackQuestionsForGiverType(
                        feedbackSessionName, courseId, STUDENTS));
        questions.addAll(
                fqDb.getFeedbackQuestionsForGiverType(
                        feedbackSessionName, courseId, TEAMS));
        
        return questions;
    }
    
    /**
     * Gets a given {@code FeedbackQuestion} and its previously filled {@code FeedbackResponses}
     * for a student.
     * a {@link FeedbackQuestionBundle}
     */
    public FeedbackQuestionBundle getFeedbackQuestionBundleForStudent(
            String feedbackSessionName, String courseId,
            String feedbackQuestionId, String userEmail)
            throws EntityDoesNotExistException {
        FeedbackSessionAttributes fs = fsLogic.getFeedbackSession(feedbackSessionName, courseId);
        if (fs == null) {
            throw new EntityDoesNotExistException("Trying to get a feedback session that does not exist.");
        }
        
        FeedbackQuestionAttributes question = fqDb
                .getFeedbackQuestion(feedbackQuestionId);
        if (question == null) {
            throw new EntityDoesNotExistException("Trying to get a feedback question that does not exist.");
        } else if (question.giverType != FeedbackParticipantType.STUDENTS
                && question.giverType != FeedbackParticipantType.TEAMS) {
            throw new UnauthorizedAccessException("Trying to access a question not meant for the user.");
        }
        
        Assumption.assertEquals(fs.courseId, question.courseId);
        Assumption.assertEquals(fs.feedbackSessionName, question.feedbackSessionName);

        List<FeedbackResponseAttributes> responses =
                frLogic.getFeedbackResponsesFromGiverForQuestion(
                        question.getId(), userEmail);
        Map<String, String> recipients =
                getRecipientsForQuestion(question, userEmail);
        if (question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS) {
            question.numberOfEntitiesToGiveFeedbackTo = recipients.size();
        }

        return new FeedbackQuestionBundle(fs, question, responses, recipients);
    }
    
    /**
     * Gets a {@code List} of all questions from the given list of questions 
     * that students can view/submit
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForStudents(
            List<FeedbackQuestionAttributes> allQuestions) 
                    throws EntityDoesNotExistException {
        
        List<FeedbackQuestionAttributes> questions =
                new ArrayList<FeedbackQuestionAttributes>();
        
        for (FeedbackQuestionAttributes question : allQuestions) {
            if (question.giverType == FeedbackParticipantType.STUDENTS ||
                question.giverType == FeedbackParticipantType.TEAMS) {
                questions.add(question);
            }
        }
        
        return questions;
    }

    /**
     * Gets a {@code List} of all <b>unanswered</b> questions corresponding to
     * the given session and team.
     * TODO: remove this function after ensuring no other references exist
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForTeam(
            String feedbackSessionName, String courseId, String teamName)
                    throws EntityDoesNotExistException {
        
        List<FeedbackQuestionAttributes> questions =
                fqDb.getFeedbackQuestionsForGiverType(
                feedbackSessionName, courseId, TEAMS);
        
        List<FeedbackQuestionAttributes> unansweredQuestions =
                new ArrayList<FeedbackQuestionAttributes>();
        
        for (FeedbackQuestionAttributes question : questions) {
            if (isQuestionFullyAnsweredByTeam(
                    question, teamName) == false)
                unansweredQuestions.add(question);
        }
        
        return unansweredQuestions;
    }

    public Map<String,String> getRecipientsForQuestion(
            FeedbackQuestionAttributes question, String giver)
                    throws EntityDoesNotExistException {

        Map<String,String> recipients = new HashMap<String,String>();
        
        FeedbackParticipantType recipientType = question.recipientType;
        
        String giverTeam = null;
        
        InstructorAttributes instructorGiver =
                instructorsLogic.getInstructorForEmail(question.courseId, giver);
        StudentAttributes studentGiver = 
                studentsLogic.getStudentForEmail(question.courseId, giver);
        
        if (studentGiver != null) {
            giverTeam = studentGiver.team;
        } else if (instructorGiver != null) {
            giverTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
        } else {
            giverTeam = giver;
        }
        
        switch (recipientType) {
        case SELF:
            recipients.put(giver, Const.USER_NAME_FOR_SELF);
            break;
        case STUDENTS:
            List<StudentAttributes> studentsInCourse =
                studentsLogic.getStudentsForCourse(question.courseId);
            for(StudentAttributes student : studentsInCourse) {
                // Ensure student does not evaluate himself
                if(giver.equals(student.email) == false) {
                    recipients.put(student.email, student.name);
                }
            }
            break;
        case INSTRUCTORS:
            List<InstructorAttributes> instructorsInCourse =
                instructorsLogic.getInstructorsForCourse(question.courseId);
            for(InstructorAttributes instr : instructorsInCourse) {
                // Ensure instructor does not evaluate himself
                if (!giver.equals(instr.email)) {
                    recipients.put(instr.email, instr.name);
                }
            }
            break;
        case TEAMS:
            List<TeamDetailsBundle> teams =
                coursesLogic.getTeamsForCourse(question.courseId);
            for(TeamDetailsBundle team : teams) {
                // Ensure student('s team) does not evaluate own team.
                if (giverTeam.equals(team.name) == false) {
                    // recipientEmail doubles as team name in this case.
                    recipients.put(team.name, team.name);
                }
            }
            break;
        case OWN_TEAM:
            recipients.put(giverTeam, giverTeam);
            break;
        case OWN_TEAM_MEMBERS:
            List<StudentAttributes> students = 
                studentsLogic.getStudentsForTeam(giverTeam, question.courseId);
            for (StudentAttributes student : students) {
                if(student.email.equals(giver) == false) {
                    recipients.put(student.email, student.name);
                }
            }
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            List<StudentAttributes> students_Member = 
                studentsLogic.getStudentsForTeam(giverTeam, question.courseId);
            for (StudentAttributes student : students_Member) {
                    //accepts self feedback too
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
    
    public boolean isQuestionHasResponses(String feedbackQuestionId) {
        return (frLogic.getFeedbackResponsesForQuestionWithinRange(feedbackQuestionId, 1).isEmpty() == false);
    }
    
    public boolean isQuestionAnsweredByUser(FeedbackQuestionAttributes question, String email) 
            throws EntityDoesNotExistException {
        
        int numberOfResponsesGiven = 
                frLogic.getFeedbackResponsesFromGiverForQuestion(question.getId(), email).size();
        
        // As long as a user has responded, we count the question as answered.
        return numberOfResponsesGiven > 0 ? true : false;
    }
    
    public boolean isQuestionAnsweredByUser(FeedbackQuestionAttributes question, String email,
            List<FeedbackResponseAttributes> responses) {
        for (FeedbackResponseAttributes response : responses) {
            if (response.giverEmail.equals(email) &&
                response.feedbackQuestionId.equals(question.getId())) {
                return true;
            }
        }
        return false;
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
        
        return numberOfResponsesGiven >= numberOfResponsesNeeded ? true : false;
    }

    /**
     * Checks if a question has been fully answered by a team.
     * @param question
     * @param teamName
     * @return {@code True} if there are no more recipients to give feedback to for the given
     * {@code teamName}. {@code False} if not.
     */
    public boolean isQuestionFullyAnsweredByTeam(FeedbackQuestionAttributes question, 
            String teamName) throws EntityDoesNotExistException {

        List<StudentAttributes> studentsInTeam =
                studentsLogic.getStudentsForTeam(question.courseId, teamName);
        
        int numberOfResponsesNeeded =
                question.numberOfEntitiesToGiveFeedbackTo;
        
        if (numberOfResponsesNeeded == Const.MAX_POSSIBLE_RECIPIENTS) {
            numberOfResponsesNeeded = getRecipientsForQuestion(question, teamName).size();
        }
                
        for (StudentAttributes student : studentsInTeam) {
            List<FeedbackResponseAttributes> responses = 
                    frLogic.getFeedbackResponsesFromGiverForQuestion(question.getId(), student.email);
            for (FeedbackResponseAttributes response : responses) {
                if (response.giverEmail.equals(student.email)) {
                    numberOfResponsesNeeded -= 1;
                }
            }
        }
        return numberOfResponsesNeeded <= 0 ? true : false;
    }
    
    
    /**
     * Updates the feedback question number, shifts other questions up/down
     * depending on the change.
     */
    public void updateFeedbackQuestionNumber(FeedbackQuestionAttributes newQuestion)
        throws InvalidParametersException, EntityDoesNotExistException {
        
        FeedbackQuestionAttributes oldQuestion = 
                fqDb.getFeedbackQuestion(newQuestion.getId());
        
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
     * @param oldQuestionNumber
     * @param newQuestionNumber
     * @param questions
     */
    private void adjustQuestionNumbers(int oldQuestionNumber,
            int newQuestionNumber, List<FeedbackQuestionAttributes> questions){
        
        if(oldQuestionNumber > newQuestionNumber && oldQuestionNumber >= 1){
            for(int i = oldQuestionNumber-1; i >= newQuestionNumber; i--){
                FeedbackQuestionAttributes question = questions.get(i-1);
                question.questionNumber += 1;
                try {
                    updateFeedbackQuestion(question);
                } catch (InvalidParametersException e) {
                    Assumption.fail("Invalid question.");
                } catch (EntityDoesNotExistException e) {
                    Assumption.fail("Question disappeared.");
                }
            }
        } else if(oldQuestionNumber < newQuestionNumber && oldQuestionNumber < questions.size()){
            for(int i = oldQuestionNumber+1; i <= newQuestionNumber; i++){
                FeedbackQuestionAttributes question = questions.get(i-1);
                question.questionNumber -= 1;
                try {
                    updateFeedbackQuestion(question);
                } catch (InvalidParametersException e) {
                    Assumption.fail("Invalid question.");
                } catch (EntityDoesNotExistException e) {
                    Assumption.fail("Question disappeared.");
                }
            }
        }
    }
    
    /**
     * Updates the feedback session identified by {@code newAttributes.getId()}.
     * For the remaining parameters, the existing value is preserved 
     *   if the parameter is null (due to 'keep existing' policy).<br> 
     * Existing responses for the question are automatically deleted if giverType/recipientType
     * are changed, or if the response visibility is increased.
     * Preconditions: <br>
     * * {@code newAttributes} is non-null and it's ID corresponds to an 
     * existing feedback question. <br>
     */
    public void updateFeedbackQuestion(FeedbackQuestionAttributes newAttributes)
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
        
        if(oldQuestion.isChangesRequiresResponseDeletion(newAttributes)) {
            frLogic.deleteFeedbackResponsesForQuestionAndCascade(oldQuestion.getId());
        }
        
        oldQuestion.updateValues(newAttributes);
        newAttributes.removeIrrelevantVisibilityOptions();
        fqDb.updateFeedbackQuestion(newAttributes);
    }
    
    public void deleteFeedbackQuestionsForSession(String feedbackSessionName, String courseId) 
            throws EntityDoesNotExistException{
        List<FeedbackQuestionAttributes> questions = 
                getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        
        for(FeedbackQuestionAttributes question : questions) {
            deleteFeedbackQuestionCascade(question.getId());
        }
        
    }
    
    /**
     * Deletes a question by it's auto-generated ID. <br>
     * Cascade the deletion of all existing responses for the question and then 
     * shifts larger question numbers down by one to preserve number order.
     * @param feedbackQuestionId
     */
    public void deleteFeedbackQuestionCascade(String feedbackQuestionId){
        FeedbackQuestionAttributes questionToDeleteById = 
                        getFeedbackQuestion(feedbackQuestionId);
        
        if (questionToDeleteById != null) {
            deleteFeedbackQuestionCascade(questionToDeleteById.feedbackSessionName,
                                        questionToDeleteById.courseId, 
                                        questionToDeleteById.questionNumber);
        } else {
            // Silently fail if question does not exist.
        }
        
    }
    
    /**
     * Deletes a question.<br> Question is identified by it's question number, and
     * the feedback session name and course ID of the question.<br>
     * Can be used when the question ID is unknown. <br>
     * Cascade the deletion of all existing responses for the question and then 
     * shifts larger question numbers down by one to preserve number order.
     */
    public void deleteFeedbackQuestionCascade(
            String feedbackSessionName, String courseId, int questionNumber) {
        
        FeedbackQuestionAttributes questionToDelete =
                getFeedbackQuestion(feedbackSessionName, courseId, questionNumber);
        
        if (questionToDelete == null) {
            return; // Silently fail if question does not exist.
        } else {
            // Cascade delete responses for question.
            frLogic.deleteFeedbackResponsesForQuestionAndCascade(questionToDelete.getId());
        }
        
        List<FeedbackQuestionAttributes> questionsToShiftQnNumber = null;
        try {
            questionsToShiftQnNumber = getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        } catch (EntityDoesNotExistException e) {
            Assumption.fail("Session disappeared.");
        }
        
        fqDb.deleteEntity(questionToDelete);
        
        if(questionToDelete.questionNumber < questionsToShiftQnNumber.size()) {
            shiftQuestionNumbersDown(questionToDelete.questionNumber, questionsToShiftQnNumber);
        }
    }
    
    // Shifts all question numbers after questionNumberToShiftFrom down by one.
    private void shiftQuestionNumbersDown(int questionNumberToShiftFrom,
            List<FeedbackQuestionAttributes> questionsToShift) {
        for (FeedbackQuestionAttributes question : questionsToShift) {                
            if(question.questionNumber > questionNumberToShiftFrom){
                question.questionNumber -= 1;
                try {
                    updateFeedbackQuestion(question);
                } catch (InvalidParametersException e) {
                    Assumption.fail("Invalid question.");
                } catch (EntityDoesNotExistException e) {
                    Assumption.fail("Question disappeared.");
                }
            }
        }
    }
}
