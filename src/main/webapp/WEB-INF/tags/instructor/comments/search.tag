<%@ tag description="Instructor Comments Search" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/comments" prefix="comments" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="displayArchive" required="true" %>
<%@ attribute name="instructorCommentsLink" required="true" %>
<%@ attribute name="instructorSearchLink" required="true" %>
<%@ attribute name="commentsForStudentsTables" type="java.util.Collection" required="true" %>
<%@ attribute name="feedbackSessions" type="java.util.Collection" required="true" %>
<div class="well well-plain">
    <div class="row">
        <div class="col-md-12">
			<form method="get" action="${instructorSearchLink}" name="search_form">
			    <div class="input-group">
			        <input type="text" name="<%= Const.ParamsNames.SEARCH_KEY %>"
			            title="Search for comment"
			            class="form-control"
			            placeholder="Any info related to comments"
			            id="searchBox"> 
			        <span class="input-group-btn">
			            <button class="btn btn-default"
			                type="submit" value="Search"
			                id="buttonSearch">
			                Search
			            </button>
			        </span>
			    </div>
			    <input type="hidden" name="<%= Const.ParamsNames.SEARCH_COMMENTS_FOR_STUDENTS %>" value="true">
			    <input type="hidden" name="<%= Const.ParamsNames.SEARCH_COMMENTS_FOR_RESPONSES %>" value="true">
			    <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
			</form>
			<comments:filter displayArchive="${displayArchive}" instructorCommentsLink="${instructorCommentsLink}" commentsForStudentsTables="${commentsForStudentsTables}" feedbackSessions="${feedbackSessions}"/>
		</div>
	</div>
</div>