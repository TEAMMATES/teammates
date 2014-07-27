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
<%@ page import="teammates.common.util.Sanitizer"%>

<%
    AdminSearchPageData data = (AdminSearchPageData) request
            .getAttribute("data");
%>

<html>
<head>
<link rel="shortcut icon" href="/favicon.png" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<title>TEAMMATES - Administrator</title>
<link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
<link href="/stylesheets/teammatesCommon.css" rel="stylesheet">
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
          <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
          <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
          <![endif]-->

<script type="text/javascript" src="/js/googleAnalytics.js"></script>
<script type="text/javascript" src="/js/jquery-minified.js"></script>
<script type="text/javascript"
    src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/js/adminSearch.js"></script>

</head>
<body>
    <div id="dhtmltooltip"></div>
    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />

    <div class="container theme-showcase" role="main">

        <div id="frameBody">
            <div id="frameBodyWrapper">
                <div id="topOfPage"></div>
                <div id="headerOperation" class="page-header">
                    <h1>
                        Admin Search <small><span
                            class="label label-primary"> Tips:
                                Surround key word to search a whole
                                string or string contains punctuation
                                like "-" "." </span></small>
                    </h1>


                </div>


                <div class="well well-plain">
                    <form class="form-horizontal" method="get" action=""
                        id="activityLogFilter" role="form">

                        <div class="panel-heading" id="filterForm">

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-md-12">
                                        <div class="input-group">
                                            <span
                                                class="input-group-btn">
                                            </span> <input type="text"
                                                class="form-control"
                                                id="filterQuery"
                                                name="<%=Const.ParamsNames.ADMIN_SEARCH_KEY%>"
                                                value="<%=data.searchKey%>"><span
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
                    List<StudentAttributes> studentResultList = data.studentResultBundle.studentList;

                    if (!studentResultList.isEmpty()) {
                %>

                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong>Students Found </strong>

                    </div>

                    <div class="table-responsive">
                        <table class="table table-condensed dataTable"
                            id="search_table">

                            <thead>
                                <tr>
                                    <th>Course [Section]</th>
                                    <th>Team</th>
                                    <th>Name</th>
                                    <th>Google ID[Email]</th>
                                    <th>Comments</th>


                                </tr>

                            </thead>
                            <tbody>

                                <%
                                    for (StudentAttributes student : studentResultList) {
                                        
                                        String id = Sanitizer.sanitizeForSearch(student.getIdentificationString());
                                        id = id.replace(" ", "").replace("@", "");
                                %>

                                <tr id="<%=id%>" class="studentRow">
                                    <td><%=student.course%>&nbsp;[<%=student.section%>]
                                    </td>
                                    <td><%=student.team%></td>
                                    <td><%=student.name%></td>
                                    <td><%=student.googleId%></td>
                                    <td><%=student.comments%></td>
                               
                                 </tr>
                               
                                    <tr class="info fslink fslink<%=id%>">
                                        <td>
                                        
                                        <strong>Course Join Link</strong>
                                        
                                        </td>
                                        
                                        <td colspan="4">
                                        
                                        <input value="<%=student.getRegistrationUrl()%>" readonly="readonly" class="form-control"/ >
                                        
                                        </td>
                                    </tr> 
                                   <% 
                                    
                                     if(data.studentfeedbackSessionLinksMap.get(student.getIdentificationString()) == null){
                                            continue;
                                     }
                                   
                                   %>
                                      
                                      
                                   <%
                                   
                                     for (String link : data.studentfeedbackSessionLinksMap.get(student.getIdentificationString())) {
                                                                        
                                   %> 
                                        
                                        <tr class="warning fslink fslink<%=id%>">
                                        
                                        <td>
                                        
                                        <strong> <%=data.feedbackSeesionLinkToNameMap.get(link)%> </strong>
                                        
                                        </td>
                                        
                                        <td colspan="4">
                                        
                                        <input value=<%=link%> readonly="readonly" class="form-control"/ >
                                        
                                        </td>
                                        </tr> 
                                                                   
                                   
                                <%
                                          }
                                   }

                            }
                                %>
                            </tbody>
                        </table>
                    </div>
                </div>




                <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />

            </div>





        </div>

    </div>


    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>