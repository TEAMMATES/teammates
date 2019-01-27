import {
    sendRemindersToStudents,
} from './instructor';

function populateCheckBoxes($button) {
    // if clicked button is on no-response panel, then populate check boxes otherwise not
    if ($button.hasClass('remind-btn-no-response')) {
        const $studentList = $('#studentList');
        const $studentsNotResponded = $studentList.find('.bg-danger');
        for (let i = 0; i < $studentsNotResponded.length; i += 1) {
            const $studentNotResponded = $($studentsNotResponded[i]);
            const $checkbox = $studentNotResponded.find('input[type="checkbox"]');
            $checkbox.prop('checked', 'true');
        }
    }
}

function bindingSelectAllCheckBoxes() {
    // set default checkboxes as unchecked
    $('#remindAll').prop('checked', false);
    $('#remindNotSubmitted').prop('checked', false);
    // checks binding of select all checkboxes populated in the header
    $('#remindAll').on('change', function () {
        if (this.checked) {
            $('input[class^="student-"]').prop('checked', true);
            $('#remindNotSubmitted').prop('checked', true);
        } else {
            $('input[class^="student-"]').prop('checked', false);
            $('#remindNotSubmitted').prop('checked', false);
        }
    });
    $('#remindNotSubmitted').on('change', function () {
        if (this.checked) {
            $('input[class^="student-not-"]').prop('checked', true);
            if ($('input[class^="student-"]:checked').length === $('input[class^="student-"]').length) {
                $('#remindAll').prop('checked', true);
            }
        } else {
            $('input[class^="student-not-"]').prop('checked', false);
            $('#remindAll').prop('checked', false);
        }
    });
    // checks binding of each checkbox populated in the data
    $('input[class^="student-"]').on('change', () => {
        if ($('input[class^="student-"]:checked').length === $('input[class^="student-"]').length) {
            $('#remindAll').prop('checked', true);
        } else {
            $('#remindAll').prop('checked', false);
        }
        if ($('input[class^="student-not-"]:checked').length === $('input[class^="student-not-"]').length) {
            $('#remindNotSubmitted').prop('checked', true);
        } else {
            $('#remindNotSubmitted').prop('checked', false);
        }
    });
}

function prepareRemindModal() {
    $('#remindModal').on('show.bs.modal', (event) => {
        const button = $(event.relatedTarget); // Button that triggered the modal
        const actionlink = button.data('actionlink');

        $.ajax({
            type: 'POST',
            cache: false,
            url: actionlink,
            beforeSend() {
                $('#studentList').html('<img class="margin-center-horizontal" src="/images/ajax-loader.gif"/>');
                $('#remindModal .remind-particular-button').prop('disabled', true).prop('value', 'Loading...');
            },
            error() {
                $('#studentList').html('Error retrieving student list. Please close the dialog window and try again.');
            },
            success(data) {
                setTimeout(() => {
                    $('#studentList').html(data);
                    populateCheckBoxes(button);
                    $('#remindModal .remind-particular-button').prop('disabled', false).prop('value', 'Remind');
                    bindingSelectAllCheckBoxes();
                }, 500);
            },
        });
    });
    $('#remindModal .remind-particular-button').on('click', (event) => {
        const $remindButton = $(event.currentTarget);
        const $form = $remindButton.parents('form:first');
        const action = $form.attr('action');
        const formData = $form.serialize();
        const url = `${action}&${formData}`;
        sendRemindersToStudents(url);
    });
}

export {
    prepareRemindModal,
};
