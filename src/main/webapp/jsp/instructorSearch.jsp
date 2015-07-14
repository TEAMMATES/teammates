<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/search" prefix="search" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>

<c:set var="jsIncludes">
    <link href="&#x2f;stylesheets&#x2f;omniComment.css" rel="stylesheet"/>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorSearch.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
</c:set>

<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Search" jsIncludes="${jsIncludes}">

    <search:searchPageInput />
    <br>
    <t:statusMessage />
    
    <c:if test="${not data.commentsForStudentsEmpty}">
        <shared:commentsPanel commentsForStudentsTables="${data.searchCommentsForStudentsTables}" />
    </c:if>
    
    <c:if test="${not data.commentsForResponsesEmpty}">
        <search:commentsForResponsesSearchResults commentsForResponsesTables="${data.searchCommentsForResponsesTables}">
            Comments for responses
        </search:commentsForResponsesSearchResults>
    </c:if>
    
    <c:if test="${not data.studentsEmpty}">
        <search:studentsSearchResults searchStudentsTables="${data.searchStudentsTables}">
            Students
        </search:studentsSearchResults>
    </c:if>
    
</ti:instructorPage>