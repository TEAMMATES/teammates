$(document).ready(function() {
    setupFsCopyModal();
    
    //Click event binding for radio buttons
    var radiobuttons = $("label[name='sortby']");
    $.each(radiobuttons, function() {
        $(this).click(function () {
            var currentPath = window.location.pathname;
            var query = window.location.search.substring(1);
            var params = {};
            
            var param_values = query.split("&");
            for (var i = 0; i < param_values.length; i++) {
                var param_value = param_values[i].split("=");
                params[param_value[0]] = param_value[1];
            }

            if ("user" in params == false) {
                params["user"] = $("input[name='user']").val();
            }

            window.location.href = currentPath + "?user=" + params["user"] + "&sortby=" + $(this).attr("data");
        });
    });
});

/**
 * This is the function invoked when an instructor clicks on the archive button, which asks the instructor
 * to confirm whether or not the course should be archived
 * 
 * @param courseId
 * @returns a boolean to either continue or stop the action from continuing
 */
function toggleArchiveCourseConfirmation(courseId) {
    return confirm('Are you sure you want to archive ' + courseId + '? This action can be reverted'
                   + 'by going to the "courses" tab and unarchiving the desired course(s).');
}
