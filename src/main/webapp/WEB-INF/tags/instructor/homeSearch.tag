<%@ tag description="instructorHome - Student search bar" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<form method="get" action="${data.instructorSearchLink}" 
      name="search_form">
    <div class="input-group">
        <input type="text" id="searchbox"
               title="<%=Const.Tooltips.SEARCH_STUDENT%>"
               name="<%=Const.ParamsNames.SEARCH_KEY%>"
               class="form-control"
               data-toggle="tooltip"
               data-placement="top"
               placeholder="Student Name">
        <span class="input-group-btn">
            <button class="btn btn-default" type="submit" value="Search" id="buttonSearch">
                Search
            </button>
        </span> 
    </div>
    <input type="hidden" name="<%=Const.ParamsNames.SEARCH_STUDENTS%>" value="true">
    <input type="hidden" name="<%=Const.ParamsNames.SEARCH_COMMENTS_FOR_STUDENTS%>" value="false">
    <input type="hidden" name="<%=Const.ParamsNames.SEARCH_COMMENTS_FOR_RESPONSES%>" value="false">
    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
</form>