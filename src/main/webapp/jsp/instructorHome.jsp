<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>


<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>

<%@ page import="teammates.common.util.TimeHelper"%>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.InstructorAttributes" %>
<%@ page import="teammates.common.datatransfer.CourseSummaryBundle"%>
<%@ page import="teammates.common.datatransfer.EvaluationAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="teammates.ui.controller.InstructorHomePageData"%>
<%
	int countUnarchivedCourses = 0;
	InstructorHomePageData data = (InstructorHomePageData)request.getAttribute("data");
    if (data.account.isInstructor) {
    	for (CourseSummaryBundle courseDetails : data.courses) {
            InstructorAttributes instructor = data.instructors.get(courseDetails.course.id);
            if (instructor.isArchived == null || !instructor.isArchived) {
                countUnarchivedCourses++;
            }
        }
    }
%>
    <!DOCTYPE html>
    <html>
    <head>
        <link rel="shortcut icon" href="/favicon.png" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>TEAMMATES - Instructor</title>
        <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css"/>
        <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>
        <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css"/>
        
        <script type="text/javascript" src="/js/googleAnalytics.js"></script>
        <script type="text/javascript" src="/js/jquery-minified.js"></script>
        <script type="text/javascript" src="/js/date.js"></script>
        
            <script type="text/javascript" src="/js/common.js"></script>
        <script type="text/javascript"  src="/bootstrap/js/bootstrap.min.js"></script>
        
        <script type="text/javascript" src="/js/instructor.js"></script>
        <script type="text/javascript" src="/js/instructorHome.js"></script>
        <script type="text/javascript" src="/js/ajaxResponseRate.js"></script>
        <script type="text/javascript" src="/js/instructorFeedbackAjaxRemindModal.js"></script>
        <jsp:include page="../enableJS.jsp"></jsp:include>
        <!--[if lt IE 9]>
            <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
            <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
        <![endif]-->
    </head>
    
    <body>
        <jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />
    
        <div id="frameBodyWrapper" class="container theme-showcase">
            <div id="topOfPage"></div>
            <div class="inner-container">
                <div class="row">
                    <div class="col-md-5">
                        <h1>Instructor Home</h1>
                    </div>
                    <div class="col-md-5 instructor-header-bar">
                        <form method="get" action="<%=data.getInstructorSearchLink()%>" name="search_form">
                            <div class="input-group">
                                <input type="text" id="searchbox"
                                            title="<%=Const.Tooltips.SEARCH_STUDENT%>"
                                            name="<%=Const.ParamsNames.SEARCH_KEY%>"
                                            class="form-control"
                                            data-toggle="tooltip"
                                            data-placement="top"
                                            placeholder="Student Name">
                                <span class="input-group-btn">
                                    <button class="btn btn-default" type="submit" value="Search" id="buttonSearch">Search</button>
                                </span> 
                            </div>
                            <input type="hidden" name="<%=Const.ParamsNames.SEARCH_STUDENTS%>" value="true">
                            <input type="hidden" name="<%=Const.ParamsNames.SEARCH_COMMENTS_FOR_STUDENTS%>" value="false">
                            <input type="hidden" name="<%=Const.ParamsNames.SEARCH_COMMENTS_FOR_RESPONSES%>" value="false">
                            <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                        </form>
                    </div>
                    <div class="col-md-2 instructor-header-bar">
                        <a class="btn btn-primary btn-md" href="<%=data.getInstructorCourseLink() %>" id="addNewCourse">Add New Course </a>
                    </div>
                </div>
            </div>
            <br>
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            
            <div class="modal fade" id="remindModal" tabindex="-1" role="dialog" aria-labelledby="remindModal" aria-hidden="true">
              <div class="modal-dialog">
                <div class="modal-content">
                  <form method="post" name="form_remind_list" role="form"
                        action="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS%>">
                    <div class="modal-header">
                      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                      <h4 class="modal-title">
                        Remind Particular Students
                        <small>(Select the student(s) you want to remind)</small>
                      </h4>
                    </div>
                    <div class="modal-body">
                      <div id="studentList" class="form-group"></div>
                    </div>
                    <div class="modal-footer">
                      <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                      <input type="submit" class="btn btn-primary" value="Remind">
                      <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                    </div>
                  </form>
                </div>
              </div>
            </div>
            
            <% if (data.account.isInstructor) { %>
            <div class="row<%=countUnarchivedCourses < 2 ? " hidden" : "" %>">
                <div class="col-md-5 pull-right">
                    <div class="row">
                        <div class="col-md-3 btn-group">
                            <h5 class="pull-right"><strong> Sort By: </strong></h5>
                        </div>
                        <div class="col-md-9">
                            <div class="btn-group pull-right" data-toggle="buttons">
                                <label class="btn btn-default <%= data.sortCriteria.equals(Const.SORT_BY_COURSE_ID) ? "active" : "" %>" name="sortby" data="id" id="sortById">
                                    <input type="radio">
                                    Course ID
                                </label>
                                <label class="btn btn-default <%= data.sortCriteria.equals(Const.SORT_BY_COURSE_NAME) ? "active" : "" %>" name="sortby" data="name" id="sortByName">
                                    <input type="radio" name="sortby" value="name" >
                                    Course Name
                                </label>
                                <label class="btn btn-default <%= data.sortCriteria.equals(Const.SORT_BY_COURSE_CREATION_DATE) ? "active" : "" %>" name="sortby" data="createdAt" id="sortByDate">
                                    <input type="radio">
                                    Creation Date
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <br>
        <%
            int courseIdx = -1;
            int sessionIdx = -1;
            for (CourseSummaryBundle courseDetails : data.courses) {
                InstructorAttributes instructor = data.instructors.get(courseDetails.course.id);
                if (instructor.isArchived==null || !instructor.isArchived) {
                    courseIdx++;
        %>
                    <div class="panel panel-primary" id="course<%=courseIdx%>">
                        <div class="panel-heading">
                            <div class="row">
                                <div class="col-md-6">
                                    <strong>
                                        [<%=courseDetails.course.id%>] :
                                        <%=PageData.sanitizeForHtml(courseDetails.course.name)%>
                                    </strong>
                                </div>
                                <div class="col-md-6">
                                    <span class="pull-right">
                                         <a class="btn btn-primary btn-xs btn-tm-actions course-enroll-for-test"
                                            href="<%=data.getInstructorCourseEnrollLink(courseDetails.course.id)%>"
                                            title="<%=Const.Tooltips.COURSE_ENROLL%>" data-toggle="tooltip" data-placement="top"
                                            <% 
                                               if (!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT)) {%>
                                                disabled="disabled"
                                            <% } %>
                                            > Enroll</a>
                                             
                                         <a class="btn btn-primary btn-xs btn-tm-actions course-view-for-test"
                                            href="<%=data.getInstructorCourseDetailsLink(courseDetails.course.id)%>"
                                            title="<%=Const.Tooltips.COURSE_DETAILS%>" data-toggle="tooltip" data-placement="top"> View</a> 
                                            
                                         <a class="btn btn-primary btn-xs btn-tm-actions course-edit-for-test"
                                            href="<%=data.getInstructorCourseEditLink(courseDetails.course.id)%>"
                                            title="<%=Const.Tooltips.COURSE_EDIT%>" data-toggle="tooltip" data-placement="top"> Edit</a>
                                            
                                         <a class="btn btn-primary btn-xs btn-tm-actions course-add-eval-for-test"
                                            href="<%=data.getInstructorEvaluationLinkForCourse(courseDetails.course.id)%>"
                                            title="<%=Const.Tooltips.COURSE_ADD_EVALUATION%>" data-toggle="tooltip" data-placement="top"
                                            <% if (!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION)) {%>
                                                disabled="disabled"
                                            <% } %>
                                            > Add Session</a>
                                         
                                         <a class="btn btn-primary btn-xs btn-tm-actions course-archive-for-test"
                                            href="<%=data.getInstructorCourseArchiveLink(courseDetails.course.id, true, true)%>"
                                            title="<%=Const.Tooltips.COURSE_ARCHIVE%>" data-toggle="tooltip" data-placement="top"
                                            onclick="return toggleArchiveCourseConfirmation('<%=courseDetails.course.id%>')">Archive</a>
                                            
                                        <% int numberOfPendingCommentsForThisCourse =  data.numberOfPendingComments.get(courseDetails.course.id);%>
                                        <a class="btn btn-primary btn-xs btn-tm-actions course-notify-pending-comments-for-test
                                            <%=numberOfPendingCommentsForThisCourse==0?"hidden":""%>"
                                            href="<%=data.getInstructorClearPendingCommentsLink(courseDetails.course.id)%>"
                                            title="Send email notification to recipients of <%=numberOfPendingCommentsForThisCourse%> pending <%=numberOfPendingCommentsForThisCourse>1?"comments":"comment"%>" 
                                            data-toggle="tooltip" data-placement="top">
                                            <span class="badge"><%=numberOfPendingCommentsForThisCourse%></span>
                                            <span class="glyphicon glyphicon-comment"></span>
                                            <span class="glyphicon glyphicon-arrow-right"></span>
                                            <span class="glyphicon glyphicon-envelope"></span>
                                        </a>
                                            
                                         <a class="btn btn-primary btn-xs btn-tm-actions course-delete-for-test"
                                            href="<%=data.getInstructorCourseDeleteLink(courseDetails.course.id,true)%>"
                                            title="<%=Const.Tooltips.COURSE_DELETE%>" data-toggle="tooltip" data-placement="top"
                                            onclick="return toggleDeleteCourseConfirmation('<%=courseDetails.course.id%>')"
                                            <% if (!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE)) {%>
                                                disabled="disabled"
                                            <% } %>
                                            > Delete</a>
                                    </span>
                                </div>
                            </div>
                        </div>
                        <%
                            if (courseDetails.evaluations.size() > 0||
                                courseDetails.feedbackSessions.size() > 0) {
                        %>
                                <table class="table-responsive table table-striped table-bordered">
                                    <thead>
                                        <tr>
                                            <th id="button_sortname" onclick="toggleSort(this,1);"
                                                class="button-sort-none">
                                                Session Name<span class="icon-sort unsorted"></span></th>
                                            <th>Status</th>
                                            <th>
                                                <span title="<%=Const.Tooltips.EVALUATION_RESPONSE_RATE%>" data-toggle="tooltip" data-placement="top">Response Rate</span>
                                            </th>
                                            <th class="no-print">Action(s)</th>
                                        </tr>
                                    </thead>
                            <%
                                int displayEvaluationStatsCount = 0;
                                for (EvaluationAttributes edd : courseDetails.evaluations){
                                    sessionIdx++;
                            %>
                                    <tr id="session<%=sessionIdx%>">
                                        <td><%=PageData.sanitizeForHtml(edd.name)%></td>
                                        <td>
                                            <span title="<%=PageData.getInstructorHoverMessageForEval(edd)%>" data-toggle="tooltip" data-placement="top">
                                                <%=PageData.getInstructorStatusForEval(edd)%>
                                            </span>
                                        </td>
                                        <td class="session-response-for-test<% 
                                            if(edd.getStatus() == EvaluationAttributes.EvalStatus.OPEN || edd.getStatus() == EvaluationAttributes.EvalStatus.AWAITING) { 
                                                out.print(" recent");
                                            } else if (displayEvaluationStatsCount < data.MAX_CLOSED_SESSION_STATS && !TimeHelper.isOlderThanAYear(edd.endTime)) { 
                                                out.print(" recent"); 
                                                displayEvaluationStatsCount++; 
                                            }%>">
                                            <a oncontextmenu="return false;" href="<%=data.getEvaluationStatsLink(edd.courseId, edd.name)%>">Show</a>
                                        </td>
                                        <td class="no-print"><%=data.getInstructorEvaluationActions(edd, true, instructor)%>
                                        </td>
                                    </tr>
                            <%
                                }
                                int displayFeedbackStatsCount = 0;
                                Map<String, List<String>> courseIdSectionNamesMap = data.getCourseIdSectionNamesMap(courseDetails.feedbackSessions);
                                for(FeedbackSessionAttributes fdb: courseDetails.feedbackSessions) {
                                    sessionIdx++;
                            %>
                                    <tr id="session<%=sessionIdx%>">
                                        <td><%=PageData
                                                .sanitizeForHtml(fdb.feedbackSessionName)%></td>
                                        <td>
                                            <span title="<%=PageData.getInstructorHoverMessageForFeedbackSession(fdb)%>" data-toggle="tooltip" data-placement="top">
                                                <%=PageData.getInstructorStatusForFeedbackSession(fdb)%>
                                            </span>
                                        </td>
                                        <td class="session-response-for-test<% 
                                            if(fdb.isOpened() || fdb.isWaitingToOpen()) { 
                                                out.print(" recent");
                                            } else if (displayFeedbackStatsCount < data.MAX_CLOSED_SESSION_STATS && !TimeHelper.isOlderThanAYear(fdb.createdTime)) { 
                                                out.print(" recent"); 
                                                displayFeedbackStatsCount++; 
                                            }%>">
                                            <a oncontextmenu="return false;" href="<%=data.getFeedbackSessionStatsLink(fdb.courseId, fdb.feedbackSessionName)%>">Show</a>
                                        </td>
                                        <td class="no-print"><%=data.getInstructorFeedbackSessionActions(fdb, false, instructor, courseIdSectionNamesMap.get(fdb.courseId))%></td>
                                    </tr>
                            <%
                                }
                            %>
                                </table>
                        <%
                            }
                        %>
                    </div>
                    <br>
        <%
                    out.flush();
                }
            }
        %>
        </div>    
        <br>
        <br>
        <br>
        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </body>
    </html>
<%
    }
%>