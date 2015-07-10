package teammates.common.util;

public class LocalLibrariesFilePathHelper {
    private String jQueryFilePath;
    private String jQueryUiFilePath;
    
    public LocalLibrariesFilePathHelper() {
        boolean isDevEnvironment = Boolean.parseBoolean(System.getProperty("isDevEnvironment"));
        
        if (isDevEnvironment) {
            jQueryFilePath = "<script type=\"text/javascript\" src=\"/js/lib/jquery-1.11.3.min.js\"></script>";
            jQueryUiFilePath = "<script type=\"text/javascript\" src=\"/js/lib/jquery-ui-1.11.4.min.js\"></script>";
        } else {
            jQueryFilePath = "<script type=\"text/javascript\" src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>";
            jQueryUiFilePath = "<script type=\"text/javascript\" src=\"//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js\"></script>";
        }
    }

    public String getJQueryFilePath() {
        return jQueryFilePath;
    }

    public String getJQueryUiFilePath() {
        return jQueryUiFilePath;
    }
}
