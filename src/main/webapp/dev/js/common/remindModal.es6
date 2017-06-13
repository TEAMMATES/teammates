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
                $('#remindModal input[type="submit"]').prop('disabled', true).prop('value', 'Loading...');
            },
            error() {
                $('#studentList').html('Error retrieving student list. Please close the dialog window and try again.');
            },
            success(data) {
                setTimeout(() => {
                    $('#studentList').html(data);
                    $('#remindModal input[type="submit"]').prop('disabled', false).prop('value', 'Remind');
                }, 500);
            },
        });
    });
}
