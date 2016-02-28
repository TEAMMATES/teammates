var isFetchingCourses = false;
var needsRetrying = false;

$(document).ready(function(){
    var ajaxRequest = function(e) {
        if (isFetchingCourses) {
            return;
        }
        e.preventDefault();
        var formData = $(this).serialize();
        $.ajax({
            type: 'POST',
            cache: false,
            url: $(this).attr('action') + '?' + formData,
            beforeSend: function() {
                $('#coursesList').html(
                    '<img height="75" width="75" class="margin-center-horizontal" src="/images/ajax-preload.gif"/>'
                );
                isFetchingCourses = true;
            },
            error: function() {
                isFetchingCourses = false;
                needsRetrying = true;
                $('#coursesList').html('');
                setStatusMessage(
                    'Courses could not be loaded. Click <a href="#" id="retryAjax">here</a> to retry.'
                );
                $('#retryAjax').click(function(e) {
                    e.preventDefault();
                    $('#ajaxForCourses').trigger('submit');
                });
            },
            success: function(data) {
                isFetchingCourses = false;
                if (needsRetrying) {
                    clearStatusMessage();
                    needsRetrying = false;
                }
                var statusMessage = $(data).find('#statusMessage').text();
                appendStatusMessage(statusMessage);
                var appendedCoursesTable = $(data).find('#coursesList').html();
                $('#coursesList')
                    .removeClass('align-center')
                    .html(appendedCoursesTable);
                toggleSort($("#button_sortcourseid"));
                linkAjaxForCourseStats();
            }
        });
    };
    $('#ajaxForCourses').submit(ajaxRequest);
});