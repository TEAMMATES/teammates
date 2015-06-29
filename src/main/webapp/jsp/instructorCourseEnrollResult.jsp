<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/instructor.js"></script>
</c:set>

<c:set var="COURSE_ID">
    <%=Const.ParamsNames.COURSE_ID%>
</c:set>

<c:set var="STUDENTS_ENROLLMENT_INFO">
    <%=Const.ParamsNames.STUDENTS_ENROLLMENT_INFO%>
</c:set>

<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Enrollment Results for ${data.courseId}" jsIncludes="${jsIncludes}">
    <div class="alert alert-success">
        <form name='goBack' action="${data.instructorCourseEnrollLink}" method="post" role="form"> 
            Enrollment Successful. Summary given below. Click <a id="edit_enroll" href="javascript:document.forms['goBack'].submit()">here</a> to do further changes to the student list.
            <input type="hidden" name="${COURSE_ID}" value="${data.courseId}">
            <input type="hidden" name="${STUDENTS_ENROLLMENT_INFO}" value="${data.enrollStudents}">
        </form>
    </div>
    
    <c:forEach items="${data.students}" var="studentList" varStatus="i">
        <c:if test="${not empty studentList}">
            <c:choose>
                <c:when test="${i.index == 0}">
                    <c:set var="panelClass">panel panel-danger</c:set>
                </c:when>
                <c:when test="${i.index == 1}">
                    <c:set var="panelClass">panel panel-primary</c:set>
                </c:when>
                <c:when test="${i.index == 2}">
                    <c:set var="panelClass">panel panel-warning</c:set>
                </c:when>
                <c:when test="${i.index == 3}">
                    <c:set var="panelClass">panel panel-info</c:set>
                </c:when>
                <c:when test="${i.index == 4}">
                    <c:set var="panelClass">panel panel-default</c:set>
                </c:when>
                <c:otherwise>
                    <c:set var="panelClass">panel panel-danger</c:set>
                </c:otherwise>
            </c:choose>
            <div class="${panelClass}">
                <div class="panel-heading">
                    ${data.messageForEnrollmentStatus[i.index]}
                </div>
                <table class="table table-striped table-bordered">
                    <tr> 
                        <c:if test="${data.hasSection}">
                            <th>Section</th>
                        </c:if>
                        <th>Team</th>
                        <th>Student Name</th>
                        <th>E-mail address</th>
                        <th>Comments</th>
                    </tr>
                    <c:forEach items="${studentList}" var="student">
                        <tr>
                            <c:if test="${data.hasSection}">
                                <td>${student.section}</td>
                            </c:if>
                            <td>${student.team}</td>
                            <td>${student.name}</td>
                            <td>${student.email}</td>
                            <td>${student.comments}</td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
            <br>
            <br>
        </c:if>
    </c:forEach>
    <div id="instructorCourseEnrollmentButtons"></div>
</ti:instructorPage>