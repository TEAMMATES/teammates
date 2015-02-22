<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.StringHelper"%>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.SectionDetailsBundle" %>
<%@ page import="teammates.common.datatransfer.TeamDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.InstructorAttributes" %>
<%@ page import="teammates.common.datatransfer.CourseAttributes" %>
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
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>TEAMMATES - Instructor</title>
        <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css"/>
        <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>
        <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css"/>

        <script type="text/javascript" src="/js/googleAnalytics.js"></script>
        <script type="text/javascript" src="/js/jquery-minified.js"></script>
        <script type="text/javascript" src="/js/common.js"></script>
        
        <script type="text/javascript" src="/js/instructor.js"></script>
        <script type="text/javascript" src="/js/instructorStudentList.js"></script>
        <script type="text/javascript" src="/js/instructorStudentListAjax.js"></script>

        <jsp:include page="../enableJS.jsp"></jsp:include>
        <script type="text/javascript"  src="/bootstrap/js/bootstrap.min.js"></script>
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
            <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>

    <body>
        <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />    
        <div id="frameBodyWrapper" class="container theme-showcase">
            <div id="topOfPage"></div>
            <h1>Find Students</h1>
            <div class="well well-plain">
                <div class="row">
                    <div class="col-md-12">
                        <form method="get" action="<%=data.getInstructorSearchLink()%>" name="search_form">
                            <div class="row">
                                <div class="col-md-10">
                                    <div class="form-group">
                                        <input type="text" id="searchbox"
                                            title="<%=Const.Tooltips.SEARCH_STUDENT%>"
                                            name="<%=Const.ParamsNames.SEARCH_KEY%>"
                                            class="form-control"
                                            data-toggle="tooltip"
                                            data-placement="top"
                                            placeholder="e.g. Charles Shultz"
                                            value="<%=data.searchKey == null ? "" : PageData.sanitizeForHtml(data.searchKey) %>">
                                    </div>
                                </div>
                                <div class="col-md-2 nav">
                                    <div class="form-group">
                                        <button id="buttonSearch" class="btn btn-primary" type="submit" value="Search">
                                            <span class="glyphicon glyphicon-search"></span> Find students
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <input type="hidden" name="<%=Const.ParamsNames.SEARCH_STUDENTS%>" value="true">
                            <input type="hidden" name="<%=Const.ParamsNames.SEARCH_COMMENTS_FOR_STUDENTS%>" value="false">
                            <input type="hidden" name="<%=Const.ParamsNames.SEARCH_COMMENTS_FOR_RESPONSES%>" value="false">
                            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                        </form>
                     </div>
                 </div>
             </div>
            <h1>Filter Students</h1>
            <div id="moreOptionsDiv" class="well well-plain" <% if(data.courses.size() == 0){ %> style="display:none;" <% } %>>
                <div class="row">
                    <div class="col-md-3">
                        <div class="checkbox">
                            <input id="displayArchivedCourses_check" type="checkbox" <%if(data.displayArchive){%>checked="checked"<%}%>>
                            <label for="displayArchivedCourses_check">Display Archived Courses</label>
                        </div>
                    </div>
                </div>
                <form class="form-horizontal" role="form">
                    <div class="row">
                        <div class="col-sm-3">
                            <div class="text-color-primary">
                                <strong>Courses</strong>
                            </div>
                            <br>
                            <div class="checkbox">
                                <input type="checkbox" value="" id="course_all"> 
                                <label for="course_all"><strong>Select all</strong></label>
                            </div>
                            <br>
                            <%
                                int courseIdx = -1;
                                for(CourseAttributes course: data.courses){
                                    InstructorAttributes instructor = data.instructors.get(course.id);
                                    if(data.displayArchive || !data.isCourseArchived(course.id, instructor.googleId)){
                                        courseIdx++;
                            %>
                                <div class="checkbox"><input id="course_check-<%=courseIdx %>" type="checkbox">
                                    <label for="course_check-<%=courseIdx %>">
                                    [<%=course.id%>] : <%=PageData.sanitizeForHtml(course.name)%>
                                    </label>
                                </div>
                            <%
                                    }
                                }
                            %>
                        </div>
                         
                        <div class="col-sm-3">
                            <div class="text-color-primary">
                                <strong>Sections</strong>
                            </div>
                            <br>
                            <div class="checkbox" style="display:none;">
                                <input type="checkbox" value="" id="section_all"> 
                                <label for="section_all"><strong>Select all</strong></label>
                            </div>
                            <br>
                            <div id="sectionChoices">
                     
                            </div>
                        </div>
                          
                        <div class="col-sm-3">
                            <div class="text-color-primary">
                                <strong>Teams</strong>
                            </div>
                            <br>
                            <div class="checkbox" style="display:none;">
                                <input id="team_all" type="checkbox">
                                <label for="team_all"><strong>Select All</strong></label>
                            </div>
                            <br>
                            <div id="teamChoices">

                            </div>
                        </div>
                        
                        <div class="col-sm-3">
                            <div class="text-color-primary">
                                <strong>Emails</strong>
                            </div>
                            <br>
                            <div class="checkbox" style="display:none;">
                                <input id="show_email" type="checkbox" checked="checked">
                                    <label for="show_email"><strong>Show Emails</strong></label>
                            </div>
                            <br>
                            <div id="emails">
                                
                            </div>
                        </div>
                    </div>
                </form>
            </div>
    
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            
            <%
                if(data.courses.size() > 0) {
            %>
            
            <br>
            <div class="text-muted">
                Click on the panels below to expand
            </div>
            <br>
            
            <%
                }
            %>
            
            <%
                courseIdx = -1;
                
                for (CourseAttributes course : data.courses) {
                    InstructorAttributes instructor = data.instructors.get(course.id);
                    if(data.displayArchive || !data.isCourseArchived(course.id, instructor.googleId) ){
                        courseIdx++;
            %>

            <div class='panel <%= data.isCourseArchived(course.id, instructor.googleId) ? "panel-default" : "panel-info" %>'>
                <div class="panel-heading ajax_submit">
                    <form style="display:none;" id="seeMore-<%=courseIdx%>" class="seeMoreForm-<%=courseIdx%>" action="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_AJAX_PAGE%>">
                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=course.id%>">
                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
                        <input type="hidden" id="numStudents-<%=courseIdx%>" value="<%=data.numStudents.get(course.id)%>">
                    </form>
                    <a class="btn btn-default btn-xs pull-right pull-down course-enroll-for-test"
                                id="enroll-<%=courseIdx%>"
                                href="<%=data.getInstructorCourseEnrollLink(course.id)%>"
                                title="<%=Const.Tooltips.COURSE_ENROLL%>"
                                data-toggle="tooltip" data-placement="top"
                                <% if (!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) { %>
                                disabled="disabled"
                                <% } %>>
                                    <span class="glyphicon glyphicon-list"></span> Enroll
                    </a>
                    <div class='display-icon pull-right'>
                    </div>
                    <strong>[<%=course.id%>] : </strong><%=PageData.sanitizeForHtml(course.name)%>
                </div>
                <div class="panel-collapse collapse">
                <div class="panel-body padding-0">    
            
                </div>
                </div>
            </div>
            <%
                    out.flush();
                    }
                }
            %>
        </div>

        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </body>
</html>