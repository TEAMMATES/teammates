<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/student.js"></script>
</c:set>
<ts:studentPage pageTitle="TEAMMATES - Student" bodyTitle="" jsIncludes="${jsIncludes}">
    <t:statusMessage />
    <br>
	<div class="panel panel-primary panel-narrow">
	    <div class="panel-heading">
	        <h4>Confirm your Google account</h4>
	    </div>
	    <div class="panel-body">
	        <p>
	            You are currently logged in as <span><strong>${data.account.googleId}</strong></span>. 
	            <br>If this is not you please <a href="${data.logoutUrl}">log out</a> and re-login using your own Google account.
	            <br>If this is you, please confirm below to complete your registration.
	            <br>
	        </p>
	        <div class="align-center">
	            <a href="${data.confirmUrl}" 
	                class="btn btn-success"
	                id="button_confirm">Yes, this is my account</a>
	            <a href="${data.logoutUrl}" 
	                class="btn btn-danger"
	                id="button_cancel">No, this is not my account</a>
	        </div>
	        
	    </div>
	</div>
</ts:studentPage>
