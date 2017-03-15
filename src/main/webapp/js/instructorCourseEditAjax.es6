/* global checkTheRoleThatApplies:false bindChangingRole:false enableEditInstructor:false */

let instructorSize;

$(document).ready(() => {
    const editLinks = $('a[id^=instrEditLink]');
    instructorSize = editLinks.length;
    $(editLinks).click(editFormRequest);
});

function editFormRequest(e) {
    e.preventDefault();
    const editButton = this;
    const displayIcon = $(this).parent().find('.display-icon');
    const form = $(this).prev('.editForm');
    const formData = form.serialize();
    const index = $(this).attr('id').replace('instrEditLink', '');
    const editForm = $(`#accessControlEditDivForInstr${index}`);

    $.ajax({
        type: 'POST',
        cache: false,
        url: `${$(form).attr('action')}?${formData}`,
        beforeSend() {
            displayIcon.html("<img height='25' width='25' src='/images/ajax-preload.gif'/>");
        },
        error() {
            displayIcon.html('');
            const warningSign = '<span class="glyphicon glyphicon-warning-sign"></span>';
            const errorMsg = 'Edit failed. Click here to retry.';
            $(editButton).html(`${warningSign} ${errorMsg}`);
        },
        success(data) {
            const appendedData = $($(data).find('div[id^=accessControlEditDivForInstr]')[0]).html();
            $(data).remove();
            $(editForm[0]).html(appendedData);
            displayIcon.html('');
            checkTheRoleThatApplies(index);
            bindChangingRole(index);
            $(editButton).off('click');
            $(editButton).click({
                instructorIndex: parseInt(index, 10),
                total: instructorSize,
            }, enableEditInstructor);
            $(editButton).trigger('click');
        },
    });
}
