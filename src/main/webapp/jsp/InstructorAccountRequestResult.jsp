<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>

<t:staticPage>

<div id="requestAccountResultsDiv" class="well well-plain well-narrow well-sm-wide">

    <h1 class="color_orange">
        Request for an Account - Results
    </h1>
    
    <h4> Dear Mr./Mrs. (name), <br>
    Thank you for applying for becoming a TEAMMATES user.<br>
    You have submitted the following data:
    (...)
    
    We will review it as soon as possible and we will get back to you.
    </h4>
    
    <form name='goBack' action="${data.instructorRequestAccountLink}" method="post" role="form"> 
        <div class = "alert alert-success"> 
            Request Successful. Summary given above. Click <a id="edit_enroll" href="javascript:document.forms['goBack'].submit()">here</a> to do further changes to the request form.
            <input type="hidden" name="${COURSE_ID}" value="${data.courseId}">
            <input type="hidden" name="${STUDENTS_ENROLLMENT_INFO}" value="${fn:escapeXml(data.enrollStudents)}">
        </div>
    </form>
    
    
</div>
 
</t:staticPage>
