package teammates.common.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import teammates.common.datatransfer.FeedbackQuestionAttributes;

public class FeedbackSessionTemplates {
    public static String FEEDBACK_SESSION_TEAMEVALUATION = FileHelper.readResourseFile("feedbackSessionTeamEvaluationTemplate.json");

    
    //TODO: Consider adding instructions for the feedback session into template?
    //TODO: Or simply use static strings here?
    
    /** 
     * Get the list of questions for the specified feedback session template
     */
    public static List<FeedbackQuestionAttributes> getFeedbackSessionTemplateQuestions(String template, String courseId, String feedbackSessionName, String creatorEmail) {
        String jsonString = template;
        List<FeedbackQuestionAttributes> questionAttributesList = new ArrayList<FeedbackQuestionAttributes>();
        
        //Replace placeholder
        jsonString = jsonString.replace("${courseId}", courseId);
        jsonString = jsonString.replace("${feedbackSessionName}", feedbackSessionName);
        jsonString = jsonString.replace("${creatorEmail}", creatorEmail);
        
        Gson gson = Utils.getTeammatesGson();
        Type listType = new TypeToken<ArrayList<FeedbackQuestionAttributes>>() {}.getType();
        questionAttributesList = gson.fromJson(jsonString, listType);
        
        return questionAttributesList;
    }
}
