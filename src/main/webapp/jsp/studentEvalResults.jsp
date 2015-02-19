<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.ui.controller.StudentEvalResultsPageData"%>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes" %>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="teammates.ui.controller.StudentEvalResultsPageData"%>
<%
    StudentEvalResultsPageData data = (StudentEvalResultsPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>TEAMMATES - Student</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css"/>

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include> 

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->   
</head>

<body>

    <jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />
    
    <div class="container" id="frameBodyWrapper">
        <div id="headerOperation">
            <h1>Evaluation Results</h1>
        </div>

        <div class="well well-plain">
            <form class="form-horizontal" role="form">
                <div class="form-group">
                    <label class="col-sm-3 control-label">Course:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static">
                            <%=data.eval.courseId%>
                        </p>
                    </div>
                </div> 
                <div class="form-group">
                    <label class="col-sm-3 control-label">Session:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static">
                            <%=PageData.sanitizeForHtml(data.eval.name)%>
                        </p>
                    </div>
                </div>  
                <div class="form-group">
                    <label class="col-sm-3 control-label">Student:</label>
                    <div class="col-sm-9">
                        <p class="form-control-static">
                            <%=PageData.sanitizeForHtml(data.student.name)%>
                        </p>
                    </div>
                </div>  
            </form>
        </div>

        <h3 style="display:inline;">
            <span class="label label-primary">Your results</span> &nbsp; &nbsp; &nbsp;
        </h3>

        <h4 style="display:inline; float:right;">
            <span class="label label-danger">E</span> Equal Share
        </h4>

        <br>
        <br>

        <div class="panel panel-primary">
            <div class="panel-heading">Comparison of work distribution </div>
                <table class="table table-striped">
                    <tbody>
                        <tr>
                          <td><strong>My view:</strong></td>
                          <td><span class="text-muted"><strong>of me:</strong></span> 
                            <%=StudentEvalResultsPageData.getPointsAsColorizedHtml(data.evalResult.outgoing.get(0).points)%>
                          </td>
                          <td><span class="text-muted"><strong>of others:</strong></span>
                            <%=StudentEvalResultsPageData.getPointsListOriginal(data.outgoing)%>
                          </td>
                        </tr>
                        <tr>
                          <td><strong>Team's view:</strong></td>
                          <td><span class="text-muted"><strong>of me:</strong></span> <%=StudentEvalResultsPageData.getPointsAsColorizedHtml(data.evalResult.incoming.get(0).details.normalizedToStudent)%>
                          </td>
                          <td><span class="text-muted"><strong>of others:</strong></span>
                            <%=StudentEvalResultsPageData.getNormalizedToStudentsPointsList(data.incoming)%></td>
                        </tr>
                    </tbody>
                </table>
        </div>

        <div class="panel panel-primary">
            <div class="panel-heading">Anonymous feedback from others</div>
            <table class="table table-striped">
                <tbody>
                    <%
                        for(SubmissionAttributes sub: data.incoming) {
                    %>
                    <tr>
                        <td>
                            <%=StudentEvalResultsPageData.formatP2PFeedback(PageData.sanitizeForHtml(sub.p2pFeedback.getValue()), data.eval.p2pEnabled)%>
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>

        <div class="panel panel-primary">
            <div class="panel-heading">What others said about their own contribution</div>
            <table class="table table-striped table-bordered">
                <tbody>
                    <%
                        for(SubmissionAttributes sub: data.selfEvaluations){
                    %>
                    <tr>
                        <td><strong>
                            <%=PageData.sanitizeForHtml(sub.details.reviewerName)%>
                        </strong></td>
                        <td>
                            <%=PageData.sanitizeForHtml(sub.justification.getValue())%>
                        </td>    
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>

        <p><span class="glyphicon glyphicon-question-sign"> </span> <a href="#interpret">How do I interpret these results? </a></p>

        <h3><span class="label label-default">Your submission</span></h3>

        <div class="panel panel-default">
            <div class="panel-heading">Self evaluation</div>
            <table class="table table-striped">
                <tbody>
                <tr>
                  <td><strong>Your estimated contribution:</strong></td>
                  <td>
                    <%=StudentEvalResultsPageData.getPointsAsColorizedHtml(data.evalResult.getSelfEvaluation().points)%>
                  </td>
                </tr>
                <tr>
                  <td><strong>Comments about your contribution:</strong></td>
                  <td>
                    <%=PageData.sanitizeForHtml(data.evalResult.getSelfEvaluation().justification.getValue())%>
                  </td>
                </tr>
                <tr>
                  <td><strong>Comments about team dynamics:</strong></td>
                  <td>
                    <%=PageData.sanitizeForHtml(data.evalResult.getSelfEvaluation().p2pFeedback.getValue())%>
                  </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="panel panel-default">
            <div class="panel-heading">Evaluation of team members</div>
            <table class="table table-striped table-bordered">
                <tbody>
                    <tr>
                      <th>Name</th>
                      <th>Points</th>
                      <th>Comments about the team member</th>
                      <th>Feedback to the team member</th>
                    </tr>
                </tbody>
                <tbody>
                    <%
                        for(SubmissionAttributes sub: data.outgoing){
                    %>
                        <tr>
                            <td><strong>
                                <%=PageData.sanitizeForHtml(sub.details.revieweeName)%>
                            </strong></td>
                            <td width = "6%">
                                <%=StudentEvalResultsPageData.getPointsAsColorizedHtml(sub.points)%>
                            </td> 
                            <td>
                                <%=PageData.sanitizeForHtml(sub.justification.getValue())%>
                            </td>
                            <td>
                                <%=StudentEvalResultsPageData.formatP2PFeedback(PageData.sanitizeForHtml(sub.p2pFeedback.getValue()), data.eval.p2pEnabled)%>
                            </td>
                        </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>

        <br>
        <br>

        <h4 id = "interpret">How do I interpret these results? </h4>
        <ul class="bulletedList">
            <li>
                Compare values given under 'My view' to those under 'Team's view'.
                This tells you whether your perception matches with what the team thinks, 
                particularly regarding your own contribution. You may ignore minor differences.
            </li>
            <li>
                Team's view of your contribution is the average value of the contribution your 
                team members attributed to you, excluding the contribution you attributed to yourself. 
                That means you cannot boost your perceived contribution by claiming a high contribution 
                for yourself or attributing a low contribution to team members.
            </li>
            <li>
                Also note that the team’s view has been scaled up/down so that the sum of values in your 
                view matches the sum of values in team’s view. That way, you can make a direct comparison 
                between your view and the team’s view. As a result, the values you see in team’s view may 
                not match the values your team members see in their results.
            </li>
        </ul>
        <br>
        <h4>How are these results used in grading?</h4>
        TEAMMATES does not calculate grades. It is up to the instructors to use evaluation results in any way they want. 
        However, TEAMMATES recommend that evaluation results are used only as flags to identify teams with contribution imbalances. 
        Once identified, the instructor is recommended to investigate further before taking action.
        <br><br>
        <h4>How are the contribution numbers calculated?</h4>
        Here are the important things to note:
        <ul class="bulletedList">
            <li>
                The contribution you attributed to yourself is not used when calculating the perceived 
                contribution of you or team members.
            </li>
            <li>
                From the estimates you submit, we try to deduce the answer to this question: 
                In your opinion, if your teammates are doing the project by themselves without you, 
                how do they compare against each other in terms of contribution? This is because we 
                want to deduce your unbiased opinion about your team members’ contributions. Then, 
                we use those values to calculate the average perceived contribution of each team member.
            </li>
            <li>
                When deducing the above, we first adjust the estimates you submitted to remove artificial 
                inflations/deflations. For example, giving everyone [Equal share + 20%] is as same as giving 
                everyone [Equal share] because in both cases all members have done a similar share of work.
            </li>
        </ul>
        The actual calculation is a bit complex and the finer details can be found 
        <a href="/dev/spec.html#supplementaryrequirements-pointcalculationscheme">here</a>.
        <br>
        <br>
        <br>
    </div>
    
    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    
</body>
</html>