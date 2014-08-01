<%@page import="teammates.common.datatransfer.CommentRecipientType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="teammates.common.util.Config"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page
    import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page
    import="teammates.ui.controller.InstructorCourseStudentDetailsPageData"%>
<%
    InstructorCourseStudentDetailsPageData data = (InstructorCourseStudentDetailsPageData) request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
<link rel="shortcut icon" href="/favicon.png">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>TEAMMATES - Instructor</title>
<link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css"
    type="text/css" />
<link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css"
    type="text/css" />
<link rel="stylesheet" href="/stylesheets/teammatesCommon.css"
    type="text/css" />

<script type="text/javascript" src="/js/googleAnalytics.js"></script>
<script type="text/javascript" src="/js/jquery-minified.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/js/instructor.js"></script>
<script type="text/javascript" src="/js/contextualcomments.js"></script>
<jsp:include page="../enableJS.jsp"></jsp:include>

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
<script type="text/javascript">
    var isShowCommentBox = <%=data.commentRecipient != null 
            && (data.commentRecipient.equals("student") 
                    || data.commentRecipient.equals("team")
                    || data.commentRecipient.equals("section"))%>;
    var commentRecipient = "<%=data.commentRecipient != null? data.commentRecipient: ""%>";
</script>
</head>


<body>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div class="container theme-showcase" id="frameBodyWrapper">
        <div id="topOfPage"></div>
        <div id="headerOperation">
            <h1>Student Details</h1>
        </div>

        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        
        <%
            if (data.studentProfile != null) {
            	 String pictureUrl = Const.ActionURIs.STUDENT_PROFILE_PICTURE + 
                     "?blob-key=" + data.studentProfile.pictureKey +
                     "&user="+data.account.googleId;
                 if (data.studentProfile.pictureKey.isEmpty()) {
                     pictureUrl = Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH;
                 }
        %>
                <div class="modal fade" id="studentProfileMoreInfo" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                <h4 class="modal-title"><%=data.student.name%>'s Profile - More Info</h4>
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
                <div class="row">
                    <div class="col-xs-12">
                        <div class="row" id="studentProfile">
                            <div class="col-md-2 col-xs-3 block-center">
                                <img src="<%=pictureUrl %>" class="profile-pic pull-right">
                            </div>
                            <div class="col-md-10 col-sm-9 col-xs-8">
                                <table class="table table-striped">
                                    <thead>
                                        <tr>
                                            <th colspan="2"> Profile </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td class="text-bold">Short Name (Gender)</td>
                                            <td><%=data.studentProfile.shortName.isEmpty() ? "<i class='text-muted'>" + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</i>" : data.studentProfile.shortName %> 
                                            (<i>
                                                <%=data.studentProfile.gender.equals(Const.GenderTypes.OTHER) ?
                                                    "<span class='text-muted'>" + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</span>" : data.studentProfile.gender %>
                                            </i>)</td>
                                        </tr>
                                        <tr>
                                            <td class="text-bold">Personal Email</td>
                                            <td><%=data.studentProfile.email.isEmpty() ? "<i  class='text-muted'>" + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</i>" : data.studentProfile.email %></td>
                                        </tr>
                                        <tr>
                                            <td class="text-bold">Institution</td>
                                            <td><%=data.studentProfile.institute.isEmpty() ? "<i  class='text-muted'>" + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</i>" : data.studentProfile.institute %></td>
                                        </tr>
                                        <tr>
                                            <td class="text-bold">Nationality</td>
                                            <td><%=data.studentProfile.nationality.isEmpty() ? "<i  class='text-muted'>" + Const.STUDENT_PROFILE_FIELD_NOT_FILLED + "</i>" : data.studentProfile.nationality %></td>
                                        </tr>                                
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
        <%
            }
        %>
        <div class="well well-plain">
            <button type="button" class="btn btn-default btn-xs icon-button pull-right"
                id="button_add_comment" data-toggle="tooltip"
                data-placement="top" title="" data-original-title="Add comment"
                <% if (!data.currentInstructor.isAllowedForPrivilege(data.student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)) { %>
                    disabled="disabled"
                <% } %>
                >
                <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
            </button>
            <div class="form form-horizontal"
                id="studentInfomationTable">
                <div class="form-group">
                    <label class="col-sm-1 control-label">Student
                        Name:</label>
                    <div class="col-sm-11"
                        id="<%=Const.ParamsNames.STUDENT_NAME%>">
                        <p class="form-control-static"><%=data.student.name%></p>
                    </div>
                </div>
                <%
                    if (data.hasSection) {
                %>
                <div class="form-group">
                    <label class="col-sm-1 control-label">Section
                        Name:</label>
                    <div class="col-sm-11"
                        id="<%=Const.ParamsNames.SECTION_NAME%>">
                        <p class="form-control-static"><%=sanitizeForHtml(data.student.section)%></p>
                    </div>
                </div>
                <%
                    }
                %>
                <div class="form-group">
                    <label class="col-sm-1 control-label">Team
                        Name:</label>
                    <div class="col-sm-11"
                        id="<%=Const.ParamsNames.TEAM_NAME%>">
                        <p class="form-control-static"><%=sanitizeForHtml(data.student.team)%></p>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-1 control-label">Official Email
                        Address:</label>
                    <div class="col-sm-11"
                        id="<%=Const.ParamsNames.STUDENT_EMAIL%>">
                        <p class="form-control-static"><%=sanitizeForHtml(data.student.email)%></p>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-1 control-label">Comments:</label>
                    <div class="col-sm-11"
                        id="<%=Const.ParamsNames.COMMENTS%>">
                        <p class="form-control-static"><%=sanitizeForHtml(data.student.comments)%></p>
                    </div>
                </div>
            </div>
        </div>
        <div id="commentArea" class="well well-plain" style="display: none;">
            <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_ADD%>" name="form_commentadd">
                <div class="form-group form-inline">
                    <label style="margin-right: 24px;">Recipient:
                    </label> 
                    <select id="comment_recipient_select" class="form-control">
                        <option value="<%=CommentRecipientType.PERSON%>" selected><%=sanitizeForHtml(data.student.name)%></option>
                        <option value="<%=CommentRecipientType.TEAM%>"><%=sanitizeForHtml(data.student.team)%></option>
                        <% if (data.hasSection && !data.student.section.equals("None")) {%>
                        <option value="<%=CommentRecipientType.SECTION%>"><%=sanitizeForHtml(data.student.section)%></option>
                        <% } %>
                    </select>
                    <a id="visibility-options-trigger"
                        class="btn btn-sm btn-info pull-right"><span
                        class="glyphicon glyphicon-eye-close"></span>
                        Show Visibility Options</a>
                </div>
                <p class="form-group text-muted">
                    The default visibility for your comment is private. You may change it using the visibility options above.
                </p>
                <div id="visibility-options" class="panel panel-default"
                    style="display: none;">
                    <div class="panel-heading">Visibility Options</div>
                    <table class="table text-center"
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
                            <tr id="recipient-person">
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
                            <tr id="recipient-team">
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
                            <% if(data.hasSection){ %>
                            <tr id="recipient-section">
                                <td class="text-left">
                                    <div data-toggle="tooltip"
                                        data-placement="top" title=""
                                        data-original-title="Control what students in the same section can view">
                                        Recipient's Section</div>
                                </td>
                                <td><input
                                    class="visibilityCheckbox answerCheckbox"
                                    type="checkbox"
                                    value="<%=CommentRecipientType.SECTION%>">
                                </td>
                                <td><input
                                    class="visibilityCheckbox giverCheckbox"
                                    type="checkbox"
                                    value="<%=CommentRecipientType.SECTION%>">
                                </td>
                                <td><input
                                    class="visibilityCheckbox recipientCheckbox"
                                    type="checkbox"
                                    value="<%=CommentRecipientType.SECTION%>">
                                </td>
                            </tr>
                            <% } %>
                            <tr id="recipient-course">
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
                <textarea class="form-control" rows="6" placeholder="Enter your comment here ..." style="margin-bottom: 15px;"
                 name=<%=Const.ParamsNames.COMMENT_TEXT%> id="commentText"></textarea>
                <div style="text-align: center;">
                    <input type="submit" class="btn btn-primary"
                        id="button_save_comment" value="Save"> 
                    <input type="button" class="btn btn-default"
                        id="button_cancel_comment" value="Cancel">
                    <input type="hidden" name=<%=Const.ParamsNames.COURSE_ID%> value="<%=data.student.course%>">
                    <input type="hidden" name=<%=Const.ParamsNames.STUDENT_EMAIL%> value="<%=data.student.email%>">
                    <input type="hidden" name=<%=Const.ParamsNames.RECIPIENT_TYPE%> value="<%=CommentRecipientType.PERSON%>">
                    <input type="hidden" name=<%=Const.ParamsNames.RECIPIENTS%> value="<%=data.student.email%>">
                    <input type="hidden" name=<%=Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO%> value="">
                    <input type="hidden" name=<%=Const.ParamsNames.COMMENTS_SHOWGIVERTO%> value="">
                    <input type="hidden" name=<%=Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO%> value="">
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                    <input type="hidden" name="<%=Const.ParamsNames.FROM_STUDENT_DETAILS_PAGE%>" value="true">
                </div>
            </form>
        </div>
        <%
            if (data.studentProfile != null) {
        %>
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
        <%
            }
        %>
    </div>


    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>