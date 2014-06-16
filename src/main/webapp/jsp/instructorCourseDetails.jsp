<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

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
    <jsp:include page="../enableJS.jsp"></jsp:include>   

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]--> 
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
                                 value="Remind Students to Join" tabindex="1">
                         <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_LIST_DOWNLOAD%>" style="display:inline;">
                            <input id="button_download" type="submit" class="btn btn-primary"
                                name="<%=Const.ParamsNames.FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON%>"
                                value=" Download Student List ">
                            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                            <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.courseDetails.course.id%>">
                        </form>
                     </div>
                 </div>
                 <%
                     }
                 %>
            </div>
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
                            <a class="btn btn-default btn-xs t_student_details<%=idx%>"
                                    href="<%=data.getCourseStudentDetailsLink(student)%>"
                                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_DETAILS%>">
                                    View</a>
                            <a class="btn btn-default btn-xs t_student_edit<%=idx%>" href="<%=data.getCourseStudentEditLink(student)%>"
                                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_EDIT%>">
                                    Edit</a>
                            <%
                                if(data.getStudentStatus(student).equals(Const.STUDENT_COURSE_STATUS_YET_TO_JOIN)){
                            %>
                                <a class="btn btn-default btn-xs t_student_resend<%=idx%>" href="<%=data.getCourseStudentRemindLink(student)%>"
                                        onclick="return toggleSendRegistrationKey()"
                                        data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_REMIND%>">
                                        Send Invite</a>
                            <%
                                }
                            %>
                            <a class="btn btn-default btn-xs t_student_delete<%=idx%>" href="<%=data.getCourseStudentDeleteLink(student)%>"
                                    onclick="return toggleDeleteStudentConfirmation('<%=sanitizeForJs(student.name)%>')"
                                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_DELETE%>">
                                    Delete</a>
                            <a class="btn btn-default btn-xs t_student_records-c<%=data.courseDetails.course.id %>.<%=idx%>"
                                    href="<%=data.getStudentRecordsLinkWithAddComment(data.courseDetails.course.id, student)%>"
                                    data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_STUDENT_RECORDS%>"> 
                                    Add Comment</a>
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