package teammates.client.scripts;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Script to migrate giver name from student's name to team's name for question where the giver type is TEAMS.
 *
 * <p>This only affects very old response entities, created before late 2015.
 *
 * <p>See issue #4584.
 */
public class DataMigrationForTeamNameInResponseGiver extends
        DataMigrationEntitiesBaseScript<FeedbackQuestion> {

    private final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private final StudentsLogic studentsLogic = StudentsLogic.inst();

    public static void main(String[] args) throws IOException {
        new DataMigrationForTeamNameInResponseGiver().doOperationRemotely();
    }

    @Override
    protected Query<FeedbackQuestion> getFilterQuery() {
        // Version 1: question has createdAt field
        // Instant earliestDate = TimeHelper.parseInstant("2015-11-30T16:00:00.00Z");
        // return ofy().load().type(FeedbackQuestion.class)
        //         .filter("createdAt <=", earliestDate)
        //         .order("-createdAt");

        // Version 2: question does not have createdAt field
        return ofy().load().type(FeedbackQuestion.class)
                .filter("giverType =", FeedbackParticipantType.TEAMS);
    }

    @Override
    protected boolean isPreview() {
        return false;
    }

    @Override
    protected boolean isMigrationNeeded(FeedbackQuestion question) {
        // Version 1: question has createdAt field
        // return question.getGiverType() == FeedbackParticipantType.TEAMS;

        // Version 2: question does not have createdAt field
        try {
            Field createdAt = question.getClass().getDeclaredField("createdAt");
            createdAt.setAccessible(true);
            return createdAt.get(question) == null;
        } catch (ReflectiveOperationException e) {
            return true;
        }
    }

    @Override
    protected void migrateEntity(FeedbackQuestion question)
            throws EntityDoesNotExistException, InvalidParametersException, EntityAlreadyExistsException {
        System.out.println("Question created at " + question.getCreatedAt());

        List<StudentAttributes> students = studentsLogic.getStudentsForCourse(question.getCourseId());
        Map<String, String> studentEmailToTeam = new HashMap<>();
        for (StudentAttributes student : students) {
            studentEmailToTeam.put(student.getEmail(), student.getTeam());
        }
        List<FeedbackResponseAttributes> responses = frLogic.getFeedbackResponsesForQuestion(question.getId());
        List<FeedbackResponseAttributes> affectedResponses = responses.stream()
                .filter(r -> r.getGiver().contains("@"))
                .collect(Collectors.toList());
        List<String> teamsWithResponses = responses.stream()
                .map(r -> r.getGiver())
                .filter(giver -> !giver.contains("@"))
                .collect(Collectors.toList());
        System.out.printf("Affected responses: %s / %s%n", affectedResponses.size(), responses.size());

        for (FeedbackResponseAttributes response : affectedResponses) {
            String studentTeam = studentEmailToTeam.get(response.getGiver());
            if (studentTeam == null) {
                System.out.println("Student not found in course. Deleting the response.");
                frLogic.deleteFeedbackResponseCascade(response.getId());
            } else {
                if (teamsWithResponses.contains(studentTeam)) {
                    System.out.println("Duplicate response for team due to legacy bug. Deleting the duplicate response.");
                    frLogic.deleteFeedbackResponseCascade(response.getId());
                } else {
                    FeedbackResponseAttributes.UpdateOptions updateOptions =
                            FeedbackResponseAttributes.updateOptionsBuilder(response.getId())
                                    .withGiver(studentTeam)
                                    .build();
                    frLogic.updateFeedbackResponseCascade(updateOptions);
                    teamsWithResponses.add(studentTeam);
                }
            }
        }
    }

}
