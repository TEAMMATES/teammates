<%@ page import="teammates.common.Common" %>
<%@ page import="teammates.ui.controller.AdminAccountManagementHelper"%>
<%@ page import="teammates.common.datatransfer.InstructorData" %>
<%@ page import="teammates.common.exception.EntityDoesNotExistException" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<% AdminAccountManagementHelper helper = (AdminAccountManagementHelper)request.getAttribute("helper"); %>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Teammates - Administrator Account Management</title>
    <link rel="stylesheet" href="/stylesheets/adminAccountManagement.css" type="text/css">
    <link rel="stylesheet" href="/stylesheets/common.css" type="text/css">

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/tooltip.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/administrator.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
    <div id="dhtmltooltip"></div>
    <div id="frameTop">
    <jsp:include page="<%= Common.JSP_ADMIN_HEADER %>" />
    </div>
    <div id="frameBody">
        <div id="frameBodyWrapper">
            <div id="topOfPage"></div>
            <div id="headerOperation">
            <h1>Instructor Account Management</h1>
            </div>
            <table class="dataTable">
            <tr>
                <th class="bold" width="40%">Account Info</th>
                <th class="bold">Instructor for</th>
                <th class="bold" width="30%">Options</th>
            </tr>
            <%
	            for (Map.Entry<Integer, List<String>> entry : helper.accountList.entrySet()) {
	                Integer key = entry.getKey();
	                List<String> coursesList = entry.getValue();
	                InstructorData instructor = helper.instructorList.get(key);
            %>
                <tr>
                     <td><%="<span class=\"bold\">Google ID: </span>" + instructor.googleId + " <br><span class=\"bold\">Name: </span>" + instructor.name + "<br><span class=\"bold\">Email: </span>" + instructor.email %></td>
                     <td>
                     <%
                         for(String course: coursesList){
                             out.print(" - " + course + "<br>");
                         }
                     %>
                     </td>
                     <td>
                        None
                     </td>
                </tr>
            <%
               }
            %>
            </table>
            <br>
            <jsp:include page="<%= Common.JSP_STATUS_MESSAGE %>" />
            <br>
            <br>
        </div>
    </div>

    <div id="frameBottom">
        <jsp:include page="<%= Common.JSP_FOOTER %>" />
    </div>
</body>
</html>