package teammates.client.scripts;

import java.io.IOException;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.JsonUtils;
import teammates.logic.api.Logic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.test.driver.FileHelper;
import teammates.test.driver.TestProperties;

public class GenerateLargeScaledData extends RemoteApiClient {

    public static void main(String[] args) throws IOException {
        GenerateLargeScaledData dataGenerator = new GenerateLargeScaledData();
        dataGenerator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Logic logic = new Logic();
        DataBundle largeScaleBundle = loadDataBundle("/largeScaleTest.json");

        try {
            int index = 0;
            /*
            for (StudentAttributes student : largeScaleBundle.students.values()) {
                logic.createStudent(student);
                index++;
                if (index % 100 == 0) {
                    logger.info("Create student " + index);
                }
            }
            */

            for (FeedbackResponseAttributes response : largeScaleBundle.feedbackResponses.values()) {
                logic.createFeedbackResponse(injectRealIds(response));
                index++;
                if (index % 100 == 0) {
                    System.out.println("Create response " + index);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FeedbackResponseAttributes injectRealIds(FeedbackResponseAttributes response) {
        try {
            int qnNumber = Integer.parseInt(response.feedbackQuestionId);

            response.feedbackQuestionId =
                FeedbackQuestionsLogic.inst().getFeedbackQuestion(
                        response.feedbackSessionName, response.courseId,
                        qnNumber).getId();
        } catch (NumberFormatException e) {
            // Correct question ID was already attached to response.
        }

        return response;
    }

    private static DataBundle loadDataBundle(String pathToJsonFileParam) {
        try {
            String pathToJsonFile = (pathToJsonFileParam.startsWith("/") ? TestProperties.TEST_DATA_FOLDER : "")
                                  + pathToJsonFileParam;
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return JsonUtils.fromJson(jsonString, DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
