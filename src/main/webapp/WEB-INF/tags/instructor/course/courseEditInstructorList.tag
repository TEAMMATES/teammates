<%@ tag description="instructorCourseEdit - List of Instructors" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>

<%@ attribute name="instructorPanelList" type="java.util.Collection" required="true" %>

<c:forEach items="${instructorPanelList}" var="instructorPanel">
    <div class="panel panel-primary">
        <course:courseEditInstructorListPanelHeading 
                index="${instructorPanel.index}" 
                resendInviteButton="${instructorPanel.resendInviteButton}"
                editButton="${instructorPanel.editButton}"
                deleteButton="${instructorPanel.deleteButton}" 
                instructor="${instructorPanel.instructor}" 
        />
        
        <course:courseEditInstructorListPanelBody 
                instructorPanel="${instructorPanel}" 
        />
    </div>
    <br>
    <br>
</c:forEach>