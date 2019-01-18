<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="<%= FrontEndLibrary.TINYMCE %>"></script>
  <script type="text/javascript" src="/js/instructorFeedbackResultsGQR.js"></script>
</c:set>

<results:resultsPage title="Session Results" jsIncludes="${jsIncludes}">
  <results:bySectionPanels isGroupedByQuestion="${true}" isGroupedByTeam="${data.groupedByTeam}"
      isShowingAll="${data.bundle.complete}" />
</results:resultsPage>
