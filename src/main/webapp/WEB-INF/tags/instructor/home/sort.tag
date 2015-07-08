<%@ tag description="instructorHome - Course sorting buttons" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<c:set var="byCourse" value="<%= Const.SORT_BY_COURSE_ID %>" />
<c:set var="byName" value="<%= Const.SORT_BY_COURSE_NAME %>" />
<c:set var="byCreationDate" value="<%= Const.SORT_BY_COURSE_CREATION_DATE %>" />
<div class="col-md-7 pull-right">
    <div class="row">
        <div class="col-md-3">
            <h5 class="pull-right"><strong> Sort By: </strong></h5>
        </div>
        <div class="col-md-6">
            <div class="btn-group" data-toggle="buttons">
                <label class="btn btn-default<c:if test="${data.sortCriteria == byCourse}"> active</c:if>"
                       name="sortby" data="id" id="sortById">
                    <input type="radio">
                    Course ID
                </label>
                <label class="btn btn-default<c:if test="${data.sortCriteria == byName}"> active</c:if>" 
                       name="sortby" data="name" id="sortByName">
                    <input type="radio" name="sortby" value="name" >
                    Course Name
                </label>
                <label class="btn btn-default<c:if test="${data.sortCriteria == byCreationDate}"> active</c:if>" 
                       name="sortby" data="createdAt" id="sortByDate">
                    <input type="radio">
                    Creation Date
                </label>
            </div>
        </div>
        <div class="col-md-3">
            <a class="btn btn-primary btn-md pull-right" href="${data.instructorCourseLink}" 
                id="addNewCourse">Add New Course</a>
        </div>
    </div>
</div>