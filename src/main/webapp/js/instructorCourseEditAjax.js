function editFormRequest(e){
	e.preventDefault();
	var editButton = this;
	var form = $(this).prev(".editForm");
	var formData = form.serialize();
	var index = $(this).attr("id").replace("instrEditLink", "");
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
          	var appendedData = $(data).find('#accessControlEditDivForInstr1').html();
            $(data).remove();
            $(editForm[0]).html(appendedData);
            $(saveButton).show();
        	$(editButton).off('click');
        	bindChangingRole(index);
        }	
	});

}

$(document).ready(function(){
    $("a[id^=instrEditLink]").click(editFormRequest);
});