<%@ tag description="searchCommentFeedbackQuestion.tag - Feedback response"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/instructor/search" prefix="search"%>
<%@ attribute name="fsIndx" required="true" %>
<%@ attribute name="qnIndx" required="true" %>
<%@ attribute name="responseIndex" required="true" %>
<%@ attribute name="responseRow" type="teammates.ui.template.ResponseRow" required="true"%>

<tr>
    <td>
        <b>From:</b> ${responseRow.giverName}
        <b>To:</b> ${responseRow.recipientName}
    </td>
</tr>

<tr>
    <td>
        <strong>Response:</strong> ${responseRow.response}
    </td>
</tr>

<tr class="active">
    <td>Comment(s):</td>
</tr>

<tr>
    <td>
        <ul class="list-group comments" id="responseCommentTable-${fsIndx}-${qnIndx}-${responseIndex}"
            <c:if test="${responseRow.commentsEmpty}">style="display:none"</c:if>>
                    
            <c:set var="responseCommentIndex" value="${0}" />
            
            <c:forEach items="${responseRow.feedbackResponseCommentRows}" var="frcRow">
                <c:set var="responseCommentIndex" value="${responseCommentIndex + 1}" />
                <search:feedbackResponseComment qnIndx="${qnIndx}" responseIndex="${responseIndex}" 
                                                responseCommentIndex="${responseCommentIndex}" 
                                                feedbackResponseCommentRow="${frcRow}" fsIndx="${fsIndx}" />
            </c:forEach>  

        </ul>
    </td>
</tr>