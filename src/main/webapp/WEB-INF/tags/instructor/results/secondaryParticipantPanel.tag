<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbackResults - panel body inside participant panels" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="secondaryParticipantPanelBody" type="teammates.ui.template.InstructorFeedbackResultsSecondaryParticipantPanelBody" required="true" %>
<%@ attribute name="primaryParticipantPanel" type="teammates.ui.template.InstructorFeedbackResultsGroupByParticipantPanel" required="true" %>
<%@ attribute name="secondaryParticipantIndex" type="java.lang.Integer" required="true" %>

<div class="row ${secondaryParticipantIndex == 0 ? '' : 'border-top-gray'}">
  <%-- Left side of the panel --%>
  <results:secondaryParticipantPanelSide primaryParticipantPanel="${primaryParticipantPanel}"
      secondaryParticipantPanelBody="${secondaryParticipantPanelBody}" />

  <div class="col-md-10">
    <c:forEach var="responsePanel" items="${secondaryParticipantPanelBody.responsePanels}">
      <results:responsePanel responsePanel="${responsePanel}"/>
    </c:forEach>
  </div>
</div>
