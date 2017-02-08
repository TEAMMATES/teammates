package teammates.common.datatransfer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * The search result bundle for {@link CommentAttributes}.
 */
public class CommentSearchResultBundle extends SearchResultBundle {
    
    public Map<String, List<CommentAttributes>> giverCommentTable = new TreeMap<String, List<CommentAttributes>>();
    public Map<String, String> giverTable = new HashMap<String, String>();
    public Map<String, String> recipientTable = new HashMap<String, String>();
    
}
