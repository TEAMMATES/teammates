<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.TimeHelper" %>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle" %>
<%@ page import="teammates.common.datatransfer.EvaluationDetailsBundle" %>
<%@ page import="teammates.common.datatransfer.FeedbackSessionDetailsBundle"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="teammates.ui.controller.StudentHomePageData"%>
<%
    StudentHomePageData data = (StudentHomePageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TEAMMATES - Student</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css">

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script src="/bootstrap/js/bootstrap.min.js"></script>
    
    <script type="text/javascript" src="/js/student.js"></script>
    <script type="text/javascript" src="/js/studentHome.js"></script>
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
      <![endif]-->
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
    <jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />

    <div id="frameBodyWrapper" class="container theme-showcase">
        <div id="topOfPage"></div>
        <h2>Student Home</h2>

        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <br>

        <%
            int courseIdx = -1;
            int sessionIdx = -1;
            for (CourseDetailsBundle courseDetails : data.courses) {
                courseIdx++;
        %>
        <div class="panel panel-primary">
            <div class="panel-heading">
                <strong>
                [<%=courseDetails.course.id%>] : <%=PageData.sanitizeForHtml(courseDetails.course.name)%>
                </strong>
                <span class="pull-right">
                    <a class="btn btn-primary btn-xs"
                        href="<%=data.getStudentCourseDetailsLink(courseDetails.course.id)%>"
                        data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.STUDENT_COURSE_DETAILS%>"
                        >View Team</a>
                </span>
            </div>
            
            <table class="table-responsive table table-striped">
            <%
                if (courseDetails.evaluations.size() > 0 || 
                    courseDetails.feedbackSessions.size() > 0) {
            %>
                        <thead>
                            <tr>
                                <th>Session Name</th>
                                <th>Deadline</th>
                                <th>Status</th>
                                <th class="studentHomeActions">Action(s)</th>
                            </tr>
                        </thead>
                    <%
                        for (EvaluationDetailsBundle edd : courseDetails.evaluations) {
                            sessionIdx++;
                    %>
                            <tr id="evaluation<%=sessionIdx%>">
                                <td><%=PageData.sanitizeForHtml(edd.evaluation.name)%></td>
                                <td><%=TimeHelper.formatTime(edd.evaluation.endTime)%></td>
                                <td><span data-toggle="tooltip" data-placement="top" 
                                    title="<%=data.getStudentHoverMessageForEval(data.getStudentStatusForEval(edd.evaluation))%>">
                                    <%=data.getStudentStatusForEval(edd.evaluation)%>
                                    </span>
                                </td>
                                <td class="studentHomeActions">
                                    <%=data.getStudentEvaluationActions(edd.evaluation,sessionIdx)%>
                                </td>
                            </tr>
                    <%
                        }
                            for (FeedbackSessionDetailsBundle fsd : courseDetails.feedbackSessions) {
                                sessionIdx++;
                        %>
                                <tr class="home_evaluations_row" id="evaluation<%=sessionIdx%>">
                                    <td><%=PageData.sanitizeForHtml(fsd.feedbackSession.feedbackSessionName)%></td>
                                    <td><%=TimeHelper.formatTime(fsd.feedbackSession.endTime)%></td>
                                    <td><span data-toggle="tooltip" data-placement="top" 
                                            title="<%=data.getStudentHoverMessageForSession(fsd.feedbackSession)%>">
                                            <%=data.getStudentStatusForSession(fsd.feedbackSession)%>
                                        </span>
                                    </td>
                                    <td class="studentHomeActions"><%=data.getStudentFeedbackSessionActions(fsd.feedbackSession,sessionIdx)%>
                                    </td>
                                </tr>
                        <%
                            }
                        } else {
                    %>
                            <tr>
                                <th class="align-center bold color_white">
                                    Currently, there are no open evaluation/feedback sessions in this course. When a session is open for submission you will be notified.
                                </th>
                            </tr>
                    <%
                        }
                    %>
            </table>
        </div>
        <br>
        <br>
        <%
            out.flush();
                        }
        %>
    </div>
    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>