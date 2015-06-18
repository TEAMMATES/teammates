<%@ tag description="instructorFeedbackResults - by question" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="java.util.Map"%>
<%@ tag import="java.util.List"%>
<%@ tag import="teammates.common.util.Const"%>
<%@ tag import="teammates.common.datatransfer.FeedbackParticipantType"%>
<%@ tag import="teammates.common.datatransfer.FeedbackSessionResponseStatus"%>
<%@ tag import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ tag import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ tag import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%@ tag import="teammates.common.datatransfer.FeedbackQuestionDetails"%>
<%@ tag import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="data" type="teammates.ui.controller.InstructorFeedbackResultsPageData" required="true" %>
<%@ attribute name="showAll" type="java.lang.Boolean" required="true" %>
<%@ attribute name="shouldCollapsed" type="java.lang.Boolean" required="true" %>

    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_TOP%>" />
    <br>
    <%
        int questionIndex = -1;
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries : data.bundle
                .getQuestionResponseMap().entrySet()) {
            questionIndex++;
            FeedbackQuestionAttributes question = responseEntries.getKey();
    %>
   
     
    <%
        }
    %>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BOTTOM%>" />
