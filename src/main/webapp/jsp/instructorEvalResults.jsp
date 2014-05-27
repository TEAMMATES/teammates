<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.datatransfer.StudentResultBundle"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="teammates.common.datatransfer.TeamResultBundle"%>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes"%>
<%@ page import="teammates.common.datatransfer.SubmissionDetailsBundle"%>
<%@ page import="teammates.ui.controller.InstructorEvalResultsPageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml"%>

<%
    InstructorEvalResultsPageData data = (InstructorEvalResultsPageData)request.getAttribute("data");
%>

<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>TEAMMATES - Instructor</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css" >
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css" >
    <link rel="stylesheet" href="/stylesheets/common.css" type="text/css" >
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css" >

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/date.js"></script>
    <script type="text/javascript" src="/js/CalendarPopup.js"></script>
    <script type="text/javascript" src="/js/AnchorPosition.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>

    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorEvalResults.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div class="container" id="frameBodyWrapper">
        <div id="headerOperation">
            <h1>Evaluation Results</h1>
        </div>

        <!-- Evaluation Results Top -->
        <div class="well well-plain">
            <form class="form-horizontal" role="form" id="download_eval_report" method="GET" action=<%=Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_DOWNLOAD%>>
                <div class="form-group">
                    <label class="col-sm-3 control-label">Course ID:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static">
                            <%=data.evaluationResults.evaluation.courseId%>
                        </p>
                    </div>
                </div> 
                <div class="form-group">
                    <label class="col-sm-3 control-label">Evaluation name:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static">
                            <%=InstructorEvalResultsPageData.sanitizeForHtml(data.evaluationResults.evaluation.name)%>
                        </p>
                    </div>
                </div>  
                <div class="form-group">
                    <label class="col-sm-3 control-label">Opening time:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static">
                           <%=TimeHelper.formatTime(data.evaluationResults.evaluation.startTime)%>
                        </p>
                    </div>
                </div>  
                <div class="form-group">
                    <label class="col-sm-3 control-label">Closing time:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static">
                           <%=TimeHelper.formatTime(data.evaluationResults.evaluation.endTime)%>
                        </p>
                    </div>
                </div>
                 <div class="form-group">
                    <label class="col-sm-3 control-label">Report type:</label>
                    <label class="radio inline col-sm-2">
                        <input  type="radio" name="radio_reporttype"
                                id="radio_summary" value="instructorEvaluationSummaryTable"
                                checked="checked" onclick="showReport(this.value)">
                        Summary
                    </label>
                    <label class="radio inline col-sm-3">
                        <input  type="radio" name="radio_reporttype" id="radio_reviewer"
                                value="instructorEvaluationDetailedReviewerTable"
                                onclick="showReport(this.value)">
                        Detailed: By Reviewer
                    </label>
                    <label class="radio inline col-sm-3">
                        <input  type="radio" name="radio_reporttype" id="radio_reviewee"
                                value="instructorEvaluationDetailedRevieweeTable"
                                onclick="showReport(this.value)">
                        Detailed: By Reviewee
                    </label>           
                </div>
                <br>
                <div class="form-group">
                    <div class="col-sm-4"></div>
                    <div class="col-sm-2">
                        <%
                            if(InstructorEvalResultsPageData.getInstructorStatusForEval(data.evaluationResults.evaluation).equals(Const.INSTRUCTOR_EVALUATION_STATUS_PUBLISHED)) {
                        %>
                        <button type="button" class="btn btn-primary" id="button_unpublish"
                            value="Unpublish"
                            onclick="if(toggleUnpublishEvaluation('<%=data.evaluationResults.evaluation.name%>')) window.location.href='<%=data.getInstructorEvaluationUnpublishLink(data.evaluationResults.evaluation.courseId,data.evaluationResults.evaluation.name,false)%>';">Unpublish</button>
                        <%
                            } else {
                        %>
                        <button type="button" class="btn btn-primary" id="button_publish"
                            value="Publish"
                            onclick="if(togglePublishEvaluation('<%=data.evaluationResults.evaluation.name%>')) window.location.href='<%=data.getInstructorEvaluationPublishLink(data.evaluationResults.evaluation.courseId,data.evaluationResults.evaluation.name,false)%>';"
                            <%
                                if (!InstructorEvalResultsPageData.getInstructorStatusForEval(data.evaluationResults.evaluation).equals(Const.INSTRUCTOR_EVALUATION_STATUS_CLOSED)) {
                            %>
                            disabled="disabled"
                            <%
                                }
                            %>
                            >Publish</button>
                        <%
                            }
                        %>
                    </div>

                    <div class="col-sm-2">
                        <button type="submit" value="Download Report" class="btn btn-primary">Download Report</button>    
                    </div>
                    <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>"
                            value="<%=data.account.googleId%>">
                    <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>"
                            value="<%=data.evaluationResults.evaluation.courseId%>">
                    <input type="hidden" name="<%=Const.ParamsNames.EVALUATION_NAME%>"
                            value="<%=sanitizeForHtml(data.evaluationResults.evaluation.name)%>">
                      
                </div>
            </form>
        </div>

        <br>
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <br>

        <%
            out.flush();
        %>

        <!-- Evaluation Results Summary -->

        <div id="instructorEvaluationSummaryTable" class="evaluation_result">
            <h4 style="display:inline;">
                <span class="label label-info">CC</span> Claimed Contribution &nbsp; &nbsp; &nbsp;

                <span class="label label-success">PC</span> Perceived Contribution &nbsp; &nbsp; &nbsp;
  
                <span class="label label-danger">E</span> Equal Share &nbsp; &nbsp; &nbsp;
            </h4> 
             [ <a href="/instructorHelp.html#faq7a" target="_blank" id="interpret_help_link">How do I interpret/use these values?</a> ]

            <br>
            <br>
           
            <table class="table table-bordered table-striped">
                <thead class="fill-primary">
                <tr>
                    <th class="button-sort-none" id="button_sortteam" onclick="toggleSort(this,1);">Team 
                        <span class="sort-icon unsorted"></span></th>
                    <th class="button-sort-none" id="button_sortname" onclick="toggleSort(this,2)">Student
                        <span class="sort-icon unsorted"></span></th>
                    <th class="button-sort-none" id="button_sortclaimed" onclick="toggleSort(this,3,sortByPoint)"> CC
                        <span class="sort-icon unsorted"></span></th>
                    <th class="button-sort-none" id="button_sortperceived" onclick="toggleSort(this,4,sortByPoint)">PC
                        <span class="sort-icon unsorted"></span></th>
                    <th class="button-sort-none" id="button_sortdiff" onclick="toggleSort(this,5,sortByDiff)"
                        data-toggle="tooltip" data-placement="top" data-container="body"  
                        title='<%=Const.Tooltips.EVALUATION_DIFF%>'>Diff
                        <span class="sort-icon unsorted"></span></th>
                    <th class="centeralign" data-toggle="tooltip" data-placement="top" data-container="body"
                        title='<%=Const.Tooltips.EVALUATION_POINTS_RECEIVED%>'>Ratings Received</th>
                    <th class="centeralign">Action(s)</th>
                </tr>
                </thead>
                <%
                    int idx = 0;
                                                                    for(TeamResultBundle teamResultBundle: data.evaluationResults.teamResults.values()){
                                                                            for(StudentResultBundle studentResult: teamResultBundle.studentResults){
                                                                                StudentAttributes student = studentResult.student;
                %>
                <tr class="student_row" id="student<%=idx%>">
                    <td><%=sanitizeForHtml(student.team)%></td>
                    <td id="<%=Const.ParamsNames.STUDENT_NAME%>"
                        data-toggle="tooltip" data-placement="top" data-container="body"  
                        title='<%=InstructorEvalResultsPageData.sanitizeForJs(student.comments)%>'> 
                        <%=student.name%>
                    </td>
                    <td><%=InstructorEvalResultsPageData.getPointsAsColorizedHtml(studentResult.summary.claimedToInstructor)%></td>
                    <td><%=InstructorEvalResultsPageData.getPointsAsColorizedHtml(studentResult.summary.perceivedToInstructor)%></td>
                    <td><%=InstructorEvalResultsPageData.getPointsDiffAsHtml(studentResult)%></td>
                    <td><%=InstructorEvalResultsPageData.getNormalizedPointsListColorizedDescending(studentResult.incoming)%></td>
                    <td class="centeralign no-print"><a class="btn btn-default btn-xs"
                        name="viewEvaluationResults<%=idx%>"
                        id="viewEvaluationResults<%=idx%>" target="_blank"
                        href="<%=data.getInstructorEvaluationSubmissionViewLink(data.evaluationResults.evaluation.courseId, data.evaluationResults.evaluation.name, student.email)%>"
                        data-toggle="tooltip" data-placement="top" data-container="body"  
                        title='<%=Const.Tooltips.EVALUATION_SUBMISSION_VIEW_REVIEWER%>'> View</a> <a class="btn btn-default btn-xs" name="editEvaluationResults<%=idx%>"
                        id="editEvaluationResults<%=idx%>" target="_blank"
                        href="<%=data.getInstructorEvaluationSubmissionEditLink(data.evaluationResults.evaluation.courseId, data.evaluationResults.evaluation.name, student.email)%>"
                        data-toggle="tooltip" data-placement="top" data-container="body"  
                        title='<%=Const.Tooltips.EVALUATION_SUBMISSION_INSTRUCTOR_EDIT%>'
                        onclick="return openChildWindow(this.href)">Edit</a></td>
                </tr>
                <%
                    idx++;
                                                                                                                                                            }
                                                                                                                                                        }
                %>
            </table>
            <br> <br> <br>
        </div>

        <%
            out.flush();
        %>

        <!-- Evaluation Results Detailed-->

        <%
            for(boolean byReviewer = true, repeat=true; repeat; repeat = byReviewer, byReviewer=false){
        %>
        <div
            id="instructorEvaluationDetailed<%=byReviewer ? "Reviewer" : "Reviewee"%>Table"
            class="evaluation_result" style="display: none;">
            <div>
                <h2>
                    Detailed Evaluation Results - By <%=byReviewer ? "Reviewer" : "Reviewee"%></h2>
            </div>

            <%
                boolean firstTeam = true;
                                        for(TeamResultBundle teamResultBundle: data.evaluationResults.teamResults.values()){
            %>
            <%
                if(firstTeam) firstTeam = false; else out.print("<br>");
            %>
            <br>
            <div class="backgroundBlock">
                <h2 class="color_white"><%=sanitizeForHtml(teamResultBundle.getTeamName())%></h2>
                <%
                    boolean firstStudent = true;
                                                        for(StudentResultBundle studentResult: teamResultBundle.studentResults){
                                                            StudentAttributes student = studentResult.student;
                %>
                <%
                    if(firstStudent) firstStudent = false; else out.print("<br>");
                %>
                <table class="resultTable">
                    <thead>
                        <tr>
                            <th colspan="2" width="10%" class="leftalign bold"><span
                                class="resultHeader"><%=byReviewer ? "Reviewer" : "Reviewee"%>:
                            </span><%=student.name%></th>
                            <th class="leftalign bold"><span class="resultHeader"
                                onmouseover="ddrivetip('<%=Const.Tooltips.CLAIMED%>')"
                                onmouseout="hideddrivetip()"> Claimed Contribution: </span><%=InstructorEvalResultsPageData.getPointsInEqualShareFormatAsHtml(studentResult.summary.claimedToInstructor,true)%></th>
                            <th class="leftalign bold"><span class="resultHeader"
                                onmouseover="ddrivetip('<%=Const.Tooltips.PERCEIVED%>')"
                                onmouseout="hideddrivetip()"> Perceived Contribution: </span><%=InstructorEvalResultsPageData.getPointsInEqualShareFormatAsHtml(studentResult.summary.perceivedToInstructor,true)%>
                            </th>
                            <th class="rightalign no-print">
                                <%
                                    if(byReviewer){
                                %> <a target="_blank" class="color_black"
                                href="<%=data.getInstructorEvaluationSubmissionEditLink(student.course, data.evaluationResults.evaluation.name, student.email)%>"
                                onclick="return openChildWindow(this.href)"> Edit</a> <%
 }
%>
                            </th>
                        </tr>
                    </thead>
                    <tr>
                        <td colspan="5"><span class="bold">Self evaluation:</span><br>
                            <%=InstructorEvalResultsPageData.getJustificationAsSanitizedHtml(studentResult.getSelfEvaluation())%><br></td>
                    </tr>
                    <tr>
                        <td colspan="5"><span class="bold">Comments about team:</span><br> <%=InstructorEvalResultsPageData.getP2pFeedbackAsHtml(sanitizeForHtml(studentResult.getSelfEvaluation().p2pFeedback.getValue()), data.evaluationResults.evaluation.p2pEnabled)%><br></td>
                    </tr>
                    <tr class="resultSubheader bold">
                        <td width="15%"><%=byReviewer ? "To" : "From"%> Student</td>
                        <td width="5%">Contribution</td>
                        <td width="40%">Confidential comments</td>
                        <td colspan="2" width="40%">Feedback to peer</td>
                    </tr>
                    <%
                        for(SubmissionAttributes sub: (byReviewer ? studentResult.outgoing : studentResult.incoming)){ 
                                                                            if(sub.reviewer.equals(sub.reviewee)) continue;
                    %>
                    <tr>
                        <td><b><%=sanitizeForHtml(byReviewer ? sub.details.revieweeName : sub.details.reviewerName)%></b></td>
                        <td><%=InstructorEvalResultsPageData.getPointsInEqualShareFormatAsHtml(sub.details.normalizedToInstructor,false)%></td>
                        <td><%=InstructorEvalResultsPageData.getJustificationAsSanitizedHtml(sub)%></td>
                        <td colspan="2"><%=InstructorEvalResultsPageData.getP2pFeedbackAsHtml(sanitizeForHtml(sub.p2pFeedback.getValue()), data.evaluationResults.evaluation.p2pEnabled)%></td>
                    </tr>
                    <%
                        }
                    %>
                </table>
                <br>
                <%
                    }
                %>
            </div>
            <%
                }
            %>
            <br> <br>
            <div class="centeralign">
                <input type="button" class="button" name="button_top"
                    id="button_top" value="To Top" onclick="scrollToTop()">
            </div>
            <br> <br> <br>
        </div>
        <%
            }
        %>
        <%
            out.flush();
        %>
    </div>
    </div>

    
    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    <script>
        setStatusMessage("");
    </script>
</body>
</html>