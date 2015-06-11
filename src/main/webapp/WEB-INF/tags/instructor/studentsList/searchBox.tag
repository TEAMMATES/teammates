<%@ tag description="instructorStudentList - Student search box" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="data" type="teammates.ui.template.InstructorStudentsListSearchBox" required="true" %>
<c:set var="TOOLTIP_SEARCH_STUDENT" value="<%= Const.Tooltips.SEARCH_STUDENT %>" />
<c:set var="SEARCH_KEY" value="<%= Const.ParamsNames.SEARCH_KEY %>" />
<c:set var="SEARCH_STUDENTS" value="<%= Const.ParamsNames.SEARCH_STUDENTS %>" />
<c:set var="SEARCH_COMMENTS_FOR_STUDENTS" value="<%= Const.ParamsNames.SEARCH_COMMENTS_FOR_STUDENTS %>" />
<c:set var="SEARCH_COMMENTS_FOR_RESPONSES" value="<%= Const.ParamsNames.SEARCH_COMMENTS_FOR_RESPONSES %>" />
<c:set var="USER_ID" value="<%= Const.ParamsNames.USER_ID %>" />
<div class="well well-plain">
    <div class="row">
        <div class="col-md-12">
            <form method="get" action="${data.instructorSearchLink}" name="search_form">
                <div class="row">
                    <div class="col-md-10">
                        <div class="form-group">
                            <input type="text" id="searchbox"
                                   title="${TOOLTIP_SEARCH_STUDENT}"
                                   name="${SEARCH_KEY}"
                                   class="form-control"
                                   data-toggle="tooltip"
                                   data-placement="top"
                                   placeholder="e.g. Charles Shultz"
                                   value="${data.searchKey}">
                        </div>
                    </div>
                    <div class="col-md-2 nav">
                        <div class="form-group">
                            <button id="buttonSearch" class="btn btn-primary" type="submit" value="Search">
                                <span class="glyphicon glyphicon-search"></span> Find students
                            </button>
                        </div>
                    </div>
                </div>
                <input type="hidden" name="${SEARCH_STUDENTS}" value="true">
                <input type="hidden" name="${SEARCH_COMMENTS_FOR_STUDENTS}" value="false">
                <input type="hidden" name="${SEARCH_COMMENTS_FOR_RESPONSES}" value="false">
                <input type="hidden" name="${USER_ID}" value="${data.googleId}">
            </form>
        </div>
    </div>
</div>