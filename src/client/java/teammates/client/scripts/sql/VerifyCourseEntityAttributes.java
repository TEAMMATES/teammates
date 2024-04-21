package teammates.client.scripts.sql;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.util.HibernateUtil;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Class for verifying account attributes.
 */
@SuppressWarnings("PMD")
public class VerifyCourseEntityAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<Course, teammates.storage.sqlentity.Course> {

    public VerifyCourseEntityAttributes() {
        super(Course.class,
                teammates.storage.sqlentity.Course.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.Course sqlEntity) {
        return sqlEntity.getId();
    }

    public static void main(String[] args) {
        VerifyCourseEntityAttributes script = new VerifyCourseEntityAttributes();
        script.doOperationRemotely();
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Course sqlEntity, Course datastoreEntity) {
        try {
            return verifyCourse(sqlEntity, datastoreEntity) && verifySectionChain(sqlEntity)
                    && verifyFeedbackChain(sqlEntity);
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    private boolean verifyCourse(teammates.storage.sqlentity.Course sqlEntity, Course datastoreEntity) {
        return sqlEntity.getId().equals(datastoreEntity.getUniqueId())
                && sqlEntity.getName().equals(datastoreEntity.getName())
                && sqlEntity.getTimeZone().equals(datastoreEntity.getTimeZone())
                && sqlEntity.getInstitute().equals(datastoreEntity.getInstitute())
                && sqlEntity.getCreatedAt().equals(datastoreEntity.getCreatedAt())
                && datastoreEntity.getDeletedAt() == null ? sqlEntity.getDeletedAt() == null
                        : sqlEntity.getDeletedAt().equals(datastoreEntity.getDeletedAt());
    }

    // methods for verify section chain  -----------------------------------------------------------------------------------

    private boolean verifySectionChain(teammates.storage.sqlentity.Course newCourse) {
        // Get old and new students
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", newCourse.getId())
                .list();
        List<Student> newStudents = getNewStudents(newCourse.getId());

        // Group students by section
        Map<String, List<CourseStudent>> sectionToOldStuMap = oldStudents.stream()
                .collect(Collectors.groupingBy(CourseStudent::getSectionName));
        Map<String, List<Student>> sectionToNewStuMap = newStudents.stream()
                .collect(Collectors.groupingBy(Student::getSectionName));

        List<Section> newSection = newCourse.getSections();

        boolean isNotSectionsCountEqual = newSection.size() != sectionToOldStuMap.size()
                || newSection.size() != sectionToNewStuMap.size();
        if (isNotSectionsCountEqual) {
            return false;
        }

        return newSection.stream().allMatch(section -> {
            List<CourseStudent> oldSectionStudents = sectionToOldStuMap.get(section.getName());
            List<Student> newSectionStudents = sectionToNewStuMap.get(section.getName());

            // If either of the sectionStudent is null,
            // then section is not present in the corresponding datastore or sql
            // which means a possible migration error
            boolean isSectionNamePresent = oldSectionStudents != null && newSectionStudents != null;
            if (!isSectionNamePresent) {
                return false;
            }

            // Group students by team
            Map<String, List<CourseStudent>> teamNameToOldStuMap = oldSectionStudents.stream()
                    .collect(Collectors.groupingBy(CourseStudent::getTeamName));
            Map<String, List<Student>> teamNameToNewStuMap = newSectionStudents.stream()
                    .collect(Collectors.groupingBy(Student::getTeamName));
            return verifyTeams(section, teamNameToOldStuMap, teamNameToNewStuMap);
        });

    }

    private boolean verifyTeams(Section newSection,
            Map<String, List<CourseStudent>> teamNameToOldStuMap, Map<String, List<Student>> teamNameToNewStuMap) {

        List<Team> newTeams = newSection.getTeams();

        boolean isNotTeamCountEqual = newTeams.size() != teamNameToNewStuMap.size()
                || newTeams.size() != teamNameToOldStuMap.size();
        if (isNotTeamCountEqual) {
            return false;
        }

        return newTeams.stream().allMatch(team -> {
            List<CourseStudent> oldTeamStudents = teamNameToOldStuMap.get(team.getName());
            List<Student> newTeamStudents = teamNameToNewStuMap.get(team.getName());

            // If either of the teamStudent is null,
            // then team is not present in the corresponding datastore or sql
            // which means a possible migration error
            boolean isTeamNamePresent = oldTeamStudents != null && newTeamStudents != null;
            if (!isTeamNamePresent) {
                return false;
            }
            return verifyStudents(oldTeamStudents, newTeamStudents);
        });
    }

    private boolean verifyStudents(
            List<CourseStudent> oldTeamStudents, List<Student> newTeamStudents) {
        if (oldTeamStudents.size() != newTeamStudents.size()) {
            return false;
        }
        oldTeamStudents.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
        newTeamStudents.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
        for (int i = 0; i < oldTeamStudents.size(); i++) {
            CourseStudent oldStudent = oldTeamStudents.get(i);
            Student newStudent = newTeamStudents.get(i);
            if (!verifyStudent(oldStudent, newStudent)) {
                return false;
            }
        }
        return true;
    }

    private boolean verifyStudent(CourseStudent oldStudent,
            Student newStudent) {
        return newStudent.getName().equals(oldStudent.getName())
                && newStudent.getEmail().equals(oldStudent.getEmail())
                && newStudent.getComments().equals(oldStudent.getComments())
                && newStudent.getUpdatedAt().equals(oldStudent.getUpdatedAt())
                && newStudent.getCreatedAt().equals(oldStudent.getCreatedAt())
                && newStudent.getRegKey().equals(oldStudent.getRegistrationKey());

    }

    private List<Student> getNewStudents(String courseId) {
        // HibernateUtil.beginTransaction();
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.Student> cr = cb
                .createQuery(teammates.storage.sqlentity.Student.class);
        Root<teammates.storage.sqlentity.Student> courseRoot = cr.from(teammates.storage.sqlentity.Student.class);
        cr.select(courseRoot).where(cb.equal(courseRoot.get("courseId"), courseId));
        List<Student> newStudents = HibernateUtil.createQuery(cr).getResultList();
        // HibernateUtil.commitTransaction();
        return newStudents;
    }

    // methods for verify feedback chain -----------------------------------------------------------------------------------

    private boolean verifyFeedbackChain(teammates.storage.sqlentity.Course newCourse) {
        List<teammates.storage.sqlentity.FeedbackSession> newSessions = newCourse.getFeedbackSessions();
        List<FeedbackSession> oldSessions = ofy().load().type(FeedbackSession.class)
                .filter("courseId", newCourse.getId()).list();

        if (newSessions.size() != oldSessions.size()) {
            log(String.format("Mismatched session counts for course id: %s", newCourse.getId()));
            return false;
        }

        Map<String, FeedbackSession> sessionNameToOldSessionMap = oldSessions.stream()
                .collect(Collectors.toMap(FeedbackSession::getFeedbackSessionName, session -> session));

        return newSessions.stream().allMatch(newSession -> {
            FeedbackSession oldSession = sessionNameToOldSessionMap.get(newSession.getName());
            return verifyFeedbackSession(oldSession, newSession);
        });
    }

    private boolean verifyFeedbackSession(FeedbackSession oldSession, teammates.storage.sqlentity.FeedbackSession newSession) {
        boolean doFieldsMatch = newSession.getCourse().getId().equals(oldSession.getCourseId())
                && newSession.getName().equals(oldSession.getFeedbackSessionName())
                && newSession.getCreatorEmail().equals(oldSession.getCreatorEmail())
                && newSession.getInstructions().equals(SanitizationHelper.sanitizeForRichText(oldSession.getInstructions()))
                && newSession.getStartTime().equals(oldSession.getStartTime())
                && newSession.getEndTime().equals(oldSession.getEndTime())
                && newSession.getSessionVisibleFromTime().equals(oldSession.getSessionVisibleFromTime())
                && newSession.getResultsVisibleFromTime().equals(oldSession.getResultsVisibleFromTime())
                && newSession.getGracePeriod().equals(Duration.ofMinutes(oldSession.getGracePeriod()))
                && newSession.isOpeningEmailEnabled() == oldSession.isOpeningEmailEnabled()
                && newSession.isClosingEmailEnabled() == oldSession.isClosingEmailEnabled()
                && newSession.isOpenEmailSent() == oldSession.isSentOpenEmail()
                && newSession.isOpeningSoonEmailSent() == oldSession.isSentOpeningSoonEmail()
                && newSession.isClosedEmailSent() == oldSession.isSentClosedEmail()
                && newSession.isClosingSoonEmailSent() == oldSession.isSentClosingEmail()
                && newSession.isPublishedEmailSent() == oldSession.isSentPublishedEmail()
                && newSession.getCreatedAt().equals(oldSession.getCreatedTime())
                && (newSession.getDeletedAt() == oldSession.getDeletedTime()
                        || newSession.getDeletedAt().equals(oldSession.getDeletedTime()));
        if (!doFieldsMatch) {
            log(String.format("Mismatched fields for session: %s, course id: %s",
                    oldSession.getFeedbackSessionName(), oldSession.getCourseId()));
            return false;
        }

        List<teammates.storage.sqlentity.FeedbackQuestion> newQuestions = newSession.getFeedbackQuestions();
        List<FeedbackQuestion> oldQuestions = ofy().load().type(FeedbackQuestion.class)
                .filter("courseId", newSession.getCourse().getId())
                .filter("feedbackSessionName", newSession.getName()).list();

        if (newQuestions.size() != oldQuestions.size()) {
            log(String.format("Mismatched question counts for session: %s, course id: %s",
                    oldSession.getFeedbackSessionName(), oldSession.getCourseId()));
            return false;
        }

        Map<Integer, FeedbackQuestion> questionNumberToOldQuestionMap = oldQuestions.stream()
                .collect(Collectors.toMap(FeedbackQuestion::getQuestionNumber, question -> question));

        return newQuestions.stream().allMatch(newQuestion -> {
            FeedbackQuestion oldQuestion = questionNumberToOldQuestionMap.get(newQuestion.getQuestionNumber());
            return verifyFeedbackQuestion(oldQuestion, newQuestion);
        });
    }

    private boolean verifyFeedbackQuestion(FeedbackQuestion oldQuestion, teammates.storage.sqlentity.FeedbackQuestion newQuestion) {
        boolean doFieldsMatch = newQuestion.getQuestionNumber() == oldQuestion.getQuestionNumber()
                && newQuestion.getDescription().equals(oldQuestion.getQuestionDescription())
                && newQuestion.getGiverType().equals(oldQuestion.getGiverType())
                && newQuestion.getRecipientType().equals(oldQuestion.getRecipientType())
                && newQuestion.getNumOfEntitiesToGiveFeedbackTo().equals(oldQuestion.getNumberOfEntitiesToGiveFeedbackTo())
                && newQuestion.getShowResponsesTo().equals(oldQuestion.getShowResponsesTo())
                && newQuestion.getShowGiverNameTo().equals(oldQuestion.getShowGiverNameTo())
                && newQuestion.getShowRecipientNameTo().equals(oldQuestion.getShowRecipientNameTo())
                && newQuestion.getQuestionDetailsCopy()
                        .equals(DataMigrationForCourseEntitySql.getFeedbackQuestionDetails(oldQuestion))
                && newQuestion.getCreatedAt().equals(oldQuestion.getCreatedAt())
                && newQuestion.getUpdatedAt().equals(oldQuestion.getUpdatedAt());
        if (!doFieldsMatch) {
            log(String.format("Mismatched fields for question %s, session: %s, course id: %s",
                    oldQuestion.getQuestionNumber(), oldQuestion.getFeedbackSessionName(), oldQuestion.getCourseId()));
            return false;
        }

        List<teammates.storage.sqlentity.FeedbackResponse> newResponses = newQuestion.getFeedbackResponses();
        List<FeedbackResponse> oldResponses = ofy().load().type(FeedbackResponse.class)
                .filter("feedbackQuestionId", oldQuestion.getId()).list();

        if (newResponses.size() != oldResponses.size()) {
            log(String.format("Mismatched response counts for question %s, session: %s, course id: %s",
                    oldQuestion.getQuestionNumber(), oldQuestion.getFeedbackSessionName(), oldQuestion.getCourseId()));
            return false;
        }

        Map<String, FeedbackResponse> responseIdToOldResponseMap = oldResponses.stream()
                .collect(Collectors.toMap(FeedbackResponse::getId, response -> response));

        return newResponses.stream().allMatch(newResponse -> {
            String oldResponseId = FeedbackResponse.generateId(oldQuestion.getId(), newResponse.getGiver(),
                    newResponse.getRecipient());
            FeedbackResponse oldResponse = responseIdToOldResponseMap.get(oldResponseId);
            return verifyFeedbackResponse(oldResponse, newResponse);
        });
    }

    private boolean verifyFeedbackResponse(FeedbackResponse oldResponse, teammates.storage.sqlentity.FeedbackResponse newResponse) {
        boolean allFieldsMatch = newResponse.getGiver().equals(oldResponse.getGiverEmail())
                && newResponse.getGiverSection().getCourse().getId().equals(oldResponse.getCourseId())
                && newResponse.getGiverSectionName().equals(oldResponse.getGiverSection())
                && newResponse.getRecipient().equals(oldResponse.getRecipientEmail())
                && newResponse.getRecipientSectionName().equals(oldResponse.getRecipientSection())
                && newResponse.getCreatedAt().equals(oldResponse.getCreatedAt())
                && newResponse.getUpdatedAt().equals(oldResponse.getUpdatedAt())
                && newResponse.getFeedbackResponseDetailsCopy().getJsonString().equals(oldResponse.getAnswer());
        if (!allFieldsMatch) {
            log(String.format("Mismatched fields for response %s, question %s, session: %s, course id: %s",
                    oldResponse.getId(), oldResponse.getFeedbackQuestionId(), oldResponse.getFeedbackSessionName(),
                    oldResponse.getCourseId()));
            return false;
        }

        List<teammates.storage.sqlentity.FeedbackResponseComment> newComments = newResponse.getFeedbackResponseComments();
        List<FeedbackResponseComment> oldComments = ofy().load()
                .type(teammates.storage.entity.FeedbackResponseComment.class)
                .filter("feedbackResponseId", oldResponse.getId()).list();
        
        if (newComments.size() != oldComments.size()) {
            log(String.format("Mismatched comment counts for response %s, question %s, session: %s, course id: %s",
                    oldResponse.getId(), oldResponse.getFeedbackQuestionId(), oldResponse.getFeedbackSessionName(),
                    oldResponse.getCourseId()));
            return false;
        }

        return oldComments.stream().allMatch(oldComment -> newComments.stream()
                .anyMatch(newComment -> verifyFeedbackResponseComment(oldComment, newComment)));
    }

    private boolean verifyFeedbackResponseComment(FeedbackResponseComment oldComment, teammates.storage.sqlentity.FeedbackResponseComment newComment) {
        boolean allFieldsMatch = 
                newComment.getGiver().equals(oldComment.getGiverEmail())
                && newComment.getCommentText().equals(oldComment.getCommentText())
                && newComment.getGiverType().equals(oldComment.getCommentGiverType())
                && newComment.getGiverSection().getCourse().getId().equals(oldComment.getCourseId())
                && newComment.getGiverSection().getName().equals(oldComment.getGiverSection())
                && newComment.getRecipientSection().getName().equals(oldComment.getReceiverSection())
                && newComment.getIsVisibilityFollowingFeedbackQuestion()
                        == oldComment.getIsVisibilityFollowingFeedbackQuestion()
                && newComment.getIsCommentFromFeedbackParticipant() == oldComment.getIsCommentFromFeedbackParticipant()
                && newComment.getShowCommentTo().equals(oldComment.getShowCommentTo())
                && newComment.getShowGiverNameTo().equals(oldComment.getShowGiverNameTo())
                && newComment.getCreatedAt().equals(oldComment.getCreatedAt())
                && newComment.getUpdatedAt().equals(oldComment.getLastEditedAt())
                && newComment.getLastEditorEmail().equals(oldComment.getLastEditorEmail());

        if (allFieldsMatch) {
            return true;
        } else {
            log(String.format("Mismatched fields for comment %s, response %s, question %s, session: %s, course id: %s",
                    oldComment.getFeedbackResponseCommentId(), oldComment.getFeedbackResponseId(), oldComment.getFeedbackQuestionId(),
                    oldComment.getFeedbackSessionName(), oldComment.getCourseId()));
            return false;
        }
    }
}
