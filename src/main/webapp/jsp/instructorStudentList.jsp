<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.TeamDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForJs"%>
<%@ page import="teammates.ui.controller.InstructorStudentListPageData"%>
<%
    InstructorStudentListPageData data = (InstructorStudentListPageData) request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="shortcut icon" href="/favicon.png" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>TEAMMATES - Instructor</title>
        <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css"/>
        <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>
        <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css"/>

        <script type="text/javascript" src="/js/googleAnalytics.js"></script>
        <script type="text/javascript" src="/js/jquery-minified.js"></script>
        <script type="text/javascript" src="/js/tooltip.js"></script>
        <script type="text/javascript" src="/js/date.js"></script>
        <script type="text/javascript" src="/js/CalendarPopup.js"></script>
        <script type="text/javascript" src="/js/AnchorPosition.js"></script>
        <script type="text/javascript" src="/js/common.js"></script>
        <script type="text/javascript"  src="/bootstrap/js/bootstrap.min.js"></script>
        
        <script type="text/javascript" src="/js/instructor.js"></script>
        <script type="text/javascript" src="/js/instructorStudentList.js"></script>
        <jsp:include page="../enableJS.jsp"></jsp:include>
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
            <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>

    <body>
        <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />    
        <div id="frameBodyWrapper" class="container theme-showcase">
                <div id="topOfPage"></div>
                <h1>Instructor Students List</h1>
                <div class="well well-plain">
                    <div class="row">
                        <div class="col-md-12">
                            <div class="row">
                                <div class="col-md-10">
                                    <div class="form-group">
                                        <input type="text" id="searchbox"
                                            title="<%=Const.Tooltips.SEARCH_STUDENT%>"
                                            class="form-control"
                                            data-toggle="tooltip"
                                            data-placement="top"
                                            placeholder="e.g. Charles Shultz"
                                            value="<%=data.searchKey == null ? "" : PageData.sanitizeForHtml(data.searchKey) %>">
                                    </div>
                                </div>
                                <div class="col-md-2 nav">
                                    <div class="form-group">
                                        <button id="button_search" class="btn btn-primary" type="submit" onclick="return applyFilters();" value="Search">
                                            <span class="glyphicon glyphicon-search"></span>Search
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-2">
                                    <div class="checkbox">
                                        <input id="option_check" type="checkbox">
                                        <label for="option_check">Show More Options</label>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="checkbox">
                                        <input id="displayArchivedCourses_check" type="checkbox" <%if(data.displayArchive){%>checked="checked"<%}%>>
                                        <label for="displayArchivedCourses_check">Display Archived Courses</label>
                                    </div>
                                </div>
                            </div>
                         </div>
                     </div>
                 </div>
                <br><br>
                <table class="inputTable" id="optionsTable" style="display: none;">    
                    <tr>
                        <td width="250px">
                            <h4 class="bold">Courses</h4>
                            <div class="leftalign" id="course_checkboxes">
                            <br>
                                <ul>
                                <li>
                                    <input id="course_all" type="checkbox" checked="checked">
                                    <label for="course_all" class="bold">Select All</label>
                                </li>
                                <%
                                    int courseIdx = -1;
                                    for(CourseDetailsBundle courseDetails: data.courses){
                                        if((courseDetails.course.isArchived && data.displayArchive) || !courseDetails.course.isArchived){
                                            courseIdx++;
                                %>
                                    <li><input class="course_check" id="course_check-<%=courseIdx %>" type="checkbox" checked="checked">
                                        <label for="course_check-<%=courseIdx %>">
                                        [<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(courseDetails.course.name)%>
                                        </label>
                                    </li>
                                <%
                                        }
                                    }
                                %>
                                </ul>
                            <br>
                            </div>
                        </td>
                        <td width="250px">
                            <h4 class="bold">Teams</h4>
                            <div class="leftalign" id="team_checkboxes">
                            <br>
                            <ul>
                                <li>
                                    <input id="team_all" type="checkbox" checked="checked">
                                    <label for="team_all" class="bold">Select All</label>
                                </li>
                                <%
                                    courseIdx = -1;
                                    for(CourseDetailsBundle courseDetails: data.courses){
                                        if((courseDetails.course.isArchived && data.displayArchive) || !courseDetails.course.isArchived){
                                            courseIdx++;
                                            int teamIdx = -1;
                                            for(TeamDetailsBundle teamDetails: courseDetails.teams){
                                                teamIdx++;
                                %>
                                    <li><input class="team_check" id="team_check-<%=courseIdx %>-<%=teamIdx %>" type="checkbox" checked="checked">
                                        <label for="team_check-<%=courseIdx %>-<%=teamIdx%>">
                                        [<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(teamDetails.name)%>
                                        </label>
                                    </li>
                                <%
                                            }
                                        }
                                    }
                                %>
                            </ul>
                            <br>
                            </div>
                        </td>
                        <td width="250px">
                            <h4 class="bold">
                                <input id="show_email" type="checkbox">
                                    <label for="show_email">
                                    Show Emails
                                    </label>
                            </h4>
                            <div class="leftalign" id="emails" style="display: none;">
                            <br>
                            <ul>
                            <%
                                
                                courseIdx = -1;
                                for(CourseDetailsBundle courseDetails: data.courses){
                                    if((courseDetails.course.isArchived && data.displayArchive) || !courseDetails.course.isArchived){
                                        courseIdx++;
                                        int totalCourseStudents = courseDetails.stats.studentsTotal;
                                        if(totalCourseStudents >= 1){
                                            int studentIdx = -1;
                                            for(TeamDetailsBundle teamDetails: courseDetails.teams){
                                                for(StudentAttributes student: teamDetails.students){
                                                    studentIdx++;
                            %>
                                    <li class="student_email" id="student_email-c<%=courseIdx %>.<%=studentIdx%>" style="display: list-item;"><%=student.email %></li>
                            <%
                                                }
                                            }
                                        }
                                    }
                                }
                            %>
                            </ul>
                            <br>
                            </div>
                        </td>
                    </tr>
                </table>
    
                <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
                <%
                    courseIdx = -1;
                    for (CourseDetailsBundle courseDetails : data.courses) {
                        if((courseDetails.course.isArchived && data.displayArchive) || !courseDetails.course.isArchived){
                            courseIdx++;
                            int totalCourseStudents = courseDetails.stats.studentsTotal;
                %>
    
                <div class="backgroundBlock" id="course-<%=courseIdx%>">
                    <div class="courseTitle">
                        <h2 class="color_white">
                            [<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(courseDetails.course.name)%>
                        </h2>
                    </div>
                    <div class="enrollLink blockLink rightalign">
                        <a class="t_course_enroll-<%=courseIdx%> color_white bold"
                            href="<%=data.getInstructorCourseEnrollLink(courseDetails.course.id)%>"
                            title="<%=Const.Tooltips.COURSE_ENROLL%>"
                            data-toggle="tooltip"
                            data-placement="top"> Enroll Students</a>
                    </div>
                    <div style="clear: both;"></div>
                    <br>
                    <%
                            if (totalCourseStudents > 0) {
                    %>
                    <table class="dataTable">
                        <tr>
                            <th class="leftalign color_white bold">
                                <input class="buttonSortAscending" type="button"
                                id="button_sortteam" onclick="toggleSort(this,1)">Team
                            </th>
                            <th class="leftalign color_white bold"><input
                                class="buttonSortNone" type="button"
                                id="button_sortstudentname" onclick="toggleSort(this,2)">Student Name</th>
                            <th class="centeralign color_white bold no-print">Action(s)</th>
                        </tr>
                        <%
                                int teamIdx = -1;
                                int studentIdx = -1;
                                for(TeamDetailsBundle teamDetails: courseDetails.teams){
                                    teamIdx++;
                                    for(StudentAttributes student: teamDetails.students){
                                        studentIdx++;
                        %>
                        <tr class="student_row" id="student-c<%=courseIdx %>.<%=studentIdx%>" style="display: table-row;">
                            <td id="studentteam-c<%=courseIdx %>.<%=teamIdx%>"><%=PageData.sanitizeForHtml(teamDetails.name)%></td>
                            <td id="studentname-c<%=courseIdx %>.<%=studentIdx%>"><%=PageData.sanitizeForHtml(student.name)%></td>
                            <td class="centeralign no-print">
                                <a class="color_black t_student_details-c<%=courseIdx %>.<%=studentIdx%>" 
                                href="<%=data.getCourseStudentDetailsLink(courseDetails.course.id, student)%>"
                                onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_DETAILS%>')"
                                onmouseout="hideddrivetip()"> View</a> 
                                
                                <a class="color_black t_student_edit-c<%=courseIdx %>.<%=studentIdx%>"
                                href="<%=data.getCourseStudentEditLink(courseDetails.course.id, student)%>"
                                onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_EDIT%>')"
                                onmouseout="hideddrivetip()"> Edit</a> 
                                
                                <a class="color_black t_student_delete-c<%=courseIdx %>.<%=studentIdx%>"
                                href="<%=data.getCourseStudentDeleteLink(courseDetails.course.id, student)%>"
                                onclick="return toggleDeleteStudentConfirmation('<%=sanitizeForJs(courseDetails.course.id)%>','<%=sanitizeForJs(student.name)%>')"
                                onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_DELETE%>')"
                                onmouseout="hideddrivetip()"> Delete</a>
                                
                                <a class="color_black t_student_records-c<%=courseIdx %>.<%=studentIdx%>"
                                href="<%=data.getStudentRecordsLink(courseDetails.course.id, student)%>"
                                onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_STUDENT_RECORDS%>')"
                                onmouseout="hideddrivetip()"> All Records</a>
                            </td>
                        </tr>
                        <%
                                    }
                                }
                        %>
                    </table>
                    <%
                            } else {
                    %>
                    <table class="dataTable">
                        <tr>
                            <th class="centeralign color_white bold"><%=Const.StatusMessages.INSTRUCTOR_COURSE_EMPTY %></th>
                        </tr>
                    </table>
                    <%
                            }
                    %>
                </div>
                <%
                        out.flush();
                        }
                    }
                %>
            <br> <br> <br>
        </div>

        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </body>
</html>