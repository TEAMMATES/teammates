'use strict';

var instructorSize;

$(document).ready(function() {
    var editLinks = $('a[id^=instrEditLink]');
    instructorSize = editLinks.length;
    $(editLinks).click(editFormRequest);
});

function editFormRequest(e) {
    e.preventDefault();
    var editButton = this;
    var displayIcon = $(this).parent().find('.display-icon');
    var form = $(this).prev('.editForm');
    var formData = form.serialize();
    var index = $(this).attr('id').replace('instrEditLink', '');
    var editForm = $('#accessControlEditDivForInstr' + index);

    $.ajax({
        type: 'POST',
        cache: false,
        url: $(form).attr('action') + '?' + formData,
        beforeSend: function() {
            displayIcon.html("<img height='25' width='25' src='/images/ajax-preload.gif'/>");
        },
        error: function() {
            displayIcon.html('');
            var warningSign = '<span class="glyphicon glyphicon-warning-sign"></span>';
            var errorMsg = 'Edit failed. Click here to retry.';
            $(editButton).html(warningSign + ' ' + errorMsg);
        },
        success: function(data) {
            var appendedData = $($(data).find('div[id^=accessControlEditDivForInstr]')[0]).html();
            $(data).remove();
            $(editForm[0]).html(appendedData);
            displayIcon.html('');
            checkTheRoleThatApplies(index);
            bindChangingRole(index);
            $(editButton).off('click');
            $(editButton).click({
                instructorIndex: parseInt(index),
                total: instructorSize
            }, enableEditInstructor);
            $(editButton).trigger('click');
        }
    });

}
