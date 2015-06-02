package teammates.common.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import teammates.common.datatransfer.FeedbackQuestionAttributes;

public class FeedbackSessionTemplates {
    private static final Map<String, String> TEMPLATES = createSessionTemplatesMap();
    
    private static Map<String, String> createSessionTemplatesMap() {
        Map<String, String> templates = new HashMap<String, String>();
        templates.put("TEAMEVALUATION", "feedbackSessionTeamEvaluationTemplate.json");
        return templates;
    }
    
    //TODO: Consider adding instructions for the feedback session into template?
    //TODO: Or simply use static strings here?
    
    /** 
     * Get the list of questions for the specified feedback session template
     */
    public static List<FeedbackQuestionAttributes> getFeedbackSessionTemplateQuestions(
            String templateType, String courseId, String feedbackSessionName, String creatorEmail) {
        Assumption.assertNotNull(templateType);
        
        if (!TEMPLATES.containsKey(templateType)) {
            return new ArrayList<FeedbackQuestionAttributes>();
        }
        
        String jsonString = FileHelper.readResourseFile(TEMPLATES.get(templateType));
        
        List<FeedbackQuestionAttributes> questionAttributesList =
                new ArrayList<FeedbackQuestionAttributes>();
        
        //Replace placeholder
        jsonString = jsonString.replace("${courseId}", courseId);
        jsonString = jsonString.replace("${feedbackSessionName}", feedbackSessionName);
        jsonString = jsonString.replace("${creatorEmail}", creatorEmail);
        
        Gson gson = Utils.getTeammatesGson();
        Type listType = new TypeToken<ArrayList<FeedbackQuestionAttributes>>(){}.getType();
        questionAttributesList = gson.fromJson(jsonString, listType);
        
        return questionAttributesList;
    }
}
