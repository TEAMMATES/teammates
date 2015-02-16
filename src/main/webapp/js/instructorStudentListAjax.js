var COURSE_STUDENT_EDIT = "Use this to edit the details of this student. <br>To edit multiple students in one go, you can use the enroll page: <br>Simply enroll students using the updated data and existing data will be updated accordingly";
var COURSE_STUDENT_DELETE = "Delete the student and the corresponding evaluations from the course";
var COURSE_STUDENT_RECORDS = "View all student's evaluations and feedbacks";    
var STUDENT_LIMIT = 3000;
var PERFORMANCE_ISSUE_MESSAGE = 'Due to performance issue, it is not allowed to show more than ' + STUDENT_LIMIT 
                                + ' students. Please deselect some courses to view student list of other courses.';
var numStudents = 0;

function sanitizeForHtml(str){
    if(typeof str == 'undefined')
        return "";

    return str.replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/\"/g, "&quot;")
                .replace(/\//g, "&#x2f;")
                .replace(/\'/, "&#39;")
                //To ensure when apply sanitizeForHtml for multiple times, the string's still fine
                //Regex meaning: replace '&' with safe encoding, but not the one that is safe already
                .replace(/&(?!(amp;)|(lt;)|(gt;)|(quot;)|(#x2f;)|(#39;))/g, "&amp;");
}

function sanitizeForJs(str){
    return  sanitizeForHtml(
                str.replace(/\/\//g, "\\\\")
                .replace(/\"/g, "\\\"")
                .replace(/\'/g, "\\'")
                .replace(/#/g, "\\#"));
}

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

function bindPhotos(courseIdx) {
    $("td[id^=studentphoto-c" + courseIdx + "]").each(function(){
    	bindErrorImages($(this).children('.profile-pic-icon-click'));
        bindStudentPhotoLink($(this).children('.profile-pic-icon-click').children('.student-profile-pic-view-link'));
    });
}


function getAppendedData(data, courseIdx) {
    var appendedHtml = "";
    var sortIdx = 2;
    if(data.courseSectionDetails.length > 0){
        appendedHtml += '<table class="table table-responsive table-striped table-bordered margin-0">';
        appendedHtml += '<thead class="background-color-medium-gray text-color-gray font-weight-normal">';
        appendedHtml += '<tr id="resultsHeader-' + courseIdx + '"><th>Photo</th>';
        if(data.hasSection) { 
            appendedHtml += '<th id="button_sortsection-' + courseIdx + '" class="button-sort-none" onclick="toggleSort(this,' + (sortIdx++) + ')">';
            appendedHtml += 'Section <span class="icon-sort unsorted"></span></th>';
        } else {
            appendedHtml += '<th id="button_sortsection-' + courseIdx + '" class="button-sort-none hidden" onclick="toggleSort(this,' + (sortIdx++) + ')">';
            appendedHtml += 'Section <span class="icon-sort unsorted"></span></th>';
        }
        appendedHtml += '<th id="button_sortteam-' + courseIdx + '" class="button-sort-none" onclick="toggleSort(this,' + (sortIdx++) + ')">';
        appendedHtml += 'Team <span class="icon-sort unsorted"></span></th>';
        appendedHtml += '<th id="button_sortstudentname-' + courseIdx + '" class="button-sort-none" onclick="toggleSort(this,' + (sortIdx++) + ')">';
        appendedHtml += 'Student Name <span class="icon-sort unsorted"></span></th>';
        appendedHtml += '<th id="button_sortemail-' + courseIdx + '" class="button-sort-none" onclick="toggleSort(this,' + (sortIdx++) + ')">';
        appendedHtml += 'Email <span class="icon-sort unsorted"></span></th><th>Action(s)</th></tr>';

        appendedHtml += '<tr id="searchNoResults-' + courseIdx + '" class="hidden"><th class="align-center color_white bold">Cannot find students in this course</th>';
        appendedHtml += '</tr></thead>';

        var sectionIdx = -1;
        var teamIdx = -1;
        var studentIdx = -1;
        for(var i = 0; i < data.courseSectionDetails.length; i++){
            sectionIdx++;
            var section = data.courseSectionDetails[i];
            var appendedSection = '';
            appendedSection += '<div class="checkbox"><input id="section_check-' + courseIdx + '-' + sectionIdx + '" type="checkbox" checked="checked" class="section_check">';
            appendedSection += '<label for="section_check-' + courseIdx + '-' + sectionIdx + '">';
            appendedSection += '[' + data.course.id + '] : ' + sanitizeForHtml(section.name) + '</label></div>';
            $("#sectionChoices").append(appendedSection);
            
            for(var j = 0; j < section.teams.length; j++){
                teamIdx++;
                var team = section.teams[j];
                var appendedTeam = '';
                appendedTeam += '<div class="checkbox"><input id="team_check-' + courseIdx + '-' + sectionIdx + '-' + teamIdx + '" type="checkbox" checked="checked" class="team_check">';
                appendedTeam += '<label for="team_check-' + courseIdx + '-' + sectionIdx + '-' + teamIdx + '">';
                appendedTeam += '[' + data.course.id + '] : ' + sanitizeForHtml(team.name) + '</label></div>';
                $('#teamChoices').append(appendedTeam);

                for(var k = 0; k < team.students.length; k++){
                    studentIdx++;
                    var student = team.students[k];
                    var appendedEmail = '<div id="student_email-c' + courseIdx + '.' + studentIdx + '">' + student.email + '</div>';
                    $('#emails').append(appendedEmail);

                    appendedHtml += '<tr id="student-c' + courseIdx + '.' + studentIdx + '">';
                    appendedHtml += '<td id="studentphoto-c' + courseIdx + '.' + studentIdx + '">';
                    appendedHtml += '<div class="profile-pic-icon-click align-center" data-link="' + data.emailPhotoUrlMapping[student.email] + '">';
                    appendedHtml += '<a class="student-profile-pic-view-link btn-link">'
                                       + 'View Photo</a><img src="" alt="No Image Given" class="hidden"></div></td>';
                    if(data.hasSection) { 
                        appendedHtml += '<td id="studentsection-c' + courseIdx + '.' + sectionIdx + '">' + sanitizeForHtml(section.name) + '</td>';
                    } else {
                        appendedHtml += '<td id="studentsection-c' + courseIdx + '.' + sectionIdx + '" class="hidden">' + sanitizeForHtml(section.name) + '</td>';
                    }
                    appendedHtml += '<td id="studentteam-c' + courseIdx + '.' + sectionIdx + '.' + teamIdx + '">' + sanitizeForHtml(team.name) + '</td>';
                    appendedHtml += '<td id="studentname-c' + courseIdx + '.' + studentIdx + '">' + sanitizeForHtml(student.name) + '</td>';
                    appendedHtml += '<td id="studentemail-c' + courseIdx + '.' + studentIdx + '">' + sanitizeForHtml(student.email) + '</td>';
                    appendedHtml += '<td class="no-print align-center">';
                    appendedHtml += '<a class="btn btn-default btn-xs student-view-for-test"'
                                        + 'href="' + getCourseStudentDetailsLink(student, data.account.googleId) + '"'
                                        + 'title="View the details of the student" target="_blank" data-toggle="tooltip" data-placement="top"';
                    if(!data.sectionPrivileges[section.name]['canviewstudentinsection']){
                        appendedHtml += 'disabled="disabled"';
                    }
                    appendedHtml += '> View</a>&nbsp;';

                    appendedHtml += '<a class="btn btn-default btn-xs student-edit-for-test"'
                                     + 'href="' + getCourseStudentEditLink(student, data.account.googleId) + '"'
                                     + 'title="' + COURSE_STUDENT_EDIT + '" target="_blank" data-toggle="tooltip" data-placement="top"';
                    if(!data.sectionPrivileges[section.name]['canmodifystudent']){
                        appendedHtml += 'disabled="disabled"';
                    }
                    appendedHtml += '> Edit</a>&nbsp;';

                    appendedHtml += '<a class="btn btn-default btn-xs student-delete-for-test"'
                                     + 'href="' + getCourseStudentDeleteLink(student, data.account.googleId) + '"'
                                     + 'onclick="return toggleDeleteStudentConfirmation(\'' + sanitizeForJs(student.course) + '\',\'' + sanitizeForJs(student.name) + '\')"';
                                     + 'title="' + COURSE_STUDENT_DELETE + '" data-toggle="tooltip" data-placement="top"';
                    if(!data.sectionPrivileges[section.name]['canmodifystudent']){
                        appendedHtml += 'disabled="disabled"';
                    }
                    appendedHtml += '> Delete</a>&nbsp;';
                                    
                    appendedHtml += '<a class="btn btn-default btn-xs student-records-for-test"'
                                     + 'href="' + getStudentRecordsLink(student, data.account.googleId) + '"'
                                     + 'title="' + COURSE_STUDENT_RECORDS + '" target="_blank" data-toggle="tooltip" data-placement="top"> All Records</a>&nbsp;';
                    appendedHtml += '<div class="dropdown inline"><a class="btn btn-default btn-xs dropdown-toggle"' 
                                       + ' href="javascript:;" data-toggle="dropdown"';
                    if(!data.sectionPrivileges[section.name]['cangivecommentinsection']){
                        appendedHtml += 'disabled="disabled"';
                    }
                    appendedHtml += '> Add Comment</a>';
                    appendedHtml += '<ul class="dropdown-menu align-left" role="menu" aria-labelledby="dLabel">';
                    appendedHtml += '<li role="presentation"><a target="_blank" role="menuitem" tabindex="-1" href="' + getCourseStudentDetailsLink(student, data.account.googleId) 
                                            +"&addComment=student" + '">';
                    appendedHtml += 'Comment on ' + sanitizeForHtml(student.name) + '</a></li>';
                    appendedHtml += '<li role="presentation"><a target="_blank" role="menuitem" tabindex="-1" href="' + getCourseStudentDetailsLink(student, data.account.googleId) 
                                            + "&addComment=team" + '">';
                    appendedHtml += 'Comment on ' + sanitizeForHtml(team.name) + '</a></li>';
                    if(data.hasSection) { 
                        appendedHtml += '<li role="presentation"><a target="_blank" role="menuitem" tabindex="-1" href="' + getCourseStudentDetailsLink(student, data.account.googleId) 
                                            +"&addComment=section" + '">';
                        appendedHtml += 'Comment on ' + sanitizeForHtml(section.name) + '</a></li>';
                    } 
                    appendedHtml += '</ul></div></td></tr>';
                }
            }
        }
        appendedHtml += '</table>';
    } else {
        appendedHtml += '<table class="table table-responsive table-striped table-bordered margin-0">';
        appendedHtml += '<thead class="background-color-medium-gray text-color-gray font-weight-normal">';
        appendedHtml += '<tr><th class="align-center color_white bold">There are no students in this course</th>';
        appendedHtml += '</tr></thead></table>';
    }
    return appendedHtml;
}

var seeMoreRequest = function(e) {
    var panelHeading = $(this);
    var panelCollapse = $(this).parent().children('.panel-collapse');
    var panelBody = $(panelCollapse[0]).children('.panel-body');
    var displayIcon = $(this).children('.display-icon');
    var courseIdx = $(panelCollapse[0]).attr("id").split('-')[1];
    var courseCheck = $('#course_check-' + courseIdx);
    var courseNumStudents = parseInt($('#numStudents-' + courseIdx).val());
    
    if($(panelHeading).attr('class').indexOf('ajax_submit') == -1){
        setStatusMessage('', false);
        if($(panelCollapse[0]).attr('class').indexOf('checked') != -1){
            $(panelCollapse[0]).collapse("hide");
            $(panelHeading).addClass('ajax_submit');
            $(panelBody[0]).html('');
            $(panelCollapse[0]).removeClass('checked');
            $(courseCheck).prop('checked', false);
            numStudents -= courseNumStudents;
        } else {
            $(panelCollapse).collapse("show");
            $(panelCollapse[0]).addClass('checked');
            $(courseCheck).prop('checked', true);
        }
        checkCourseBinding(courseCheck);
    } else {
        numStudents += courseNumStudents;
        if(numStudents < STUDENT_LIMIT){
            setStatusMessage('', false);
            var formObject = $(this).children("form");
            var courseIdx = $(formObject[0]).attr('class').split('-')[1];
            var formData = formObject.serialize();
            e.preventDefault();
            if(displayIcon.html().indexOf('img') == -1){
                $.ajax({
                    type : 'POST',
                    url :   $(formObject[0]).attr('action') + "?" + formData,
                    beforeSend : function() {
                        displayIcon.html("<img height='25' width='25' src='/images/ajax-preload.gif'/>")
                    },
                    error : function() {
                        numStudents -= courseNumStudents;
                        console.log('Error');
                    },
                    success : function(data) {
                        var appendedData = getAppendedData(data, courseIdx);
                        $(panelBody[0]).html(appendedData);
                        bindPhotos(courseIdx);
                        $(panelHeading).removeClass('ajax_submit');
                        displayIcon.html('');
                        if($(panelCollapse[0]).attr('class').indexOf("in") == -1){
                            $(panelHeading).trigger('click');
                        }
                        $("[data-toggle='tooltip']").tooltip({html: true, container: 'body'}); 
                    }
                });
            }
        } else {
            numStudents -= courseNumStudents;
            courseCheck.prop('checked', false);
            setStatusMessage(PERFORMANCE_ISSUE_MESSAGE, true);
            displayIcon.html('');
        }
    }
};

$(document).ready(function(){
    $(".ajax_submit").click(seeMoreRequest);
});