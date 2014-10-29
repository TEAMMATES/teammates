<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="teammates.common.datatransfer.CommentRecipientType"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="teammates.common.datatransfer.InstructorAttributes"%>
<%@ page import="teammates.common.datatransfer.TeamResultBundle"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForJs"%>
<%@ page import="teammates.ui.controller.InstructorCourseDetailsPageData"%>
<%
    InstructorCourseDetailsPageData data = (InstructorCourseDetailsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>TEAMMATES - Instructor</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css"/>
   
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorCourseDetails.js"></script>
    <script type="text/javascript" src="/js/contextualcomments.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>   

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]--> 
<script type="text/javascript">
    var isShowCommentBox = false;
</script>
</head>

<body>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div class="container theme-showcase" id="frameBodyWrapper">
        <div id="topOfPage"></div>
        
        <div id="headerOperation">
            <h1>Course Details</h1>
        </div>
        <br>
        
        
        <div class="well well-plain" id="courseInformationHeader">
            <button type="button" class="btn btn-default btn-xs icon-button pull-right"
                id="button_add_comment" data-toggle="tooltip"
                data-placement="top" title="" data-original-title="Give a comment about all students in the course"
                <% if (!data.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)) { %>
                disabled="disabled"
                <% } %>
                >
                <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
            </button>
            <div class="form form-horizontal">
                <div class="form-group">
                    <label class="col-sm-3 control-label">Course ID:</label>
                    <div class="col-sm-6" id="courseid">
                        <p class="form-control-static"><%=sanitizeForHtml(data.courseDetails.course.id)%></p>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">Course name:</label>
                    <div class="col-sm-6" id="coursename">
                        <p class="form-control-static"><%=sanitizeForHtml(data.courseDetails.course.name)%></p>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">Sections:</label>
                    <div class="col-sm-6" id="total_sections">
                        <p class="form-control-static"><%=data.courseDetails.stats.sectionsTotal%></p>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label">Teams:</label>
                    <div class="col-sm-6" id="total_teams">
                        <p class="form-control-static"><%=data.courseDetails.stats.teamsTotal%></p>
                    </div>
                 </div>
                 <div class="form-group">
                    <label class="col-sm-3 control-label">Total students:</label>
                    <div class="col-sm-6" id="total_students">
                        <p class="form-control-static"><%=data.courseDetails.stats.studentsTotal%></p>
                    </div>
                 </div>
                 <div class="form-group">
                    <label class="col-sm-3 control-label">Instructors:</label>
                    <div class="col-sm-6" id="instructors">
                        <div class="form-control-static">
                    <%
                        for (int i = 0; i < data.instructors.size(); i++){
                            InstructorAttributes instructor = data.instructors.get(i);
                            String instructorRole = instructor.role == null ? Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER : instructor.role;
                            String instructorInfo = instructorRole + ": " + instructor.name + " (" + instructor.email + ")";
                    %>
                        <%=sanitizeForHtml(instructorInfo)%><br><br>
                    <%
                        }
                    %>
                        </div>
                    </div>
                 </div>
                 <%
                     if(data.courseDetails.stats.studentsTotal>1){
                 %>
                 <div class="form-group">
                     <div class="align-center">
                         <input type="button" class="btn btn-primary"
                                 id="button_remind"
                                 data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_REMIND%>"
                                 onclick="if(toggleSendRegistrationKeysConfirmation('<%=data.courseDetails.course.id%>')) window.location.href='<%=data.getInstructorCourseRemindLink()%>';"
                                 value="Remind Students to Join" tabindex="1"
                                 <% if (!data.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) { %>
                                 disabled="disabled"
                                 <% } %>
                                 >
                         <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_LIST_DOWNLOAD%>" style="display:inline;">
                            <input id="button_download" type="submit" class="btn btn-primary"
                                name="<%=Const.ParamsNames.FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON%>"
                                value=" Download Student List ">
                            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                            <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.courseDetails.course.id%>">
                        </form>

                            
                            
                        <div>
                         <span class="help-block">
                            Non-English characters not displayed properly in the downloaded file?<span class="btn-link"
                            data-toggle="modal"
                            data-target="#studentTableWindow"
                            onclick="submitFormAjax()"
                            >
                            click here</span>
                         </span>
                        </div>
                        
                        <form id="csvToHtmlForm">
                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.courseDetails.course.id%>">
                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                        <input type="hidden" name="<%=Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED%>" value=true>
                        </form>
                        
                        <div class="modal fade" id="studentTableWindow">
                            <div class="modal-dialog modal-lg">
                                <div class="modal-content">
                                    <div class="modal-header">       
                                        
                                         <span class="pull-left help-block">
                                        Tips: After Selecting the table, <kbd>Ctrl + C</kbd> to COPY and <kbd>Ctrl + V</kbd> to PASTE to your Excel Workbook.
                                        </span>
                                        <button type="button"
                                            class="btn btn-default"
                                            data-dismiss="modal">Close</button>
                                        <button type="button"
                                            class="btn btn-primary"
                                            onclick="selectElementContents( document.getElementById('detailsTable') );">
                                            Select Table</button>                                
                                    </div>
                                    <div class="modal-body">
                                     <div class="table-responsive">
                                    
                                    <div id="detailsTable">                                  
                                    </div>
                                    <br>                                    
                                    <div id="ajaxStatus">
                                    </div>
                                    
                                    
                               
                                    </div>
                                    </div>
                                </div>
                                <!-- /.modal-content -->
                            </div>
                            <!-- /.modal-dialog -->
                        </div>
                        <!-- /.modal -->

                    </div>
                 </div>
                 <%
                     }
                 %>
            </div>
        </div>
        <div id="commentArea" class="well well-plain" style="display: none;">
            <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_ADD%>" name="form_commentadd">
                <div class="form-group form-inline">
                    <label style="margin-right: 24px;">Recipient:
                    </label> 
                    <select id="comment_recipient_select" class="form-control" disabled="disabled">
                        <option value="<%=CommentRecipientType.COURSE%>" selected>The whole class</option>
                    </select>
                    <a id="visibility-options-trigger"
                        class="btn btn-sm btn-info pull-right"><span
                        class="glyphicon glyphicon-eye-close"></span>
                        Show Visibility Options</a>
                </div>
                <p class="form-group text-muted">
                    The default visibility for your comment is private. You may change it using the ‘show visibility options’ button above.
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
                            <tr id="recipient-course">
                                <td class="text-left">
                                    <div data-toggle="tooltip"
                                        data-placement="top" title=""
                                        data-original-title="Control what students in this course can view">
                                        Students in this course</div>
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
                                    type="checkbox" value="<%=CommentRecipientType.COURSE%>" disabled="disabled">
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
                    <input type="hidden" name=<%=Const.ParamsNames.COURSE_ID%> value="<%=data.courseDetails.course.id%>">
                    <input type="hidden" name=<%=Const.ParamsNames.RECIPIENT_TYPE%> value="<%=CommentRecipientType.COURSE%>">
                    <input type="hidden" name=<%=Const.ParamsNames.RECIPIENTS%> value="<%=data.courseDetails.course.id%>">
                    <input type="hidden" name=<%=Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO%> value="">
                    <input type="hidden" name=<%=Const.ParamsNames.COMMENTS_SHOWGIVERTO%> value="">
                    <input type="hidden" name=<%=Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO%> value="">
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                    <input type="hidden" name="<%=Const.ParamsNames.FROM_COURSE_DETAILS_PAGE%>" value="true">
                </div>
            </form>
        </div>
        <br>
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <br>

        <table class="table table-bordered table-striped">
            <thead class="fill-primary">
                <tr>
                    <%  int sortIdx = 1;
                        boolean hasSection = data.courseDetails.stats.sectionsTotal != 0;
                        if(hasSection) { %>
                        <th onclick="toggleSort(this, <%=sortIdx++%>);" id="button_sortstudentsection" class="button-sort-none">
                        Section<span class="icon-sort unsorted"></span>
                        </th>
                    <% } %>
                    <th onclick="toggleSort(this, <%=sortIdx++%>);" id="button_sortstudentteam" class="button-sort-none">
                        Team<span class="icon-sort unsorted"></span>
                    </th>
                    <th onclick="toggleSort(this, <%=sortIdx++%>);" id="button_sortstudentname" class="button-sort-none">
                        Student Name<span class="icon-sort unsorted"></span>
                    </th>
                    <th onclick="toggleSort(this, <%=sortIdx++%>);" id="button_sortstudentstatus" class="button-sort-none">
                        Status<span class="icon-sort unsorted"></span>
                    </th>
                    <th class="align-center no-print">
                        Action(s)
                    </th>
                </tr>
            </thead>
            <%
                int idx = -1;
                                                            for(StudentAttributes student: data.students){ idx++;
            %>
                    <tr class="student_row" id="student<%=idx%>">
                        <% if(hasSection) { %>
                            <td id="<%=Const.ParamsNames.SECTION_NAME%>"><%=sanitizeForHtml(student.section)%></td>
                        <% } %>
                        <td id="<%=Const.ParamsNames.TEAM_NAME%>"><%=sanitizeForHtml(student.team)%></td>
                        <td id="<%=Const.ParamsNames.STUDENT_NAME%>"><%=sanitizeForHtml(student.name)%></td>
                        <td class="align-center"><%=data.getStudentStatus(student)%></td>
                        <td class="align-center no-print">
                            <a class="btn btn-default btn-xs"
                                href="<%=data.getCourseStudentDetailsLink(student)%>"
                                data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_DETAILS%>" 
                                <% if (!data.currentInstructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)) { %>
                                disabled="disabled"
                                <% } %>
                                > View</a>
                            <a class="btn btn-default btn-xs" href="<%=data.getCourseStudentEditLink(student)%>"
                                data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_EDIT%>"
                                <% if (!data.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) { %>
                                disabled="disabled"
                                <% } %>
                                > Edit</a>
                            <%
                                if(data.getStudentStatus(student).equals(Const.STUDENT_COURSE_STATUS_YET_TO_JOIN)){
                            %>
                            <a class="btn btn-default btn-xs" href="<%=data.getCourseStudentRemindLink(student)%>"
                                onclick="return toggleSendRegistrationKey()"
                                data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_REMIND%>"
                                <% if (!data.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) { %>
                                disabled="disabled"
                                <% } %>
                                > Send Invite</a>
                            <%
                                }
                            %>
                            <a class="btn btn-default btn-xs" href="<%=data.getCourseStudentDeleteLink(student)%>"
                                onclick="return toggleDeleteStudentConfirmation('<%=sanitizeForJs(student.name)%>')"
                                data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_DELETE%>"
                                <% if (!data.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) { %>
                                disabled="disabled"
                                <% } %>
                                > Delete</a>
                            <a class="btn btn-default btn-xs" href="<%=data.getStudentRecordsLink(student)%>"
                                data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_RECORDS%>"
                                > All Records</a>
                            <div class="btn-group">
                                <a class="btn btn-default btn-xs cursor-default" href="javascript:;"
                                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_COMMENT%>"
                                    <% if (!data.currentInstructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)) { %>
                                    disabled="disabled"
                                    <% } %>
                                    > Add Comment</a>
                                <a href="javascript:;" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown"
                                    <% if (!data.currentInstructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS)) { %>
                                    disabled="disabled"
                                    <% } %>
                                    ><span class="caret"></span><span class="sr-only">Add comments</span></a>
                                <ul class="dropdown-menu" role="menu" aria-labelledby="dLabel" style="text-align:left;">
                                    <li role="presentation"><a class="t_student_details_tostudent-c<%=data.courseDetails.course.id %>.<%=idx%>" 
                                        role="menuitem" tabindex="-1" href="<%=data.getCourseStudentDetailsLink(student)
                                        +"&"+Const.ParamsNames.SHOW_COMMENT_BOX+"=student"%>">
                                        To student: <%=sanitizeForHtml(student.name)%></a></li>
                                    <li role="presentation"><a class="t_student_details_toteam-c<%=data.courseDetails.course.id %>.<%=idx%>"
                                        role="menuitem" tabindex="-1" href="<%=data.getCourseStudentDetailsLink(student)
                                        +"&"+Const.ParamsNames.SHOW_COMMENT_BOX+"=team"%>">
                                        To team: <%=sanitizeForHtml(student.team)%></a></li>
                                    <% if (student.section != null && !student.section.equals("None")) { %>
                                    <li role="presentation"><a class="t_student_details_tosection-c<%=data.courseDetails.course.id %>.<%=idx%>"
                                        role="menuitem" tabindex="-1" href="<%=data.getCourseStudentDetailsLink(student)
                                        +"&"+Const.ParamsNames.SHOW_COMMENT_BOX+"=section"%>">
                                        To section: <%=sanitizeForHtml(student.section)%></a></li>
                                    <% } %>
                                </ul>
                            </div>
                        </td>
                     </tr>
                 <%
                     if(idx%10==0) out.flush();
                 %>
            <%
                }
            %>
        </table>
        <br>
        <br>
        <br>
            
    </div>
    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>