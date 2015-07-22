<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/search" prefix="adminSearch" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="${data.jQueryFilePath}"></script>
    <script type="text/javascript" src="${data.jQueryUiFilePath}"></script>
    <script type="text/javascript" src="/js/adminSearch.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
</c:set>

<ta:adminPage bodyTitle="Admin Search" pageTitle="TEAMMATES - Administrator" jsIncludes="${jsIncludes}">
    <adminSearch:searchPageInput searchKey="${data.searchKey}"/>
    
    <c:if test="${not empty data.instructorResultList}">
        <adminSearch:instructorResultsTable instructorResultsTable="${data.instructorTable}"/>
    </c:if>
    
    <c:if test="${not empty data.studentResultList}">
        <adminSearch:studentResultsTable studentResultsTable="${data.studentTable}"/>
    </c:if>
    
    <t:statusMessage/>
</ta:adminPage>

<!--  -->
<%-- 
<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="com.google.appengine.api.search.Document"%>
<%@ page import="com.google.appengine.api.search.Field"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="teammates.ui.controller.AdminSearchPageData"%>
<%@ page import="teammates.common.datatransfer.StudentAttributes"%>
<%@ page import="teammates.common.datatransfer.InstructorAttributes"%>
<%@ page import="teammates.common.util.Sanitizer"%>
<%@ page import="teammates.common.util.StringHelper"%>

<%
    AdminSearchPageData data = (AdminSearchPageData) request
            .getAttribute("data");
%>

<html>
<head>
    <title>TEAMMATES - Administrator</title>

    <link rel="shortcut icon" href="/favicon.png" />

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link type="text/css" href="/bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
    <link type="text/css" href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet"/>
    <link type="text/css" href="/stylesheets/teammatesCommon.css" rel="stylesheet"/>

    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="<%= data.getjQueryFilePath() %>"></script>
    <script type="text/javascript" src="<%= data.getjQueryUiFilePath() %>"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>

    <jsp:include page="../enableJS.jsp"></jsp:include>

    <script type="text/javascript" src="/js/adminSearch.js"></script>
</head>
<body>
    <div id="dhtmltooltip"></div>
    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />


    <div class="container" id="mainContent">
                <div id="topOfPage"></div>
                <h1>Admin Search</h1>
                <br>


                <div class="well well-plain">
                    <form class="form-horizontal" method="get" action=""
                        id="activityLogFilter" role="form">

                        <div class="panel-heading" id="filterForm">

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-md-12">
                                        <span class="help-block">Tips:
                                            Surround key word to search
                                            a whole string or string
                                            contains punctuation like
                                            "-" "."</span>
                                        <div class="input-group">

                                            <input type="text"
                                                class="form-control"
                                                id="filterQuery"
                                                name="<%=Const.ParamsNames.ADMIN_SEARCH_KEY%>"
                                                value="<%=data.searchKey%>">

                                            <span
                                                class="input-group-btn">
                                                <button
                                                    class="btn btn-default"
                                                    type="submit"
                                                    name="<%=Const.ParamsNames.ADMIN_SEARCH_BUTTON_HIT%>"
                                                    id="searchButton"
                                                    value="true">Search</button>
                                            </span>


                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>

                <%
                    List<InstructorAttributes> instructorResultList = data.instructorResultBundle.instructorList;
                    if (!instructorResultList.isEmpty()) {
                %>

                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong>Instructors Found </strong> <span
                            class="pull-right"><button
                                class="btn btn-primary btn-xs"
                                type="button"
                                onclick="adminSearchDiscloseAllInstructors()">Disclose
                                All</button>
                            <button class="btn btn-primary btn-xs"
                                type="button"
                                onclick="adminSearchCollapseAllInstructors()">Collapse
                                All</button></span>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-striped dataTable"
                            id="search_table_instructor">

                            <thead>
                                <tr>
                                    <th>Course</th>
                                    <th>Name</th>
                                    <th>Google ID</th>
                                    <th>Institute</th>
                                    <th>Options</th>
                                </tr>

                            </thead>
                            
                            <tbody>
                                <%
                                  for (InstructorAttributes instructor: instructorResultList){
                                      
                                      String id = Sanitizer.sanitizeForSearch(instructor.getIdentificationString());
                                      id = StringHelper.removeExtraSpace(id);
                                      id = id.replace(" ", "").replace("@", "");
                                      id = "instructor_" + id;
                                %>
                                
                                  <tr id="<%=id%>"  class="instructorRow">
                                  
                                    <%String courseName = data.courseIdToCourseNameMap.get(instructor.courseId);
                                       if(courseName != null){
                                    %>
                                        <td data-toggle="tooltip" data-placement="top" title="<%=courseName%>">
                                        <%=instructor.courseId%>
                                        </td>                                 
                                    <%     
                                       } else {
                                    %>
                                        <td>
                                        <%=instructor.courseId%>
                                        </td>
                                    <%   
                                       }
                                    %>                                    
                                    <td><%=instructor.name%></td>
                                    <td> 
                                    <a
                                        href="<%=data.instructorHomaPageLinkMap.get(instructor.googleId)%>"
                                        target="blank"
                                        class="homePageLink"><%=instructor.googleId == null? "" : instructor.googleId%></a>                              
                                    </td>
                                    <td><%=data.instructorInstituteMap.get(instructor.getIdentificationString()) == null? 
                                           "" : data.instructorInstituteMap.get(instructor.getIdentificationString())%></td>

                                    <td>
                                    
                                        <%String availableIdString = "";
                                                  if(instructor.googleId != null && !instructor.googleId.trim().isEmpty()){
                                                      availableIdString = instructor.googleId;
                                                  } else if(instructor.name != null && !instructor.name.trim().isEmpty()){
                                                      availableIdString = instructor.name;
                                                  } else if(instructor.email != null && !instructor.email.trim().isEmpty()){
                                                      availableIdString = instructor.email;
                                                  }
                                        %>
                                        
                                        <% 
                                            if(!availableIdString.isEmpty()){
                                        %>

                                            <form method="post"
                                                target="blank"
                                                action="<%=Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE%>">
                                                <button type="submit"
                                                    id="<%=availableIdString + "_recentActions"%>"
                                                    class="btn btn-link btn-xs recentActionButton">
                                                    <span
                                                        class="glyphicon glyphicon-zoom-in"></span>
                                                    View Recent Actions
                                                </button>
                                                
                                                
                                                
                                                <input type="hidden"
                                                    name="filterQuery"
                                                    
                                                    value="person:<%=availableIdString%>">
                                            </form>
                                        
                                        <% 
                                            }
                                        %>
                                    </td>

                                </tr>
                                  
                                  <tr
                                    class="has-danger list-group fslink fslink_instructor fslink<%=id%>"
                                    style="display: none;">
                                    <td colspan="5">
                                        <ul class="list-group">
                                            <li
                                                class="list-group-item list-group-item-success has-success">
                                                <strong>Email</strong> <input
                                                value="<%=instructor.email%>"
                                                readonly="readonly"
                                                class="form-control" />
                                            </li>
                                            
                                            <% if(data.instructorCourseJoinLinkMap.get(instructor.getIdentificationString()) != null){
                                            
                                            %>
                                            
                                            <li
                                                class="list-group-item list-group-item-info">
                                                <strong>Course Join Link</strong> <input
                                                value="<%=data.instructorCourseJoinLinkMap.get(instructor.getIdentificationString())%>"
                                                readonly="readonly"
                                                class="form-control" />
                                            </li>
                                            
                                            <% 
                                               }
                                            %>
                                        
                                        </ul>
                                    </td>
                                  </tr>
                                  
                                  
                                <% 
                                  }
                               
                                %>

                            </tbody>
                        </table>
                    </div>




                </div>



                <%
                    }
                %>



                <%
                    List<StudentAttributes> studentResultList = data.studentResultBundle.studentList;

                    if (!studentResultList.isEmpty()) {
                %>

                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong>Students Found </strong> <span
                            class="pull-right"><button
                                class="btn btn-primary btn-xs"
                                type="button"
                                onclick="adminSearchDiscloseAllStudents()">Disclose
                                All</button>
                            <button class="btn btn-primary btn-xs"
                                type="button"
                                onclick="adminSearchCollapseAllStudents()">Collapse
                                All</button></span>
                    </div>

                    <div class="table-responsive">
                        <table class="table table-striped dataTable"
                            id="search_table">

                            <thead>
                                <tr>
                                    <th>Institute </th>
                                    <th>Course[Section](Team)</th>
                                    <th>Name</th>
                                    <th>Google ID[Details]</th>
                                    <th>Comments</th>
                                    <th>Options</th>

                                </tr>

                            </thead>
                            <tbody>

                                <%
                                    for (StudentAttributes student : studentResultList) {

                                            String id = Sanitizer.sanitizeForSearch(student
                                                    .getIdentificationString());
                                            id = id.replace(" ", "").replace("@", "");
                                            id = "student_" + id;
                                %>

                                <tr id="<%=id%>" class="studentRow">


                                    <td><%=data.studentInstituteMap.get(student.getIdentificationString()) == null?
                                           "" : data.studentInstituteMap.get(student.getIdentificationString())%>
                                    </td>
                                    
                                    <%String courseName = data.courseIdToCourseNameMap.get(student.course);
                                       if(courseName != null){
                                    %>
                                        <td data-toggle="tooltip" data-placement="top" title="<%=courseName%>">
                                        <%=student.course%><br><%=student.section%><br><%=student.team%>
                                        </td>                                 
                                    <%     
                                       } else {
                                    %>
                                        <td>
                                        <%=student.course%><br><%=student.section%><br><%=student.team%>
                                        </td>
                                    <%   
                                       }
                                    %>                   
                                    
                                    <%if(data.studentRecordsPageLinkMap.get(student.getIdentificationString()) != null){%>
                                    <td><a class="detailsPageLink"
                                        href=<%=data.studentRecordsPageLinkMap.get(student.getIdentificationString())%>
                                        target="blank"> <%=student.name%></a></td>
                                    <%} else {%>
                                    <td><%=student.name%></td>                                  
                                    <%} %>
                                    <td><a
                                        href="<%=data.studentIdToHomePageLinkMap.get(student.googleId)%>"
                                        target="blank"
                                        class="homePageLink"><%=data.studentIdToHomePageLinkMap.get(student.googleId) == null? 
                                                                "" : student.googleId%></a>
                                    </td>
                                    <td><%=student.comments%></td>

                                    <td>
                                    
                                        <%String availableIdString = "";
                                                  if(student.googleId != null && !student.googleId.trim().isEmpty()){
                                                      availableIdString = student.googleId;
                                                  } else if(student.name != null && !student.name.trim().isEmpty()){
                                                      availableIdString = student.name;
                                                  } else if(student.email != null && !student.email.trim().isEmpty()){
                                                      availableIdString = student.email;
                                                  }
                                        %>
                                        
                                        <% 
                                            if(!availableIdString.isEmpty()){
                                        %>

                                            <form method="post"
                                                target="blank"
                                                action="<%=Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE%>">
                                                <button type="submit"
                                                    id="<%=availableIdString + "_recentActions"%>"
                                                    class="btn btn-link btn-xs recentActionButton">
                                                    <span
                                                        class="glyphicon glyphicon-zoom-in"></span>
                                                    View Recent Actions
                                                </button>                                 
                                                
                                                <input type="hidden"
                                                    name="filterQuery"
                                                    
                                                    value="person:<%=availableIdString%>">
                                            </form>
                                        
                                        <% 
                                            }
                                        %>
                                        
                                        <% 
                                        if (student.googleId != null && !student.googleId.isEmpty()){
                                         
                                        %>
                                            <button type="button"
                                                id="<%=student.googleId + "_resetGoogleId"%>"
                                                onclick="submitResetGoogleIdAjaxRequest('<%=student.course%>' , '<%=student.email%>', '<%=student.googleId%>', this);"
                                                class="btn btn-link btn-xs resetGoogleIdButton">
                                                <span
                                                class="glyphicon glyphicon-refresh"></span>
                                                Reset Google Id
                                            </button> 
                                        <%
                                        }
                                        %>
                                        
                                        
                                    </td>
                                </tr>

                                <tr
                                    class="has-danger list-group fslink fslink_student fslink<%=id%>"
                                    style="display: none;">
                                    <td colspan="5">
                                        <ul class="list-group">

                                            <%
                                                if (student.email != null
                                                                && !student.email.trim().isEmpty()) {
                                            %>
                                            <li
                                                class="list-group-item list-group-item-success has-success">
                                                <strong>Email</strong> <input
                                                value="<%=student.email%>"
                                                readonly="readonly"
                                                class="form-control" />
                                            </li>
                                            <%
                                                }
                                            %>

                                            <li
                                                class="list-group-item list-group-item-info">
                                                <strong>Course
                                                    Join Link</strong> <input
                                                value="<%=student.getRegistrationUrl()%>"
                                                readonly="readonly"
                                                class="form-control" />
                                            </li>

                                            <%
                                                if (data.studentOpenFeedbackSessionLinksMap.get(student
                                                                .getIdentificationString()) != null) {
                                            %>



                                            <%
                                                for (String link : data.studentOpenFeedbackSessionLinksMap
                                                                    .get(student.getIdentificationString())) {
                                            %>



                                            <li
                                                class="list-group-item list-group-item-warning">
                                                <strong> <%=data.feedbackSeesionLinkToNameMap
                                    .get(link)%>
                                            </strong> <input value=<%=link%>
                                                readonly="readonly"
                                                class="form-control"/ >
                                            </li>



                                            <%
                                                }
                                                        }
                                            %>


                                            <%
                                                if (data.studentUnOpenedFeedbackSessionLinksMap.get(student
                                                                .getIdentificationString()) != null) {
                                            %>



                                            <%
                                                for (String link : data.studentUnOpenedFeedbackSessionLinksMap
                                                                    .get(student.getIdentificationString())) {
                                            %>



                                            <li
                                                class="list-group-item list-group-item-danger">
                                                <strong> <%=data.feedbackSeesionLinkToNameMap
                                    .get(link)%>
                                            </strong> <input value=<%=link%>
                                                readonly="readonly"
                                                class="form-control"/ >
                                            </li>



                                            <%
                                                }
                                                        }
                                            %>
                                            
                                            <%
                                                if (data.studentPublishedFeedbackSessionLinksMap.get(student
                                                                .getIdentificationString()) != null) {
                                            %>



                                            <%
                                                for (String link : data.studentPublishedFeedbackSessionLinksMap
                                                                    .get(student.getIdentificationString())) {
                                            %>



                                            <li
                                                class="list-group-item list-group-item-success">
                                                <strong> <%=data.feedbackSeesionLinkToNameMap.get(link)%>
                                            </strong> <input value=<%=link%>
                                                readonly="readonly"
                                                class="form-control"/>
                                            </li>



                                            <%
                                                }
                                                        }
                                            %>

                                        </ul>
                                    </td>

                                </tr>
                                <%
                                    }

                                    }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>


                <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />

            </div>



    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html> --%>