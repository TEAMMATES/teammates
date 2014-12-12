<%@page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@page import="teammates.common.datatransfer.SessionAttributes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="teammates.common.datatransfer.CommentRecipientType"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.CommentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionResultsBundle"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%@ page import="teammates.common.datatransfer.SessionResultsBundle"%>
<%@ page import="teammates.common.datatransfer.StudentResultBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes"%>
<%@ page import="teammates.ui.controller.InstructorEvalSubmissionPageData"%>
<%@ page import="teammates.ui.controller.InstructorStudentRecordsPageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForJs"%>
<%
	InstructorStudentRecordsPageData data = (InstructorStudentRecordsPageData)request.getAttribute("data");
%>
<%
	if(data.targetSessionName.isEmpty()){
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>TEAMMATES - Instructor</title>
    <!-- Bootstrap core CSS -->
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css" media="screen">
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
        <script type="text/javascript" src="/js/common.js"></script>
    
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorStudentRecords.js"></script>
    <script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
    <!-- Bootstrap core JavaScript ================================================== -->
    <script src="/bootstrap/js/bootstrap.min.js"></script>
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <script type="text/javascript">
        var showCommentBox = "<%=data.showCommentBox%>";
    </script>
</head>

<body onload="readyStudentRecordsPage();">
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div id="frameBodyWrapper" class="container">
        <div id="topOfPage"></div>
        <h2><%=InstructorStudentRecordsPageData.sanitizeForHtml(data.student.name)%>'s Records<small class="muted"> - <%=data.courseId%></small></h2>
        <br />
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <%
        	if (data.studentProfile != null) {
        %>
                <div class="modal fade" id="studentProfileMoreInfo" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                <h4 class="modal-title"><%=InstructorStudentRecordsPageData.sanitizeForHtml(data.student.name)%>'s Profile - More Info</h4>
                            </div>
                            <div class="modal-body">
                                <br>
                                <p class="text-preserve-space height-fixed-md"><%=data.studentProfile.moreInfo.isEmpty() ? 
                                                    "<i class='text-muted'>" + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</i>" : data.studentProfile.moreInfo%></p>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
                            </div>
                        </div><!-- /.modal-content -->
                    </div><!-- /.modal-dialog -->
                </div><!-- /.modal -->
        <%
        	}
        %>
        <div class="container-fluid">
            <%
            	if (data.studentProfile != null) {
                                String pictureUrl = Const.ActionURIs.STUDENT_PROFILE_PICTURE + 
                                    "?blob-key=" + data.studentProfile.pictureKey +
                                    "&user="+data.account.googleId;
                                if (data.studentProfile.pictureKey.isEmpty()) {
                                    pictureUrl = Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH;
                                }
            %>
                    <div class="row">
                        <div class="col-xs-12">
                            <div class="row" id="studentProfile">
                                <div class="col-md-2 col-xs-3 block-center">
                                    <img src="<%=pictureUrl%>" class="profile-pic pull-right">
                                </div>
                                <div class="col-md-10 col-sm-9 col-xs-8">
                                    <table class="table table-striped">
                                        <thead>
                                            <tr>
                                                <th colspan="2"> Profile </td>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td class="text-bold">Short Name (Gender)</td>
                                                <td><%=data.studentProfile.shortName.isEmpty() ? "<i class='text-muted'>" + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</i>" : data.studentProfile.shortName%> 
                                                (<i>
                                                    <%=data.studentProfile.gender.equals(Const.GenderTypes.OTHER) ?
                                                        "<span class='text-muted'>" + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</span>" : data.studentProfile.gender%>
                                                </i>)</td>
                                            </tr>
                                            <tr>
                                                <td class="text-bold">Email</td>
                                                <td><%=data.studentProfile.email.isEmpty() ? "<i  class='text-muted'>" + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</i>" : data.studentProfile.email%></td>
                                            </tr>
                                            <tr>
                                                <td class="text-bold">Institution</td>
                                                <td><%=data.studentProfile.institute.isEmpty() ? "<i  class='text-muted'>" + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</i>" : data.studentProfile.institute%></td>
                                            </tr>
                                            <tr>
                                                <td class="text-bold">Nationality</td>
                                                <td><%=data.studentProfile.nationality.isEmpty() ? "<i  class='text-muted'>" + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</i>" : data.studentProfile.nationality%></td>
                                            </tr>                                
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="panel panel-default">
                                    <div class="panel-body">
                                    <span data-toggle="modal" data-target="#studentProfileMoreInfo" 
                                          class="text-muted pull-right glyphicon glyphicon-resize-full cursor-pointer"></span>
                                        <h5>More Info </h5>                                    
                                        <p class="text-preserve-space height-fixed-md"><%=data.studentProfile.moreInfo.isEmpty() ? 
                                                "<i class='text-muted'>" + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</i>" : data.studentProfile.moreInfo%></p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            <%
            	}
            %>
            <div class="row">
                <div class="col-md-12">
                    <div class="panel panel-info">
                        <div id="studentComments" class="panel-heading">Comments for <%=InstructorStudentRecordsPageData.sanitizeForHtml(data.student.name)%></div>
                        
                          <div class="panel-body">
                            Your comments on this student:
                            <button type="button" class="btn btn-default btn-xs icon-button pull-right" id="button_add_comment" onclick="showAddCommentBox();"
                        data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COMMENT_ADD%>"
                        <%if (!data.currentInstructor.isAllowedForPrivilege(data.student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)) {%>
                            disabled="disabled"
                        <%}%>
                        >
                              <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
                            </button>
                            <ul class="list-group" style="margin-top:15px;">
                            <%
                            	int commentIdx = -1;
                                                            for(CommentAttributes comment : data.comments){
                                                                commentIdx++;
                            %>
                              <li class="list-group-item list-group-item-warning">
                              <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_EDIT%>" name="form_commentedit" class="form_comment" id="form_commentedit-<%=commentIdx%>">
                                <div id="commentBar<%=commentIdx%>">
                                    <span class="text-muted"><%=TimeHelper.formatTime(comment.createdAt)%></span>
                                    <a type="button" id="commentdelete-<%=commentIdx%>" class="btn btn-default btn-xs icon-button pull-right" onclick="return deleteComment('<%=commentIdx%>');"
                                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COMMENT_DELETE%>"> 
                                        <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
                                    </a>
                                    <a type="button" id="commentedit-<%=commentIdx%>" class="btn btn-default btn-xs icon-button pull-right" onclick="return enableEdit('<%=commentIdx%>', '<%=data.comments.size()%>');"
                                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COMMENT_EDIT%>">
                                        <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
                                    </a>
                                    <%
                                    	if(comment.showCommentTo.size() > 0){ 
                                                                               String peopleCanSee = data.getTypeOfPeopleCanViewComment(comment);
                                    %>
                                    <span class="glyphicon glyphicon-eye-open" data-toggle="tooltip" style="margin-left: 5px;"
                                        data-placement="top"
                                        title="This comment is public to <%=peopleCanSee%>"></span>
                                    <%
                                    	}
                                    %>
                                </div>
                                <div id="plainCommentText<%=commentIdx%>"><%=comment.commentText.getValue()%></div>
                                <div id="commentTextEdit<%=commentIdx%>" style="display:none;">
                                    <div class="form-group form-inline">
                                        <div class="form-group text-muted">
                                            You may change comment's visibility using the visibility options on the right hand side.
                                        </div>
                                        <a id="visibility-options-trigger<%=commentIdx%>"
                                            class="btn btn-sm btn-info pull-right">
                                            <span class="glyphicon glyphicon-eye-close"></span>
                                            Show Visibility Options
                                        </a>
                                    </div>
                                    <div id="visibility-options<%=commentIdx%>" class="panel panel-default"
                                        style="display: none;">
                                        <div class="panel-heading">Visibility Options</div>
                                        <table class="table text-center" style="color:#000;"
                                            style="background: #fff;">
                                            <tbody>
                                                <tr>
                                                    <th class="text-center">User/Group</th>
                                                    <th class="text-center">Can see
                                                        your comment</th>
                                                    <th class="text-center">Can see
                                                        giver's name</th>
                                                    <th class="text-center">Can see
                                                        recipient's name</th>
                                                </tr>
                                                <tr id="recipient-person<%=commentIdx%>">
                                                    <td class="text-left">
                                                        <div data-toggle="tooltip"
                                                            data-placement="top" title=""
                                                            data-original-title="Control what comment recipient(s) can view">
                                                            Recipient(s)</div>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox answerCheckbox centered"
                                                        name="receiverLeaderCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.PERSON%>"
                                                        <%=comment.showCommentTo.contains(CommentRecipientType.PERSON)?"checked=\"checked\"":""%>>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox giverCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.PERSON%>"
                                                        <%=comment.showGiverNameTo.contains(CommentRecipientType.PERSON)?"checked=\"checked\"":""%>>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox recipientCheckbox"
                                                        name="receiverFollowerCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.PERSON%>"
                                                        disabled="disabled"></td>
                                                </tr>
                                                <tr id="recipient-team<%=commentIdx%>">
                                                    <td class="text-left">
                                                        <div data-toggle="tooltip"
                                                            data-placement="top" title=""
                                                            data-original-title="Control what team members of comment recipients can view">
                                                            Recipient's Team</div>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox answerCheckbox"
                                                        type="checkbox"
                                                        value="<%=CommentRecipientType.TEAM%>"
                                                        <%=comment.showCommentTo.contains(CommentRecipientType.TEAM)?"checked=\"checked\"":""%>>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox giverCheckbox"
                                                        type="checkbox"
                                                        value="<%=CommentRecipientType.TEAM%>"
                                                        <%=comment.showGiverNameTo.contains(CommentRecipientType.TEAM)?"checked=\"checked\"":""%>>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox recipientCheckbox"
                                                        type="checkbox"
                                                        value="<%=CommentRecipientType.TEAM%>"
                                                        <%=comment.showRecipientNameTo.contains(CommentRecipientType.TEAM)?"checked=\"checked\"":""%>>
                                                    </td>
                                                </tr>
                                                <%
                                                	if(comment.showCommentTo.contains(CommentRecipientType.SECTION)){
                                                %>
                                                <tr id="recipient-section<%=commentIdx%>">
                                                    <td class="text-left">
                                                        <div data-toggle="tooltip"
                                                            data-placement="top" title=""
                                                            data-original-title="Control what other students in the same section can view">
                                                            Recipient's Section</div>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox answerCheckbox"
                                                        type="checkbox"
                                                        value="<%=CommentRecipientType.SECTION%>"
                                                        <%=comment.showCommentTo.contains(CommentRecipientType.SECTION)?"checked=\"checked\"":""%>>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox giverCheckbox"
                                                        type="checkbox"
                                                        value="<%=CommentRecipientType.SECTION%>"
                                                        <%=comment.showGiverNameTo.contains(CommentRecipientType.SECTION)?"checked=\"checked\"":""%>>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox recipientCheckbox"
                                                        type="checkbox"
                                                        value="<%=CommentRecipientType.SECTION%>"
                                                        <%=comment.showRecipientNameTo.contains(CommentRecipientType.SECTION)?"checked=\"checked\"":""%>>
                                                    </td>
                                                </tr>
                                                <%
                                                	}
                                                %>
                                                <tr id="recipient-course<%=commentIdx%>">
                                                    <td class="text-left">
                                                        <div data-toggle="tooltip"
                                                            data-placement="top" title=""
                                                            data-original-title="Control what other students in this course can view">
                                                            Other students in this course</div>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox answerCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.COURSE%>"
                                                        <%=comment.showCommentTo.contains(CommentRecipientType.COURSE)?"checked=\"checked\"":""%>>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox giverCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.COURSE%>"
                                                        <%=comment.showGiverNameTo.contains(CommentRecipientType.COURSE)?"checked=\"checked\"":""%>>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox recipientCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.COURSE%>"
                                                        <%=comment.showRecipientNameTo.contains(CommentRecipientType.COURSE)?"checked=\"checked\"":""%>>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="text-left">
                                                        <div data-toggle="tooltip"
                                                            data-placement="top" title=""
                                                            data-original-title="Control what instructors can view">
                                                            Instructors</div>
                                                </td>
                                                <td><input
                                                    class="visibilityCheckbox answerCheckbox"
                                                    type="checkbox" value="<%=CommentRecipientType.INSTRUCTOR%>"
                                                    <%=comment.showCommentTo.contains(CommentRecipientType.INSTRUCTOR)?"checked=\"checked\"":""%>>
                                                </td>
                                                <td><input
                                                    class="visibilityCheckbox giverCheckbox"
                                                    type="checkbox" value="<%=CommentRecipientType.INSTRUCTOR%>"
                                                    <%=comment.showGiverNameTo.contains(CommentRecipientType.INSTRUCTOR)?"checked=\"checked\"":""%>>
                                                </td>
                                                    <td><input
                                                        class="visibilityCheckbox recipientCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.INSTRUCTOR%>"
                                                        <%=comment.showRecipientNameTo.contains(CommentRecipientType.INSTRUCTOR)?"checked=\"checked\"":""%>>
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                    <div class="form-group">
                                        <textarea class="form-control" rows="3" placeholder="Your comment about this student" name=<%=Const.ParamsNames.COMMENT_TEXT%> id="commentText<%=commentIdx%>"><%=comment.commentText.getValue()%></textarea>
                                    </div>
                                    <div class="col-sm-offset-5">
                                        <input id="commentsave-<%=commentIdx%>" title="Save comment" onclick="return submitCommentForm('<%=commentIdx%>');" type="submit" class="btn btn-primary" id="button_save_comment" value="Save">
                                        <input type="button" class="btn btn-default" value="Cancel" onclick="return disableComment('<%=commentIdx%>');">
                                    </div>
                                </div>
                                <input type="hidden" name=<%=Const.ParamsNames.COMMENT_EDITTYPE%> id="<%=Const.ParamsNames.COMMENT_EDITTYPE%>-<%=commentIdx%>" value="edit">
                                <input type="hidden" name=<%=Const.ParamsNames.COMMENT_ID%> value="<%=comment.getCommentId()%>">
                                <input type="hidden" name=<%=Const.ParamsNames.COURSE_ID%> value="<%=data.courseId%>">
                                <input type="hidden" name=<%=Const.ParamsNames.STUDENT_EMAIL%> value="<%=data.student.email%>">
                                <input type="hidden" 
                                    name=<%=Const.ParamsNames.RECIPIENT_TYPE%> 
                                    value="<%=comment.recipientType%>">
                                <input type="hidden" 
                                    name=<%=Const.ParamsNames.RECIPIENTS%> 
                                    value="<%=data.removeBracketsForArrayString(comment.recipients.toString())%>">
                                <input type="hidden" 
                                    name=<%=Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO%> 
                                    value="<%=data.removeBracketsForArrayString(comment.showCommentTo.toString())%>">
                                <input type="hidden" 
                                    name=<%=Const.ParamsNames.COMMENTS_SHOWGIVERTO%> 
                                    value="<%=data.removeBracketsForArrayString(comment.showGiverNameTo.toString())%>">
                                <input type="hidden" 
                                    name=<%=Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO%> 
                                    value="<%=data.removeBracketsForArrayString(comment.showRecipientNameTo.toString())%>">
                                <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                              </form>
                              </li>
                              <%
                              	}
                                                                if(commentIdx == -1){
                              %>
                                    <li  class="list-group-item list-group-item-warning">You don't have any comments on this student.</li>
                              <%
                              	}
                              %>
                              <li class="list-group-item list-group-item-warning" id="comment_box" style="display:none;">
                                  <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_ADD%>" name="form_commentadd" class="form_comment">
                                    <div class="form-group form-inline">
                                        <div class="form-group text-muted">
                                            The default visibility for your comment is private. You may change it using the visibility options.
                                        </div>
                                        <a id="visibility-options-trigger<%=commentIdx%>"
                                            class="btn btn-sm btn-info pull-right">
                                            <span class="glyphicon glyphicon-eye-close"></span>
                                            Show Visibility Options
                                        </a>
                                    </div>
                                    <div id="visibility-options<%=commentIdx%>" class="panel panel-default"
                                        style="display: none;">
                                        <div class="panel-heading">Visibility Options</div>
                                        <table class="table text-center" style="color:#000;"
                                            style="background: #fff;">
                                            <tbody>
                                                <tr>
                                                    <th class="text-center">User/Group</th>
                                                    <th class="text-center">Can see
                                                        your comment</th>
                                                    <th class="text-center">Can see
                                                        giver's name</th>
                                                    <th class="text-center">Can see
                                                        recipient's name</th>
                                                </tr>
                                                <tr id="recipient-person<%=commentIdx%>">
                                                    <td class="text-left">
                                                        <div data-toggle="tooltip"
                                                            data-placement="top" title=""
                                                            data-original-title="Control what comment recipient(s) can view">
                                                            Recipient(s)</div>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox answerCheckbox centered"
                                                        name="receiverLeaderCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.PERSON%>">
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox giverCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.PERSON%>">
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox recipientCheckbox"
                                                        name="receiverFollowerCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.PERSON%>"
                                                        disabled="disabled"></td>
                                                </tr>
                                                <tr id="recipient-team<%=commentIdx%>">
                                                    <td class="text-left">
                                                        <div data-toggle="tooltip"
                                                            data-placement="top" title=""
                                                            data-original-title="Control what team members of comment recipients can view">
                                                            Recipient's Team</div>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox answerCheckbox"
                                                        type="checkbox"
                                                        value="<%=CommentRecipientType.TEAM%>">
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox giverCheckbox"
                                                        type="checkbox"
                                                        value="<%=CommentRecipientType.TEAM%>">
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox recipientCheckbox"
                                                        type="checkbox"
                                                        value="<%=CommentRecipientType.TEAM%>">
                                                    </td>
                                                </tr>
                                                <tr id="recipient-course<%=commentIdx%>">
                                                    <td class="text-left">
                                                        <div data-toggle="tooltip"
                                                            data-placement="top" title=""
                                                            data-original-title="Control what other students in this course can view">
                                                            Other students in this course</div>
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox answerCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.COURSE%>">
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox giverCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.COURSE%>">
                                                    </td>
                                                    <td><input
                                                        class="visibilityCheckbox recipientCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.COURSE%>">
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="text-left">
                                                        <div data-toggle="tooltip"
                                                            data-placement="top" title=""
                                                            data-original-title="Control what instructors can view">
                                                            Instructors</div>
                                                </td>
                                                <td><input
                                                    class="visibilityCheckbox answerCheckbox"
                                                    type="checkbox" value="<%=CommentRecipientType.INSTRUCTOR%>">
                                                </td>
                                                <td><input
                                                    class="visibilityCheckbox giverCheckbox"
                                                    type="checkbox" value="<%=CommentRecipientType.INSTRUCTOR%>">
                                                </td>
                                                    <td><input
                                                        class="visibilityCheckbox recipientCheckbox"
                                                        type="checkbox" value="<%=CommentRecipientType.INSTRUCTOR%>">
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                    <div class="form-group">
                                      <textarea class="form-control" rows="3" placeholder="Your comment about this student" name=<%=Const.ParamsNames.COMMENT_TEXT%> id="commentText"></textarea>
                                    </div>
                                    <div class="col-sm-offset-5">
                                      <input type="submit" class="btn btn-primary" id="button_save_comment" value="Save">
                                      <input type="button" class="btn btn-default" value="Cancel" onclick="hideAddCommentBox();">
                                      <input type="hidden" name=<%=Const.ParamsNames.COURSE_ID%> value="<%=data.courseId%>">
                                      <input type="hidden" name=<%=Const.ParamsNames.STUDENT_EMAIL%> value="<%=data.student.email%>">
                                      <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                                      <input type="hidden" 
                                            name=<%=Const.ParamsNames.RECIPIENT_TYPE%> 
                                            value="<%=CommentRecipientType.PERSON%>">
                                        <input type="hidden" 
                                            name=<%=Const.ParamsNames.RECIPIENTS%> 
                                            value="<%=data.student.email%>">
                                        <input type="hidden" 
                                            name=<%=Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO%> 
                                            value="">
                                        <input type="hidden" 
                                            name=<%=Const.ParamsNames.COMMENTS_SHOWGIVERTO%> 
                                            value="">
                                        <input type="hidden" 
                                            name=<%=Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO%> 
                                            value="">
                                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                                    </div>
                                  </form>
                              </li>
                            </ul>
                          </div>
                        </div>
                        <br>
                        <%
                        	int fbIndex = -1;
                                                for(SessionAttributes generalSession : data.sessions){
                                                    if(generalSession instanceof FeedbackSessionAttributes){
                                                        fbIndex++;
                                                        FeedbackSessionAttributes fbSession = (FeedbackSessionAttributes) generalSession;
                        %>
                        <div class="well well-plain student_feedback" id="studentFeedback-<%=fbIndex%>" 
                            onclick="loadFeedbackSession('<%=fbSession.courseId%>', '<%=data.student.email%>', '<%=data.account.googleId%>','<%=fbSession.feedbackSessionName%>', this)">
                            <div class="text-primary">
                                <h2 id="feedback_name-<%=fbIndex%>">
                                    <strong>Feedback Session : <%=InstructorStudentRecordsPageData.sanitizeForHtml(fbSession.feedbackSessionName)%></strong>
                                </h2>
                            </div>
                            <div class="placeholder-img-loading"></div>
                            <div id="target-feedback-<%=fbIndex%>">
                            </div>
                        </div>
                        <br />
                        <%
                        	}
                                                }
                        %>
                        <%
                        	int evalIndex = -1;
                                                    int sessionIndex = -1;
                                                    for(SessionResultsBundle sessionResult: data.results){
                                                        sessionIndex++;
                                                        if(sessionResult instanceof StudentResultBundle){
                                                            evalIndex++;
                                                            StudentResultBundle studentResult = (StudentResultBundle) sessionResult;
                                                            EvaluationAttributes eval = (EvaluationAttributes) data.evals.get(sessionIndex);
                        %>
                                <div class="well well-plain student_eval" id="studentEval-<%=evalIndex%>">
                                <div class="text-primary">
                                    <h2 id="eval_name-<%=evalIndex%>">Evaluation Name: <%=InstructorStudentRecordsPageData.sanitizeForHtml(eval.name)%></h2>
                                </div>
                            <%
                            	for(boolean byReviewee = true, repeat=true; repeat; repeat = byReviewee, byReviewee=false){
                            %>
                            <h3>
                                <span class="label <%=byReviewee ? "label-primary" : "label-default"%>"><%=(byReviewee ? "Result" : "Submission")%></span>
                            </h3>
                            <div class="panel <%=byReviewee ? "panel-primary" : "panel-default"%>">
                                <table class="table panel-heading">
                                    <tr>
                                            <td class="col-sm-4"><%=byReviewee ? "Reviewee" : "Reviewer"%>: <strong><%=InstructorStudentRecordsPageData.sanitizeForHtml(data.student.name)%></strong>
                                            </td>
                                            <td class="col-sm-4">
                                                <div class="pull-right"><span data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.CLAIMED%>">Claimed Contribution: </span>
                                                    <%=InstructorEvalSubmissionPageData.getPointsInEqualShareFormatAsHtml(studentResult.summary.claimedToInstructor,true)%>
                                                </div>
                                            </td>
                                            <td class="col-sm-4">
                                                <div class="pull-right"><span data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.PERCEIVED%>">Perceived Contribution: </span>
                                                    <%=InstructorEvalSubmissionPageData.getPointsInEqualShareFormatAsHtml(studentResult.summary.perceivedToInstructor,true)%>
                                                </div>
                                            </td>
                                    </tr>
                                </table>
                                <table class="table">
                                    <tbody>
                                        <tr>
                                            <td><strong>Self evaluation: </strong><br />
                                                <%=InstructorEvalSubmissionPageData.getJustificationAsSanitizedHtml(studentResult.getSelfEvaluation())%></td>
                                        </tr>
                                        <tr>
                                            <td><strong>Comments about the team: </strong><br />
                                                <%
                                                	String commentAboutTeam = 
                                                                                                	    InstructorEvalSubmissionPageData.sanitizeForHtml(studentResult.getSelfEvaluation().p2pFeedback.getValue());
                                                                                                  if(commentAboutTeam == null || commentAboutTeam.isEmpty()) {
                                                %>
                                                  N/A
                                                <%
                                                	} else {
                                                %>
                                                <%=commentAboutTeam%>
                                                <%
                                                	}
                                                %>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            <%
                            	if(byReviewee){
                            %>
                            <table class="table">
                            <tr class="fill-primary"><td>
                                Feedback from others
                            </td></tr></table>
                            <%
                            	} else {
                            %>
                            <div class="panel-heading panel-default">
                                <div style="margin-left:-5px">
                                Feedback to others</div>
                            </div>
                            <%
                            	}
                            %>
                            <table class="table table-bordered table-striped">
                                        <thead>
                                            <tr class="border-top-gray <%=byReviewee ? "fill-info" : ""%>">
                                                <td width="15%"><strong><%=byReviewee ? "From" : "To"%> student</strong></td>
                                                <td width="5%"><strong>Contribution</strong></td>
                                                <td width="40%"><strong>Confidential comments</strong></td>
                                                <td width="40%"><strong>Feedback to peer</strong></td>
                                            </tr>
                                        </thead><tbody>
                                <%
                                	for(SubmissionAttributes sub: (byReviewee ? studentResult.incoming : studentResult.outgoing)){
                                                                        if(sub.reviewer.equals(sub.reviewee)) continue;
                                %>
                                    
                                        <tr>
                                            <td><b><%=InstructorEvalSubmissionPageData.sanitizeForHtml(byReviewee ? sub.details.reviewerName : sub.details.revieweeName)%></b></td>
                                            <td><%=InstructorEvalSubmissionPageData.getPointsInEqualShareFormatAsHtml(sub.details.normalizedToInstructor,true)%></td>
                                            <td><%=InstructorEvalSubmissionPageData.getJustificationAsSanitizedHtml(sub)%></td>
                                            <td><%=InstructorEvalSubmissionPageData.getP2pFeedbackAsHtml(InstructorEvalSubmissionPageData.sanitizeForHtml(sub.p2pFeedback.getValue()), eval.p2pEnabled)%></td>
                                        </tr>
                                <%
                                	}
                                %></tbody>
                            </table></div>
                            <%
                            	}
                            %>
                                <div class="align-center">
                                    <input type="button" class="btn btn-primary" id="button_edit-<%=evalIndex%>" value="Edit Submission"
                                        onclick="window.location.href='<%=data.getInstructorEvaluationSubmissionEditLink(eval.courseId, eval.name, data.student.email)%>'"
                                        <%if (!data.currentInstructor.isAllowedForPrivilege(data.student.section, eval.name, 
                                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)) {%>
                                                disabled="disabled"
                                        <%}%>
                                        >
                                </div>
                                </div>
                                <br>
                        <%
                        	}
                                                    }
                        %>
                    </div>
            </div>
        </div>
    </div>
    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>
<%
	} else {
%>
<%
	//Ajax content to be loaded
int sessionIndex = -1;
int fbIndex = -1;
for(SessionResultsBundle sessionResult: data.results){
    sessionIndex++;
if(sessionResult instanceof FeedbackSessionResultsBundle){
    FeedbackSessionResultsBundle feedback = (FeedbackSessionResultsBundle) sessionResult;
    
    fbIndex++;

    String giverName = feedback.appendTeamNameToName(InstructorStudentRecordsPageData.sanitizeForHtml(data.student.name), data.student.team);
    String recipientName = giverName;
    Map<String, List<FeedbackResponseAttributes>> received = feedback 
            .getResponsesSortedByRecipient().get(recipientName);
    Map<String, List<FeedbackResponseAttributes>> given = feedback
            .getResponsesSortedByGiver().get(giverName);
%>
<%
	if(received != null){
%>
<br>
<div class="panel panel-primary">
    <div class="panel-heading">
        To: <strong><%=recipientName%></strong>
</div>
    <div class="panel-body">
    <%
    	int giverIndex = 0;
            for (Map.Entry<String, List<FeedbackResponseAttributes>> responsesReceived : received.entrySet()) {
                giverIndex++;
    %>
    <div class="row <%=giverIndex == 1?"":"border-top-gray"%>">
        <div class="col-md-2"><strong>From: <%=responsesReceived.getKey()%></strong></div>
        <div class="col-md-10">
        <%
        	int qnIndx = 1;
                    for (FeedbackResponseAttributes singleResponse : responsesReceived.getValue()) {
        %>
                <div class="panel panel-info">
                    <div class="panel-heading">Question <%=feedback.questions.get(singleResponse.feedbackQuestionId).questionNumber%>: <%=feedback.getQuestionText(singleResponse.feedbackQuestionId)%><%
                    	Map<String, FeedbackQuestionAttributes> questions = feedback.questions;
                                            FeedbackQuestionAttributes question = questions.get(singleResponse.feedbackQuestionId);
                                            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
                                            out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "giver-"+giverIndex+"-session-"+fbIndex));
                    %></div>
                <div class="panel-body"><span class="text-preserve-space"><%=feedback.getResponseAnswerHtml(singleResponse, question)%></span>
            <%
            	List<FeedbackResponseCommentAttributes> responseComments = feedback.responseComments.get(singleResponse.getId());
                            if (responseComments != null) {
            %>
                <ul class="list-group comment-list">
                            <%
                            	for (FeedbackResponseCommentAttributes comment : responseComments) {
                            %>
                                    <li class="list-group-item list-group-item-warning">
                                        <span class="text-muted">From: <%=comment.giverEmail%> [<%=comment.createdAt%>]</span>
                                        <div><%=comment.commentText.getValue()%></div>
                                    </li>
                            <%
                            	}
                            %>
                </ul>
            <%
            	}
            %></div></div>
            <%
            	}qnIndx++;
                            if (responsesReceived.getValue().isEmpty()) {
            %>
                <div class="col-sm-12" style="color:red;">No feedback from this user.</div>
            <%
            	}
            %>
            </div></div>
            <br>
    <%
    	}
    %>
    </div></div>
    
<%
    	} else{
    %>
    <br>
    <div class="panel panel-info">
            <div class="panel-body">No feedback for <%=InstructorStudentRecordsPageData.sanitizeForHtml(data.student.name)%> found</div>
    </div>
<%
	}
    if(given != null){
%>
<div class="panel panel-primary">
    <div class="panel-heading">
        From: <strong><%=giverName%></strong>
    </div>
    <div class="panel-body">
    <%
    	int recipientIndex = 0;
            for (Map.Entry<String, List<FeedbackResponseAttributes>> responsesGiven : given.entrySet()) {
            recipientIndex++;
    %>
    <div class="row <%=recipientIndex == 1?"":"border-top-gray"%>">
        <div class="col-md-2"><strong>To: <%=responsesGiven.getKey()%></strong></div>
        <div class="col-md-10">
        <%
        	int qnIndx = 1;
                    for (FeedbackResponseAttributes singleResponse : responsesGiven.getValue()) {
        %>
                
                <div class="panel panel-info">
                    <div class="panel-heading">Question <%=feedback.questions.get(singleResponse.feedbackQuestionId).questionNumber%>: <%=feedback.getQuestionText(singleResponse.feedbackQuestionId)%><%
                    	Map<String, FeedbackQuestionAttributes> questions = feedback.questions;
                                            FeedbackQuestionAttributes question = questions.get(singleResponse.feedbackQuestionId);
                                            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
                                            out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "recipient-"+recipientIndex+"-session-"+fbIndex));
                    %></div>
                <div class="panel-body"><%=singleResponse.getResponseDetails().getAnswerHtml(questionDetails)%>
            <%
                List<FeedbackResponseCommentAttributes> responseComments = feedback.responseComments.get(singleResponse.getId());
                if (responseComments != null) {
            %>
                <ul class="list-group comment-list">
                            <%
                                for (FeedbackResponseCommentAttributes comment : responseComments) {
                            %>
                                    <li class="list-group-item list-group-item-warning">
                                        <span class="text-muted">From: <%=comment.giverEmail %> [<%=comment.createdAt %>]</span>
                                        <div><%=comment.commentText.getValue() %></div>
                                    </li>
                            <%
                                }
                            %>
                </ul>
            <%
                } %></div></div>
            <%
                qnIndx++;
            }
            if (responsesGiven.getValue().isEmpty()) {
            %>
                <div class="col-sm-12" style="color:red;">No feedback from this user.</div>
        <%
            }
        %>
            </div></div>
            <br>
        
    <%
        }
    %>
    </div></div>
<%
    } else{
%>      <br>
        <div class="panel panel-info">
            <div class="panel-body">No feedback by <%=InstructorStudentRecordsPageData.sanitizeForHtml(data.student.name)%> found</div>
        </div>
<%
    }}}}
%> 