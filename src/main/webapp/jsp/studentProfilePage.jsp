<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/student/profile" prefix="tsp" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/studentProfile.js"></script>
</c:set>
<ts:studentPage title="Student Profile" jsIncludes="${jsIncludes}">
  <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
  <br>
  <tsp:uploadPhotoModal modal="${data.uploadPhotoModal}" sessionToken="${data.sessionToken}" />
  <tsp:studentProfileDiv profile="${data.profileEditBox}" sessionToken="${data.sessionToken}" />
</ts:studentPage>
