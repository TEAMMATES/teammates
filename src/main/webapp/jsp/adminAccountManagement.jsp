<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.InstructorAttributes"%>
<%@ page import="teammates.common.datatransfer.AccountAttributes"%>
<%@ page import="teammates.common.exception.EntityDoesNotExistException"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="teammates.ui.controller.AdminAccountManagementPageData"%>

<%
	AdminAccountManagementPageData data = (AdminAccountManagementPageData) request.getAttribute("data");
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>TEAMMATES - Administrator Account Management</title>
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
    <script type="text/javascript" src="/js/administrator.js"></script>
    <script type="text/javascript"
        src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/adminAccountManagement.js"></script>
    
    <jsp:include page="../enableJS.jsp"></jsp:include>

</head>

<body>

    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />

    <div class="container theme-showcase" id="frameBodyWrapper"
        role="main">
        <div id="topOfPage"></div>
        <div id="headerOperation" class="page-header">
            <h1>Instructor Account Management<small id="instructorCount">Total Instructors: <%=data.instructorAccountsTable.size()%></small>
            </h1>
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        </div>
        <div id="pagination_top">
            <ul class="pagination">
                <li class="previous"><a href="#"> <span>&laquo;</span>
                </a></li>
                <li><a class="pageNumber" href="#">1</a></li>
                <li><a class="pageNumber" href="#">2</a></li>
                <li><a class="pageNumber" href="#">3</a></li>
                <li><a class="pageNumber" href="#">4</a></li>
                <li><a class="pageNumber" href="#">5</a></li>
                <li class="next"><a href="#"><span>&raquo;</span>
                </a></li>
            </ul>
        </div>

        <div class="panel panel-primary">
            <div class="panel-heading">
                <strong>Instructor List</strong>
                <strong class="pull-right"><span id="currentPageEntryCount">1</span>&nbsp;/&nbsp;<span id="totalEntryCount">10</span></strong>
            </div>
            <div class="table-responsive">
                <table class="table table-striped dataTable">
                    <thead>
                        <tr>
                            <th width="10%">Account Info</th>
                            <th width="5%">Instructor for</th>
                            <th width="20%" onclick="toggleSort(this,3); reLabelOrderedAccountEntries();"
                                class="button-sort-ascending">
                                Institute <span
                                class="icon-sort unsorted"
                                id="button_sort_institute"></span>
                            </th>
                            <th width="30%"
                                onclick="toggleSort(this,4); reLabelOrderedAccountEntries();"
                                class="button-sort-ascending">Create
                                At <span class="icon-sort unsorted"
                                id="button_sort_createat"></span>
                            </th>
                            <th width="5%">Options</th>
                        </tr>
                    </thead>

                    <tbody>
                        <%
                        	for (Map.Entry<String, AccountAttributes> entry : data.instructorAccountsTable
                        			.entrySet()) {
                        		String key = entry.getKey();
                        		AccountAttributes acc = entry.getValue();
                                
                                if(data.isTestingAccount(acc) && data.isToShowAll == false){
                                     continue;   
                                }
                                
                        		ArrayList<InstructorAttributes> coursesList = data.instructorCoursesTable
                        				.get(key);
                        %>


                        <tr class="accountEntry">
                            <td><%="<span class=\"bold\">Google ID: </span><a href=\""
						+ data.getInstructorHomePageViewLink(acc.googleId)
						+ "\" target=\"blank\">" + acc.googleId
						+ "</a><br><span class=\"bold\">Name: </span>"
						+ acc.name + "<br><span class=\"bold\">Email: </span>"
						+ acc.email%></td>

                            <td id="courses_<%=acc.googleId%>">
                                <%
                                	if (coursesList != null) {
                                			out.print("Total Courses: " + coursesList.size() + "<br>");
                                			for (InstructorAttributes i : coursesList) {
                                				out.print(" --- " + i.courseId + "<br>");
                                			}
                                		} else {
                                			out.print("No Courses found");
                                		}
                                %>
                            </td>

                            <td id="<%=acc.googleId + "_institude"%>"><%=acc.institute%>
                            </td>

                            <td id="<%=acc.googleId + "_createAt"%>"><%=AdminAccountManagementPageData
						.displayDateTime(acc.createdAt)%></td>

                            <td><a
                                id="<%=acc.googleId + "_details"%>"
                                href="<%=data.getAdminViewAccountDetailsLink(acc.googleId)%>"
                                target="blank"
                                class="btn  btn-link btn-xs"> <span
                                    class="glyphicon glyphicon-info-sign"></span>
                                    View Details
                            </a>&nbsp;&nbsp;&nbsp;&nbsp; <a
                                id="<%=acc.googleId + "_delete"%>"
                                href="<%=data.getAdminDeleteInstructorStatusLink(acc.googleId)%>"
                                class="btn  btn-link btn-xs"
                                role="button"> <span
                                    class="glyphicon glyphicon-remove"></span>
                                    Delete Instructor Status
                            </a> <a
                                id="<%=acc.googleId + "_deleteAccount"%>"
                                href="<%=data.getAdminDeleteAccountLink(acc.googleId)%>"
                                onclick="return toggleDeleteAccountConfirmation('<%=acc.googleId%>')"
                                class="btn btn-link btn-xs "> <span
                                    class="glyphicon glyphicon-trash"></span>
                                    Delete Entire Account
                            </a>

                                <form method="post" target="blank"
                                    action="<%=Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE%>">
                                    <button type="submit" 
                                        id="<%=acc.googleId + "_recentActions"%>"
                                        class="btn btn-link btn-xs">
                                        <span
                                            class="glyphicon glyphicon-zoom-in"></span>
                                        View Recent Actions
                                    </button>
                                    <input type="hidden"
                                        name="filterQuery"
                                        value="person:<%=acc.googleId%>">
                                </form>
                        </tr>
                        <%
                        	}
                        %>
                    </tbody>

                </table>
            </div>
        </div>

        <div id="pagination_bottom">
            <ul class="pagination">
                <li class="previous"><a href="#"> <span>&laquo;</span>
                </a></li>
                <li><a class="pageNumber" href="#">1</a></li>
                <li><a class="pageNumber" href="#">2</a></li>
                <li><a class="pageNumber" href="#">3</a></li>
                <li><a class="pageNumber" href="#">4</a></li>
                <li><a class="pageNumber" href="#">5</a></li>
                <li class="next"><a href="#"><span>&raquo;</span>
                </a></li>
            </ul>
        </div>


        <a href="#" class="back-to-top-left"><span
            class="glyphicon glyphicon-arrow-up"></span>&nbsp;Top</a> 
            
            <a
            href="#" class="back-to-top-right">Top&nbsp;<span
            class="glyphicon glyphicon-arrow-up"></span></a>

    </div>



    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />

</body>
</html>