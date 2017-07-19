import {
    sendRemindersToStudents,
} from './instructor.es6';

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
                }, 500);
            },
        });
    });
    $('#remindModal .remind-particular-button').on('click', (event) => {
        const $remindButton = $(event.target);
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
