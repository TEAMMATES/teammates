package teammates.ui.template;

public class StudentRow {
    private String viewPhotoLink;
    private String section;
    private String team;
    private String name;
    private String email;
    private String actions;

    public StudentRow(String viewPhotoLink, String section, String team, 
                                    String name, String email, String actions) {
        this.viewPhotoLink = viewPhotoLink;
        this.section = section;
        this.team = team;
        this.name = name;
        this.email = email;
        this.actions = actions;
    }
    
    public String getViewPhotoLink() {
        return viewPhotoLink;
    }
    
    public String getSection() {
        return section;
    }
    
    public String getTeam() {
        return team;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getActions() {
        return actions;
    }
}
