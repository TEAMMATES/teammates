<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.util.FieldValidator"%>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>
<%@ page import="teammates.ui.controller.InstructorCoursesPageData"%>
<%
    InstructorCoursesPageData data = (InstructorCoursesPageData)request.getAttribute("data");
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
    <script type="text/javascript" src="/js/instructorCourses.js"></script>
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
            <h1>Add New Course</h1>
        </div>
        
        <div class="panel panel-primary">
            <div class="panel-body fill-plain">
                <form method="get" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_ADD%>" name="form_addcourse" class="form form-horizontal">
                    <input type="hidden" id="<%=Const.ParamsNames.INSTRUCTOR_ID%>" name="<%=Const.ParamsNames.INSTRUCTOR_ID%>" value="<%=data.account.googleId%>">
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                    <div class="form-group">
                        <label class="col-sm-3 control-label">Course ID:</label>
                        <div class="col-sm-3"><input class="form-control" type="text"
                            name="<%=Const.ParamsNames.COURSE_ID%>" id="<%=Const.ParamsNames.COURSE_ID%>"
                            value="<%=(sanitizeForHtml(data.courseIdToShow))%>"
                            data-toggle="tooltip" data-placement="top" title="Enter the identifier of the course, e.g.CS3215-2013Semester1."
                            maxlength=<%=FieldValidator.COURSE_ID_MAX_LENGTH%> tabindex="1"
                            placeholder="e.g. CS3215-2013Semester1" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">Course Name:</label>
                        <div class="col-sm-9"><input class="form-control" type="text"
                            name="<%=Const.ParamsNames.COURSE_NAME%>" id="<%=Const.ParamsNames.COURSE_NAME%>"
                            value="<%=(sanitizeForHtml(data.courseNameToShow))%>"
                            data-toggle="tooltip" data-placement="top" title="Enter the name of the course, e.g. Software Engineering."
                            maxlength=<%=FieldValidator.COURSE_NAME_MAX_LENGTH%> tabindex=2
                            placeholder="e.g. Software Engineering" />
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-9"><input id="btnAddCourse" type="submit" class="btn btn-primary"
                                onclick="return verifyCourseData();" value="Add Course" tabindex="3">
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <br>
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <br>
        
        <h2>Active courses</h2>
        
        <table class="table table-bordered table-striped">
            <thead class="fill-primary">
                <tr>
                    <th>Course ID</th>
                    <th>Course Name</th>
                    <th>Teams</th>
                    <th>Total Students</th>
                    <th>Total Unregistered</th>
                    <th class="no-print">Action(s)</th>
                </tr>
            </thead>
            <%
                int idx = -1;
                for(CourseDetailsBundle courseDetails: data.allCourses){ 
                    if (!courseDetails.course.isArchived) {
                        idx++;
            %>
                <tr class="courses_row">
                    <td id="courseid<%=idx%>"><%=sanitizeForHtml(courseDetails.course.id)%></td>
                    <td id="coursename<%=idx%>"><%=sanitizeForHtml(courseDetails.course.name)%></td>
                    <td class="t_course_teams centeralign"><%=courseDetails.stats.teamsTotal%></td>
                    <td class="centeralign"><%=courseDetails.stats.studentsTotal%></td>
                    <td class="centeralign"><%=courseDetails.stats.unregisteredTotal%></td>
                    <td class="centeralign no-print">
                        <a class="btn btn-default btn-xs t_course_enroll<%=idx%>"
                            href="<%=data.getInstructorCourseEnrollLink(courseDetails.course.id)%>"
                            data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_ENROLL%>">
                            Enroll</a>
                        <a class="btn btn-default btn-xs t_course_view<%=idx%>"
                            href="<%=data.getInstructorCourseDetailsLink(courseDetails.course.id)%>"
                            data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_DETAILS%>">
                            View</a>
                        <a class="btn btn-default btn-xs t_course_edit<%=idx%>"
                            href="<%=data.getInstructorCourseEditLink(courseDetails.course.id)%>"
                            data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_EDIT%>">
                            Edit</a>
                        <a class="btn btn-default btn-xs t_course_delete<%=idx%>"
                            href="<%=data.getInstructorCourseDeleteLink(courseDetails.course.id,false)%>"
                            onclick="return toggleDeleteCourseConfirmation('<%=courseDetails.course.id%>');"
                            data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_DELETE%>">
                            Delete</a>
                        <a class="btn btn-default btn-xs t_course_archive<%=idx%>"
                            href="<%=data.getInstructorCourseArchiveLink(courseDetails.course.id, true, false)%>"
                            data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_ARCHIVE%>">
                            Archive</a>
                    </td>
                </tr>
            <%
                    }
                }
                if(idx==-1){ // Print empty row
            %>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
            <%
                }
            %>
        </table>
        <br>
        <br>
        <%
            if(idx==-1){
        %>
            No records found. <br>
            <br>
        <%
            }
        %>
        <br>
        <br>
        
        <%
            if (!data.archivedCourses.isEmpty()) {
        %>
        
        <h2 class="centeralign">Archived courses</h2>
        
        <table class="dataTable" style="width:600px">
            <tr>
                <th><input class="buttonSortAscending" type="button"
                    id="button_sortcourseid"
                    onclick="toggleSort(this,1);">
                    Course ID</th>
                <th><input class="buttonSortNone" type="button"
                    id="button_sortcoursename"
                    onclick="toggleSort(this,2);">
                    Course Name</th>
                <th class="centeralign no-print">Action(s)</th>
            </tr>
            <%
                for (CourseAttributes course: data.archivedCourses) { 
                    idx++;
            %>
                <tr class="courses_row">
                    <td id="courseid<%=idx%>"><%=sanitizeForHtml(course.id)%></td>
                    <td id="coursename<%=idx%>"><%=sanitizeForHtml(course.name)%></td>
                    <td class="centeralign no-print">
                        <a class="color_black t_course_delete<%=idx%>"
                            href="<%=data.getInstructorCourseDeleteLink(course.id,false)%>"
                            onclick="return toggleDeleteCourseConfirmation('<%=course.id%>');"
                            data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COURSE_DELETE%>">
                            Delete</a>
                        <a class="color_black t_course_unarchive<%=idx%>"
                            href="<%=data.getInstructorCourseArchiveLink(course.id, false, false)%>">
                            Unarchive</a>
                    </td>
                </tr>
            <%
                }
                if(idx==-1){ // Print empty row
            %>
                <tr>
                    <td></td>
                    <td></td>
                    <td></td>
                </tr>
            <%
                }
            %>
        </table>
        <br>
        <br>
        <br>
        <br>
        <%
            }
        %>
    </div>

    <div id="frameBottom">
        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </div>
</body>
</html>