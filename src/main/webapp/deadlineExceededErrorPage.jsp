<%@ page import="teammates.common.util.Const"%>
<!DOCTYPE html>

<html>
<head>
<link rel="shortcut icon" href="/favicon.png">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>TEAMMATES</title>
<link rel="stylesheet" href="/stylesheets/common.css" type="text/css">
</head>
<body>
	<div id="frameTop">
		<div id="frameTopWrapper">
			<div id="logo">
				<a href="/index.html">
				<img alt="TEAMMATES" src="/images/teammateslogo.jpg"
					height="47px" width="150px">
				</a>
			</div>
		</div>
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div style="background: rgb(255, 255, 204); margin: 30px auto; padding: 10px; border: 1px solid rgb(51, 51, 51); width: 550px; height: 100px; text-align: left;">
				<img src="/images/error.png"
					style="margin: 0px 10px 10px 0px; height: 90px; float: left;">
				<p style="text-align: left;">
					Server failed to respond within a reasonable time. <br>
					This may be due to an unusually high load at this time.<br> 
					Please try again in a few minutes. If the problem persists,<br>
					please inform TEAMMATES <a href="/contact.html">support team</a>. 
				</p>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>