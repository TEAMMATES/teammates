import {
    enableHoverToDisplayEditOptions,
    registerResponseCommentCheckboxEvent,
    registerResponseCommentsEvent,
} from '../common/feedbackResponseComments';

import {
    prepareInstructorPages,
} from '../common/instructor';

import {
    toggleAdditionalQuestionInfo,
    toggleSingleCollapse,
} from '../common/ui';

window.toggleAdditionalQuestionInfo = toggleAdditionalQuestionInfo;

function loadFeedbackSession(courseId, stuEmail, user, fsName, sender) {
    $('.tooltip').hide();
    const targetDiv = $(sender).find('div[id^="target-feedback-"]');
    const fsNameForUrl = encodeURIComponent(fsName);
    const url = `/page/instructorStudentRecordsAjaxPage?courseid=${courseId}&studentemail=${stuEmail}`
            + `&user=${user}&fsname=${fsNameForUrl}`;
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
    registerResponseCommentsEvent();
    registerResponseCommentCheckboxEvent();
    enableHoverToDisplayEditOptions();

    $('.panel-heading.student_feedback').click(toggleSingleCollapse);

    $('.load-feedback-session').on('click', (e) => {
        const $entry = $(e.target);
        const courseId = $entry.data('courseid');
        const studentEmail = $entry.data('studentemail');
        const googleId = $entry.data('googleid');
        const fsName = $entry.data('fsname');
        loadFeedbackSession(courseId, studentEmail, googleId, fsName, $entry);
    });

    // Auto-loading for feedback responses
    $('.load-feedback-session').click();
});
