<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="com.google.appengine.api.search.Document" %>
<%@ page import="com.google.appengine.api.search.Field" %>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="teammates.ui.controller.AdminSearchPageData"%>

<%
	AdminSearchPageData data = (AdminSearchPageData)request.getAttribute("data");
%>
<%
	String outcome = (String) request.getAttribute("outcome");
  if (outcome == null || outcome.isEmpty()) {
    outcome = "&nbsp;";
  }
  String query = (String) request.getParameter("query");
  if (query == null) {
    query = "";
  }
  String limit = (String) request.getParameter("limit");
  if (limit == null || limit.isEmpty()) {
    limit = "20";
  }
%>
<html>
<head>
<link rel="shortcut icon" href="/favicon.png" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<title>TEAMMATES - Administrator</title>
<link rel=stylesheet href="/stylesheets/common.css" type="text/css" />
<link rel=stylesheet href="/stylesheets/adminSearch.css" type="text/css" />

<script type="text/javascript" src="/js/googleAnalytics.js"></script>
<script language="JavaScript" src="/js/jquery-minified.js"></script>
<script language="JavaScript" src="/js/tooltip.js"></script>
</head>
  <body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
	<jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />
	</div>
<div id="frameBody">
<div id="frameBodyWrapper">
	<div id="topOfPage"></div>
	<div id="headerOperation">

    <form name="search" action="" method="get">
    <select name="limit">
    <option <%="5".equals(limit)? "selected" : ""%>>5</option>
    <option <%="10".equals(limit)? "selected" : ""%>>10</option>
    <option <%="15".equals(limit)? "selected" : ""%>>15</option>
    <option <%="20".equals(limit)? "selected" : ""%>>20</option>
    <option <%="50".equals(limit)? "selected" : ""%>>50</option>
    </select>
      <input placeholder="Search" style="width:500px;"
        type="search" name="query" id="query" value='<%=query%>'/>
      
      <input name="search" type="submit" name = "search" value="Search" style=""/>
      <input name = "build_doc" type="submit" value="Rebuild Document" style=""/>

    </form>
    <br>
    <hr/>
    <br>
    <%
    	List<Document> found = data.results;
                  if (found != null) {
    %>
    <form name="delete" action="" method="get">
      <!-- repeated so that we can execute a search after deletion -->
      <input type="hidden" name="query" value="<%=query%>"/>
      <table class="dataTable">
        <tr>
        <!--
          <th>
            <input type="checkbox" name="x" onclick="toggleSelection(this);"/>
          </th>
         --> 
          <th>Name</th>
          <th>ID</th>
          <th>Email</th>
          <th>Link</th>
        </tr>
    <%
    	if (found.isEmpty()) {
    %>
        <tr>
          <td colspan='4'><i>No matching documents found</i></td>
        </tr>
    <%
    	} else {
                      for (Document doc : found) {
    %>
        <tr>
         <!--
          <td>
            <input type="checkbox" name="docid" value="<%=doc.getId()%>"/>
          </td>
          -->
          <td>
            <%=doc.getOnlyField("name").getText()%>
          </td>
          <td>
          <%=doc.getOnlyField("id").getText()%>
          </td>
          <td>
            <%=doc.getOnlyField("email").getHTML()%>
          </td>
          <td>
        	<%=doc.getOnlyField("link").getHTML()%>
        </td>
        </tr>
    <%
    	}
                    }
    %>
      </table>
    </form>
    <%
    	}
    %>
    </div>
	<jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
</div>
</div>
    <div id="frameBottom">
	<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</div>
</body>
</html>