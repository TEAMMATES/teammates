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
			<div style="margin: 30px auto; border: 1px solid #333; padding: 10px; text-align: left; width: 550px; background: #FFFFCC; height: 100px">
				<img src="/images/embarrassed.png"
					style="float: left; margin: 0 10px 10px 0; height: 90px;">
				<h2>
					TEAMMATES is currently unavailable due to routine maintenance activities.
					It will be back within ONE HOUR. We apologize for the inconvenience.<br>
				</h2>
			</div>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>