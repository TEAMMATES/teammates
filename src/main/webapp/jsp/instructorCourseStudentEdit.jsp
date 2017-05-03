<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/courseStudentDetailsEdit" prefix="csde" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorCourses.js"></script>
    <script type="text/javascript" src="/js/instructorStudentEdit.js"></script>
</c:set>
<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Edit Student Details" jsIncludes="${jsIncludes}">
    <csde:studentInformationTable
        studentInfoTable="${data.studentInfoTable}"
        newEmail="${data.newEmail}"
        openOrPublishedEmailSentForTheCourse="${data.openOrPublishedEmailSentForTheCourse}" />
    <br><br>
</ti:instructorPage>