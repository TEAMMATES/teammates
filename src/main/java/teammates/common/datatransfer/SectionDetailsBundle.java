package teammates.common.datatransfer;

import java.util.ArrayList;

/**
 * Represents details of teams in a section.
 * <br> Contains:
 * <br> * The section name .
 * <br> * {@link TeamDetailsBundle} objects for all teams in the section.
 */
public class SectionDetailsBundle {

    public String name;
    public ArrayList<TeamDetailsBundle> teams = new ArrayList<TeamDetailsBundle>();

}
