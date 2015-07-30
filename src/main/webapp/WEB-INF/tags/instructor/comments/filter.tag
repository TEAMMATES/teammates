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
                <input id="displayArchivedCourses_check"
                    type="checkbox"
                    ${displayArchive ? 'checked=\"checked\"' : ''}>
                <label for="displayArchivedCourses_check">
                    Include Archived Courses
                </label>
                <div id="displayArchivedCourses_link" style="display:none;">
                    <a href="${instructorCommentsLink}">link back to the page</a>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div id="more-options" class="well well-plain">
            <form class="form-horizontal" role="form">
                <div class="row">
                    <div class="col-sm-4">
                        <div class="text-color-primary">
                            <strong>Show comments for: </strong>
                        </div>
                        <br>
                        <div class="checkbox">
                            <input id="panel_all"
                                type="checkbox"
                                checked="checked">
                            <label for="panel_all"><strong>All</strong></label>
                        </div>
                        <br>
                        <c:set var="panelIdx" value="0" scope="page" />
                        <c:if test="${not empty commentsForStudentsTables}"> 
                            <c:set var="panelIdx" value="${panelIdx + 1}" scope="page" />
                            <div class="checkbox">
                                <input id="panel_check-${panelIdx}"
                                    type="checkbox"
                                    checked="checked"> 
                                <label
                                    for="panel_check-${panelIdx}">
                                    Students 
                                </label>
                            </div>
                        </c:if>
                        <c:forEach items="${feedbackSessions}" var="fs"> 
                            <c:set var="panelIdx" value="${panelIdx + 1}" scope="page" />
                            <div class="checkbox">
                                <input id="panel_check-${panelIdx}"
                                    type="checkbox"
                                    checked="checked"> 
                                <label for="panel_check-${panelIdx}">
                                    Session: ${fs.sessionName}
                                </label>
                            </div>
                        </c:forEach>
                    </div>
                    <div class="col-sm-4">
                        <div class="text-color-primary">
                            <strong>Show comments from: </strong>
                        </div>
                        <br>
                        <div class="checkbox">
                            <input type="checkbox" value=""
                                id="giver_all"
                                checked="checked"> 
                            <label for="giver_all">
                                <strong>All</strong>
                            </label>
                        </div>
                        <br>
                        <div class="checkbox">
                            <input id="giver_check-by-you"
                                type="checkbox"
                                checked="checked">
                            <label for="giver_check-by-you">
                                You
                            </label>
                        </div>
                        <div class="checkbox">
                            <input id="giver_check-by-others"
                                type="checkbox"
                                checked="checked">
                            <label for="giver_check-by-others">
                                Others
                            </label>
                        </div>
                    </div>
                    <div class="col-sm-4">
                        <div class="text-color-primary">
                            <strong>Show comments with status: </strong>
                        </div>
                        <br>
                        <div class="checkbox">
                            <input type="checkbox" value=""
                                id="status_all"
                                checked="checked"> 
                            <label for="status_all">
                                <strong>All</strong>
                            </label>
                        </div>
                        <br>
                        <div class="checkbox">
                            <input id="status_check-public"
                                type="checkbox"
                                checked="checked">
                            <label for="status_check-public">
                                Public
                            </label>
                        </div>
                        <div class="checkbox">
                            <input id="status_check-private"
                                type="checkbox"
                                checked="checked">
                            <label for="status_check-private">
                                Private
                            </label>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>