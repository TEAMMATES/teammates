package teammates.common.util;

public class LocalLibrariesFilePathHelper {
    private String jQueryFilePath;
    private String jQueryUiFilePath;
    
    public LocalLibrariesFilePathHelper() {
        boolean isDevEnvironment = Boolean.parseBoolean(System.getProperty("isDevEnvironment"));
        
        if (isDevEnvironment) {
            jQueryFilePath = "/js/lib/jquery-1.11.3.min.js";
            jQueryUiFilePath = "/js/lib/jquery-ui-1.11.4.min.js";
        } else {
            jQueryFilePath = "https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js";
            jQueryUiFilePath = "//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js";
        }
    }

    public String getJQueryFilePath() {
        return jQueryFilePath;
    }

    public String getJQueryUiFilePath() {
        return jQueryUiFilePath;
    }
}
