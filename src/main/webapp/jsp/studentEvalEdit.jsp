<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.TimeHelper" %>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes.EvalStatus" %>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes" %>
<%@ page import="teammates.common.datatransfer.StudentAttributes" %>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes" %>
<%@ page import="teammates.ui.controller.StudentEvalSubmissionEditPageData"%>
<%@ page import="java.util.Date" %>
<%
    StudentEvalSubmissionEditPageData data = (StudentEvalSubmissionEditPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>TEAMMATES - Student</title>
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css" media="screen">
    <link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
    <link rel="stylesheet" href="/stylesheets/studentEvalEdit-print.css" type="text/css" media="print">
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/tooltip.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    
    <script type="text/javascript" src="/js/student.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>  
    
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
      <![endif]-->  
</head>

<body>
    <div id="dhtmltooltip"></div>
    <%
        if (!data.isPreview) {
    %>
            <jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />
    <%
        } else {
    %>
            <div id="frameTop">
                <div id="frameTopWrapper">
                    <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_EVAL_PREVIEW%>" 
                        name="form_previewasstudent" class="form_preview">
                        <h1 class="color_white centeralign">
                            Previewing Evaluation as
                            <select name="<%=Const.ParamsNames.PREVIEWAS%>" onchange="this.form.submit()" style="font-size:80%;">
                            <%
                                for (StudentAttributes student : data.studentList) {
                            %>
                                <option value="<%=student.email%>"
                                    <%=student.email.equals(data.student.email) ? "selected=\"selected\"" : ""%>>
                                        [<%=student.team%>] <%=student.name%>
                                </option>
                            <%
                                }
                            %>
                            </select>
                        </h1>
                        <input type="hidden" name="<%=Const.ParamsNames.EVALUATION_NAME%>" value="<%=data.eval.name%>">
                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.eval.courseId%>">
                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                    </form>
                </div>
            </div>
    <% 
        }
    %>

    <div class="container theme-showcase" role="main">
        <div id="topOfPage"></div>
        <div id="headerOperation">
            <h1>Evaluation Submission</h1>
        </div>
        
        <div class="row">
            <div class="col-sm-2"></div>
            <div class="col-sm-8">
                <div class="panel panel-default">
                    <div class="form form-horizontal" id="studentEvaluationInformation">
                        <div class="panel-body">
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Course ID:</label>
                                <div class="col-sm-5" id="<%=Const.ParamsNames.COURSE_ID%>"><%=data.eval.courseId%></div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Evaluation name:</label>
                                <div class="col-sm-5" id="<%=Const.ParamsNames.EVALUATION_NAME%>"><%=StudentEvalSubmissionEditPageData.sanitizeForHtml(data.eval.name)%></div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Opening time:</label>
                                <div class="col-sm-5" id="<%=Const.ParamsNames.EVALUATION_STARTTIME%>"><%=TimeHelper.formatTime(data.eval.startTime)%></div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Closing time:</label>
                                <div class="col-sm-5" id="<%=Const.ParamsNames.EVALUATION_DEADLINETIME%>"><%=TimeHelper.formatTime(data.eval.endTime)%></div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Instructions:</label>
                                <div class="col-sm-5 multiline" id="<%=Const.ParamsNames.EVALUATION_INSTRUCTIONS%>"><%=StudentEvalSubmissionEditPageData.sanitizeForHtml(data.eval.instructions.getValue())%></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-2"></div>
        </div>
        
        <br>
        <br>
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <br>
        <br>
        <div class="text-muted">
                        <span class="bold">How do I choose ‘estimated contribution’ values?</span><br>
                        Choose ‘Equal share’ if this team member did an equal share of the work. 
                        ‘Equal share + 10%’ means the team member did 10% more than an equal share of the work.
        </div>
        <br>
        <br>
        
        <form name="form_submitevaluation" id="form_submitevaluation" method="post"
                action="<%=Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_SAVE%>"
                class="form form-horizontal" role="form">
            <jsp:include page="<%=Const.ViewURIs.EVAL_SUBMISSION_EDIT%>">
            <jsp:param name="isStudent" value="true" />
            </jsp:include>
            <br>
            <br>
            <div id="studentEvaluationSubmissionButtons" class="centeralign">
                <input type="submit" class="btn btn-primary" name="submitEvaluation"
                        id="button_submit" value="Submit Evaluation" 
                        <%=(!data.disableAttribute.isEmpty() || data.isPreview) ? "disabled=\"disabled\"" : ""%>
                        <%
                            if (!data.disableAttribute.isEmpty()) {
                        %>        
                            style="background: #66727A;"
                        <%
                            }
                        %>
                >
            </div>
            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
        </form>
        <br>
        <br>
        <br>
    
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>