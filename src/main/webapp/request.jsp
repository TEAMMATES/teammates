<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>

<t:staticPage>

<div id="requestAccountDiv" class="well well-plain well-narrow well-sm-wide">

    <h1 class="color_orange">
        Request for an Account
    </h1>

	<h4> Dear Instructor, <br>
	We are glad that you are thinking of joining the TEAMMATES team.<br>
	Please complete the form below and we will get back to you as soon as possible. 
	</h4>

	<form action="${data.instructorRequestAccountLink}" method="post" class="form center-block" role="form">
                <div class="col-md-12">
                    <div class="form-group">
                        <label for="instructions" class="col-sm-1 control-label"></label>
                        <div class="col-sm-11">
                        	<label>Your Full Name*:</label>
                            <textarea class="form-control" id="name" name="name" rows="1" cols="2" style="max-width:50%;" placeholder="full name" required></textarea>
                            <br>
                            <label>University/School/Institution*:</label>
                            <textarea class="form-control" id="university" name="university" rows="1" cols="2" style="max-width:50%;" placeholder="university" required></textarea>
                            <br>
                            <label>Country*:</label>
                            <textarea class="form-control" id="country" name="country" rows="1" cols="1" style="max-width:20%;" placeholder="country" required></textarea>
                            <br>
                            <label>URL of your home page (if any):</label>
                            <textarea class="form-control" id="home_page" name="home_page" rows="1" cols="2" style="max-width:50%;" placeholder="url"></textarea>
                            <br>
                            <label>Official Email Address*:</label>
                            <textarea class="form-control" id="email" name="email" rows="1" cols="2" style="max-width:50%;" placeholder="email" required></textarea>
                            <br>
                            <label>Any other comments/queries:</label>
                            <textarea class="form-control" id="comments" name="comments" rows="3" cols="2" style="max-width:50%;" placeholder="comments"></textarea>
                            <br>
                            
                            <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
                            
                            <button type="submit" title="Request" id="button_request" name="button_request" class="btn btn-primary btn-md">
                                Request Account
                            </button>
                        </div>
                    </div>
                </div>
	</form>

   </div>
 
</t:staticPage>
