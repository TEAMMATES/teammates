<%@ tag description="Filter panel" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="displayArchive" required="true" %>
<%@ attribute name="instructorCommentsLink" required="true" %>
<%@ attribute name="commentsForStudentsTables" type="java.util.Collection" required="true" %>
<%@ attribute name="feedbackSessions" type="java.util.Collection" required="true" %>
<div class="well well-plain">
    <div class="row">
        <div class="col-md-2">
            <div class="checkbox">
                <input id="option-check" type="checkbox">
                <label for="option-check">
                    Show More Options
                </label>
            </div>
        </div>
        <div class="col-md-3">
            <div class="checkbox">
                <input id="displayArchivedCourses-check"
                    type="checkbox"
                    ${displayArchive ? 'checked' : ''}>
                <label for="displayArchivedCourses-check">
                    Include Archived Courses
                </label>
                <div id="displayArchivedCourses-link" style="display:none;">
                    <a href="${instructorCommentsLink}">link back to the page</a>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div id="more-options" class="well well-plain">
            <form class="form-horizontal" role="form">
                <div class="row">
                    <div class="col-sm-4 filter-options">
                        <div class="text-color-primary">
                            <strong>Show comments for: </strong>
                        </div>
                        <br>
                        <div class="checkbox">
                            <input id="panel-all"
                                type="checkbox"
                                checked>
                            <label for="panel-all"><strong>All</strong></label>
                        </div>
                        <br>
                        <c:set var="panelIdx" value="0" scope="page" />
                        <c:if test="${not empty commentsForStudentsTables}"> 
                            <c:set var="panelIdx" value="${panelIdx + 1}" scope="page" />
                            <div class="checkbox">
                                <input id="panel-check-${panelIdx}"
                                    type="checkbox"
                                    checked> 
                                <label
                                    for="panel-check-${panelIdx}">
                                    Students 
                                </label>
                            </div>
                        </c:if>
                        <c:forEach items="${feedbackSessions}" var="fs"> 
                            <c:set var="panelIdx" value="${panelIdx + 1}" scope="page" />
                            <div class="checkbox">
                                <input id="panel-check-${panelIdx}"
                                    type="checkbox"
                                    checked> 
                                <label for="panel-check-${panelIdx}">
                                    Session: ${fs.sessionName}
                                </label>
                            </div>
                        </c:forEach>
                    </div>
                    <div class="col-sm-4 filter-options">
                        <div class="text-color-primary">
                            <strong>Show comments from: </strong>
                        </div>
                        <br>
                        <div class="checkbox">
                            <input type="checkbox" value=""
                                id="giver-all"
                                checked> 
                            <label for="giver-all">
                                <strong>All</strong>
                            </label>
                        </div>
                        <br>
                        <div class="checkbox">
                            <input id="giver-check-by-you"
                                type="checkbox"
                                checked>
                            <label for="giver-check-by-you">
                                You
                            </label>
                        </div>
                        <div class="checkbox">
                            <input id="giver-check-by-others"
                                type="checkbox"
                                checked>
                            <label for="giver-check-by-others">
                                Others
                            </label>
                        </div>
                    </div>
                    <div class="col-sm-4 filter-options">
                        <div class="text-color-primary">
                            <strong>Show comments with status: </strong>
                        </div>
                        <br>
                        <div class="checkbox">
                            <input type="checkbox" value=""
                                id="status-all"
                                checked> 
                            <label for="status-all">
                                <strong>All</strong>
                            </label>
                        </div>
                        <br>
                        <div class="checkbox">
                            <input id="status-check-public"
                                type="checkbox"
                                checked>
                            <label for="status-check-public">
                                Public
                            </label>
                        </div>
                        <div class="checkbox">
                            <input id="status-check-private"
                                type="checkbox"
                                checked>
                            <label for="status-check-private">
                                Private
                            </label>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>