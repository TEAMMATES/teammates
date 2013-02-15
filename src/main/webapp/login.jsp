<%@ page import="com.google.appengine.api.utils.SystemProperty"%>
<%@ page import="teammates.common.Common" %>
<!DOCTYPE html>

<html>
<head>
<link rel="shortcut icon" href="/favicon.png">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Teammates</title>
<link rel="stylesheet" href="/stylesheets/common.css" type="text/css">
<script type="text/javascript" src="js/index.js"></script>
<script type="text/javascript" src="js/blockUnsupportedBrowsers.js"></script>
<jsp:include page="enableJS.jsp"></jsp:include>
</head>

<body>
	<div id="frameTop">
		<div id="frameTopWrapper">
			<div id="logo">
				<a href="/index.html">
				<img alt="Teammates" src="/images/teammateslogo.jpg"
					height="47px" width="150px">
				</a>
			</div>
		</div>
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="loginTableHolder">
				<table id="login">
					<tr>
						<td class="loginCell">
							<form action="<%= Common.PAGE_LOGIN %>">
								<input type="submit" name="student" 
										class="button" id="btnStudentLogin" value="Student">
							</form>
						</td>
					</tr>
					<tr>
						<td class="loginCell">
							<form action="<%= Common.PAGE_LOGIN %>">
								<input type="submit" name="instructor" 
										class="button" id="btnInstructorLogin" value="Instructor">
							</form>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="/jsp/footer.jsp" />
	</div>
</body>
</html>