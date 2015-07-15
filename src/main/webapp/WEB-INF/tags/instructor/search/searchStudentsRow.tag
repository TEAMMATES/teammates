<%@ tag description="searchStudentsTable.tag - Display student row (photo, section, team, name, email, actions)" %>
<%@ attribute name="student" type="teammates.ui.template.StudentRow" required="true" %>
<%@ attribute name="courseIdx" required="true" %>
<%@ attribute name="studentIdx" required="true" %>

<tr id="student-c${courseIdx}.${studentIdx}">
    <td id="studentphoto-c${courseIdx}.${studentIdx}">
        <div class="profile-pic-icon-click align-center" data-link="${student.viewPhotoLink}">
            <a class="student-profile-pic-view-link btn-link">View Photo</a>
            <img src="" alt="No Image Given" class="hidden">
        </div>
    </td>
                            
    <td id="studentsection-c${courseIdx}.${studentIdx}">
        ${student.section}
    </td>
    
    <td id="studentteam-c${courseIdx}.${studentIdx}">
        ${student.team}
    </td>
    
    <td id="studentname-c${courseIdx}.${studentIdx}">
        ${student.name}
    </td>
                            
    <td id="studentemail-c${courseIdx}.${studentIdx}">
        ${student.email}
    </td>
    
    <td class="no-print align-center">
        ${student.actions}
    </td>                              
</tr>