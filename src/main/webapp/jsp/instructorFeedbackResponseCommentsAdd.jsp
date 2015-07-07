<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/comments" prefix="comments" %>
<li class="list-group-item list-group-item-warning"
    id="responseCommentRow-<%= data.commentId %>">
    <div id="commentBar-<%= data.commentId %>">
        <span class="text-muted">
            From: <%= data.comment.giverEmail %> [<%= data.comment.createdAt %>]
        </span>
        <form class="responseCommentDeleteForm pull-right">
            <a href="/page/instructorFeedbackResponseCommentDelete"
               type="button"
               id="commentdelete-<%= data.comment.getId() %>"
               class="btn btn-default btn-xs icon-button"
               data-toggle="tooltip"
               data-placement="top"
               title="Delete this comment">
                <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
            </a>
            <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="<%= data.comment.feedbackResponseId %>">
            <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="<%= data.comment.getId() %>">
            <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="<%= data.comment.courseId %>">
            <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%= data.comment.feedbackSessionName %>">
            <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="<%= data.account.googleId %>">
        </form>
        <a type="button"
           id="commentedit-<%= data.commentId %>"
           class="btn btn-default btn-xs icon-button pull-right"
           onclick="showResponseCommentEditForm(<%= data.commentId.replaceAll("-", ",") %>)"
           data-toggle="tooltip"
           data-placement="top"
           title="Edit this comment">
            <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
        </a>
    </div>
    <!-- do not add whitespace between the opening and closing tags -->
    <div id="plainCommentText-<%= data.commentId %>"><%= data.comment.commentText.getValue() %></div>
    <comments:visibilityOptions commentRow="${commentRow}" commentIdx="${commentIdx}" courseId="${courseId}"/>
</li>