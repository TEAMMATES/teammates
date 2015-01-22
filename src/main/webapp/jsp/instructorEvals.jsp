<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>

<%@page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionDetailsBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationStats"%>
<%@ page import="teammates.common.util.FieldValidator"%>
<%@ page import="teammates.ui.controller.InstructorEvalPageData"%>
<%
    InstructorEvalPageData data = (InstructorEvalPageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TEAMMATES - Instructor</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css"/>
    <link rel="stylesheet" href="/stylesheets/datepicker.css" type="text/css" media="screen">

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/js/date.js"></script>
    <script type="text/javascript" src="/js/datepicker.js"></script>
        <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>

    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorEvals.js"></script>
    <script type="text/javascript" src="/js/ajaxResponseRate.js"></script>
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <%
        if(data.newEvaluationToBeCreated==null){
    %>
    <script type="text/javascript">
        var doPageSpecificOnload = selectDefaultTimeOptions;
    </script>
    <%
        }
    %>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
    <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />

    <div id="frameBodyWrapper" class="container theme-showcase">
        <div id="topOfPage"></div>
        <h1>Add New Evaluation Session</h1>
        
        <div class="well well-plain">
            <div class="row">
                <h4 class="label-control col-md-2 text-md">Create new </h4>
                <div class="col-md-5">
                    <select class="form-control"
                        name="feedbackchangetype"
                        id="feedbackchangetype"
                        title="Select a different type of session here."
                        data-toggle="tooltip" 
                        data-placement="top">
                        <option value="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE%>">
                            Feedback Session with customizable questions</option>
                    </select>
                </div>
                <div class="col-md-5">
                    <h5>
                        <span class="glyphicon glyphicon-info-sign glyphicon-primary"></span>
                        <span class="text-muted"> Select a session type before filling out the form below.</span>
                    </h5>
                </div>
            </div>
            <br>
            <form class="form-group" method="post" action="<%=Const.ActionURIs.INSTRUCTOR_EVAL_ADD%>" name="form_addevaluation">
                <div class="panel panel-primary">
                    <div class="panel-body">
                        <div class="row">
                            <div class="col-md-7">
                                <div class="row">
                                    <div class="form-group"
                                        title="<%=Const.Tooltips.EVALUATION_INPUT_COURSE%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <h5 class="col-sm-4">                                        
                                            <label 
                                                for="<%=Const.ParamsNames.COURSE_ID%>"
                                                class="control-label">
                                                Course
                                            </label>
                                        </h5>
                                        <div class="col-md-7">
                                            <select class="form-control" 
                                                name="<%=Const.ParamsNames.COURSE_ID%>"
                                                id="<%=Const.ParamsNames.COURSE_ID%>">
                                                    <%
                                                        for(String opt: data.getCourseIdOptions()) out.println(opt);
                                                    %>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="form-group"
                                        title="<%=Const.Tooltips.EVALUATION_INPUT_NAME%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <h5 class="col-sm-4 ">
                                            <label 
                                                for="<%=Const.ParamsNames.EVALUATION_NAME%>"
                                                class="control-label">
                                                Evaluation name
                                            </label>
                                        </h5>
                                        <div class="col-md-7">
                                            <input class="form-control" 
                                                type="text"
                                                name="<%=Const.ParamsNames.EVALUATION_NAME%>" 
                                                id="<%=Const.ParamsNames.EVALUATION_NAME%>"
                                                maxlength =<%=FieldValidator.EVALUATION_NAME_MAX_LENGTH%>
                                                value="<%if(data.newEvaluationToBeCreated!=null) out.print(InstructorEvalPageData.sanitizeForHtml(data.newEvaluationToBeCreated.name));%>"
                                                placeholder="e.g. Midterm Evaluation">
                                        </div>
                                    </div>
                                </div>
                                <div class="row"
                                    title="<%=Const.Tooltips.EVALUATION_INPUT_COMMENTSSTATUS%>"
                                    data-toggle="tooltip"
                                    data-placement="top">
                                    <h5 class="col-md-4">
                                        <label for="" class="control-label">
                                            Peer feedback
                                        </label>
                                    </h5>
                                    <div class="col-md-7">
                                        <div class="radio">
                                            <label for="commentsstatus_enabled">Enabled</label>
                                            <input type="radio" name="<%=Const.ParamsNames.EVALUATION_COMMENTSENABLED%>"
                                                id="commentsstatus_enabled" value="true"
                                                <%if(data.newEvaluationToBeCreated==null || data.newEvaluationToBeCreated.p2pEnabled) out.print("checked=\"checked\"");%>>
                                        </div>
                                        <div class="radio">
                                            <label for="commentsstatus_disabled">Disabled</label>
                                            <input type="radio" name="<%=Const.ParamsNames.EVALUATION_COMMENTSENABLED%>"
                                                id="commentsstatus_disabled" value="false"
                                                <%if(data.newEvaluationToBeCreated!=null && !data.newEvaluationToBeCreated.p2pEnabled) out.print("checked=\"checked\"");%>>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="col-md-5 border-left-gray">
                                <div class="row">
                                    <div class="form-group" 
                                        title="<%=Const.Tooltips.EVALUATION_INPUT_START%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <h5 class="col-md-4">
                                            <label for="<%=Const.ParamsNames.EVALUATION_START%>" class="control-label">
                                                Starting time
                                            </label>
                                        </h5>
                                        <div class="col-md-5" >
                                            <input class="form-control"
                                                type="text"
                                                name="<%=Const.ParamsNames.EVALUATION_START%>"
                                                id="<%=Const.ParamsNames.EVALUATION_START%>"
                                                value="<%=(data.newEvaluationToBeCreated==null? TimeHelper.formatDate(TimeHelper.getNextHour()) : TimeHelper.formatDate(data.newEvaluationToBeCreated.startTime))%>">
                                        </div>
                                        <div class="col-md-3">
                                            <select class="form-control"
                                                    name="<%=Const.ParamsNames.EVALUATION_STARTTIME%>"
                                                    id="<%=Const.ParamsNames.EVALUATION_STARTTIME%>">
                                                        <%
                                                            for(String opt: data.getTimeOptionsAsHtml(true)) out.println(opt);
                                                        %>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="form-group" 
                                        title="<%=Const.Tooltips.EVALUATION_INPUT_DEADLINE%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <h5 class="col-md-4">
                                            <label for="<%=Const.ParamsNames.EVALUATION_DEADLINE%>" class="control-label">
                                                Closing time
                                            </label>
                                        </h5>
                                        <div class="col-md-5" >
                                            <input class="form-control"
                                                type="text"
                                                name="<%=Const.ParamsNames.EVALUATION_DEADLINE%>"
                                                id="<%=Const.ParamsNames.EVALUATION_DEADLINE%>"
                                                value="<%=(data.newEvaluationToBeCreated==null? "" : TimeHelper.formatDate(data.newEvaluationToBeCreated.endTime))%>">
                                        </div>
                                        <div class="col-md-3">
                                            <select class="form-control"
                                                    name="<%=Const.ParamsNames.EVALUATION_DEADLINETIME%>"
                                                    id="<%=Const.ParamsNames.EVALUATION_DEADLINETIME%>">
                                                        <%
                                                            for(String opt: data.getTimeOptionsAsHtml(true)) out.println(opt);
                                                        %>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="form-group" 
                                        title="<%=Const.Tooltips.EVALUATION_INPUT_GRACEPERIOD%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <h5 class="col-md-4">
                                            <label for="<%=Const.ParamsNames.EVALUATION_GRACEPERIOD%>" class="control-label">
                                                Grace Period
                                            </label>
                                        </h5>
                                        <div class="col-md-5">
                                            <select class="form-control" 
                                                name="<%=Const.ParamsNames.EVALUATION_GRACEPERIOD%>"
                                                id="<%=Const.ParamsNames.EVALUATION_GRACEPERIOD%>">
                                                    <%
                                                    for(String opt: data.getGracePeriodOptionsAsHtml()) {
                                                    	out.println(opt);
                                                    }
                                                    %>
                                        </select>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="form-group" 
                                        title="<%=Const.Tooltips.EVALUATION_INPUT_TIMEZONE%>"
                                        data-toggle="tooltip"
                                        data-placement="top">
                                        <h5 class="col-md-4">
                                            <label for="<%=Const.ParamsNames.EVALUATION_GRACEPERIOD%>" class="control-label">
                                                Timezone
                                            </label>
                                        </h5>
                                        <div class="col-md-5">
                                            <select class="form-control" 
                                                name="<%=Const.ParamsNames.EVALUATION_TIMEZONE%>"
                                                id="<%=Const.ParamsNames.EVALUATION_TIMEZONE%>">
                                                    <%
                                                    for(String opt: data.getTimeZoneOptionsAsHtml()) out.println(opt);
                                                    %>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <br>
                        <div class="row"
                            title="<%=Const.Tooltips.EVALUATION_INPUT_INSTRUCTIONS%>"
                            data-toggle="tooltip"
                            data-placement="top">
                            <h5 class="col-md-2">
                                <label for="<%=Const.ParamsNames.EVALUATION_INSTRUCTIONS%>" class="control-label">
                                    Instructions
                                </label>
                            </h5>
                            <div class="col-md-10">
                                <%
                                    if(data.newEvaluationToBeCreated==null){
                                %>
                                    <textarea rows="3" class="form-control" 
                                        name="<%=Const.ParamsNames.EVALUATION_INSTRUCTIONS%>" 
                                        id="<%=Const.ParamsNames.EVALUATION_INSTRUCTIONS%>"
                                        placeholder="Please submit your peer evaluation based on the overall contribution of your teammates so far."
                                        
                                            >Please submit your peer evaluation based on the overall contribution of your teammates so far.</textarea>
                                <%
                                    } else {
                                %>
                                    <textarea rows="3" class="form-control" 
                                        name="<%=Const.ParamsNames.EVALUATION_INSTRUCTIONS%>" 
                                        id="<%=Const.ParamsNames.EVALUATION_INSTRUCTIONS%>"
                                        placeholder="Please submit your peer evaluation based on the overall contribution of your teammates so far."
                                        
                                            ><%=InstructorEvalPageData.sanitizeForHtml(data.newEvaluationToBeCreated.instructions.getValue())%></textarea>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                        <br>
                        <div class="row">
                            <div class="col-md-12">
                                <button type="submit" 
                                    id="button_submit" 
                                    class="btn btn-primary center-block"
                                    onclick="return checkAddEvaluation(this.form);"
                                    disabled="disabled">
                                    Create Evaluation Session
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
            </form>
        </div>
        <br>
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <br>


        <table class="table-responsive table table-striped table-bordered">
            <thead>
                <tr class="fill-primary">
                    <th id="button_sortid" onclick="toggleSort(this,1);"
                        class="button-sort-ascending">Course ID <span
                        class="icon-sort unsorted"></span>
                    </th>
                    <th id="button_sortname" onclick="toggleSort(this,2)"
                        class="button-sort-none">Session Name <span
                        class="icon-sort unsorted"></span>
                    </th>
                    <th>Status</th>
                    <th><span
                        title="<%=Const.Tooltips.EVALUATION_RESPONSE_RATE%>"
                        data-toggle="tooltip" data-placement="top">
                            Response Rate</span></th>
                <th class="no-print">Action(s)</th>
            </tr>
            </thead>
            <%
                int sessionIdx = -1;
                if (data.existingFeedbackSessions.size() > 0
                        || data.existingEvalSessions.size() > 0) {
                    for (EvaluationAttributes edd : data.existingEvalSessions) {
                        sessionIdx++;
            %>
            <tr class="sessionsRow" id="evaluation<%=sessionIdx%>">
                <td><%=edd.courseId%></td>
                <td><%=InstructorEvalPageData
                            .sanitizeForHtml(edd.name)%></td>
                <td><span title="<%=InstructorEvalPageData.getInstructorHoverMessageForEval(edd)%>"
                        data-toggle="tooltip" data-placement="top">
                        <%=InstructorEvalPageData.getInstructorStatusForEval(edd)%>
                    </span>
                </td>
                <td
                    class="session-response-for-test<%if (!TimeHelper.isOlderThanAYear(edd.endTime)) {
                        out.print(" recent");
                    }%>">
                    <a oncontextmenu="return false;"
                    href="<%=data.getEvaluationStatsLink(edd.courseId,
                            edd.name)%>">Show</a>
                </td>
                <td class="no-print"><%=data.getInstructorEvaluationActions(edd, false, data.instructors.get(edd.courseId))%></td>
            </tr>
            <%
                }
                Map<String, List<String>> courseIdSectionNamesMap = data.getCourseIdSectionNamesMap(data.existingFeedbackSessions);
                for (FeedbackSessionAttributes fdb : data.existingFeedbackSessions) {
                    sessionIdx++;
            %>
            <tr class="sessionsRow" id="session<%=sessionIdx%>">
                <td><%=fdb.courseId%></td>
                <td><%=InstructorEvalPageData
                            .sanitizeForHtml(fdb.feedbackSessionName)%></td>
                <td><span title="<%=InstructorEvalPageData.getInstructorHoverMessageForFeedbackSession(fdb)%>" 
                        data-toggle="tooltip" data-placement="top">
                        <%=InstructorEvalPageData.getInstructorStatusForFeedbackSession(fdb)%>
                    </span>
                </td>
                <td
                    class="session-response-for-test<%if (!TimeHelper.isOlderThanAYear(fdb.createdTime)) {
                        out.print(" recent");
                    }%>">
                    <a oncontextmenu="return false;"
                    href="<%=data.getFeedbackSessionStatsLink(fdb.courseId,
                            fdb.feedbackSessionName)%>">Show</a>
                </td>
                <td class="no-print"><%=data.getInstructorFeedbackSessionActions(fdb, false, data.instructors.get(fdb.courseId), courseIdSectionNamesMap.get(fdb.courseId))%></td>
            </tr>
            <%
                }
                } else {
            %>
            <tr>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
            <%
                }
            %>
        </table>
        <br> <br> <br>
        <%
            if (sessionIdx == -1) {
        %>
        <div class="align-center">No records found.</div>
        <br> <br> <br>
        <%
            }
        %>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>