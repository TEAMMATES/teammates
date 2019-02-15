package teammates.ui.webapi.output;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackSessionResponseStatus;

/**
 * The API output format of a list of {@link FeedbackSessionResponseStatus}.
 */
public class FeedbackSessionStudentsResponseData extends ApiOutput {

    private List<FeedbackSessionStudentResponseData> studentsResponse;

    public FeedbackSessionStudentsResponseData(FeedbackSessionResponseStatus responseStatus) {
        studentsResponse = new ArrayList<>();

        Map<String, Boolean> allStudentsResponse = responseStatus.studentsWhoResponded.stream()
                .collect(Collectors.toMap(studentEmail -> studentEmail, response -> true));
        allStudentsResponse.putAll(responseStatus.studentsWhoDidNotRespond.stream()
                .collect(Collectors.toMap(studentEmail -> studentEmail, response -> false)));

        allStudentsResponse.forEach((studentEmail, response) -> {
            studentsResponse.add(new FeedbackSessionStudentResponseData(
                    studentEmail, responseStatus.emailNameTable.get(studentEmail),
                    responseStatus.emailSectionTable.get(studentEmail),
                    responseStatus.emailTeamNameTable.get(studentEmail), response));
        });
    }

    public List<FeedbackSessionStudentResponseData> getStudentsResponse() {
        return studentsResponse;
    }
}
