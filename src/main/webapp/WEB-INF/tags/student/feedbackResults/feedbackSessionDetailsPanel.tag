<%@ tag description="studentFeedbackResults.jsp - Displays feedback session details" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackResults" prefix="feedbackResults" %>
<%@ attribute name="feedbackSession" type="teammates.common.datatransfer.FeedbackSessionAttributes" required="true" %>

<div class="well well-plain">
    <div class="panel-body">
        <div class="form-horizontal">
            <div class="panel-heading">         
                <!-- Course ID -->
                <feedbackResults:displayFeedbackSessionInfo>
                    <jsp:attribute name="heading">Course ID:</jsp:attribute>
                    <jsp:body>
                        <c:out value="${feedbackSession.courseId}"/>
                    </jsp:body>
                </feedbackResults:displayFeedbackSessionInfo>
                
                <!-- Session -->
                <feedbackResults:displayFeedbackSessionInfo>
                    <jsp:attribute name="heading">Session:</jsp:attribute>
                    <jsp:body>
                        <c:out value="${feedbackSession.feedbackSessionName}"/>
                    </jsp:body>
                </feedbackResults:displayFeedbackSessionInfo>
                
                <!-- Opening time -->
                <feedbackResults:displayFeedbackSessionInfo>
                    <jsp:attribute name="heading">Opening time:</jsp:attribute>
                    <jsp:body>
                        ${feedbackSession.startTime}
                    </jsp:body>
                </feedbackResults:displayFeedbackSessionInfo>
                
                <!-- Closing time -->
                <feedbackResults:displayFeedbackSessionInfo>
                    <jsp:attribute name="heading">Closing time:</jsp:attribute>
                    <jsp:body>
                        ${feedbackSession.endTime}
                    </jsp:body>
                </feedbackResults:displayFeedbackSessionInfo>
            </div>
        </div>
    </div>
</div>
<br>