package teammates.test.scenariobuilder;

import java.util.UUID;

import teammates.storage.entity.Section;
import teammates.storage.entity.Team;

/**
 * Builder for Team entities used in test scenarios.
 */
public final class GivenTeam {
    private GivenData given;
    private Team team;

    public GivenTeam(GivenData given, UUID teamId) {
        this.given = given;
        this.team = defaultTeam(teamId);
    }

    public Team build() {
        return team;
    }

    /**
     * Sets the name for the team.
     */
    public GivenTeam name(String name) {
        team.setName(name);
        return this;
    }

    /**
     * Sets the section for the team.
     */
    public GivenTeam section(String sectionAlias) {
        assert team.getSection() == null : "Section has already been set for this team";
        Section s = given.getOrCreate(sectionAlias, given.dataBundle.sections, given::section);
        s.addTeam(team);
        return this;
    }

    /**
     * Sets the course for the team.
     */
    public GivenTeam course(String courseAlias) {
        assert team.getSection() == null : "Section has already been set for this team";
        given.getOrCreate(courseAlias, given.dataBundle.courses, given::course);
        String sectionAlias = GivenSection.getDefaultAlias(courseAlias);
        Section s = given.getOrCreate(sectionAlias, given.dataBundle.sections, (String sAlias) -> {
            given.section(sAlias, sect -> sect.course(courseAlias));
        });
        s.addTeam(team);
        return this;
    }

    void ensureConsistent() {
        if (team.getSection() == null) {
            String courseAlias = GivenCourse.getDefaultAlias();
            String sectionAlias = GivenSection.getDefaultAlias(courseAlias);
            this.section(sectionAlias);
        }
    }

    /**
     * Generates a default alias for a team.
     */
    public static String getDefaultAlias(String courseAlias, String sectionAlias, String teamAlias) {
        return "default:" + courseAlias + ":" + sectionAlias + ":" + teamAlias;
    }

    private Team defaultTeam(UUID teamId) {
        Team t = new Team(teamId.toString());
        t.setId(teamId);
        return t;
    }
}
