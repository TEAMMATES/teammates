<%@ tag description="responseTable.tag - Responses given to a particular recipient" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared"%>
<%@ attribute name="response" type="teammates.ui.template.FeedbackResultsResponse" required="true" %>

<tr class="resultSubheader">
    <td>
        <span class="bold"><b>From:</b></span> ${response.giverName}
    </td>
</tr>

<tr>
    <%-- Note: When an element has class text-preserve-space, do not insert HTML spaces --%>
    <td class="text-preserve-space">${response.answer}</td>
</tr>
                                                            
<c:if test="${not empty response.comments}">
    <tr>
        <td>
            <ul class="list-group comment-list">                                                                           
                <c:forEach items="${response.comments}" var="comment">
                    <shared:feedbackResponseComment frc="${comment}" />
                </c:forEach>                                                                                   
            </ul>
        </td>
    </tr>                                                            
</c:if>
                                                            