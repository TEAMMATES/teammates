<%@ tag description="studentFeedbackResults.jsp - Displays feedback session details" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackResults" prefix="feedbackResults" %>
<%@ attribute name="feedbackSession" type="teammates.common.datatransfer.FeedbackSessionAttributes" required="true" %>

<div class="well well-plain">
    <div class="panel-body">
        <div class="form-horizontal">
            <div class="panel-heading">         
                <feedbackResults:displayFeedbackSessionInfo>
                    <jsp:attribute name="heading">Course ID:</jsp:attribute>
                    <jsp:body>
                        <c:out value="${feedbackSession.courseId}"/>
                    </jsp:body>
                </feedbackResults:displayFeedbackSessionInfo>
                
                <feedbackResults:displayFeedbackSessionInfo>
                    <jsp:attribute name="heading">Session:</jsp:attribute>
                    <jsp:body>
                        <c:out value="${feedbackSession.feedbackSessionName}"/>
                    </jsp:body>
                </feedbackResults:displayFeedbackSessionInfo>
                
                <feedbackResults:displayFeedbackSessionInfo>
                    <jsp:attribute name="heading">Opening time:</jsp:attribute>
                    <jsp:body>
                        ${feedbackSession.startTimeString}
                    </jsp:body>
                </feedbackResults:displayFeedbackSessionInfo>
                
                <feedbackResults:displayFeedbackSessionInfo>
                    <jsp:attribute name="heading">Closing time:</jsp:attribute>
                    <jsp:body>
                        ${feedbackSession.endTimeString}
                    </jsp:body>
                </feedbackResults:displayFeedbackSessionInfo>
            </div>
        </div>
    </div>
</div>
<br>