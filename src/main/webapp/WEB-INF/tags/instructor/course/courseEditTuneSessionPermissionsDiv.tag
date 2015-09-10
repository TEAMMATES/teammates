<%@ tag description="instructorCourseEdit - Panel Heading of Instructor List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>

<%@ attribute name="instructorIndex" required="true" %>
<%@ attribute name="sectionIndex" required="true" %>
<%@ attribute name="sectionRow" type="teammates.ui.template.CourseEditSectionRow" required="true" %>

<div id="tuneSessionPermissionsDiv${sectionIndex}ForInstructor${instructorIndex}" class="row"
        <c:if test="${not sectionRow.sessionsInSectionSpecial}">
            style="display: none;"
        </c:if> >
    <input type="hidden" name="is<%=Const.ParamsNames.INSTRUCTOR_SECTION_GROUP%>${sectionIndex}sessionsset" value="${sectionRow.sessionsInSectionSpecial}"/>
    
    <table class="table table-striped">
        <thead>
            <tr>
                <td>SessionName</td>
                <td>Submit Responses and Add Comments</td>
                <td>View Responses and Comments</td>
                <td>Edit/Delete Responses/Comments by Others</td>
            </tr>
        </thead>
        <tbody>
            <c:if test="${empty sectionRow.feedbackSessions}">
                <tr>
                    <td colspan="4" class="text-center text-bold">No sessions in this course for you to configure</td>
                </tr>
            </c:if>
            
            <c:forEach items="${sectionRow.feedbackSessions}" var="feedbackSession">
                <tr>
                    <td>${feedbackSession.feedbackSessionName}</td>
                    <c:forEach items="${feedbackSession.permissionCheckBoxes}" var="checkbox">
                        <td class="align-center">
                            <input ${checkbox.attributesToString} />
                        </td>
                    </c:forEach>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>