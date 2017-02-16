const instructorCourseAjaxRequest = (function() {
    const MESSAGES = {
        error: 'Courses could not be loaded. Click <a href="#" id="retryAjax">here</a> to retry.'
    };

    let isFetchingCourses = false;
    let needsRetrying = false;

    function showLoadingImage() {
        $('#coursesList').html(
            '<img height="75" width="75" class="margin-center-horizontal" src="/images/ajax-preload.gif"/>'
        );
    }

    function ajaxRequest(e) {
        if (isFetchingCourses) {
            return;
        }
        e.preventDefault();
        const formData = $(this).serialize();
        $.ajax({
            type: 'POST',
            cache: false,
            url: $(this).attr('action') + '?' + formData,
            beforeSend: () => {
                showLoadingImage();
                isFetchingCourses = true;
            },
            error: () => {
                isFetchingCourses = false;
                needsRetrying = true;
                $('#coursesList').html('');
                setStatusMessage(MESSAGES.error, StatusType.WARNING);
                $('#retryAjax').click(function(e) {
                    e.preventDefault();
                    $('#ajaxForCourses').trigger('submit');
                });
            },
            success: (data) => {
                isFetchingCourses = false;
                if (needsRetrying) {
                    clearStatusMessages();
                    needsRetrying = false;
                }
                const statusMessages = $(data).find('.statusMessage');
                appendStatusMessage(statusMessages);

                const appendedCoursesTable = $(data).find('#coursesList').html();
                $('#coursesList')
                    .removeClass('align-center')
                    .html(appendedCoursesTable);
                toggleSort($('#button_sortcourseid'));
                linkAjaxForCourseStats();
            }
        });
    }
    return {
        ajaxRequest
    };
})();

$(document).ready(function() {
    $('#ajaxForCourses').submit(instructorCourseAjaxRequest.ajaxRequest);
});
