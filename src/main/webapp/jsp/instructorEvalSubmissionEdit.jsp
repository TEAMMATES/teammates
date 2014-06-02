<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes"%>
<%@ page import="teammates.ui.controller.InstructorEvalSubmissionEditPageData"%>
<%
    InstructorEvalSubmissionEditPageData data = (InstructorEvalSubmissionEditPageData)request.getAttribute("data");
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

    <div class="container theme-showcase" id="frameBodyWrapper">
        <div id="topOfPage"></div>
        <div id="headerOperation">
            <h1>Edit Student's Submission</h1>
        </div>
        
        <div class="well well-plain">
            <div class="form form-horizontal" id="studentEvaluationInfo">
                <div class="form-group">
                    <label class="col-sm-3 control-label text-bold">Course ID:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static"><%=data.eval.courseId%></p>
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label text-bold">Evaluation Name:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static"><%=InstructorEvalSubmissionEditPageData.sanitizeForHtml(data.eval.name)%></p>
                    </div>
                </div>
            </div>
        </div>
        <br><br><br>
        
        <div id="studentEvaluationSubmissions">
            <form name="form_submitevaluation" class="form form-horizontal" id="form_submitevaluation" method="post"
                    action="<%=Const.ActionURIs.INSTRUCTOR_EVAL_SUBMISSION_EDIT_SAVE%>">
                <jsp:include page="<%=Const.ViewURIs.EVAL_SUBMISSION_EDIT%>">
                <jsp:param name="isStudent" value="false" />
                </jsp:include>
                <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
                <div class="align-center">
                    <input type="submit" class="btn btn-primary align-center" name="submitEvaluation"
                            onclick="return checkEvaluationForm(this.form)"
                            id="button_submit" value="Save Changes">
                </div>
                <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
            </form>
             <br><br>
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>