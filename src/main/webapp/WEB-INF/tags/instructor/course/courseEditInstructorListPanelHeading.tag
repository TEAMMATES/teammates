<%@ tag description="instructorCourseEdit - Panel Heading of Instructor List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ attribute name="index" required="true" %>
<%@ attribute name="resendInviteButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="editButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="deleteButton" type="teammates.ui.template.ElementTag" required="true" %>
<%@ attribute name="instructor" type="teammates.common.datatransfer.InstructorAttributes" required="true" %>
<%@ attribute name="googleId" required="true" %>

<div class="panel-heading">
    <strong>Instructor ${index}:</strong>
    <div class="pull-right">
        <div class="display-icon" style="display:inline;"></div>
        
        <c:if test="${not empty resendInviteButton}">
            <a  <c:forEach items="${resendInviteButton.attributes}" var="attribute">
                    ${attribute.key}="${attribute.value}"
                </c:forEach> >
                ${resendInviteButton.content}
            </a>
        </c:if>
        
        <form style="display:none;" id="edit-${index}" class="editForm" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE%>">
            <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="${instructor.courseId}">
            <input type="hidden" name="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>" value="${instructor.email}">
            <input type="hidden" name="<%=Const.ParamsNames.COURSE_EDIT_MAIN_INDEX%>" value="${index}">
            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${googleId}">
        </form>
    
        <a  <c:forEach items="${editButton.attributes}" var="attribute">
                ${attribute.key}="${attribute.value}"
            </c:forEach> >
            ${editButton.content}
        </a>
    
        <a  <c:forEach items="${deleteButton.attributes}" var="attribute">
                ${attribute.key}="${attribute.value}"
            </c:forEach> >
            ${deleteButton.content}
        </a>
    </div>
</div>