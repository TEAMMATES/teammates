<%@ tag description="displayDetails.tag - Displays teammates list on student course details page" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:choose>
    <c:when test="${(empty data.studentCourseDetailsPanel.teammates) 
                         or (fn:length(data.studentCourseDetailsPanel.teammates) eq 1)}">
        <span style="font-style: italic;">
            You have no team members or you are not registered in any team
        </span>
    </c:when>
                        
    <c:otherwise>
        <c:forEach items="${data.studentCourseDetailsPanel.teammates}" var="student">
            <c:if test="${not (student.email eq data.studentCourseDetailsPanel.studentEmail)}">
                <a href="mailto:${student.email}">
                    <c:out value="${student.name}" />
                </a>
                <br>
            </c:if>
        </c:forEach>
    </c:otherwise>
</c:choose>