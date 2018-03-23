<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/instructorFeedbackResultsRQG.js"></script>
</c:set>

<results:resultsPage title="Session Results" jsIncludes="${jsIncludes}">
  <results:bySectionPanels isGroupedByQuestion="${true}" isShowingAll="${data.bundle.complete}" isGroupedByTeam="${data.groupedByTeam}" />
</results:resultsPage>
