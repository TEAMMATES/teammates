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
            $(sender).removeClass('load-feedback-session');
        }
        $(sender).find('div[class^="placeholder-img-loading"]').html('');
    });
}

$(document).ready(() => {
    prepareInstructorPages();

    $('.panel-heading.student_feedback').click(toggleSingleCollapse);

    $('.load-feedback-session').on('click', (e) => {
        loadFeedbackSession($(e.target).data('courseid'), $(e.target).data('studentemail'),
                $(e.target).data('googleid'), $(e.target).data('fsname'), e.target);
    });

    // Auto-loading for feedback responses
    $('.load-feedback-session').click();
});
