package teammates.client.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.logic.api.Logic;

/**
 * Generates the feedback report as a csv.
 */
public class GenerateFeedbackReport extends RemoteApiClient {

    public static void main(String[] args) throws IOException {
        GenerateFeedbackReport reportGenerator = new GenerateFeedbackReport();
        reportGenerator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Logic logic = new Logic();

        try {
            String fileContent =
                    logic.getFeedbackSessionResultSummaryAsCsv(
                            "CourseID", "Session Name", "instructor@email.com", true, true, null);
            writeToFile("result.csv", fileContent);
        } catch (EntityDoesNotExistException | ExceedingRangeException e) {
            e.printStackTrace();
        }

    }

    private void writeToFile(String fileName, String fileContent) {
        try {

            File file = new File(fileName);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()))) {
                bw.write(fileContent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
