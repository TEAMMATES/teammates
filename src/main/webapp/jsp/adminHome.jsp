<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/home" prefix="adminHome" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="${data.jQueryFilePath}"></script>
    <script type="text/javascript" src="${data.jQueryUiFilePath}"></script>
    <script type="text/javascript" src="/js/administrator.js"></script>
</c:set>

<ta:adminPage bodyTitle="Add New Instructor" pageTitle="TEAMMATES - Administrator" jsIncludes="${jsIncludes}">
    <adminHome:adminHomePageForm/>
    <t:statusMessage/>
</ta:adminPage>
