<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.CourseSummaryBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="teammates.ui.controller.InstructorHomePageData"%>
<%
    InstructorHomePageData data = (InstructorHomePageData)request.getAttribute("data");
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
    <script type="text/javascript" src="/js/instructorHome.js"></script>
    <script type="text/javascript" src="/js/ajaxResponseRate.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>

</head>

<body>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div class="container theme-showcase">
        <div id="topOfPage"></div>
        <div class="inner-container">
            <div class="row">
                <div class="col-md-5">
                    <h1>Instructor Home</h1>
                </div>
                <div class="col-md-5 instructor-header-bar">
                    <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE%>" name="search_form">
                        <div class="input-group">
                            <input type="text" name=<%=Const.ParamsNames.SEARCH_KEY %>
                                    onmouseover="ddrivetip('<%=Const.Tooltips.SEARCH_STUDENT%>')"
                                    onmouseout="hideddrivetip()" class="form-control" placeholder="Student Name">
                            <span class="input-group-btn">
                                <button class="btn btn-default" type="submit" value="Search">Search</button>
                            </span>
                        </div>
                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                    </form>
                </div>
                <div class="col-md-2 instructor-header-bar">
                    <a class="btn btn-primary btn-md" href="<%=data.getInstructorCourseLink() %>" id="addNewCourse">Add New Course </a>
                </div>
            </div>
        </div>
        <br>
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        
        <div class="inner-container well well-plain">
            <div class="row text-center">
            
                <div class="col-md-4">
                    <input type="radio" name="sortby" value="id" <%= data.sortCriteria.equals(Const.SORT_BY_COURSE_ID) ? "checked" : "" %>>
                    <label class="label-control" name="sortby" value="id"> Sort by Course ID </label>
                </div>
                <div class="col-md-4">
                    <input type="radio" name="sortby" value="name" <%= data.sortCriteria.equals(Const.SORT_BY_COURSE_NAME) ? "checked" : "" %>>
                    <label class="label-control" name="sortby" value="name">Sort by Course Name<label>
                </div>
                <div class="col-md-4">
                    <input type="radio" name="sortby" value="createdAt" <%= data.sortCriteria.equals(Const.SORT_BY_COURSE_CREATION_DATE) ? "checked" : "" %>>
                    <label class="label-control" name="sortby" value="createdAt">Sort by Course Creation Date </label>
                </div>
            </div>
        </div>
        <br>
    <%
        int courseIdx = -1;
        int sessionIdx = -1;
        for (CourseSummaryBundle courseDetails : data.courses) {
            // TODO: optimize in future
            // We may be able to reduce database reads here because we don't need to retrieve certain data for archived courses
            if (!courseDetails.course.isArchived) {
                courseIdx++;
    %>
                <div class="panel panel-primary" id="course<%=courseIdx%>">
                    <div class="panel-heading">
                        <strong class="color_white">
                            [<%=courseDetails.course.id%>] :
                            <%=PageData.sanitizeForHtml(courseDetails.course.name)%>
                        </strong>
                        <span class="pull-right">
                             <a class="btn btn-primary btn-xs btn-tm-actions"
                                href="<%=data.getInstructorCourseEnrollLink(courseDetails.course.id)%>"
                                onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_ENROLL%>')"
                                onmouseout="hideddrivetip()"> Enroll</a>
                                 
                             <a class="btn btn-primary btn-xs btn-tm-actions"
                                href="<%=data.getInstructorCourseDetailsLink(courseDetails.course.id)%>"
                                onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_DETAILS%>')"
                                onmouseout="hideddrivetip()"> View</a> 
                                
                             <a class="btn btn-primary btn-xs btn-tm-actions"
                                href="<%=data.getInstructorCourseEditLink(courseDetails.course.id)%>"
                                onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_EDIT%>')"
                                onmouseout="hideddrivetip()"> Edit</a>
                                
                             <a class="btn btn-primary btn-xs btn-tm-actions"
                                href="<%=data.getInstructorEvaluationLinkForCourse(courseDetails.course.id)%>"
                                onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_ADD_EVALUATION%>')"
                                onmouseout="hideddrivetip()"> Add Session</a>
                             
                             <a class="btn btn-primary btn-xs btn-tm-actions"
                                href="<%=data.getInstructorCourseArchiveLink(courseDetails.course.id, true, true)%>"
                                onclick="hideddrivetip(); return toggleArchiveCourseConfirmation('<%=courseDetails.course.id%>')"
                                onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_ARCHIVE%>')"
                                onmouseout="hideddrivetip()">Archive</a>
                                
                             <a class="btn btn-primary btn-xs btn-tm-actions"
                                href="<%=data.getInstructorCourseDeleteLink(courseDetails.course.id,true)%>"
                                onclick="hideddrivetip(); return toggleDeleteCourseConfirmation('<%=courseDetails.course.id%>')"
                                onmouseover="ddrivetip('<%=Const.Tooltips.COURSE_DELETE%>')"
                                onmouseout="hideddrivetip()"> Delete</a>
                        </span>
                    </div>
                    <%
                        if (courseDetails.evaluations.size() > 0||
                            courseDetails.feedbackSessions.size() > 0) {
                    %>
                            <table class="table-responsive table table-striped">
                                <tr>
                                    <th class="leftalign color_white bold">Session Name</th>
                                    <th class="centeralign color_white bold">Status</th>
                                    <th class="centeralign color_white bold"><span
                                        onmouseover="ddrivetip('<%=Const.Tooltips.EVALUATION_RESPONSE_RATE%>')"
                                        onmouseout="hideddrivetip()">Response Rate</span></th>
                                    <th class="centeralign color_white bold no-print">Action(s)</th>
                                </tr>
                        <%
                            for (EvaluationAttributes edd: courseDetails.evaluations){
                                sessionIdx++;
                        %>
                                <tr class="home_sessions_row" id="session<%=sessionIdx%>">
                                    <td class="t_session_name<%=courseIdx%>"><%=PageData.sanitizeForHtml(edd.name)%></td>
                                    <td class="t_session_status<%=courseIdx%> centeralign"><span
                                        onmouseover="ddrivetip('<%=PageData.getInstructorHoverMessageForEval(edd)%>')"
                                        onmouseout="hideddrivetip()"><%=PageData.getInstructorStatusForEval(edd)%></span></td>
                                    <td class="t_session_response<%=courseIdx%> centeralign<% if(!TimeHelper.isOlderThanAYear(edd.endTime)) { out.print(" recent");} %>">
                                        <a oncontextmenu="return false;" href="<%=data.getEvaluationStatsLink(edd.courseId, edd.name)%>">Show</a>
                                    </td>
                                    <td class="centeralign no-print"><%=data.getInstructorEvaluationActions(edd, true)%>
                                    </td>
                                </tr>
                        <%
                            }
                            for(FeedbackSessionAttributes fdb: courseDetails.feedbackSessions) {
                                sessionIdx++;
                        %>
                                <tr class="home_sessions_row" id="session<%=sessionIdx%>">
                                    <td class="t_session_name"><%=PageData
                                            .sanitizeForHtml(fdb.feedbackSessionName)%></td>
                                    <td class="t_session_status centeralign"><span
                                        onmouseover="ddrivetip(' <%=PageData
                                            .getInstructorHoverMessageForFeedbackSession(fdb)%>')"
                                        onmouseout="hideddrivetip()"><%=PageData
                                            .getInstructorStatusForFeedbackSession(fdb)%></span></td>
                                    <td class="t_session_response centeralign<% if(!TimeHelper.isOlderThanAYear(fdb.createdTime)) { out.print(" recent");} %>">
                                        <a oncontextmenu="return false;" href="<%=data.getFeedbackSessionStatsLink(fdb.courseId, fdb.feedbackSessionName)%>">Show</a>
                                    </td>
                                    <td class="centeralign no-print"><%=data.getInstructorFeedbackSessionActions(
                                            fdb, false)%></td>
                                </tr>
                        <%
                            }
                        %>
                            </table>
                    <%
                        }
                    %>
                </div>
                <br>
    <%
                out.flush();
            }
        }
    %>
    </div>    
    <br>
    <br>
    <br>
    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>