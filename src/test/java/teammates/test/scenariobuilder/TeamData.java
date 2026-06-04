package teammates.test.scenariobuilder;

import java.util.UUID;

import teammates.storage.entity.Section;
import teammates.storage.entity.Team;

public final class TeamData {
    private GivenData given;
    private Team team;

    public TeamData(GivenData given, UUID teamId) {
        this.given = given;
        this.team = defaultTeam(teamId);
    }

    public Team build() {
        return team;
    }

    public TeamData name(String name) {
        team.setName(name);
        return this;
    }

    public TeamData section(String sectionAlias) {
        assert team.getSection() == null : "Section has already been set for this team";
        Section s = given.getOrCreate(sectionAlias, given.dataBundle.sections, given::section);
        s.addTeam(team);
        return this;
    }

    public TeamData course(String courseAlias) {
        assert team.getSection() == null : "Section has already been set for this team";
        given.getOrCreate(courseAlias, given.dataBundle.courses, given::course);
        Section s = given.getOrCreate("default:" + courseAlias, given.dataBundle.sections, (String sectionAlias) -> {
            given.section(sectionAlias, sect -> sect.course(courseAlias));
        });
        s.addTeam(team);
        return this;
    }

    void ensureConsistent() {
        if (team.getSection() == null) {
            this.section("default");
        }
    }

    private Team defaultTeam(UUID teamId) {
        Team t = new Team(teamId.toString());
        t.setId(teamId);
        return t;
    }
}
