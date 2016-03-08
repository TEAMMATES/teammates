package teammates.common.util;

import java.text.DateFormat;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const.SystemParams;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {

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
     * Json file and also reformat the Json string in pretty-print format. <br>
     * Technique found in <a href=
     * "http://code.google.com/p/google-gson/source/browse/trunk/gson/src/test/java/com/google/gson/functional/DefaultTypeAdaptersTest.java?spec=svn327&r=327"
     * >here </a>
     */
    public static Gson getTeammatesGson() {
        return new GsonBuilder()
                .setDateFormat(DateFormat.FULL)
                .setDateFormat(SystemParams.DEFAULT_DATE_TIME_FORMAT)
                .setPrettyPrinting()
                .create();
    }
    
    /**
     * This method returns a string representing a list of participants separated by delimiters.
     * 
     * @param participants
     *            a list of 'FeedbackParticipantType' objects
     * @param delimiter
     *            a delimiter to separate each participant (e.g. a comma sign ",")
     * @return a string representing all participants separated by 'delimiter'
     */
    public static String joinParticipantTypes(List<FeedbackParticipantType> participants, String delimiter) {
        String result = "";
        
        if (!participants.isEmpty()) {
            for (FeedbackParticipantType fpt : participants) {
                result += fpt + delimiter;
            }
            result = result.substring(0, result.length() - delimiter.length());
        }
        
        return result;
    }
    
}
