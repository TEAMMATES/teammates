package teammates.ui.template;

import java.util.List;

public class CoursePagination {
    private String previousPageLink;
    private String nextPageLink;
    private List<String> coursePaginationList;
    private String activeCourse;
    private String activeCourseClass;
    private String userCommentsLink;
    
    public CoursePagination(final String previousPageLink, final String nextPageLink, final List<String> coursePaginationList, 
                            final String activeCourse, final String activeCourseClass, final String userCommentsLink) {
        this.previousPageLink = previousPageLink;
        this.nextPageLink = nextPageLink;
        this.coursePaginationList = coursePaginationList;
        this.activeCourse = activeCourse;
        this.activeCourseClass = activeCourseClass;
        this.userCommentsLink = userCommentsLink;
    }
    
    public String getPreviousPageLink() {
        return previousPageLink;
    }

    public String getNextPageLink() {
        return nextPageLink;
    }

    public List<String> getCoursePaginationList() {
        return coursePaginationList;
    }

    public String getActiveCourse() {
        return activeCourse;
    }
    
    public String getActiveCourseClass() {
        return activeCourseClass;
    }

    public String getUserCommentsLink() {
        return userCommentsLink;
    }
}
