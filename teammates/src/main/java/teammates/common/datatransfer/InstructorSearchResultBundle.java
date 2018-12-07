package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;

/**
 * The search result bundle for {@link InstructorAttributes}.
 */
public class InstructorSearchResultBundle extends SearchResultBundle {

    public List<InstructorAttributes> instructorList = new ArrayList<>();

}
