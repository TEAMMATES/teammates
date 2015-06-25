<%@ tag description="studentHome - Course table" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="sessionRows" type="java.util.Collection" required="true" %>
<table class="table-responsive table table-striped">
    <c:choose>
        <c:when test="${not empty sessionRows}">
            <thead>
                <tr>
                    <th>Session Name</th>
                    <th>Deadline</th>
                    <th>Status</th>
                    <th class="studentHomeActions">Action(s)</th>
                </tr>
            </thead>
            <c:forEach items="${sessionRows}" var="sessionRow">
                <tr class="home_evaluations_row" id="evaluation${sessionRow.index}">
                    <td>
                        ${sessionRow.name}
                    </td>
                    
                    <td>
                        ${sessionRow.endTime}
                    </td>
                    
                    <td>
                        <span data-toggle="tooltip" data-placement="top" 
                              title="${sessionRow.tooltip}">
                            ${sessionRow.status}
                        </span>
                    </td>
                    
                    <td class="studentHomeActions">
                        <c:set var="actions" value="${sessionRow.actions}" />
                        <a class="btn btn-default btn-xs btn-tm-actions<c:if test="${not actions.sessionPublished}"> disabled</c:if>"
                           <c:if test="${not actions.sessionPublished}">onclick="return false"</c:if>
                           href="${actions.studentFeedbackResultsLink}"
                           name="viewFeedbackResults${actions.index}"
                           id="viewFeedbackResults${actions.index}"
                           data-toggle="tooltip"
                           data-placement="top"
                           title="<%= Const.Tooltips.FEEDBACK_SESSION_RESULTS %>"
                           role="button">
                            View Responses
                        </a>
                        <c:choose>
                            <c:when test="${actions.hasSubmitted}">
                                <a class="btn btn-default btn-xs btn-tm-actions"
                                   href="${actions.studentFeedbackResponseEditLink}"
                                   name="editFeedbackResponses${actions.index}"
                                   id="editFeedbackResponses${actions.index}"
                                   data-toggle="tooltip"
                                   data-placement="top"
                                   title="${actions.tooltipText}"
                                   role="button">
                                    ${actions.buttonText}
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a class="btn btn-default btn-xs btn-tm-actions<c:if test="${not actions.sessionVisible}"> disabled</c:if>"
                                   <c:if test="${not actions.sessionVisible}">onclick="return false"</c:if>
                                   href="${actions.studentFeedbackResponseEditLink}"
                                   id="submitFeedback${actions.index}"
                                   data-toggle="tooltip"
                                   data-placement="top"
                                   title="${actions.tooltipText}"
                                   role="button">
                                    ${actions.buttonText}
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr>
                <th class="align-center bold color_white">
                    Currently, there are no open evaluation/feedback sessions in this course. When a session is open for submission you will be notified.
                </th>
            </tr>
        </c:otherwise>
    </c:choose>
</table>