package teammates.ui.template;

import java.util.HashMap;
import java.util.Map;

public class CourseTableLink {
    private Map<String, String> attributes;
    private String text;
    
    public CourseTableLink(String text, String... attributePairs) {
        this.text = text;
        this.attributes = new HashMap<String, String>();
        for (int i = 0; i < attributePairs.length; i += 2) {
            this.attributes.put(attributePairs[i], attributePairs[i + 1]);
        }
    }
    
    public String getText() {
        return text;
    }
    
    public Map<String, String> getAttributes() {
        return attributes;
    }
}