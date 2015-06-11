<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.StringHelper"%>
<%@ page import="teammates.common.util.EmailLogEntry"%>
<%@ page import="teammates.ui.controller.AdminEmailLogPageData"%>
<%@ page import="teammates.common.util.TimeHelper"%>
<%
    AdminEmailLogPageData data = (AdminEmailLogPageData) request
            .getAttribute("data");
%>
<!DOCTYPE html>
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
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/adminEmailLog.js"></script>
    <script type="text/javascript"
        src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
</head>

<body>
    <div id="dhtmltooltip"></div>

    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />

    <div class="container" id="frameBodyWrapper">
                <div id="topOfPage"></div>
                <h1>Admin Email Log</h1>
                <br>

                <div class="well well-plain">
                    <form class="form-horizontal" method="post"
                        action="/admin/adminEmailLogPage"
                        id="activityLogFilter" role="form">

                        <div class="panel-heading" id="filterForm">

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-md-12">
                                        <div class="input-group">
                                            <span
                                                class="input-group-btn">
                                                <button
                                                    class="btn btn-default"
                                                    type="submit"
                                                    name="search_submit">Filter</button>
                                            </span> 
                                            
                                                    <input type="text"
                                                           class="form-control"
                                                           id="filterQuery"
                                                           name="filterQuery"
                                                           value="<%=data.filterQuery%>">
                                                
                                                    <input type="text" name="pageChange" value="true" style="display:none;">
                                                    
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <a href="#"
                                    class="btn btn-link center-block"
                                    onclick="toggleReference()"><span
                                    id="referenceText"> Show
                                        Reference</span><br> <span
                                    class="glyphicon glyphicon-chevron-down"
                                    id="detailButton"></span> </a>

                            </div>

                            <div id="filterReference">
                                <div class="form-group">

                                    <div class="col-md-12">
                                                                         
                                        <div class="alert alert-success">
                                            <p class="text-center">
                                                <span
                                                    class="glyphicon glyphicon-filter"></span>
                                                A query is formed by a
                                                list of filters. Each
                                                filter is in the format
                                                <strong>&nbsp;[filter
                                                    label]: [value1,
                                                    value2, value3....]</strong><br>
                                            </p>
                                        </div>


                                        <p class="text-center">
                                            <span
                                                class="glyphicon glyphicon-hand-right"></span>
                                            Combine filters with the <span
                                                class="label label-warning">
                                                AND</span> keyword or the <span
                                                class="label label-warning">|</span>
                                            separator.

                                        </p>
                                    </div>

                                </div>
                                <small>
                                    <div class="form-group">
                                        <div class="col-md-12">

                                            <div
                                                class="form-control-static">
                                                <strong>Sample
                                                    Queries:</strong> <br>
                                                <ul>
                                                    <li>E.g. receiver:
                                                        alice@gmail.com AND
                                                        subject:
                                                        welcome,TEAMMATES
                                                        AND after:
                                                        15/03/15</li>
                                                    <li>E.g. after:
                                                        13/3/15 AND before:
                                                        17/3/15 AND
                                                        Receiver:
                                                        teammates@test.com
                                                        AND info:click,join link</li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-12">

                                            <div
                                                class="form-control-static">

                                                <strong>
                                                    Possible Labels:</strong>&nbsp;after,
                                                    before, receiver, subject,
                                                    info, version<br>
                                                <ul>

                                                    <li>E.g. after: 13/03/15</li>
                                                    <li>E.g. before: 13/03/15</li>
                                                    <li>E.g. receiver: alice@gmail.com</li>
                                                    <li>E.g. subject: Welcome,teammates</li>
                                                    <li>E.g. info: you can click the link below </li>                                      
                                                    <li>E.g. info: link,cs1010 (Use "," to search multiple key strings)</li> 
                                                    <li>E.g. version: 4.15,4.16</li>
                                                    <li>E.g. version: 4-15,4.16 (both "." and "-" are acceptable)</li>
                                                    
                                                                                                                                             
                                                     
                                                </ul>
                                            </div>
                                        </div>
                                    </div>

                                    
                                </small>
                            </div>

                        </div>
                    </form>
                </div>
                
                <!-- this form is used to store parameters for ajaxloader only -->
                   <form id="ajaxLoaderDataForm">
                      <input type="hidden" name="offset" value="<%=data.offset%>">
                       <!-- This parameter determines whether the logs with requests contained in "excludedLogRequestURIs" 
                        in AdminActivityLogPageData should be shown. Use "?all=true" in URL to show all logs. This will keep showing all
                        logs despite any action or change in the page unless the the page is reloaded with "?all=false" 
                        or simply reloaded with this parameter omitted. -->
                        
                        <input type="hidden" id="filterQuery" name="filterQuery" value="<%=data.filterQuery%>">
                    </form> 
                
                 <%
                    if (data.queryMessage != null) {
                %>
                <div class="alert alert-danger" id="queryMessage">
                    <span class="glyphicon glyphicon-warning-sign"></span>
                    <%
                        out.println(" " + data.queryMessage);
                    %>
                </div>
                <%
                    }
                %>
                
                
                <br>
                <br>
                
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <strong>Email Log</strong>
                    </div>
                    <div class="table-responsive">
                        
                        <table class="table dataTable" id="emailLogsTable">
                            <thead>
                                <tr>
                                    <td><strong>Receiver</strong></td>
                                    <td><strong>Subject</strong></td>
                                    <td><strong>Date</strong></td>     
                                </tr>
                            </thead>
                            
                            <tbody>
                            
                                <%
                                  for(EmailLogEntry log : data.logs) {
                                    if(!data.shouldShowAll && log.getReceiver().endsWith(".tmt")){
                                       continue;   
                                    }
                                %>
                                    <tr class="log">
                                        <td><%=log.getReceiver()%></td>
                                        <td><%=log.getSubject()%></td>
                                        <td><%=log.getTimeForDisplay()%></td>
                                    </tr>
                                    <tr id="small">
                                        <td colspan="3">
                                            <ul class="list-group">
                                                <li class="list-group-item list-group-item-info">
                                                    <input type="text" value="<%=log.getContent()%>" class="form-control"
                                                           readonly="readonly">
                                                </li>
                                            </ul>    
                                        </td>
                                    </tr>
                                    
                                    <tr id="big" style="display:none;">
                                      <td colspan="3">
                                        <div class="well well-sm">
                                            <ul class="list-group">
                                                <li class="list-group-item list-group-item-success emailLog-text"><small>
                                                <%=StringHelper.recoverFromSanitizedText(log.getContent())%></small>
                                                </li>
                                            </ul>
                                        </div>
                                       </td>
                                    </tr>
                                <% 
                                  }
                                %>
                            </tbody>
                        </table>  
                    </div>
                </div>
            </div>
            
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE_WITHOUT_FOCUS%>" />
                    
            <a href="#" class="back-to-top-left"><span
                class="glyphicon glyphicon-arrow-up"></span>&nbsp;Top</a> <a
                href="#" class="back-to-top-right">Top&nbsp;<span
                class="glyphicon glyphicon-arrow-up"></span></a> <br> <br>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>