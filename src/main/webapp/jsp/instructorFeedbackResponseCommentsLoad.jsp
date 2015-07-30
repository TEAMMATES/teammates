<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="hidden number-of-pending-comments">${data.numberOfPendingComments}</div>
<c:choose>
    <c:when test="${empty data.questionCommentsMap}">
        <div id="no-comment-panel" style="">
            <br>
            <div class="panel panel-info">
                <ul class="list-group comments">
                    <li class="list-group-item list-group-item-warning">
                        You don't have any comment in this session.
                    </li>
                </ul>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <c:set var="fsIndex" value="${data.feedbackSessionIndex}" />
        <c:forEach items="${data.questionCommentsMap}" var="questionCommentsEntry" varStatus="responseEntriesStatus">
            <div class="panel panel-info">
                <div class="panel-heading">
                    <c:set var="question" value="${questionCommentsEntry.key}"/>
                    <b>Question ${question.questionNumber}</b>:
                    ${question.questionDetails.questionText}
                    ${question.questionAdditionalInfoHtml}
                </div>
                <table class="table">
                    <tbody>
                        <c:forEach items="${questionCommentsEntry.value}" var="response" varStatus="responseStatus">
                            <tr>
                                <td><b>From:</b> ${response.giverName} <b>To:</b> ${response.recipientName}</td>
                            </tr>
                            <tr>
                                <td><strong>Response: </strong>${response.answerHtml}</td>
                            </tr>
                            <tr class="active">
                                <td>Comment(s):
                                    <button type="button"
                                            class="btn btn-default btn-xs icon-button pull-right"
                                            id="button_add_comment-${fsIndex}-${responseEntriesStatus.count}-${responseStatus.count}"
                                            onclick="showResponseCommentAddForm(${fsIndex},${responseEntriesStatus.count},${responseStatus.count})"
                                            data-toggle="tooltip" data-placement="top"
                                            title="<%= Const.Tooltips.COMMENT_ADD %>"
                                            <c:if test="${not response.instructorAllowedToSubmit}">disabled="disabled"</c:if>>
                                        <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
                                    </button>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <ul class="list-group comments"
                                        id="responseCommentTable-${fsIndex}-${responseEntriesStatus.count}-${responseStatus.count}"
                                        <c:if test="${empty response.feedbackResponseComments}">style="display: none;"</c:if>>
                                        <c:forEach var="frc" items="${response.feedbackResponseComments}" varStatus="frcStatus">
                                            <shared:feedbackResponseComment frc="${frc}"
                                                                            firstIndex="${fsIndex}"
                                                                            secondIndex="${responseEntriesStatus.count}"
                                                                            thirdIndex="${responseStatus.count}"
                                                                            frcIndex="${frcStatus.count}" />
                                        </c:forEach>
                                        <shared:feedbackResponseCommentAdd frc="${response.feedbackResponseCommentAdd}"
                                                                           firstIndex="${fsIndex}"
                                                                           secondIndex="${responseEntriesStatus.count}"
                                                                           thirdIndex="${responseStatus.count}" />
                                    </ul>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>
