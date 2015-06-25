<%@ tag description="questionWithResponses.tag - Responses given to a particular recipient" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackResults" prefix="feedbackResults" %>
<%@ attribute name="responseTable" type="teammates.ui.template.FeedbackResultsResponseTable" required="true" %>

<c:choose>
    <c:when test="${responseTable.giverNameYou}">
        <c:set value="panel-info" var="panelHeaderClass"/>
    </c:when>
    <c:otherwise>
        <c:set value="panel-primary" var="panelHeaderClass"/>
    </c:otherwise>
</c:choose>

<div class="panel ${panelHeaderClass}">
    <div class="panel-heading"><b>To:</b> ${responseTable.recipientName}</div>
    <table class="table">
        <tbody>
            <c:forEach items="${responseTable.responses}" var="response">
                <feedbackResults:response response="${response}"/>
            </c:forEach>                                                        
        </tbody>
    </table>
</div>