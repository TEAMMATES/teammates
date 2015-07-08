<%@ tag description="instructorCourseEdit - Panel Heading of Instructor List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/course" prefix="course" %>
<%@ attribute name="instructorPanel" type="teammates.ui.template.CourseEditInstructorPanel" required="true" %>

<div id="tunePermissionsDivForInstructor${instructorPanel.index}" style="display: none;">
    <div class="form-group">
        <div class="col-xs-12">
            <div class="panel panel-info">
                <div class="panel-heading">
                    <strong>In general, this instructor can</strong>
                </div>
                
                <div class="panel-body">
                    <c:forEach items="${instructorPanel.permissionInputGroup1}" var="permissionCheckbox">
                        <div class="col-sm-3">
                            <input ${permissionCheckbox.attributesToString}> ${permissionCheckbox.content}
                        </div>
                    </c:forEach>
                    <br>
                    <br>
                    
                    <div class="col-sm-6 border-right-gray">
                        <c:forEach items="${instructorPanel.permissionInputGroup2}" var="permissionCheckbox">
                            <input ${permissionCheckbox.attributesToString}> ${permissionCheckbox.content}
                            <br>
                        </c:forEach>
                    </div>
                    
                    <div class="col-sm-5 col-sm-offset-1">
                        <c:forEach items="${instructorPanel.permissionInputGroup3}" var="permissionCheckbox">
                            <input ${permissionCheckbox.attributesToString}> ${permissionCheckbox.content}
                            <br>
                        </c:forEach>
                    </div>
                </div>
            </div>
            
            <c:if test="${not empty instructorPanel.sectionRows}" >
                <c:forEach items="${instructorPanel.sectionRows}" var="sectionRow" varStatus="i">
                    <course:courseEditTuneSectionPermissionsDiv 
                            instructorIndex="${instructorPanel.index}"
                            sectionIndex="${i.index}"
                            sectionRow="${sectionRow}"
                    />
                </c:forEach>
                
                <a ${instructorPanel.addSectionLevelForInstructorButton.attributesToString}>
                    ${instructorPanel.addSectionLevelForInstructorButton.content}
                </a>
            </c:if>
        </div>
    </div>
</div>