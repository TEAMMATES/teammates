var instructorSize;

function editFormRequest(e){
	e.preventDefault();
	var editButton = this;
	var form = $(this).prev(".editForm");
	var formData = form.serialize();
	var index = $(this).attr("id").replace("instrEditLink", "");
    console.log(index);
	var editForm = $("#accessControlEditDivForInstr" + index);
	var saveButton = $("#btnSaveInstructor" + index);

	$.ajax({
		type : 'POST',
        cache: false,
        url :   $(form).attr('action') + "?" + formData,
        beforeSend : function() {
        	console.log('Before send');
        },
        error : function() {
            console.log('Error');
        },
        success : function(data) {
          	var appendedData = $($(data).find("div[id^=accessControlEditDivForInstr]")[0]).html();
            $(data).remove();
            $(editForm[0]).html(appendedData);
            checkTheRoleThatApplies(index);
            bindChangingRole(index);
        	$(editButton).off('click');
            $(editButton).click({instructorIndex: parseInt(index), total: instructorSize}, enableEditInstructor);
            $(editButton).trigger('click');
        }	
	});

}

$(document).ready(function(){
    var editLinks = $("a[id^=instrEditLink]");
    instructorSize = editLinks.length;
    $(editLinks).click(editFormRequest);
});