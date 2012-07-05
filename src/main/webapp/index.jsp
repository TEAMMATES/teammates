<%@ page import="com.google.appengine.api.utils.SystemProperty"%>
<%@ page import="teammates.common.Common" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<link rel="shortcut icon" href="/favicon.png" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<title>Teammates</title>
<link rel=stylesheet href="/stylesheets/main.css" type="text/css" />
<script language="JavaScript" src="js/index.js"></script>
<script type="text/javascript" src="js/blockUnsupportedBrowsers.js"></script>
</head>

<body>
	<div id="frameTop">
		<div id="frameTopWrapper">
			<div id="logo">
				<img alt="Teammates" height="47px" src="/images/teammateslogo.jpg"
					width="150px" />
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
										class="button" value="Student" />
							</form>
						</td>
					</tr>
					<tr>
						<td class="loginCell">
							<form action="<%= Common.PAGE_LOGIN %>">
								<input type="submit" name="coordinator" 
										class="button" value="Coordinator" />
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