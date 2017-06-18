package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.api.Logic;

public class DataRepairForCorruptedResponses extends RemoteApiClient {

    private Logic logic = new Logic();
    String originalGiverSection = "";
    String originalRecipientSection = "";

    public static void main(String[] args) throws IOException {
        DataRepairForCorruptedResponses dataRepair = new DataRepairForCorruptedResponses();
        dataRepair.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        try {
            repairDataForSession("Course-ID", "Session name");
        } catch (EntityDoesNotExistException | InvalidParametersException | EntityAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    private void repairDataForSession(String courseId, String sessionName)
            throws EntityDoesNotExistException, InvalidParametersException, EntityAlreadyExistsException {
        List<FeedbackQuestionAttributes> questions = logic.getFeedbackQuestionsForSession(sessionName, courseId);
        for (FeedbackQuestionAttributes question : questions) {
            boolean needRepairGiverSection = isGiverContainingSection(question.giverType);
            boolean needRepairRecipientSection = isRecipientContaningSection(question.giverType, question.recipientType);
            if (needRepairGiverSection || needRepairRecipientSection) {
                repairResponsesForQuestion(question, needRepairGiverSection, needRepairRecipientSection);
            }
        }
    }

    private void repairResponsesForQuestion(FeedbackQuestionAttributes question, boolean needRepairGiverSection,
            boolean needRepairRecipientSection)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        List<FeedbackResponseAttributes> responses = logic.getFeedbackResponsesForQuestion(question.getId());
        responses.forEach((response) -> {
            boolean needUpdateResponse = false;

            if (needRepairGiverSection) {
                StudentAttributes student = logic.getStudentForEmail(question.courseId, response.giver);
                needUpdateResponse = responseNotEqualToStudentSection(response, student, needUpdateResponse);
            }
            if (needRepairRecipientSection) {
                needUpdateResponse = isTeamRecipient(question, response, needUpdateResponse);
            }
            needUpdateResponse(needUpdateResponse, response);
        });
    }

    private boolean isTeamRecipient(FeedbackQuestionAttributes question, FeedbackResponseAttributes response, boolean needUpdateResponse) {
        if (isTeamRecipient(question.recipientType)) {
            String recipientSection
                    = logic.getStudentsForTeam(response.recipient, question.courseId).get(0).section;
            return recipientSectionNotEqualToResponse(recipientSection, response, needUpdateResponse);

        }
        StudentAttributes student = logic.getStudentForEmail(question.courseId, response.recipient);
        return recipientSectionNotEqualToStudentSection(response, student, needUpdateResponse);

    }

    private void needUpdateResponse(boolean needUpdateResponse, FeedbackResponseAttributes response) {
        if (needUpdateResponse) {
            System.out.println("Repairing giver section:"
                    + originalGiverSection + "-->" + response.giverSection
                    + " receiver section:"
                    + originalRecipientSection + "-->" + response.recipientSection);
            logic.updateFeedbackResponse(response);
        }
    }

    private boolean recipientSectionNotEqualToStudentSection(FeedbackResponseAttributes response, StudentAttributes student, boolean needUpdateResponse) {
        if (!response.recipientSection.equals(student.section)) {
            originalRecipientSection = response.recipientSection;
            response.recipientSection = student.section;
            needUpdateResponse = true;
        }
        return needUpdateResponse;
    }

    private boolean recipientSectionNotEqualToResponse(String recipientSection, FeedbackResponseAttributes response, boolean needUpdateResponse) {
        if (!recipientSection.equals(response.recipientSection)) {
            originalRecipientSection = response.recipientSection;
            response.recipientSection = recipientSection;
            needUpdateResponse = true;
        }
        return needUpdateResponse;
    }

    private boolean responseNotEqualToStudentSection(FeedbackResponseAttributes response, StudentAttributes student, boolean needUpdateResponse) {
        if (!response.giverSection.equals(student.section)) {
            originalGiverSection = response.giverSection;
            response.giverSection = student.section;
            needUpdateResponse = true;
        }
        return needUpdateResponse;
    }

    private boolean isGiverContainingSection(FeedbackParticipantType giverType) {
        return giverType == FeedbackParticipantType.STUDENTS || giverType == FeedbackParticipantType.TEAMS;
    }

    private boolean isRecipientContaningSection(FeedbackParticipantType giverType, FeedbackParticipantType recipientType) {
        return recipientType == FeedbackParticipantType.SELF && isGiverContainingSection(giverType)
                || recipientType.isValidRecipient();
    }

    private boolean isTeamRecipient(FeedbackParticipantType recipientType) {
        return recipientType == FeedbackParticipantType.TEAMS || recipientType == FeedbackParticipantType.OWN_TEAM;
    }
}
