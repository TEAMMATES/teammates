<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>

<ti:instructorPage pageTitle="TEAMMATES - Instructor" bodyTitle="">
    <br />
    <t:statusMessage />
    <br />
    <div class="panel panel-primary panel-narrow">
        <div class="panel-heading">
            <h3>Confirm your Google account</h3>
        </div>
        <div class="panel-body">
            <p class="lead">
                You are currently logged in as <span><strong>${data.account.googleId}</strong></span>.
                <br>If this is not you please <a
                    href="/logout.jsp">log out</a> and re-login using your own Google account. 
                    <br>If this is you, please confirm below to complete your registration. <br>
            <div class="align-center">
                <a href="${data.confirmationLink}" id="button_confirm"
                    class="btn btn-success">Yes, this is my account</a> <a href="/logout.jsp" id="button_cancel"
                    class="btn btn-danger">No, this is not my account</a>
            </div>
            </p>
        </div>
    </div>
</ti:instructorPage>