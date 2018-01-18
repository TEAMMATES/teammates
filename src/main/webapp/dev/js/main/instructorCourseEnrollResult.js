import {
    prepareInstructorPages,
} from '../common/instructor';

$(document).ready(() => {
    prepareInstructorPages();

    $('#edit_enroll').on('click', () => {
        document.forms.goBack.submit();
    });
});
