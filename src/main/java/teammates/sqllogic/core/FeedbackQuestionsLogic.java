package teammates.sqllogic.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;
import teammates.storage.sqlapi.FeedbackQuestionsDb;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.questions.FeedbackMcqQuestion;
import teammates.storage.sqlentity.questions.FeedbackMsqQuestion;

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
    private CoursesLogic coursesLogic;
    private UsersLogic usersLogic;

    private FeedbackQuestionsLogic() {
        // prevent initialization
    }

    public static FeedbackQuestionsLogic inst() {
        return instance;
    }

    void initLogicDependencies(FeedbackQuestionsDb fqDb, CoursesLogic coursesLogic, UsersLogic usersLogic) {
        this.fqDb = fqDb;
        this.coursesLogic = coursesLogic;
        this.usersLogic = usersLogic;
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
     * Gets a {@code List} of all questions for the given session that instructors can view/submit.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForInstructors(
            FeedbackSession feedbackSession, String userEmail) {
        List<FeedbackQuestion> questions = new ArrayList<>();

        questions.addAll(
                fqDb.getFeedbackQuestionsForGiverType(
                    feedbackSession, FeedbackParticipantType.INSTRUCTORS));

        if (feedbackSession.getCreatorEmail().equals(userEmail)) {
            questions.addAll(
                    fqDb.getFeedbackQuestionsForGiverType(
                        feedbackSession, FeedbackParticipantType.SELF));
        }

        return questions;
    }

    /**
     * Gets a {@code List} of all questions for the given session that students can view/submit.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForStudents(FeedbackSession feedbackSession) {
        List<FeedbackQuestion> questions = new ArrayList<>();

        questions.addAll(fqDb.getFeedbackQuestionsForGiverType(feedbackSession, FeedbackParticipantType.STUDENTS));
        questions.addAll(fqDb.getFeedbackQuestionsForGiverType(feedbackSession, FeedbackParticipantType.SELF));

        return questions;
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

    /**
     * Populates fields that need dynamic generation in a question.
     *
     * <p>Currently, only MCQ/MSQ needs to generate choices dynamically.</p>
     *
     * @param feedbackQuestion the question to populate
     * @param courseId the ID of the course
     * @param emailOfEntityDoingQuestion the email of the entity doing the question
     * @param teamOfEntityDoingQuestion the team of the entity doing the question. If the entity is an instructor,
     *                                  it can be {@code null}.
     */
    public void populateFieldsToGenerateInQuestion(FeedbackQuestion feedbackQuestion,
            String courseId, String emailOfEntityDoingQuestion, String teamOfEntityDoingQuestion) {
        List<String> optionList;

        FeedbackParticipantType generateOptionsFor;
        FeedbackQuestionType questionType = feedbackQuestion.getQuestionDetailsCopy().getQuestionType();

        if (questionType == FeedbackQuestionType.MCQ) {
            FeedbackMcqQuestionDetails feedbackMcqQuestionDetails =
                    (FeedbackMcqQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
            optionList = feedbackMcqQuestionDetails.getMcqChoices();
            generateOptionsFor = feedbackMcqQuestionDetails.getGenerateOptionsFor();
        } else if (questionType == FeedbackQuestionType.MSQ) {
            FeedbackMsqQuestionDetails feedbackMsqQuestionDetails =
                    (FeedbackMsqQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
            optionList = feedbackMsqQuestionDetails.getMsqChoices();
            generateOptionsFor = feedbackMsqQuestionDetails.getGenerateOptionsFor();
        } else {
            // other question types
            return;
        }

        switch (generateOptionsFor) {
        case NONE:
            break;
        case STUDENTS:
        case STUDENTS_IN_SAME_SECTION:
        case STUDENTS_EXCLUDING_SELF:
            List<Student> studentList;
            if (generateOptionsFor == FeedbackParticipantType.STUDENTS_IN_SAME_SECTION) {
                Student student =
                        usersLogic.getStudentForEmail(courseId, emailOfEntityDoingQuestion);
                studentList = usersLogic.getStudentsForSection(student.getTeam().getSection().getName(), courseId);
            } else {
                studentList = usersLogic.getStudentsForCourse(courseId);
            }

            if (generateOptionsFor == FeedbackParticipantType.STUDENTS_EXCLUDING_SELF) {
                studentList.removeIf(studentInList -> studentInList.getEmail().equals(emailOfEntityDoingQuestion));
            }

            for (Student student : studentList) {
                optionList.add(student.getName() + " (" + student.getTeam() + ")");
            }

            optionList.sort(null);
            break;
        case TEAMS:
        case TEAMS_IN_SAME_SECTION:
        case TEAMS_EXCLUDING_SELF:
            List<String> teams;
            if (generateOptionsFor == FeedbackParticipantType.TEAMS_IN_SAME_SECTION) {
                Student student =
                        usersLogic.getStudentForEmail(courseId, emailOfEntityDoingQuestion);
                teams = coursesLogic.getTeamsForSection(student.getTeam().getSection())
                                    .stream()
                                    .map(team -> { return team.getName(); })
                                    .collect(Collectors.toList());
            } else {
                teams = coursesLogic.getTeamsForCourse(courseId)
                                    .stream()
                                    .map(team -> { return team.getName(); })
                                    .collect(Collectors.toList());
            }

            if (generateOptionsFor == FeedbackParticipantType.TEAMS_EXCLUDING_SELF) {
                teams.removeIf(team -> team.equals(teamOfEntityDoingQuestion));
            }

            for (String team : teams) {
                optionList.add(team);
            }

            optionList.sort(null);
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
        case OWN_TEAM_MEMBERS:
            if (teamOfEntityDoingQuestion != null) {
                List<Student> teamMembers = usersLogic.getStudentsForTeam(teamOfEntityDoingQuestion,
                        courseId);

                if (generateOptionsFor == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                    teamMembers.removeIf(teamMember -> teamMember.getEmail().equals(emailOfEntityDoingQuestion));
                }

                teamMembers.forEach(teamMember -> optionList.add(teamMember.getName()));

                optionList.sort(null);
            }
            break;
        case INSTRUCTORS:
            List<Instructor> instructorList =
                    usersLogic.getInstructorsForCourse(courseId);

            for (Instructor instructor : instructorList) {
                optionList.add(instructor.getName());
            }

            optionList.sort(null);
            break;
        default:
            assert false : "Trying to generate options for neither students, teams nor instructors";
            break;
        }

        if (questionType == FeedbackQuestionType.MCQ) {
            FeedbackMcqQuestionDetails feedbackMcqQuestionDetails =
                    (FeedbackMcqQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
            feedbackMcqQuestionDetails.setMcqChoices(optionList);
            ((FeedbackMcqQuestion) feedbackQuestion).setFeedBackQuestionDetails(feedbackMcqQuestionDetails);
        } else if (questionType == FeedbackQuestionType.MSQ) {
            FeedbackMsqQuestionDetails feedbackMsqQuestionDetails =
                    (FeedbackMsqQuestionDetails) feedbackQuestion.getQuestionDetailsCopy();
            feedbackMsqQuestionDetails.setMsqChoices(optionList);
            ((FeedbackMsqQuestion) feedbackQuestion).setFeedBackQuestionDetails(feedbackMsqQuestionDetails);
        }
    }
}
