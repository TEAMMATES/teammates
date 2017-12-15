import {
    prepareInstructorPages,
} from '../common/instructor.es6';

$(document).ready(() => {
    prepareInstructorPages();

    $('#edit_enroll').on('click', () => {
        document.forms.goBack.submit();
    });
});
