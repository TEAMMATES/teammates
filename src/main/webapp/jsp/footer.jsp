<%@ page import="com.google.appengine.api.utils.SystemProperty"%>
<div id="contentFooter">
<% 
String version = SystemProperty.applicationVersion.get().split("\\.")[0].replace("-", ".");
String footer = "[TEAMMATES Version "  + version + "] ";
footer += "Best Viewed In Firefox, Chrome, Safari and Internet Explorer 8+. For Enquires:";
out.println(footer); 
%>
<a class="footer" href="http://www.comp.nus.edu.sg/~teams/contact.html" target="_blank">Contact Us</a>
</div>