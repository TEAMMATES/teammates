<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>
<c:set var="jsIncludes">    
    <script type="text/javascript" src="/js/instructorFeedbackResultsAjaxByQuestion.js"></script>
</c:set>

<results:resultsPage pageTitle="TEAMMATES - Feedback Session Results" bodyTitle="Session Results" jsIncludes="${jsIncludes}">
    <%-- Responses are displayed when only a single question is loaded, 
         otherwise it is loaded through ajax. See /js/instructorFeedbackResultsAjaxByQuestion.js --%>
    <results:byQuestionResults isShowingResponses="${!data.largeNumberOfRespondents}"
                               questionPanels="${data.questionPanels}" />    
</results:resultsPage>
