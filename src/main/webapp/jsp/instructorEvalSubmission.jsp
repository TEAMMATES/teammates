<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes"%>
<%@ page import="teammates.ui.controller.InstructorEvalSubmissionPageData"%>
<%
    InstructorEvalSubmissionPageData data = (InstructorEvalSubmissionPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>TEAMMATES - Instructor</title>
    <!-- Bootstrap core CSS -->
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css">
    
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/date.js"></script>
        <script type="text/javascript" src="/js/common.js"></script>
    
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/instructor.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div id="frameBodyWrapper" class="container">
        <div id="topOfPage"></div>
        <h1>View Student's Evaluation</h1>
        <br>

        <div class="well well-plain inputTable" id="studentEvaluationInfo">
            <form class="form-horizontal" role="form">
                <div class="panel-heading">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">
                            Course ID:
                        </label>
                        <div class="col-sm-10">
                            <p class="form-control-static"><%=data.evaluation.courseId%></p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">
                            Evaluation Name:
                        </label>
                        <div class="col-sm-10">
                            <p class="form-control-static"><%=InstructorEvalSubmissionPageData.sanitizeForHtml(data.evaluation.name)%></p>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        <%
            for(boolean byReviewee = true, repeat=true; repeat; repeat = byReviewee, byReviewee=false){
        %>
        <h3 class="text-center">
            <%=InstructorEvalSubmissionPageData.sanitizeForHtml(data.student.name) + (byReviewee ? "'s Result" : "'s Submission")%>
        </h3>

        <div class="panel panel-primary resultsTable">
            <div class="panel-heading">
                <div class="row">
                    <div class="col-md-4">
                        <span class="resultHeader">
                            <%=byReviewee ? "Reviewee" : "Reviewer"%>:
                        </span>
                        <strong>
                            <%=data.student.name%>
                        </strong>
                    </div>
                    <div class="col-md-4">
                        <span class="resultHeader"
                            title="<%=Const.Tooltips.CLAIMED%>"
                            data-toggle="tooltip"
                            data-placement="top">
                            Claimed Contribution:
                        </span>
                        <%=InstructorEvalSubmissionPageData.getPointsInEqualShareFormatAsHtml(data.studentResult.summary.claimedToInstructor,true)%>
                    </div>
                    <div class="col-md-4">
                        <span class="resultHeader"
                            title="<%=Const.Tooltips.PERCEIVED%>"
                            data-toggle="tooltip"
                            data-placement="top">
                            Perceived Contribution: 
                        </span>
                        <%=InstructorEvalSubmissionPageData.getPointsInEqualShareFormatAsHtml(data.studentResult.summary.perceivedToInstructor,true)%>
                    </div>
                </div>
            </div>
            <table class="table table-bordered">
                <tbody>
                    <tr>
                        <td>
                            <strong>
                                Self evaluation:
                            </strong>
                            <br>
                            <%=InstructorEvalSubmissionPageData.getJustificationAsSanitizedHtml(data.studentResult.getSelfEvaluation())%></td>
                    </tr>
                    <tr>
                        <td>
                            <strong>
                                Comments about team:
                            </strong>
                            <br>
                            <%=InstructorEvalSubmissionPageData.sanitizeForHtml(data.studentResult.getSelfEvaluation().p2pFeedback.getValue())%></td>
                    </tr>
                </tbody>
            </table>
            <table class="table table-bordered table-striped">
                <thead>
                    <tr class="border-top-gray fill-info resultSubheader">
                        <th class="col-sm-1"><%=byReviewee ? "From" : "To"%></th>
                        <th class="col-sm-1">Contribution</th>
                        <th class="col-sm-5">Confidential comments</th>
                        <th class="col-sm-5">Feedback to peer</th>
                    </tr>
                </thead>
                <tbody>
                <%
                    for(SubmissionAttributes sub: (byReviewee ? data.studentResult.incoming : data.studentResult.outgoing)){
                                                                        if(sub.reviewer.equals(sub.reviewee)) continue;
                %>
                    <tr>
                        <td><b><%=InstructorEvalSubmissionPageData.sanitizeForHtml(byReviewee ? sub.details.reviewerName : sub.details.revieweeName)%></b></td>
                        <td><%=InstructorEvalSubmissionPageData.getPointsInEqualShareFormatAsHtml(sub.details.normalizedToInstructor,true)%></td>
                        <td><%=InstructorEvalSubmissionPageData.getJustificationAsSanitizedHtml(sub)%></td>
                        <td><%=InstructorEvalSubmissionPageData.getP2pFeedbackAsHtml(InstructorEvalSubmissionPageData.sanitizeForHtml(sub.p2pFeedback.getValue()), data.evaluation.p2pEnabled)%></td>
                    </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>
        <br><br>
        <%
            }
        %>
        <div class="col-sm-12">
            <input type="button" class="btn btn-primary center-block" id="button_edit" value="Edit Submission"
                    onclick="window.location.href='<%=data.getInstructorEvaluationSubmissionEditLink(data.evaluation.courseId, data.evaluation.name, data.student.email)%>'">
        </div>
        <br>
        <br>
        <br>

    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>