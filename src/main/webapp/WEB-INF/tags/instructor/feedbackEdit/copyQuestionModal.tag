<%@ tag description="instructorFeedbacks - feedback sessions 'copy question' modal" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ attribute name="copyQnForm" type="teammates.ui.template.FeedbackQuestionCopyTable" required="true"%>

<div class="modal fade" id="copyModal" tabindex="-1" role="dialog" aria-labelledby="copyModalTitle" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                </button>
                <h4 class="modal-title" id="copyModalTitle">Copy Questions</h4>
            </div>
            <div class="modal-body padding-0">
                <form class="form" id="copyModalForm" role="form" method="post"
                    action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_COPY %>">
                    <%-- Previous Questions --%>
                    <table class="table-responsive table table-hover table-bordered margin-0" id="copyTableModal">
                        <thead class="fill-primary">
                            <tr>
                                <th style="width:30px;">&nbsp;</th>
                                <th onclick="toggleSort(this,2);" id="button_sortid" class="button-sort-ascending"> 
                                    Course ID <span class="icon-sort sorted-ascending"></span>
                                </th>
                                <th onclick="toggleSort(this,3);" id="button_sortfsname" class="button-sort-none" style="width:17%;">
                                    Session Name <span class="icon-sort unsorted"></span>
                                </th>
                                <th onclick="toggleSort(this,4);" id="button_sortfqtype" class="button-sort-none"> 
                                    Question Type <span class="icon-sort unsorted"></span>
                                </th>
                                <th onclick="toggleSort(this,5);" id="button_sortfqtext" class="button-sort-none"> 
                                    Question Text <span class="icon-sort unsorted"></span>
                                </th>
                            </tr>
                        </thead>
                        <c:forEach items="${copyQnForm.questionRows}" var="row">
                            <tr style="cursor:pointer;">
                                    <td><input type="checkbox"></td>
                                    <td>${row.courseId}</td>
                                    <td>${row.fsName}</td>
                                    <td>${row.qnType}</td>
                                    <td>${row.qnText}</td>
                                    <input type="hidden" value="${row.qnId}">
                            </tr>
                        </c:forEach>
                    </table>
                    <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${copyQnForm.fsName}">
                    <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
                    <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${copyQnForm.courseId}">
                </form>
            </div>
            <div class="modal-footer margin-0">
                <button type="button" class="btn btn-primary" id="button_copy_submit" disabled="disabled">Copy</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
            </div>
        </div>
    </div>
</div>