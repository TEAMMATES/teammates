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
                                        <span class="glyphicon glyphicon-search"></span> Search
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
            <br>
            <div id="moreOptionsDiv" class="well well-plain" style="display: none;">
                <form class="form-horizontal" role="form">
                    <div class="row">
                        <div class="col-sm-4">
                            <div class="text-color-primary">
                                <strong>Courses</strong>
                            </div>
                            <br>
                            <div class="checkbox">
                                <input type="checkbox" value="" id="course_all" checked="checked"> 
                                <label for="course_all"><strong>Select all</strong></label>
                            </div>
                            <br>
                            <%
                                int courseIdx = -1;
                                for(CourseDetailsBundle courseDetails: data.courses){
                                    if((courseDetails.course.isArchived && data.displayArchive) || !courseDetails.course.isArchived){
                                        courseIdx++;
                            %>
                                <div class="checkbox"><input id="course_check-<%=courseIdx %>" type="checkbox" checked="checked">
                                    <label for="course_check-<%=courseIdx %>">
                                    [<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(courseDetails.course.name)%>
                                    </label>
                                </div>
                            <%
                                    }
                                }
                            %>
                        </div>
    
                        <div class="col-sm-4">
                            <div class="text-color-primary">
                                <strong>Teams</strong>
                            </div>
                            <br>
                            <div class="checkbox">
                                <input id="team_all" type="checkbox" checked="checked">
                                <label for="team_all"><strong>Select All</strong></label>
                            </div>
                            <br>
                            <%
                                courseIdx = -1;
                                for(CourseDetailsBundle courseDetails: data.courses){
                                    if((courseDetails.course.isArchived && data.displayArchive) || !courseDetails.course.isArchived){
                                        courseIdx++;
                                        int teamIdx = -1;
                                        for(TeamDetailsBundle teamDetails: courseDetails.teams){
                                            teamIdx++;
                            %>
                                <div class="checkbox">
                                    <input id="team_check-<%=courseIdx %>-<%=teamIdx %>" type="checkbox" checked="checked">
                                    <label for="team_check-<%=courseIdx %>-<%=teamIdx%>">
                                    [<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(teamDetails.name)%>
                                    </label>
                                </div>
                            <%
                                        }
                                    }
                                }
                            %>
                        </div>
                        <div class="col-sm-4">
                            <div class="text-color-primary">
                                <strong>Emails</strong>
                            </div>
                            <br>
                            <div class="checkbox">
                                <input id="show_email" type="checkbox" checked="checked">
                                    <label for="show_email"><strong>Show Emails</strong></label>
                            </div>
                            <br>
                            <div id="emails">
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
                                        <div id="student_email-c<%=courseIdx %>.<%=studentIdx%>"><%=student.email %></div>
                                <%
                                                    }
                                                }
                                            }
                                        }
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
    
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            <%
                courseIdx = -1;
                for (CourseDetailsBundle courseDetails : data.courses) {
                    if((courseDetails.course.isArchived && data.displayArchive) || !courseDetails.course.isArchived){
                        courseIdx++;
                        int totalCourseStudents = courseDetails.stats.studentsTotal;
            %>

            <div class="well well-plain" id="course-<%=courseIdx%>">
                <div class="row">
                    <div class="col-md-10 text-color-primary">
                        <h4>
                            <strong>
                                [<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(courseDetails.course.name)%>
                            </strong>
                        </h4>
                    </div>
                    <div class="col-md-2">
                        <a class="btn btn-default btn-xs pull-right pull-down"
                            href="<%=data.getInstructorCourseEnrollLink(courseDetails.course.id)%>"
                            title="<%=Const.Tooltips.COURSE_ENROLL%>"
                            data-toggle="tooltip"
                            data-placement="top">
                                <span class="glyphicon glyphicon-list"></span> Enroll
                        </a>
                    </div>
                </div>
                <%
                    if (totalCourseStudents > 0) {
                %>
                        <table class="table table-responsive table-striped table-bordered">
                            <thead>
                                <tr class="fill-primary">
                                    <th id="button_sortteam" class="button-sort-ascending" onclick="toggleSort(this,1)">
                                        Team <span class="sort-icon ascending-sorted"></span>
                                    </th>
                                    <th id="button_sortstudentname" class="button-sort-none" onclick="toggleSort(this,2)">
                                        Student Name <span class="sort-icon unsorted"></span>
                                    </th>
                                    <th id="button_sortteam" class="button-sort-none" onclick="toggleSort(this,3)"> 
                                        Email <span class="sort-icon unsorted"></span>
                                    </th>
                                    <th>Action(s)
                                    </th>
                                </tr>
                            </thead>
                            <%
                                    int teamIdx = -1;
                                    int studentIdx = -1;
                                    for(TeamDetailsBundle teamDetails: courseDetails.teams){
                                        teamIdx++;
                                        for(StudentAttributes student: teamDetails.students){
                                            studentIdx++;
                            %>
                            <tr id="student-c<%=courseIdx %>.<%=studentIdx%>" style="display: table-row;">
                                <td id="studentteam-c<%=courseIdx %>.<%=teamIdx%>"><%=PageData.sanitizeForHtml(teamDetails.name)%></td>
                                <td id="studentname-c<%=courseIdx %>.<%=studentIdx%>"><%=PageData.sanitizeForHtml(student.name)%></td>
                                <td id="studentemail-c<%=courseIdx %>.<%=studentIdx%>"><%=PageData.sanitizeForHtml(student.email)%></td>
                                <td class="no-print centeralign">
                                    <a class="btn btn-default btn-xs" 
                                    href="<%=data.getCourseStudentDetailsLink(courseDetails.course.id, student)%>"
                                    title="<%=Const.Tooltips.COURSE_STUDENT_DETAILS%>"
                                    data-toggle="tooltip"
                                    data-placement="top"> View</a> 
                                    
                                    <a class="btn btn-default btn-xs"
                                    href="<%=data.getCourseStudentEditLink(courseDetails.course.id, student)%>"
                                    title="<%=Const.Tooltips.COURSE_STUDENT_EDIT%>"
                                    data-toggle="tooltip"
                                    data-placement="top"> Edit</a> 
                                    
                                    <a class="btn btn-default btn-xs"
                                    href="<%=data.getCourseStudentDeleteLink(courseDetails.course.id, student)%>"
                                    onclick="return toggleDeleteStudentConfirmation('<%=sanitizeForJs(courseDetails.course.id)%>','<%=sanitizeForJs(student.name)%>')"
                                    title="<%=Const.Tooltips.COURSE_STUDENT_DELETE%>"
                                    data-toggle="tooltip"
                                    data-placement="top"> Delete</a>
                                    
                                    <a class="btn btn-default btn-xs"
                                    href="<%=data.getStudentRecordsLink(courseDetails.course.id, student)%>"
                                    title="<%=Const.Tooltips.COURSE_STUDENT_RECORDS%>"
                                    data-toggle="tooltip"
                                    data-placement="top"> All Records</a>
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
                        <table class="table table-responsive table-striped table-bordered">
                            <thead>
                                <tr class="fill-primary">
                                    <th class="centeralign color_white bold"><%=Const.StatusMessages.INSTRUCTOR_COURSE_EMPTY %></th>
                                </tr>
                            </thead>
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