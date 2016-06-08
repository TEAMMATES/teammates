<%@ tag description="instructorCourseDetails - Rename Team Modal" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="courseId" required="true" %>
<%@ attribute name="teamNames" type="java.util.Collection" required="true" %>


<div class="modal fade" id="rename-team-modal" tabindex="-1" role="dialog" aria-labelledby="rename-team-modal-label">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form action="<%= Const.ActionURIs.INSTRUCTOR_COURSE_RENAME_TEAM %>" class="form-horizontal">
                <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${courseId}">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="rename-team-modal-Label">Rename a Team</h4>
                </div>
                <div class="modal-body">
                    
                    <div class="form-group">
                        <label for="team-select" class="col-sm-3 control-label">Team to Rename</label>
                        <div class="col-sm-9">
                            <select id="team-select" class="form-control" name="<%= Const.ParamsNames.TEAM_TO_EDIT %>">
                                <c:forEach items="${teamNames}" var="teamName">
                                    <option value="${teamName}">${teamName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="new-team-name-input" class="col-sm-3 control-label">New Team Name</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control" id="new-team-name-input" placeholder="New Team Name" name="<%= Const.ParamsNames.TEAM_NAME %>">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary">Save</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                </div>
            </form>
        </div>
    </div>
</div>