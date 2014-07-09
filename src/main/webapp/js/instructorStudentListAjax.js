var COURSE_STUDENT_EDIT = "Use this to edit the details of this student. <br>To edit multiple students in one go, you can use the enroll page: <br>Simply enroll students using the updated data and existing data will be updated accordingly";
var COURSE_STUDENT_DELETE = "Delete the student and the corresponding evaluations from the course";
var COURSE_STUDENT_RECORDS = "View all student's evaluations and feedbacks";    
function getCourseStudentDetailsLink(student, userId){
    var link = '/page/instructorCourseStudentDetailsPage';
    link = addParamToUrl(link, 'courseid', student.course);
    link = addParamToUrl(link, 'studentemail',student.email);
    link = addParamToUrl(link, 'user', userId);
    return link;
}

function getCourseStudentEditLink(student, userId){
    var link = '/page/instructorCourseStudentDetailsEdit';
    link = addParamToUrl(link,'courseid', student.course);
    link = addParamToUrl(link, 'studentemail',student.email);
    link = addParamToUrl(link, 'user', userId);
    return link;
}

function getCourseStudentDeleteLink(student, userId){
    var link = '/page/instructorCourseStudentDelete';
    link = addParamToUrl(link, 'courseid', student.course);
    link = addParamToUrl(link, 'studentemail',student.email);
    link = addParamToUrl(link, 'user', userId);
    return link;
}
    
function getStudentRecordsLink(student, userId){
    var link = '/page/instructorStudentRecordsPage';
    link = addParamToUrl(link, 'courseid', student.course);
    link = addParamToUrl(link, 'studentemail',student.email);
    link = addParamToUrl(link, 'user', userId);
    return link;
}

function addParamToUrl(url, key, value) {
    if (typeof key == 'undefined' || typeof value == 'undefined'){
        return url;
    }

    if (url.indexOf("?" + key + "=") != -1 || url.indexOf("&" + key + "=") != -1){
        return url;
    }

    url += url.indexOf('?') != -1 ? '&' : '?';
    url += key + "=" + escape(value);
    return url;
}

function getAppendedData(data, courseIdx) {
    var appendedHtml = "";
    var sortIdx = 2;
    if(data.courseSectionDetails.length > 0){
        appendedHtml += '<table class="table table-responsive table-striped table-bordered">'
        appendedHtml += '<thead class="fill-' + (data.course.isArchived ? "default":"primary") + '">';
        appendedHtml += '<tr><th>Photo</th>';
        if(data.hasSection) { 
            appendedHtml += '<th id="button_sortsection-' + courseIdx + '" class="button-sort-none" onclick="toggleSort(this,' + (sortIdx++) + ')">';
            appendedHtml += 'Section <span class="icon-sort unsorted"></span></th>'
        } 
        appendedHtml += '<th id="button_sortteam-' + courseIdx + '" class="button-sort-none" onclick="toggleSort(this,' + (sortIdx++) + ')">';
        appendedHtml += 'Team <span class="icon-sort unsorted"></span></th>';
        appendedHtml += '<th id="button_sortstudentname-' + courseIdx + '" class="button-sort-none" onclick="toggleSort(this,' + (sortIdx++) + ')">';
        appendedHtml += 'Student Name <span class="icon-sort unsorted"></span></th>';
        appendedHtml += '<th id="button_sortemail-' + courseIdx + '" class="button-sort-none" onclick="toggleSort(this,' + (sortIdx++) + ')">';
        appendedHtml += 'Email <span class="icon-sort unsorted"></span></th><th>Action(s)</th></tr></thead>';

        var sectionIdx = -1;
        var teamIdx = -1;
        var studentIdx = -1;
        for(var sectionIdx = 0; sectionIdx < data.courseSectionDetails.length; sectionIdx++){
            var section = data.courseSectionDetails[sectionIdx];
            for(var teamIdx = 0; teamIdx < section.teams.length; teamIdx++){
                var team = section.teams[teamIdx];
                for(var studentIdx = 0; studentIdx < team.students.length; studentIdx++){
                    var student = team.students[studentIdx];
                    appendedHtml += '<tr id="student-c' + courseIdx + '.' + studentIdx + '" style="display: table-row;">';
                    appendedHtml += '<td id="studentphoto-c' + courseIdx + '.' + studentIdx + '" class="profile-pic-icon">';
                    appendedHtml += '<a class="student-photo-link-for-test btn-link" data-link="' + data.emailPhotoUrlMapping[student.email] + '">'
                                       + 'View Photo</a><img src="" alt="No Image Given" class="hidden"></td>';
                    if(data.hasSection) { 
                        appendedHtml += '<td id="studentsection-c' + courseIdx + '.' + sectionIdx + '">' + section.name + '</td>';
                    } 
                    appendedHtml += '<td id="studentteam-c' + courseIdx + '.' + sectionIdx + '.' + teamIdx + '">' + team.name + '</td>';
                    appendedHtml += '<td id="studentname-c' + courseIdx + '.' + studentIdx + '">' + student.name + '</td>';
                    appendedHtml += '<td id="studentemail-c' + courseIdx + '.' + studentIdx + '">' + student.email + '</td>';
                    appendedHtml += '<td class="no-print align-center">';
                    appendedHtml += '<a class="btn btn-default btn-xs student-view-for-test"'
                                        + 'href="' + getCourseStudentDetailsLink(student, data.account.googleId) + '"'
                                        + 'title="View the details of the student" data-toggle="tooltip" data-placement="top"';
                    if(!data.sectionPrivileges[section.name]['canviewstudentinsection']){
                        appendedHtml += 'disabled="disabled"';
                    }
                    appendedHtml += '> View</a>';

                    appendedHtml += '<a class="btn btn-default btn-xs student-edit-for-test"'
                                     + 'href="' + getCourseStudentEditLink(student, data.account.googleId) + '"'
                                     + 'title="' + COURSE_STUDENT_EDIT + '" data-toggle="tooltip" data-placement="top"';
                    if(!data.sectionPrivileges[section.name]['canmodifystudent']){
                        appendedHtml += 'disabled="disabled"';
                    }
                    appendedHtml += '> Edit</a>';

                    appendedHtml += '<a class="btn btn-default btn-xs student-delete-for-test"'
                                     + 'href="' + getCourseStudentDeleteLink(student, data.account.googleId) + '"'
                                     + 'onclick="return toggleDeleteStudentConfirmation(\'' + student.course + '\',\'' + student.name + '\')"';
                                     + 'title="' + COURSE_STUDENT_DELETE + '" data-toggle="tooltip" data-placement="top"';
                    if(!data.sectionPrivileges[section.name]['canmodifystudent']){
                        appendedHtml += 'disabled="disabled"';
                    }
                    appendedHtml += '> Delete</a>';
                                    
                    appendedHtml += '<a class="btn btn-default btn-xs student-records-for-test"'
                                     + 'href="' + getStudentRecordsLink(student, data.account.googleId) + '"'
                                     + 'title="' + COURSE_STUDENT_RECORDS + '"data-toggle="tooltip" data-placement="top"> All Records</a>';
                    appendedHtml += '<div class="dropdown" style="display:inline;"><a class="btn btn-default btn-xs dropdown-toggle"' 
                                       + ' href="javascript:;" data-toggle="dropdown"';
                    if(!data.sectionPrivileges[section.name]['cangivecommentinsection']){
                        appendedHtml += 'disabled="disabled"';
                    }
                    appendedHtml += '> Add Comment</a>';
                    appendedHtml += '<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel" style="text-align:left;">';
                    appendedHtml += '<li role="presentation"><a role="menuitem" tabindex="-1" href="' + getCourseStudentDetailsLink(student, data.account.googleId) 
                                            +"&addComment=student" + '">';
                    appendedHtml += 'Comment on ' + student.name + '</a></li>';
                    appendedHtml += '<li role="presentation"><a role="menuitem" tabindex="-1" href="' + getCourseStudentDetailsLink(student, data.account.googleId) 
                                            + "&addComment=team" + '">';
                    appendedHtml += 'Comment on ' + team.name + '</a></li>';
                    if(data.hasSection) { 
                        appendedHtml += '<li role="presentation"><a role="menuitem" tabindex="-1" href="' + getCourseStudentDetailsLink(student, data.account.googleId) 
                                            +"&addComment=section" + '">';
                        appendedHtml += 'Comment on ' + section.name + '</a></li>';
                    } 
                    appendedHtml += '</ul></div></td></tr>';
                }
            }
        }
        appendedHtml += '</table>';
    } else {
        appendedHtml += '<table class="table table-responsive table-striped table-bordered">';
        appendedHtml += '<thead>';
        if(data.course.isArchived){
            appendedHtml += '<tr class="fill-default">';
        } else {
            appendedHtml += '<tr class="fill-primary">';
        }
        appendedHtml += '<th class="align-center color_white bold">There are no students in this course</th>';
        appendedHtml += '</tr></thead></table>';
    }
    return appendedHtml;
}

$(document).ready(function(){
    var seeMoreRequest = function(e) {
        var panelHeading = $(this);
        var displayIcon = $(this).children('.display-icon');
        var formObject = $(this).children("form");
        var courseIdx = $(formObject).attr('class').split('-')[1];
        var panelCollapse = $(this).parent().children('.panel-collapse');
        var panelBody = $(panelCollapse[0]).children('.panel-body');
        var formData = formObject.serialize();
        e.preventDefault();
        $.ajax({
            type : 'POST',
            url : 	$(formObject[0]).attr('action') + "?" + formData,
            beforeSend : function() {
                displayIcon.html("<img height='25' width='25' src='/images/ajax-preload.gif'/>")
            },
            error : function() {
                console.log('Error');
            },
            success : function(data) {
                console.log(data);
                var appendedData = getAppendedData(data);
                $(panelBody[0]).html(appendedData);
                $(panelHeading).removeClass('ajax_submit');
                $(panelHeading).off('click');
                displayIcon.html('')
                $(panelHeading).click(toggleSingleCollapse);
                $(panelHeading).trigger('click');
            }
        });
    };
    $(".ajax_submit").click(seeMoreRequest);
});