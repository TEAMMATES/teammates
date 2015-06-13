<%@ tag description="instructorSearch.jsp - Instructor search page input"%>
<%@ tag import="teammates.common.util.Const"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div>
    <form method="get" action="${data.instructorSearchLink}" name="search_form">       
        <div class="well well-plain">
        
            <div class="form-group">
                <div class="input-group">
                    <input type="text" name="searchkey"
                           value="${data.searchKey}"
                           title="Search for comment"
                           placeholder="Your search keyword"
                           class="form-control" id="searchBox"> 
                           
                    <span class="input-group-btn">
                        <button class="btn btn-primary" type="submit"
                                value="Search" id="buttonSearch">
                            Search
                        </button>
                    </span>
                </div>
                
                <input type="hidden" name="user" value="${data.account.googleId}">
            </div>
            
            <div class="form-group">
                <ul class="list-inline">
                    <li>
                        <span data-toggle="tooltip" title="Tick the checkboxes to limit your search to certain categories"
                              class="glyphicon glyphicon-info-sign">
                        </span>
                    </li>
                    <li>
                        <input id="comments-for-student-check" type="checkbox"
                               name="<%=Const.ParamsNames.SEARCH_COMMENTS_FOR_STUDENTS%>" value="true"
                               <c:if test="${data.searchCommentForStudents}">checked</c:if>>
                        <label for="comments-for-student-check">
                            Comments for students
                        </label>
                    </li>
                    <li>
                        <input id="comments-for-responses-check" type="checkbox"
                               name="<%=Const.ParamsNames.SEARCH_COMMENTS_FOR_RESPONSES%>" value="true"
                               <c:if test="${data.searchCommentForResponses}">checked</c:if>>
                        <label for="comments-for-responses-check">
                            Comments for responses
                        </label>
                    </li>
                    <li>
                        <input id="students-check" type="checkbox" 
                               name="<%=Const.ParamsNames.SEARCH_STUDENTS%>" value="true"
                               <c:if test="${data.searchForStudents || (!data.searchCommentForStudents 
                                             && !data.searchCommentForResponses)}">checked</c:if>>
                        <label for="students-check">
                            Students
                        </label>
                    </li>
                </ul>
            </div>
        </div>
    </form>
</div>