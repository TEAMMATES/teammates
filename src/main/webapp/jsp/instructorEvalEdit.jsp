<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.datatransfer.CourseAttributes"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.ui.controller.InstructorEvalEditPageData"%>
<%
    InstructorEvalEditPageData data = (InstructorEvalEditPageData)request.getAttribute("data");
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
    <link rel="stylesheet" href="/stylesheets/datepicker.css" type="text/css" media="screen">

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/date.js"></script>
    <script type="text/javascript" src="/js/datepicker.js"></script>
        <script type="text/javascript" src="/js/common.js"></script>

    <script type="text/javascript" src="/js/instructor.js"></script>
    <script type="text/javascript" src="/js/instructorEvals.js"></script>
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
        <h1>Edit Evaluation</h1>
    </div>
    <div class="panel panel-primary">
      <div class="panel-heading">Edit evaluation</div>
      <div class="panel-body fill-plain">
        <form class="form-horizontal" role="form" action="<%=Const.ActionURIs.INSTRUCTOR_EVAL_EDIT_SAVE%>" name="form_addevaluation">
          
            <div class="col-md-7">
              <div class="row">
                <div class="form-group">
                  <label class="col-sm-4 control-label">Course:</label>
                  <div class="col-sm-7">
                    <p class="form-control-static" name="<%=Const.ParamsNames.COURSE_ID%>" id="<%=Const.ParamsNames.COURSE_ID%>"><%=data.evaluation.courseId%>
                    </p>
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-sm-4 control-label">
                    Evaluation name:
                  </label>
                  <div class="col-sm-7">
                    <p class="form-control-static" name="<%=Const.ParamsNames.EVALUATION_NAME%>" id="<%=Const.ParamsNames.EVALUATION_NAME%>">
                            <%=InstructorEvalEditPageData.sanitizeForHtml(data.evaluation.name)%>
                    </p>
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="form-group">
                  <label class="col-sm-4 control-label">Peer
                    feedback:</label>
                  <div class="col-sm-8">
                    <div class="radio">
                      <label> <input type="radio" name="<%=Const.ParamsNames.EVALUATION_COMMENTSENABLED%>" 
                                id="commentsstatus_enabled" value="true"
                                <%=data.evaluation.p2pEnabled ? "checked=\"checked\"" : ""%>
                                data-toggle="tooltip" data-placement="top" 
                                title="<%=Const.Tooltips.EVALUATION_INPUT_COMMENTSSTATUS%>"> Enabled 
                      </label>
                    </div>
                    <div class="radio">
                      <label> <input type="radio" name="<%=Const.ParamsNames.EVALUATION_COMMENTSENABLED%>" 
                                id="commentsstatus_disabled" value="false"
                                <%=!data.evaluation.p2pEnabled ? "checked=\"checked\"" : ""%>
                                data-toggle="tooltip" data-placement="top" 
                                title="<%=Const.Tooltips.EVALUATION_INPUT_COMMENTSSTATUS%>"> Disabled 
                      </label>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            
            <div class="col-md-5 border-left-gray">
              <div class="row">
                <div class="form-group">
                  <label class="col-sm-4 control-label">Starting time:</label>
                  <div class="col-md-4">
                    <input class="form-control col-sm-2 inline" type="datepicker"
                           name="<%=Const.ParamsNames.EVALUATION_START%>"
                           id="<%=Const.ParamsNames.EVALUATION_START%>"
                           data-toggle="tooltip" data-placement="top" 
                           title="<%=Const.Tooltips.EVALUATION_INPUT_START%>"
                           value="<%=TimeHelper.formatDate(data.evaluation.startTime)%>">
                  </div>
                  <div class="col-md-4">
                    <select class="form-control inline"
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
                <div class="form-group">
                  <label class="col-sm-4 control-label">Ending time:</label>
                  <div class="col-md-4">
                    <input class="form-control col-sm-2 inline" type="datepicker"
                           name="<%=Const.ParamsNames.EVALUATION_DEADLINE%>" 
                           id="<%=Const.ParamsNames.EVALUATION_DEADLINE%>"
                           data-toggle="tooltip" data-placement="top" 
                           title="<%=Const.Tooltips.EVALUATION_INPUT_DEADLINE%>"
                           value="<%=TimeHelper.formatDate(data.evaluation.endTime)%>">
                  </div>
                  <div class="col-md-4">
                    <select class="form-control inline"
                            name="<%=Const.ParamsNames.EVALUATION_DEADLINETIME%>"
                            id="<%=Const.ParamsNames.EVALUATION_DEADLINETIME%>">
                       <%
                          for(String opt: data.getTimeOptionsAsHtml(false)) out.println(opt);
                       %>
                    </select>
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="form-group">
                  <label class="col-sm-4 control-label">Grace
                    period:</label>
                  <div class="col-md-4">
                    <select class="form-control inline" name="<%=Const.ParamsNames.EVALUATION_GRACEPERIOD%>"
                            id="<%=Const.ParamsNames.EVALUATION_GRACEPERIOD%>"
                            data-toggle="tooltip" data-placement="top" 
                            title="<%=Const.Tooltips.EVALUATION_INPUT_GRACEPERIOD%>">
                       <%
                          for(String opt: data.getGracePeriodOptionsAsHtml()) out.println(opt);
                       %>
                    </select>
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="form-group">
                  <label class="col-sm-4 control-label">Timezone:</label>
                  <div class="col-md-4">
                    <select class="form-control inline" name="<%=Const.ParamsNames.EVALUATION_TIMEZONE%>" 
                            id="<%=Const.ParamsNames.EVALUATION_TIMEZONE%>"
                            data-toggle="tooltip" data-placement="top" 
                            title="<%=Const.Tooltips.EVALUATION_INPUT_TIMEZONE%>">
                      <%
                        for(String opt: data.getTimeZoneOptionsAsHtml()) out.println(opt);
                      %>
                    </select>
                  </div>
                </div>
              </div>
            </div>
    
            <div class="form-group">
              <label class="col-sm-2 control-label">Instructions:</label>
              <div class="col-sm-10">
                <textarea rows="5" class="form-control" name="<%=Const.ParamsNames.EVALUATION_INSTRUCTIONS%>" 
                          id="<%=Const.ParamsNames.EVALUATION_INSTRUCTIONS%>" style="max-width:100%;"
                          data-toggle="tooltip" data-placement="top" 
                          title="<%=Const.Tooltips.EVALUATION_INPUT_INSTRUCTIONS%>"><%=InstructorEvalEditPageData.sanitizeForHtml(data.evaluation.instructions.getValue())%></textarea>
              </div>
            </div>

          <br>

            <div class="form-group">
            <div class="row">
              <div class="col-xs-offset-2 col-xs-5 col-sm-offset-5 col-sm-2">
                <button id="button_submit" type="submit" class="btn btn-primary"
                        onclick="return checkEditEvaluation(this.form);"
                        value="Save Changes">Save Changes</button>
              </div>
              <div class="col-xs-2">
                <button id="button_back" type="button" class="btn btn-default"
                       onclick="window.location.href='<%=data.getInstructorEvaluationLink()%>'"
                       value="Cancel">Cancel</button>
                </div>
            </div>
            </div>
          <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" id="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
          <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" id="<%=Const.ParamsNames.COURSE_ID%>" value="<%=data.evaluation.courseId%>"></input>
          <input type="hidden" name="<%=Const.ParamsNames.EVALUATION_NAME%>" id="<%=Const.ParamsNames.EVALUATION_NAME%>" value="<%=InstructorEvalEditPageData.sanitizeForHtml(data.evaluation.name)%>"></input>

        </form>
      </div>
    </div>
    <br>
    <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
    <br>
    <br>
    <br>
</div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>