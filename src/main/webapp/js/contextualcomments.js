$(document).ready(function(){
	$('#button_add_comment').click(function(){
		if($('#commentArea').is(':visible')){
			$('#commentArea').hide();
		} else {
			$('#commentArea').show();
			$('#commentText').focus();
		}
	});
	
	$('form[name="form_commentadd"]').submit(function(){
        return checkComment(this);		
    });
	
	function checkComment(form){
	    var formTextField = $(form).find('[name=commenttext]').val();
	    if (isBlank(formTextField)) {
	        setStatusMessage("Please enter a valid comment. The comment can't be empty.", true);
	        $(window).scrollTop(0);
	        return false;
	    }
	}
	
	function isBlank(str) {
	    return (!str || /^\s*$/.test(str));
	}

	$('#visibility-options-trigger').click(function(){
		if($('#visibility-options').is(':visible')){
			$('#visibility-options').hide();
			$('#visibility-options-trigger').html('<span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options');
		} else {
			$('#visibility-options').show();
			$('#visibility-options-trigger').html('<span class="glyphicon glyphicon-eye-close"></span> Hide Visibility Options');
		}
	});

	$('#button_cancel_comment').click(function(){
		$('#commentArea').hide();
	});
	
	$('#comment_recipient_select').change(commentRecipientSelect_changeHandler);
	
	function commentRecipientSelect_changeHandler(){
		//TODO: replace PERSON/TEAM/SECTION etc with constants in common.js
		var selectedValue = $('#comment_recipient_select option:selected').val();
		if(selectedValue == 'PERSON'){
			$('input[name="recipienttype"]').val('PERSON');
			$('input[name="recipients"]').val($('#studentemail > p').text());
			$('#recipient-person').show();
			$('#recipient-team').show();
			$('#recipient-team').find('div[data-toggle="tooltip"]').text('Recipient\'s Team');
			$('#recipient-team').find('input[class="visibilityCheckbox recipientCheckbox"]').removeAttr('disabled');
			$('#recipient-section').find('input[class="visibilityCheckbox recipientCheckbox"]').removeAttr('disabled');
		} else if(selectedValue == 'TEAM'){
			$('input[name="recipienttype"]').val('TEAM');
			$('input[name="recipients"]').val($('#teamname > p').text());
			$('#recipient-person').hide();
			$('#recipient-team').find('input[class="visibilityCheckbox recipientCheckbox"]').attr('disabled', 'disabled');
			$('#recipient-section').find('input[class="visibilityCheckbox recipientCheckbox"]').removeAttr('disabled');
			$('#recipient-team').find('div[data-toggle="tooltip"]').text('Recipient Team');
			$('#recipient-team').show();
		} else if(selectedValue == 'SECTION'){
			$('input[name="recipienttype"]').val('SECTION');
			$('input[name="recipients"]').val($('#sectionname > p').text());
			$('#recipient-section').find('input[class="visibilityCheckbox recipientCheckbox"]').attr('disabled', 'disabled');
			$('#recipient-person').hide();
			$('#recipient-team').hide();
		}
	}
	
	$("input[type=checkbox]").on( "click", visibilityOptionsHandler);
	
	function visibilityOptionsHandler(e){
		var visibilityOptions = [];
		var _target = $(e.target);
		
		if (_target.prop("class").includes("answerCheckbox") && !_target.prop("checked")) {
    		_target.parent().parent().find("input[class*=giverCheckbox]").prop("checked", false);
    		_target.parent().parent().find("input[class*=recipientCheckbox]").prop("checked", false);
    	}
    	if ((_target.prop("class").includes("giverCheckbox") || 
    			_target.prop("class").includes("recipientCheckbox")) && _target.prop("checked")) {
    		_target.parent().parent().find("input[class*=answerCheckbox]").prop("checked", true);
    	}
		
		$('.answerCheckbox:checked').each(function () {
			visibilityOptions.push($(this).val());
	    });
	    $("input[name='showcommentsto']").val(visibilityOptions.toString());
	    
	    visibilityOptions = [];
		$('.giverCheckbox:checked').each(function () {
			visibilityOptions.push($(this).val());
	    });
	    $("input[name='showgiverto']").val(visibilityOptions.toString());
	    
	    visibilityOptions = [];
		$('.recipientCheckbox:checked').each(function () {
			visibilityOptions.push($(this).val());
	    });
	    $("input[name='showrecipientto']").val(visibilityOptions.toString());
	}
	
	if(isShowCommentBox){
		$('#button_add_comment').click();
		if(commentRecipient == "team"){
			$('#comment_recipient_select').val('TEAM');
			commentRecipientSelect_changeHandler();
		} else if(commentRecipient == "section"){
			$('#comment_recipient_select').val('SECTION');
			commentRecipientSelect_changeHandler();
		}
	}
});
