<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%
	PageData data = (PageData)request.getAttribute("data");
%>
		<div id="frameTopWrapper">
			<div id="logo">
				<a href="/index.html">
				<img alt="TEAMMATES" height="47px"
					src="/images/teammateslogo.jpg"
					width="150px">
				</a>
			</div>			
			<div id="contentLinks">
				<ul id="navbar">
					<li><a class='nav home' data-link="studentHome" href="<%=data.getStudentHomeLink()%>">Home</a></li>
					<li><a class='nav help' href="/studentHelp.html" target="_blank">Help</a></li>
					<li><a class='nav logout' href="<%=Const.ViewURIs.LOGOUT%>">Logout</a>
					<b><%
						if(data.account.googleId.length()>=Const.SystemParams.USER_ID_MAX_DISPLAY_LENGTH){
					%>
					<span onmouseover="ddrivetip('<%=data.account.googleId%>')" onmouseout="hideddrivetip()">
							(<%=PageData.truncate(data.account.googleId,Const.SystemParams.USER_ID_MAX_DISPLAY_LENGTH)%>)</span><%
						}else{
					%>
							(<%=PageData.truncate(data.account.googleId,Const.SystemParams.USER_ID_MAX_DISPLAY_LENGTH)%>)<%} %></b>
					</li>
				</ul>
			</div>
		</div>