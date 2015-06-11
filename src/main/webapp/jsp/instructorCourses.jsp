<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorCourses.js"></script>
</c:set>

<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Add New Course" jsIncludes="${jsIncludes}">
    <course:addCoursePanel courseIdToShow="${data.courseIdToShow}" courseNameToShow="${data.courseNameToShow}" googleId="${data.account.googleId}"/>
    <course:statusMessage/>
    
    <course:activeCoursesTable />
    <br>
    <br>
    <c:if test="${empty data.activeCourses.rows}">
        No records found. <br>
        <br>
    </c:if>
    <br>
    <br>
    
    <c:if test="${not empty data.archivedCourses.rows}">
        <course:archivedCoursesTable />
        <br>
        <br>
        <br>
        <br>
    </c:if>
</ti:instructorPage>