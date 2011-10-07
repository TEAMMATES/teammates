<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="java.util.*"%>
<%@ page import="teammates.Accounts"%>


<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Automated Test Driver</title>
	<link rel=stylesheet href="/stylesheets/main.css" type="text/css">
	<script language="JavaScript" src="js/jquery-1.6.2.min.js"></script>
	<script language="JavaScript" src="js/atd.js"></script>
</head>

<body>
<%
	// Check if user is allowed to view this page
	Accounts accounts = Accounts.inst();

	if (!accounts.isAdministrator()) 
	{
%>
<p>You are not authorised to view this page.<br></br><br></br> <a href="javascript:logout()">Logout and return to main page.</a></p>
<%
	} 
	else 
	{
%>
<font size = 6 face="Arial Black">Automated Testing</font>
<br></br>
<input type=submit value="Start" onclick="startATD(true);" name="button_startatd" id="button_startatd"> 
<input type=submit value="Start (exclude scalability)" onclick="startATD(false);" name="button_startatd" id="button_startatd"> 
<input type=submit value="Clear" onclick="clearATD();" name="button_startatd" id="button_startatd">
<br></br><br></br>
<div id="atd" style="display:none">
<font face="Arial">
Add/Delete Courses.. 
<br></br>Constraint #1: A coordinator cannot have two courses with the same course ID.
<div id="addDeleteCourses"></div>
<br></br>
Enrol Students/Delete Course.. 
<br></br>Constraint #1: A coordinator cannot enrol students with the same e-mail into a course.
<div id="enrolStudents"></div>
<br></br>
Edit Students/Delete Students/Delete Course.. 
<div id="editDeleteStudents"></div>
<br></br>
Add/Delete Evaluations.. 
<br></br>Constraint #1: Students can be in two different teams for two different evaluations
<div id="addDeleteEvaluations"></div>
<br></br>
Scalability Test.. 
<div id="scalability"></div>
<br></br>
Submit Evaluations.. 
<div id="submitEvaluations"></div>
</font></div>
<br></br>
<%
	}
%>
</body>
</html>
