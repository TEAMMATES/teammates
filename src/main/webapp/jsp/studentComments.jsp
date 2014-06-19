<%@page import="teammates.common.datatransfer.InstructorAttributes"%>
<%@page import="teammates.common.datatransfer.CommentAttributes"%>
<%@page import="teammates.common.datatransfer.CommentRecipientType"%>
<%@page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.TimeHelper" %>
<%@ page import="teammates.common.datatransfer.CourseDetailsBundle" %>
<%@ page import="teammates.common.datatransfer.EvaluationDetailsBundle" %>
<%@ page import="teammates.common.datatransfer.FeedbackSessionDetailsBundle"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="teammates.ui.controller.StudentCommentsPageData"%>
<%
    StudentCommentsPageData data = (StudentCommentsPageData)request.getAttribute("data");
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
        <h2>Student Comments</h2>

        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <br>
            <% if(data.coursePaginationList.size() > 0) { %>
            <ul class="pagination">
                <li><a href="<%=data.previousPageLink%>">«</a></li>
                <%
                    for (String courseId : data.coursePaginationList) {
                %>
                <li
                    class="<%=courseId.equals(data.courseId) ? "active" : ""%>">
                    <a
                    href="<%=data.getStudentCommentsLink() + "&courseid=" + courseId%>"><%=courseId%></a>
                </li>
                <%
                    }
                %>
                <li><a href="<%=data.nextPageLink%>">»</a></li>
            </ul>
            <div class="well well-plain">
                <div class="text-color-primary">
                    <h4>
                        <strong> <%=data.courseName%>
                        </strong>
                    </h4>
                </div>
                <div id="no-comment-panel" style="<%=data.comments.size() == 0?"":"display:none;"%>">
                    <br>
                    <div class="panel">
                        <div class="panel-body">
                            You don't have any comment in this course.
                        </div>
                    </div>
                </div>
                <%  if(data.comments.size() != 0){// check student comments starts 
                %>
                <br>
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong>Your Comments</strong>
                    </div>
                    <div class="panel-body">
                        <%
                            int commentIdx = 0;
                            int studentIdx = 0;
                            for (CommentAttributes comment : data.comments) {//comment loop starts
                                studentIdx++;
                        %>
                        <%
                            String recipientDisplay = data.getRecipientNames(comment.recipients);
                        %>
                        <div class="panel panel-info student-record-comments giver_display-by-you">
                            <div class="panel-heading">
                                To <b><%=recipientDisplay%></b>
                            </div>
                            <ul class="list-group comments">
                                <%
                                    CommentRecipientType recipientTypeForThisRecipient = CommentRecipientType.PERSON;//default value is PERSON
                                    commentIdx++;
                                    recipientTypeForThisRecipient = comment.recipientType;
                                %>
                                <li class="list-group-item list-group-item-warning"
                                    name="form_commentedit"
                                    class="form_comment"
                                    id="form_commentedit-<%=commentIdx%>">
                                    <div id="commentBar-<%=commentIdx%>">
                                        <% InstructorAttributes instructor = data.roster.getInstructorForEmail(comment.giverEmail);
                                           String giverDisplay = comment.giverEmail;
                                           if(instructor != null){
                                               String title = instructor.displayedName;
                                               if(!title.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR) &&
                                                       !title.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_HELPER)){
                                                   title = "Instructor";
                                               }
                                               giverDisplay = title + " " + instructor.name;
                                           }
                                        %>
                                        <span class="text-muted">From <b><%=giverDisplay%></b> on
                                            <%=TimeHelper.formatTime(comment.createdAt)%></span>
                                    </div>
                                    <div id="plainCommentText<%=commentIdx%>"><%=comment.commentText.getValue()%></div>
                                </li>
                            </ul>
                        </div>
                        <%
                            }//comment loop ends
                        %>
                    </div>
                </div>
                <% }// check student comments ends %>
            </div>
            <% } else { %>
            <div id="statusMessage" class="alert alert-warning">
                There is no comment to display
            </div>
                
            <% } %>
    </div>
    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>