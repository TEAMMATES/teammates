<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorCourses.js"></script>
</c:set>

<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Add New Course" jsIncludes="${jsIncludes}">
	<ti:courseAddCoursePanel courseIdToShow="${data.courseIdToShow}" courseNameToShow="${data.courseNameToShow}" googleId="${data.account.googleId}"/>
	<ti:courseStatusMessage/>
	
	<ti:courseActiveCoursesTable title="Active courses"/>
	<ti:courseArchivedCoursesTable title="Archived courses"/>
</ti:instructorPage>