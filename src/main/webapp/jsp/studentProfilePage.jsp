<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/student/profile" prefix="tsp" %>
<c:set var="jsIncludes">
    <link type="text/css" rel="stylesheet" href="/Jcrop/css/jquery.Jcrop.min.css">
    <script type="text/javascript" src="/Jcrop/js/jquery.Jcrop.min.js"></script>
    <script type="text/javascript" src="/js/student.js"></script>
    <script type="text/javascript" src="/js/studentProfile.js"></script>
</c:set>
<ts:studentPage pageTitle="TEAMMATES - Student Profile" bodyTitle="Student Profile" jsIncludes="${jsIncludes}">
    <t:statusMessage />
    <br>
    <tsp:uploadPhotoModal modal="${data.uploadPhotoModal}" />
    <tsp:studentProfileDiv profile="${data.profileEditBox}" />
</ts:studentPage>