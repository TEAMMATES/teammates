<%@ tag description="instructorFeedbackResults - panel body inside participant panels" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="secondaryParticipantPanelBody" type="teammates.ui.template.InstructorFeedbackResultsSecondaryParticipantPanelBody" required="true" %>
<%@ attribute name="shouldCollapsed" type="java.lang.Boolean" required="true" %>
<%@ attribute name="showAll" type="java.lang.Boolean" required="true" %>
<%@ attribute name="primaryParticipantPanel" type="teammates.ui.template.InstructorFeedbackResultsGroupByParticipantPanel" required="true" %>
<%@ attribute name="secondaryParticipantIndex" type="java.lang.Integer" required="true" %>



<div class="row ${secondaryParticipantIndex == 0 ? '': 'border-top-gray'}">
    <%-- Side of the panel --%>
    <results:secondaryParticipantPanelSide primaryParticipantPanel="${primaryParticipantPanel}" 
                                           secondaryParticipantPanelBody="${secondaryParticipantPanelBody}" />
    
    <div class="col-md-10">
        <c:forEach var="responsePanel" items="${secondaryParticipantPanelBody.responsePanels}">
            <results:responsePanel responsePanel="${responsePanel}" showAll="${showAll}" shouldCollapsed="${shouldCollapse}"/>
        </c:forEach>
    </div>
</div>
