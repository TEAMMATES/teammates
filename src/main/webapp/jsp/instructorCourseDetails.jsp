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
    <script type="text/javascript" src="/js/tooltip.js"></script>
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

    <div class="container">
        <div id="topOfPage"></div>
        
        <div id="headerOperation">
            <h1>Team Details for <%=data.courseDetails.course.id%></h1>
        </div>
        <br>

        <div class="well well-plain well-narrow" id="courseInformationHeader">
            <div class="form form-horizontal">
                <div class="form-group">
                    <label class="col-sm-3 control-label">Course ID:</label>
                    <td id="courseid"><%=sanitizeForHtml(data.courseDetails.course.id)%></td>
                </div>
                 <tr>
                     <label class="col-sm-3 control-label">Course name:</label>
                     <td id="coursename"><%=sanitizeForHtml(data.courseDetails.course.name)%></td>
                </tr>
                <tr>
                     <label class="col-sm-3 control-label">Teams:</label>
                     <td id="total_teams"><%=data.courseDetails.stats.teamsTotal%></td>
                 </tr>
                 <tr>
                     <label class="col-sm-3 control-label">Total students:</label>
                     <td id="total_students"><%=data.courseDetails.stats.studentsTotal%></td>
                 </tr>
                 <tr>
                     <label class="col-sm-3 control-label">Instructors:</label>
                     <td id="instructors">
                     <%
                         for (int i = 0; i < data.instructors.size(); i++){
                                                                                             InstructorAttributes instructor = data.instructors.get(i);
                                                                                             String instructorInfo = instructor.name + " (" + instructor.email + ")";
                     %>
                                 <%=sanitizeForHtml(instructorInfo)%><br><br>
                             <%
                                 }
                             %>
                    </td>
                 </tr>
                 <%
                     if(data.courseDetails.stats.studentsTotal>1){
                 %>
                 <tr>
                     <td class="centeralign" colspan="2">
                         <input type="button" class="button t_remind_students"
                                 id="button_remind"
                                 onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_REMIND%>')" 
                                 onmouseout="hideddrivetip();"
                                 onclick="hideddrivetip(); if(toggleSendRegistrationKeysConfirmation('<%=data.courseDetails.course.id%>')) window.location.href='<%=data.getInstructorCourseRemindLink()%>';"
                                 value="Remind Students to Join" tabindex="1">
                         <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_LIST_DOWNLOAD%>" style="display:inline;">
                            <input id="button_download" type="submit" class="button"
                                name="<%=Const.ParamsNames.FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON%>"
                                value=" Download Student List ">
                            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                            <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.courseDetails.course.id%>">
                        </form>
                     </td>
                 </tr>
                 <%
                     }
                 %>
            </div>
        </div>
            
            <br>
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            <br>

            <table class="dataTable">
                <tr>
                    <th class="centeralign color_white bold"><input class="buttonSortNone" type="button" id="button_sortstudentteam"
                            onclick="toggleSort(this,1)">Team</th>
                    <th class="centeralign color_white bold"><input class="buttonSortAscending" type="button" id="button_sortstudentname" 
                            onclick="toggleSort(this,2)">Student Name</th>
                    <th class="centeralign color_white bold"><input class="buttonSortNone" type="button" id="button_sortstudentstatus"
                            onclick="toggleSort(this,3)">Status</th>
                    <th class="centeralign color_white bold no-print">Action(s)</th>
                </tr>
                <%
                    int idx = -1;
                                                                for(StudentAttributes student: data.students){ idx++;
                %>
                        <tr class="student_row" id="student<%=idx%>">
                            <td id="<%=Const.ParamsNames.TEAM_NAME%>"><%=sanitizeForHtml(student.team)%></td>
                            <td id="<%=Const.ParamsNames.STUDENT_NAME%>"><%=sanitizeForHtml(student.name)%></td>
                             <td class="centeralign"><%=data.getStudentStatus(student)%></td>
                             <td class="centeralign no-print">
                                <a class="color_black t_student_details<%=idx%>"
                                        href="<%=data.getCourseStudentDetailsLink(student)%>"
                                        onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_DETAILS%>')"
                                        onmouseout="hideddrivetip()">
                                        View</a>
                                <a class="color_black t_student_edit<%=idx%>" href="<%=data.getCourseStudentEditLink(student)%>"
                                        onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_EDIT%>')"
                                        onmouseout="hideddrivetip()">
                                        Edit</a>
                                <%
                                    if(data.getStudentStatus(student).equals(Const.STUDENT_COURSE_STATUS_YET_TO_JOIN)){
                                %>
                                    <a class="color_black t_student_resend<%=idx%>" href="<%=data.getCourseStudentRemindLink(student)%>"
                                            onclick="return toggleSendRegistrationKey()"
                                            onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_REMIND%>')"
                                            onmouseout="hideddrivetip()">
                                            Send Invite</a>
                                <%
                                    }
                                %>
                                <a class="color_black t_student_delete<%=idx%>" href="<%=data.getCourseStudentDeleteLink(student)%>"
                                        onclick="return toggleDeleteStudentConfirmation('<%=sanitizeForJs(student.name)%>')"
                                        onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_DELETE%>')"
                                        onmouseout="hideddrivetip()">
                                        Delete</a>
                                <a class="color_black t_student_records-c<%=data.courseDetails.course.id %>.<%=idx%>"
                                        href="<%=data.getStudentRecordsLinkWithAddComment(data.courseDetails.course.id, student)%>"
                                        onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_RECORDS%>')"
                                        onmouseout="hideddrivetip()"> 
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