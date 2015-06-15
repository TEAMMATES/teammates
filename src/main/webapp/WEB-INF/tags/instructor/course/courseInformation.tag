<%@ tag description="instructorCourseDetails - Course Information Board" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>

<div class="well well-plain" id="courseInformationHeader">
    <button type="button" data-original-title="Give a comment about all students in the course"
            <c:forEach items="${data.giveCommentButton.attributes}" var="attribute" >
                ${attribute.key}="${attribute.value}"
            </c:forEach> 
    >
        ${data.giveCommentButton.content}
    </button>
            
    <div class="form form-horizontal">
        <div class="form-group">
            <label class="col-sm-3 control-label">Course ID:</label>
            <div class="col-sm-6" id="courseid">
                <p class="form-control-static">${data.courseDetails.course.id}</p>
            </div>
        </div>
        
        <div class="form-group">
            <label class="col-sm-3 control-label">Course name:</label>
            <div class="col-sm-6" id="coursename">
                <p class="form-control-static">${data.courseDetails.course.name}</p>
            </div>
        </div>
        
        <div class="form-group">
            <label class="col-sm-3 control-label">Sections:</label>
            <div class="col-sm-6" id="total_sections">
                <p class="form-control-static">${data.courseDetails.stats.sectionsTotal}</p>
            </div>
        </div>
        
        <div class="form-group">
            <label class="col-sm-3 control-label">Teams:</label>
            <div class="col-sm-6" id="total_teams">
                <p class="form-control-static">${data.courseDetails.stats.teamsTotal}</p>
            </div>
        </div>
        
        <div class="form-group">
            <label class="col-sm-3 control-label">Total students:</label>
            <div class="col-sm-6" id="total_students">
                <p class="form-control-static">${data.courseDetails.stats.studentsTotal}</p>
            </div>
        </div>
        
        <div class="form-group">
            <label class="col-sm-3 control-label">Instructors:</label>
            <div class="col-sm-6" id="instructors">
                <div class="form-control-static">
                    <c:forEach items="${data.instructors}" var="instructor" varStatus="i">
                        <c:choose>
                            <c:when test="${empty instructor.role}">
                                <%= Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER %>
                            </c:when>
                            <c:otherwise>
                                ${instructor.role}
                            </c:otherwise>
                        </c:choose>
                        :&nbsp;${instructor.name}&nbsp;(${instructor.email})
                        <br>
                        <br>
                    </c:forEach>
                </div>
            </div>
        </div>
        
        <c:if test="${data.courseDetails.stats.studentsTotal > 1}">
            <div class="form-group">
                <div class="align-center">
                    <input type="button" tabindex="1" value="Remind Students to Join"
                            <c:forEach items="${data.courseRemindButton.attributes}" var="attribute" >
                                ${attribute.key}="${attribute.value}"
                            </c:forEach> 
                    >
                                         
                    <form method="post" action="<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_LIST_DOWNLOAD%>" style="display:inline;">
                        <input id="button_download" type="submit" class="btn btn-primary"
                                name="<%=Const.ParamsNames.FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON%>"
                                value=" Download Student List ">
                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${data.courseDetails.course.id}">
                    </form>

                    <div>
                        <span class="help-block">
                            Non-English characters not displayed properly in the downloaded file?
                            <span class="btn-link" data-toggle="modal" data-target="#studentTableWindow" onclick="submitFormAjax()">
                                click here
                            </span>
                        </span>
                    </div>
                
                    <form id="csvToHtmlForm">
                        <input type="hidden" name="<%=Const.ParamsNames.COURSE_ID%>" value="${data.courseDetails.course.id}">
                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="${data.account.googleId}">
                        <input type="hidden" name="<%=Const.ParamsNames.CSV_TO_HTML_TABLE_NEEDED%>" value=true>
                    </form>
                
                    <div class="modal fade" id="studentTableWindow">
                        <div class="modal-dialog modal-lg">
                            <div class="modal-content">
                                <div class="modal-header">       
                                    <span class="pull-left help-block">
                                        Tips: After Selecting the table, <kbd>Ctrl + C</kbd> to COPY and <kbd>Ctrl + V</kbd> to PASTE to your Excel Workbook.
                                    </span>
                                    
                                    <button type="button" class="btn btn-default" data-dismiss="modal">
                                        Close
                                    </button>
                                    
                                    <button type="button" class="btn btn-primary" onclick="selectElementContents( document.getElementById('detailsTable') );">
                                        Select Table
                                    </button>                                
                                </div>
                                
                                <div class="modal-body">
                                    <div class="table-responsive">
                                        <div id="detailsTable"></div>
                                        <br>                                    
                                        
                                        <div id="ajaxStatus"></div>
                                    </div>
                                </div>
                            </div>
                            <!-- /.modal-content -->
                        </div>
                        <!-- /.modal-dialog -->
                    </div>
                    <!-- /.modal -->
                </div>
            </div>
        </c:if>
    </div>
</div>