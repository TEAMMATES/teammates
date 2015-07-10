<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>
<c:set var="jsIncludes">    
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackResults.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackResultsAjaxByQuestion.js"></script>
    <script type="text/javascript" src="/js/instructorFeedbackResultsAjaxResponseRate.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
</c:set>

<ti:instructorPage pageTitle="TEAMMATES - Feedback Session Results" bodyTitle="Session Results" jsIncludes="${jsIncludes}">
    <results:resultsPage>
        <results:byQuestionResults showAll="${data.bundle.complete}" shouldCollapsed="${data.shouldCollapsed}" data="${data}" />
    </results:resultsPage>    
</ti:instructorPage>
