package teammates.performance.scripts.create.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * Base class to create the CSV config data for Student Profile performance test.
 */
public final class CreateStudentProfileTestConfigData extends CreateTestConfigData {

    public CreateStudentProfileTestConfigData() {
        pathToCsvResultFile = "/studentProfileConfig.csv";
        pathToJsonInputFile = "/studentProfile.json";
    }

    @Override
    protected List<String> getCsvHeaders() {
        List<String> headers = new ArrayList<>();

        headers.add("email");
        headers.add("isAdmin");
        headers.add("googleid");

        return headers;
    }

    @Override
    protected List<List<String>> getCsvData() throws IOException, ParseException {
        JSONObject jsonObject = getJsonObjectFromFile();

        JSONObject studentsJson = (JSONObject) jsonObject.get("students");
        List<List<String>> csvData = new ArrayList<>();

        for (Object e : studentsJson.entrySet()) {
            Map.Entry entry = (Map.Entry) e;
            JSONObject studentJson = (JSONObject) entry.getValue();

            List<String> csvRow = new ArrayList<>();

            csvRow.add((String) studentJson.get("email"));
            csvRow.add("no");
            csvRow.add((String) studentJson.get("googleId"));

            csvData.add(csvRow);
        }

        return csvData;
    }

}
