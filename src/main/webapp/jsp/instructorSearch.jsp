<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/search" prefix="search" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorSearch.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
</c:set>

<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="Search" jsIncludes="${jsIncludes}">

    <search:searchPageInput />
    <t:statusMessage />
    
    <c:if test="${data.commentSearchResultBundle.resultSize != 0}">
        <search:commentsForStudentsSearchResults>
            Comments for students
        </search:commentsForStudentsSearchResults>
    </c:if>
    
    <c:if test="${data.feedbackResponseCommentSearchResultBundle.resultSize != 0}">
        <search:commentsForResponsesSearchResults>
            Comments for responses
        </search:commentsForResponsesSearchResults>
    </c:if>
    
    <c:if test="${data.studentSearchResultBundle.resultSize != 0}">
        <search:studentsSearchResults>
            Students
        </search:studentsSearchResults>
    </c:if>
    
</ti:instructorPage>
