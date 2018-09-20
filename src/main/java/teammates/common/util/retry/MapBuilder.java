package teammates.common.util.retry;


import java.util.HashMap;
import java.util.Map;

import teammates.common.util.Assumption;

public class MapBuilder {

    public static Map<String, String> getMapOfVariables(String... keyValuePairs) {
        Map<String, String> variables = new HashMap<>();
        Assumption.assertTrue("The number of elements in keyValuePairs passed in must be even",
                keyValuePairs.length % 2 == 0);
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            variables.put(keyValuePairs[i], keyValuePairs[i + 1]);
        }
        return variables;
    }

}
