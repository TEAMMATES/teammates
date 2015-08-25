<%@ tag description="instructorCourseDetails - Course Information Board" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="courseDetails" type="teammates.common.datatransfer.CourseDetailsBundle" required="true" %>
<%@ attribute name="instructors" type="java.util.Collection" required="true" %>
<%@ attribute name="giveCommentButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="courseRemindButton" type="teammates.ui.template.ElementTag" required="true" %>

<div class="well well-plain" id="courseInformationHeader">
    <button type="button" title="Give a comment about all students in the course"
            ${giveCommentButton.attributesToString}>
        ${giveCommentButton.content}
    </button>
            
    <div class="form form-horizontal">
        <course:courseInformation courseDetails="${courseDetails}" instructors="${instructors}" />
        
        <c:if test="${courseDetails.stats.studentsTotal > 1}">
            <course:studentInformationButtons courseDetails="${courseDetails}" courseRemindButton="${courseRemindButton}"/>
        </c:if>
    </div>
</div>