<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/student/profile" prefix="tsp" %>
<c:set var="cssIncludes">
  <link type="text/css" rel="stylesheet" href="<%= FrontEndLibrary.JQUERY_GUILLOTINE_CSS %>">
</c:set>
<c:set var="jsIncludes">
  <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY_GUILLOTINE %>"></script>
  <script type="text/javascript" src="/js/studentProfile.js"></script>
</c:set>
<ts:studentPage title="Student Profile" cssIncludes="${cssIncludes}" jsIncludes="${jsIncludes}">
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <br>
  <tsp:uploadPhotoModal modal="${data.uploadPhotoModal}" sessionToken="${data.sessionToken}" />
  <tsp:studentProfileDiv profile="${data.profileEditBox}" sessionToken="${data.sessionToken}" />
</ts:studentPage>
