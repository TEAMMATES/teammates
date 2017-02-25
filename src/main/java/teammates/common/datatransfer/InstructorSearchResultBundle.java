package teammates.common.datatransfer;

import teammates.common.datatransfer.attributes.InstructorAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * The search result bundle for {@link InstructorAttributes}.
 */
public class InstructorSearchResultBundle extends SearchResultBundle {

    public List<InstructorAttributes> instructorList = new ArrayList<InstructorAttributes>();

}
