<%@ tag description="instructorHome - Course sorting buttons" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="isSortButtonsDisabled" required="true" %>
<c:set var="byCourse" value="<%= Const.SORT_BY_COURSE_ID %>" />
<c:set var="byName" value="<%= Const.SORT_BY_COURSE_NAME %>" />
<c:set var="byCreationDate" value="<%= Const.SORT_BY_COURSE_CREATION_DATE %>" />
<div class="row">
    <div class="col-md-2 pull-left">
        <a class="btn btn-primary btn-md pull-left" href="${data.instructorCoursesLink}" 
            id="addNewCourse">Add New Course</a>
    </div>
    <div class="col-md-5 pull-right">
        <h5 class="pull-left"><strong> Sort By: </strong></h5>
        <div class="btn-group pull-right" data-toggle="buttons">
            <label class="btn btn-default<c:if test="${data.sortCriteria == byCourse}"> active</c:if>"
                   name="sortby" data="id" id="sortById"
                   <c:if test="${isSortButtonsDisabled}">disabled="disabled"</c:if>>
                <input type="radio">
                Course ID
            </label>
            <label class="btn btn-default<c:if test="${data.sortCriteria == byName}"> active</c:if>" 
                   name="sortby" data="name" id="sortByName"
                   <c:if test="${isSortButtonsDisabled}">disabled="disabled"</c:if>>
                <input type="radio" name="sortby" value="name" >
                Course Name
            </label>
            <label class="btn btn-default<c:if test="${data.sortCriteria == byCreationDate}"> active</c:if>" 
                   name="sortby" data="createdAt" id="sortByDate"
                   <c:if test="${isSortButtonsDisabled}">disabled="disabled"</c:if>>
                <input type="radio">
                Creation Date
            </label>
        </div>
    </div>
</div>