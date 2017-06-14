/* global prepareInstructorPages:false */

$(document).ready(() => {
    prepareInstructorPages();

    $('#edit_enroll').on('click', () => {
        document.forms.goBack.submit();
    });
});
