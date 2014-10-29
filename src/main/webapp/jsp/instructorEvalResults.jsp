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
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css" >

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/date.js"></script>
    
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
        <div id="topOfPage"></div>
        <div id="headerOperation">
            <h1>Evaluation Results</h1>
        </div>

        <div class="well well-plain">
            <div class="form-horizontal">
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
                    <label class="control-label col-sm-3 col-xs-3">Report type:</label>
                    <div class="col-sm-9 col-xs-5">
                        <label class="radio-inline" style="margin-left:10px;">
                            <input  type="radio" name="radio_reporttype"
                                    id="radio_summary" value="instructorEvaluationSummaryTable"
                                    checked="checked" onclick="showReport(this.value)">
                            Summary
                        </label>
                        <label class="radio-inline">
                            <input  type="radio" name="radio_reporttype" id="radio_reviewer"
                                    value="instructorEvaluationDetailedReviewerTable"
                                    onclick="showReport(this.value)">
                            Detailed: By Reviewer
                        </label>
                        <label class="radio-inline">
                            <input  type="radio" name="radio_reporttype" id="radio_reviewee"
                                    value="instructorEvaluationDetailedRevieweeTable"
                                    onclick="showReport(this.value)">
                            Detailed: By Reviewee
                        </label>
                    </div>     
                </div>
                <br>
                <div class="form-group">
                    <div class="align-center">
                    
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
                   
                <form id="download_eval_report" method="GET" action=<%=Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_DOWNLOAD%> style="display:inline;">
                    
                <button type="submit" value="Download Report" class="btn btn-primary">Download Report</button>    
                
                <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>"
                        value="<%=data.account.googleId%>">
                <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>"
                        value="<%=data.evaluationResults.evaluation.courseId%>">
                <input type="hidden" name="<%=Const.ParamsNames.EVALUATION_NAME%>"
                        value="<%=sanitizeForHtml(data.evaluationResults.evaluation.name)%>">
                              
                </form>
                            
                        <div>
                        <span class="help-block">
                            Non-English characters not displayed properly in the downloaded file?<span class="btn-link"
                            data-toggle="modal"
                            data-target="#evalResultsHtmlWindow"
                            onclick = "submitFormAjax()">
                            click here</span>
                         </span>
                        </div>
                        
                        <form id="csvToHtmlForm">
                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.courseId%>">
                        <input type="hidden" name="<%=Const.ParamsNames.EVALUATION_NAME%>" value="<%=data.evalName%>">
                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                        <input type="hidden" name="<%=Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED%>" value=true>
                        </form>

                        <div class="modal fade" id="evalResultsHtmlWindow">
                            <div class="modal-dialog modal-lg">
                                <div class="modal-content">
                                    <div class="modal-header">
                                          <span class="pull-left help-block">
                                        Tips: After Selecting the table, <kbd>Ctrl + C</kbd> to COPY and <kbd>Ctrl + V</kbd> to PASTE to your Excel Workbook.
                                        </span>
                                        <button type="button"
                                            class="btn btn-default"
                                            data-dismiss="modal">Close</button>
                                        <button type="button"
                                            class="btn btn-primary"
                                            onclick="selectElementContents( document.getElementById('summaryModalTable') );">
                                            Select Table</button>
                                    </div>
                                    <div class="modal-body">
                                    <div class="table-responsive">
                                    <div id="summaryModalTable">
                                    
                                    </div>
                                    <br>
                                    <div id="ajaxStatus">
                                    </div>
                                    </div>
                                    </div>
                                   
                                </div>
                                <!-- /.modal-content -->
                            </div>
                            <!-- /.modal-dialog -->
                        </div>
                        <!-- /.modal -->
                
                      
                   </div>
                </div>
            </div>
            
        </div>

        <br>
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <br>

        <%
            out.flush();
        %>

        <div id="instructorEvaluationSummaryTable" class="evaluation_result">
            <div class="row">
            <div class="col-sm-4">
            <h4><span class="label label-info" data-toggle="tooltip" data-placement="top" data-container="body"  
                        title='<%=Const.Tooltips.CLAIMED%>'>CC</span> Claimed Contribution 
            </h4>
            </div>
            <div class="col-sm-5"> 
            <h4><span class="label label-info" data-toggle="tooltip" data-placement="top" data-container="body"  
                        title='<%=Const.Tooltips.PERCEIVED%>'>PC</span> Perceived Contribution 
            </h4>
            </div>
            <div class="col-sm-3">
            <h4><span class="label label-info">E</span> Equal Share</h4>
            </div>
            </div>
            <div class="pull-right">
            [<a href="/instructorHelp.html#faq7a" target="_blank" id="interpret_help_link">How do I interpret/use these values?</a> ]
        </div>

            <table class="table table-bordered table-striped" id="dataTable">
                <thead class="fill-primary">
                <tr>
                    <th class="button-sort-none" id="button_sortteamname" onclick="toggleSort(this,1);">Team 
                        <span class="icon-sort unsorted"></span></th>
                    <th class="button-sort-none" id="button_sortname" onclick="toggleSort(this,2)">Student
                        <span class="icon-sort unsorted"></span></th>
                    <th class="button-sort-none" id="button_sortclaimed" onclick="toggleSort(this,3,sortByPoint)"> CC
                        <span class="icon-sort unsorted"></span></th>
                    <th class="button-sort-none" id="button_sortperceived" onclick="toggleSort(this,4,sortByPoint)">PC
                        <span class="icon-sort unsorted"></span></th>
                    <th class="button-sort-none" id="button_sortdiff" onclick="toggleSort(this,5,sortByDiff)"
                        data-toggle="tooltip" data-placement="top" data-container="body"  
                        title='<%=Const.Tooltips.EVALUATION_DIFF%>'>Diff
                        <span class="icon-sort unsorted"></span></th>
                    <th class="align-center" data-toggle="tooltip" data-placement="top" data-container="body"
                        title='<%=Const.Tooltips.EVALUATION_POINTS_RECEIVED%>'>Ratings Received</th>
                    <th class="align-center">Action(s)</th>
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
                        title="<%=InstructorEvalResultsPageData.sanitizeForHtml(student.comments)%>"> 
                        <%=student.name%>
                    </td>
                    <td><%=InstructorEvalResultsPageData.getPointsAsColorizedHtml(studentResult.summary.claimedToInstructor)%></td>
                    <td><%=InstructorEvalResultsPageData.getPointsAsColorizedHtml(studentResult.summary.perceivedToInstructor)%></td>
                    <td><%=InstructorEvalResultsPageData.getPointsDiffAsHtml(studentResult)%></td>
                    <td><%=InstructorEvalResultsPageData.getNormalizedPointsListColorizedDescending(studentResult.incoming)%></td>
                    <td class="align-center no-print">
                        <a class="btn btn-default btn-xs" name="viewEvaluationResults<%=idx%>" id="viewEvaluationResults<%=idx%>" target="_blank"
                        href="<%=data.getInstructorEvaluationSubmissionViewLink(data.evaluationResults.evaluation.courseId, data.evaluationResults.evaluation.name, student.email)%>"
                        data-toggle="tooltip" data-placement="top" data-container="body"  
                        title="<%=Const.Tooltips.EVALUATION_SUBMISSION_VIEW_REVIEWER%>"
                        <% if (!data.instructor.isAllowedForPrivilege(student.section, Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES
                                    +data.evaluationResults.evaluation.name,
                        		Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)) { %>
                                disabled="disabled"
                        <% } %>
                        > View</a>
                        <a class="btn btn-default btn-xs" name="editEvaluationResults<%=idx%>" id="editEvaluationResults<%=idx%>" target="_blank"
                        href="<%=data.getInstructorEvaluationSubmissionEditLink(data.evaluationResults.evaluation.courseId, data.evaluationResults.evaluation.name, student.email)%>"
                        data-toggle="tooltip" data-placement="top" data-container="body"  
                        title="<%=Const.Tooltips.EVALUATION_SUBMISSION_INSTRUCTOR_EDIT%>"
                        onclick="return openChildWindow(this.href)"
                        <% if (!data.instructor.isAllowedForPrivilege(student.section, Const.EVAL_PREFIX_FOR_INSTRUCTOR_PRIVILEGES
                                    +data.evaluationResults.evaluation.name,
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)) { %>
                               disabled="disabled"
                        <% } %>
                        > Edit</a>
                    </td>
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

        <%
            for(boolean byReviewer = true, repeat=true; repeat; repeat = byReviewer, byReviewer=false){
        %>
        <div
            id="instructorEvaluationDetailed<%=byReviewer ? "Reviewer" : "Reviewee"%>Table"
            class="evaluation_result" style="display: none;">
        
            <%
                boolean firstTeam = true;
                for(TeamResultBundle teamResultBundle: data.evaluationResults.teamResults.values()){
                    if(firstTeam) 
                        firstTeam = false; 
                    else 
                        out.print("<br>");
            %>
            
            <div class="well well-plain">
                <span class="text-primary"><h4>
                    <strong><%=sanitizeForHtml(teamResultBundle.getTeamName())%></strong>
                </h4></span>
                <br>
                <%
                    boolean firstStudent = true;
                    for(StudentResultBundle studentResult: teamResultBundle.studentResults){
                        StudentAttributes student = studentResult.student;
                        if(firstStudent) 
                            firstStudent = false; 
                        else 
                            out.print("<br>");
                %>
                      <div class="panel panel-primary">
                        <div class="panel-heading">
                          <div class="row">
                            <div class="col-md-3"><%=byReviewer ? "Reviewer" : "Reviewee"%>:
                            <strong><%=student.name%></strong>
                            </div>
                            <div class="col-md-4">
                              <div class="pull-right">
                                <span data-toggle="tooltip" data-placement="top" data-container="body"  
                                    title='<%=Const.Tooltips.CLAIMED%>'>
                                Claimed contribution: <%=InstructorEvalResultsPageData.getPointsInEqualShareFormatAsHtml(studentResult.summary.claimedToInstructor,true)%>
                                </span>
                              </div>
                            </div>
                            <div class="col-md-4">
                              <div class="pull-right">
                                <span data-toggle="tooltip" data-placement="top" data-container="body"  
                                    title='<%=Const.Tooltips.PERCEIVED%>'>
                                Perceived contribution: <%=InstructorEvalResultsPageData.getPointsInEqualShareFormatAsHtml(studentResult.summary.perceivedToInstructor,true)%>
                                </span>
                              </div>
                            </div>
                           
                                 <%
                                    if(byReviewer){
                                 %> 
                                <div class="col-md-1">
                                    <div class="pull-right">
                                    <% if (data.instructor.isAllowedForPrivilege(student.section, data.evaluationResults.evaluation.name,
                                           Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)) { %>
                                           <a target="_blank" class="button btn-primary btn-xs"
                                            href="<%=data.getInstructorEvaluationSubmissionEditLink(student.course, data.evaluationResults.evaluation.name, student.email)%>"
                                            onclick="return openChildWindow(this.href)"><span class="glyphicon glyphicon-pencil"></span> Edit</a> 
                                    <% } %>
                                    </div>
                                </div>
                                <%
                                  }
                                %>
                          </div>
                        </div>

                        <table class="table table-bordered">
                          <tbody>
                            <tr>
                              <td><strong>Self evaluation:</strong> <br><%=InstructorEvalResultsPageData.getJustificationAsSanitizedHtml(studentResult.getSelfEvaluation())%></td>
                            </tr>
                            <tr>
                              <td><strong>Comments about the team:</strong><br><%=InstructorEvalResultsPageData.getP2pFeedbackAsHtml(sanitizeForHtml(studentResult.getSelfEvaluation().p2pFeedback.getValue()), data.evaluationResults.evaluation.p2pEnabled)%></td>
                            </tr>

                          </tbody>
                        </table>

                        <table class="table table-bordered table-striped">
                          <thead>
                            <tr class="border-top-gray fill-info">
                              <th class="col-sm-1"><%=byReviewer ? "To" : "From"%></th>
                              <th class="col-sm-1">Contribution</th>
                              <th class="col-sm-5">Confidential comments</th>
                              <th class="col-sm-5">Feedback to peer</th>
                            </tr>
                          </thead>
                          <tbody>
                             <%
                                        for(SubmissionAttributes sub: (byReviewer ? studentResult.outgoing : studentResult.incoming)){ 
                                                                                            if(sub.reviewer.equals(sub.reviewee)) continue;
                             %>
                            <tr>
                              <td><%=sanitizeForHtml(byReviewer ? sub.details.revieweeName : sub.details.reviewerName)%></td>
                              <td><%=InstructorEvalResultsPageData.getPointsAsColorizedHtml(sub.details.normalizedToInstructor)%></td>
                              <td><%=InstructorEvalResultsPageData.getJustificationAsSanitizedHtml(sub)%></td>
                              <td><%=InstructorEvalResultsPageData.getP2pFeedbackAsHtml(sanitizeForHtml(sub.p2pFeedback.getValue()), data.evaluationResults.evaluation.p2pEnabled)%></td>
                            </tr>
                            <%
                                }
                            %>
                          </tbody>
                        </table>
                    </div>
                <%
                    }
                %>
            </div>
                <br>
                <div class="align-center">
                    <button class="btn btn-info btn-circle" value="To Top" onclick="scrollToTop()">
                        <span class="glyphicon glyphicon-arrow-up"></span></button>
                       <a onclick="scrollToTop()" href="#"> To Top </a>
                </div>
                <br>
                <%
                    }
                %>
         </div>
                <%
                    }
                %>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    <script>
        setStatusMessage("");
    </script>
</body>
</html>