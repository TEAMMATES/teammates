package teammates.common.util;

import java.text.DateFormat;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const.SystemParams;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Utils {

    private Utils() {
        // utility class
    }
    
    /** 
     * This method should be used when instantiating loggers within the system.
     * @return A {@link Logger} class configured with the name of the calling class.
     */
    public static Logger getLogger() {
        StackTraceElement logRequester = Thread.currentThread().getStackTrace()[2];
        return Logger.getLogger(logRequester.getClassName());
    }

    /**
     * This creates a Gson object that can handle the Date format we use in the
     * Json file and also reformat the Json string in pretty-print format.
     */
    public static Gson getTeammatesGson() {
        return new GsonBuilder()
                .setDateFormat(DateFormat.FULL)
                .setDateFormat(SystemParams.DEFAULT_DATE_TIME_FORMAT)
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }
    
    /**
     * Convert a list of {@link FeedbackParticipantType} to a string where each element will be separated by
     * a delimiter. 
     */
    public static String joinParticipantTypes(List<FeedbackParticipantType> participants, String delimiter) {
        String result = "";
        if (!participants.isEmpty()) {
            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0, limit = participants.size() - 1 ; i < limit; i++) {
                strBuilder.append(participants.get(i)).append(delimiter);
            }
            // Then add the last one
            strBuilder.append(participants.size()-1);
            
            result = strBuilder.toString();
        }
        
        return result;
    }

}
