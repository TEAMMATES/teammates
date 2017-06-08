/* global toggleSingleCollapse:false prepareInstructorPages:false */

function loadFeedbackSession(courseId, stuEmail, user, fsName, sender) {
    $('.tooltip').hide();
    const targetDiv = $(sender).find('div[id^="target-feedback-"]');
    const fsNameForUrl = encodeURIComponent(fsName);
    const url = `/page/instructorStudentRecordsAjaxPage?courseid=${courseId
               }&studentemail=${stuEmail}&user=${user}&fsname=${fsNameForUrl}`;
    $(sender).find('div[class^="placeholder-img-loading"]').html('<img src="/images/ajax-loader.gif">');
    targetDiv.load(url, (response, status) => {
        if (status === 'success') {
            $(sender).removeAttr('onclick');
            setCommentsCreatedTime();
            setCommentsEditedTime();
        }
        $(sender).find('div[class^="placeholder-img-loading"]').html('');
    });
}

$(document).ready(() => {
    prepareInstructorPages();

    // Auto-loading for feedback responses
    $('div[id^="studentFeedback-"]').click();

    $('.panel-heading.student_feedback').click(toggleSingleCollapse);
});
