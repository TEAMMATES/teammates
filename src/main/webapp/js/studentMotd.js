$(document).ready(function() {
     $.ajax({
        type: 'GET',
        url: motdUrl,
        success: function(data) {
            $('#student-motd').html(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log('AJAX request failed');
        }
    });
}

function closeMotd() {
    $('#student-motd-container').hide();
}