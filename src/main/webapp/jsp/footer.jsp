<%@ page import="teammates.common.BuildProperties"%>
<div id="contentFooter">
<% 
String version = BuildProperties.getAppVersion();
String footer = "[TEAMMATES Version "  + version + "] ";
footer += "Best Viewed In Firefox, Chrome, Safari and Internet Explorer 8+. For Enquires:";
out.println(footer); 
%>
<a href="http://www.comp.nus.edu.sg/%7Eteams/contact.html" target="_blank">Contact Us</a>
</div>