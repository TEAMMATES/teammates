<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>
<c:set var="jsIncludes">    
    <script type="text/javascript" src="/js/instructorFeedbackResultsAjaxByQuestion.js"></script>
</c:set>

<results:resultsPage pageTitle="TEAMMATES - Feedback Session Results" bodyTitle="Session Results" jsIncludes="${jsIncludes}" data="${data}">
    <%-- Responses are displayed when a only single question is loaded, 
         otherwise it is loaded through ajax. See /js/instructorFeedbackResultsAjaxByQuestion.js --%>
    <results:byQuestionResults isShowingResponses="${data.bundle.complete && fn:length(data.questionPanels) == 1}" 
                               isPanelsCollapsed="${data.shouldCollapsed}" 
                               questionPanels="${data.questionPanels}" />    
</results:resultsPage>
