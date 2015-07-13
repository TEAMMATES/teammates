$(document).ready(function(){
	var classNameForCommentsInFeedbackResponse = "list-group-item list-group-item-warning giver_display-by";
	var classNameForCommentsInStudentRecords = "panel panel-info student-record-comments giver_display-by";
	
	function isRedirectToSpecificComment(){
		return $(location).attr('href').indexOf('#') != -1;
	}
	
	function getRedirectSpecificCommentRow(){
		var start = $(location).attr('href').indexOf('#');
		var end = $(location).attr('href').length;
		var rowId = $(location).attr('href').substring(start, end);
		var row = $(rowId);
		return row;
	}
	
	function highlightRedirectSpecificCommentRow(row){
		row.toggleClass('list-group-item-warning list-group-item-success');
	}
	
	//for redirecting from search page, hide the header and highlight the specific comment row
	if(isRedirectToSpecificComment() && getRedirectSpecificCommentRow().length > 0){
		  $('.navbar').css('display','none');
		  highlightRedirectSpecificCommentRow(getRedirectSpecificCommentRow());
	} else if(isRedirectToSpecificComment() && getRedirectSpecificCommentRow().length == 0){
		//TODO: impl this, e.g. display a status msg that cannot find the comment etc
	}
	
	//re-display the hidden header
	var scrollEventCounter = 0;
	$( window ).scroll(function() {
		if(isRedirectToSpecificComment() && scrollEventCounter > 0){
			  $('.navbar').fadeIn("fast");
		}
		scrollEventCounter++;
	});
	
	//show on hover for comment
	$('.comments > .list-group-item').hover(
	   function(){
		$("a[type='button']", this).show();
	}, function(){
		$("a[type='button']", this).hide();
	});
	
	//check submit text before submit
	$('form.form_comment').submit(function(){
        return checkComment(this);		
    });
	
	//open or close show more options
	$('#option-check').click(function(){
		if($('#option-check').is(':checked')){
			$('#more-options').show();
		} else {
			$('#more-options').hide();
		}
	});
	
	//Binding for "Display All" panel option
	$('#panel_all').click(function(){
		//use panel_all checkbox to control its children checkboxes.
		if($('#panel_all').is(':checked')){
			$("input[id^=panel_check]").prop("checked", true);
		} else {
			$("input[id^=panel_check]").prop("checked", false);
		}
		
		filterPanel();
	});
	
	//Binding for changes in the panel check boxes
    $("input[id^=panel_check]").change(function(){
    	//based on the selected panel_check check boxes, check/uncheck panel_all check box
    	if($("input[id^='panel_check']:checked").length == $("input[id^='panel_check']").length){
        	$("#panel_all").prop("checked", true);
    	} else{
        	$("#panel_all").prop("checked", false);
    	}
    	
    	filterPanel();
    });
	
	function filterPanel(){
		//if no panel_check checkboxes are checked, show the no-comment box to user
		if($("input[id^='panel_check']:checked").length == 0){
    		$('#no-comment-panel').show();
    		//if all is checked, show giver and status for better user experience
    		if (!$('#panel_all').prop("checked")) {
     		   $('#giver_all').parent().parent().hide();
     		   $('#status_all').parent().parent().hide();
 			} else {
 				$('#giver_all').parent().parent().show();
     		    $('#status_all').parent().parent().show();
 			}
    	} else {
    		$('#no-comment-panel').hide();
    		$('#giver_all').parent().parent().show();
    		$('#status_all').parent().parent().show();
    	}
		
		//hide the panel accordingly based on panel_check checkbox
	    $("input[id^='panel_check']").each(function(){
	        var panelIdx = $(this).attr("id").split('-')[1];
	        if(this.checked){
	            $("#panel_display-" + panelIdx).show();
	        } else{
	            $("#panel_display-" + panelIdx).hide();
	        }
	    });
	}
	
	//Binding for "Display All" giver option
	$('#giver_all').click(function(){
		//use giver_all checkbox to control its children checkboxes.
		if($('#giver_all').is(':checked')){
			$("input[id^=giver_check]").prop("checked", true);
			$("#status_all").prop("disabled", false);
        	$("input[id^=status_check]").prop("disabled", false);
		} else {
			$("input[id^=giver_check]").prop("checked", false);
			$("#status_all").prop("disabled", true);
        	$("input[id^=status_check]").prop("disabled", true);
		}
		
		filterGiver();
	});
	
	//Binding for changes in the giver checkboxes.
    $("input[id^=giver_check]").change(function(){
    	//based on the selected checkboxes, check/uncheck giver_all checkbox
    	if($("input[id^='giver_check']:checked").length == $("input[id^='giver_check']").length){
        	$("#giver_all").prop("checked", true);
        	$("#status_all").prop("disabled", false);
        	$("input[id^=status_check]").prop("disabled", false);
    	} else{
        	$("#giver_all").prop("checked", false);
        	$("#status_all").prop("disabled", true);
        	$("input[id^=status_check]").prop("disabled", true);
    	}
    	
    	filterGiver();
    });
    
    function filterGiver(){
    	filterGiverCheckbox("you");
	    filterGiverCheckbox("others");
	}
    
    function filterGiverCheckbox(checkboxBy){
    	$("input[id=giver_check-by-" + checkboxBy + "]").each(function(){
	        if(this.checked){
	        	showCommentOfPanelIndex(".giver_display-by-" + checkboxBy);
	        } else{
	        	hideCommentOfPanelIndex(".giver_display-by-" + checkboxBy);
	        }
	    });
    }
    //
    //Binding for "Display All" status option
	$('#status_all').click(function(){
		//use status_all checkbox to control its children checkboxes.
		if($('#status_all').is(':checked')){
			$("input[id^=status_check]").prop("checked", true);
			$("#giver_all").prop("disabled", false);
        	$("input[id^=giver_check]").prop("disabled", false);
		} else {
			$("input[id^=status_check]").prop("checked", false);
			$("#giver_all").prop("disabled", true);
        	$("input[id^=giver_check]").prop("disabled", true);
		}
		
		filterStatus();
	});
	
	//Binding for changes in the status checkboxes.
    $("input[id^=status_check]").change(function(){
    	//based on the selected checkboxes, check/uncheck status_all checkbox
    	if($("input[id^='status_check']:checked").length == $("input[id^='status_check']").length){
        	$("#status_all").prop("checked", true);
        	$("#giver_all").prop("disabled", false);
        	$("input[id^=giver_check]").prop("disabled", false);
    	} else{
        	$("#status_all").prop("checked", false);
        	$("#giver_all").prop("disabled", true);
        	$("input[id^=giver_check]").prop("disabled", true);
    	}
    	
    	filterStatus();
    });
    
    function filterStatus(){
    	filterStatusCheckbox("public");
    	filterStatusCheckbox("private");
	}
    
    function filterStatusCheckbox(checkboxBy){
    	$("input[id=status_check-" + checkboxBy + "]").each(function(){
	        if(this.checked){
	        	showCommentOfPanelIndex(".status_display-" + checkboxBy);
	        } else{
	        	hideCommentOfPanelIndex(".status_display-" + checkboxBy);
	        }
	    });
    }
    //
    
    function showCommentOfPanelIndex(className){
    	$(className).each(function(){
    		showCommentAndItsPanel(this);
		});
    }
    
    function hideCommentOfPanelIndex(className){
    	$(className).each(function(){
    		hideCommentAndItsPanel(this);
		});
    }
    
    function showCommentAndItsPanel(comment){
    	$(comment).show();
    	
    	//to show feedback question + feedback session panel
    	//if not all list elements are hidden within fbResponse, then show fbResponse
    	if($(comment).prop("class").toString().includes(classNameForCommentsInFeedbackResponse)){
            if($(comment).parent().find('li[style*="display: none"]').length != $(comment).parent().find('li').length){
            	var commentListRegionForFeedbackResponse = $(comment).parent().parent().parent();
            	//a fbResponse in instructorCommentsPage (html) is made up of 4 rows as the followings
            	commentListRegionForFeedbackResponse.show();
            	commentListRegionForFeedbackResponse.prev().show();
            	commentListRegionForFeedbackResponse.prev().prev().show();
            	commentListRegionForFeedbackResponse.prev().prev().prev().show();
            	
            	var feedbackQuestion = commentListRegionForFeedbackResponse.parent().parent().parent();
            	if(feedbackQuestion.find('tr[style*="display: none"]').length != feedbackQuestion.find('tr').length){
            		//if not all responses are hidden within fbQuestion, then show the fbQuestion
            		feedbackQuestion.show();
            		
            		var feedbackSessionPanel = feedbackQuestion.parent().parent().parent();
            		var feedbackSessionPanelBody = feedbackQuestion.parent();
            		if(feedbackSessionPanel.find('div[class="panel panel-info"][style*="display: none"]').length != feedbackSessionPanel.find('div[class="panel panel-info"]').length){
            			//if not all questions are hidden within fbSession, then show the fbsession's body
            			feedbackSessionPanelBody.show();
            		}
            	}
            }
    	}
        //to show student comments (only works for Giver filter)
        if ($(comment).prop("class").toString().includes(classNameForCommentsInStudentRecords)){
        	var studentCommentPanel = $(comment).parent().parent().parent();
        	var studentCommentPanelBody = $(comment).parent();
        	//if not all student comments are hidden, then show the student comments panel
        	if(studentCommentPanel.find('div[class*="giver_display-by"][style*="display: none"]').length != studentCommentPanel.find('div[class*="giver_display-by"]').length)
    		{
        		studentCommentPanelBody.show();
    		}
        }
    }
    
    function hideCommentAndItsPanel(comment){
    	$(comment).hide();
    	
    	//hide comment's add form in commentListRegionForFeedbackResponse
    	$("li[id^='showResponseCommentAddForm']").hide();
    	
    	//to hide feedback question + feedback session panel
    	//if all list elements are hidden within fbResponse, then hide fbResponse
    	if($(comment).prop("class").toString().includes(classNameForCommentsInFeedbackResponse)){
            if($(comment).parent().find('li[style*="display: none"]').length == $(comment).parent().find('li').length){
            	var commentListRegionForFeedbackResponse = $(comment).parent().parent().parent();
            	//a fbResponse in instructorCommentsPage (html) is made up of 4 rows as the followings
            	commentListRegionForFeedbackResponse.hide();
            	commentListRegionForFeedbackResponse.prev().hide();
            	commentListRegionForFeedbackResponse.prev().prev().hide();
            	commentListRegionForFeedbackResponse.prev().prev().prev().hide();
            	
            	var feedbackQuestion = commentListRegionForFeedbackResponse.parent().parent().parent();
            	if(feedbackQuestion.find('tr[style*="display: none"]').length == feedbackQuestion.find('tr').length){
            		//if all responses are hidden within fbQuestion, then hide the fbQuestion
            		feedbackQuestion.hide();
            		
            		var feedbackSessionPanel = feedbackQuestion.parent().parent().parent();
            		var feedbackSessionPanelBody = feedbackQuestion.parent();
            		if(feedbackSessionPanel.find('div[class="panel panel-info"][style*="display: none"]').length == feedbackSessionPanel.find('div[class="panel panel-info"]').length){
            			//if all questions are hidden within fbSession, then hide the fbsession's body
            			feedbackSessionPanelBody.hide();
            		}
            	}
            }
    	}
    	//to hide student comments
    	if ($(comment).prop("class").toString().includes(classNameForCommentsInStudentRecords)){
        	var studentCommentPanel = $(comment).parent().parent().parent();
        	var studentCommentPanelBody = $(comment).parent();
        	//if all student comments are hidden, then hide the student comments panel
        	if(studentCommentPanel.find('div[class*="giver_display-by"][style*="display: none"]').length == studentCommentPanel.find('div[class*="giver_display-by"]').length)
    		{
        		studentCommentPanelBody.hide();
    		}
        }
    }
	
	//Binding for "Display Archived Courses" check box.
    $("#displayArchivedCourses_check").change(function(){
        var urlToGo = $('#displayArchivedCourses_link > a').attr('href');
        if(this.checked){
            gotoUrlWithParam(urlToGo, "displayarchive", "true");
        } else{
            gotoUrlWithParam(urlToGo, "displayarchive", "false");
        }
    });
    
    /**
     * Go to the url with appended param and value pair
     */
    function gotoUrlWithParam(url, param, value){
        var paramValuePair = param + "=" + value;
        if(!url.includes("?")){
            window.location.href = url + "?" + paramValuePair;
        } else if(!url.includes(param)){
            window.location.href = url + "&" + paramValuePair;
        } else if(url.includes(paramValuePair)){
            window.location.href = url;
        } else{
            var urlWithoutParam = removeParamInUrl(url, param);
            gotoUrlWithParam(urlWithoutParam, param, value);
        }
    }

    /**
     * Remove param and its value pair in the given url
     * Return the url withour param and value pair
     */
    function removeParamInUrl(url, param){
        var indexOfParam = url.indexOf("?" + param);
        indexOfParam = indexOfParam == -1? url.indexOf("&" + param): indexOfParam;
        var indexOfAndSign = url.indexOf("&", indexOfParam + 1);
        var urlBeforeParam = url.substr(0, indexOfParam);
        var urlAfterParamValue = indexOfAndSign == -1? "": url.substr(indexOfAndSign);
        return urlBeforeParam + urlAfterParamValue;
    }
    
    $('a[id^="visibility-options-trigger"]').click(function(){
    	var visibilityOptions = $(this).parent().next();
		if(visibilityOptions.is(':visible')){
			visibilityOptions.hide();
			$(this).html('<span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options');
		} else {
			visibilityOptions.show();
			$(this).html('<span class="glyphicon glyphicon-eye-close"></span> Hide Visibility Options');
		}
	});
    
    $("input[type=checkbox]").click(function(e){
    	var table = $(this).parent().parent().parent().parent();
    	var form = table.parent().parent().parent();
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
    	
    	table.find('.answerCheckbox:checked').each(function () {
			visibilityOptions.push($(this).val());
	    });
    	form.find("input[name='showcommentsto']").val(visibilityOptions.toString());
	    
	    visibilityOptions = [];
	    table.find('.giverCheckbox:checked').each(function () {
			visibilityOptions.push($(this).val());
	    });
	    form.find("input[name='showgiverto']").val(visibilityOptions.toString());
	    
	    visibilityOptions = [];
	    table.find('.recipientCheckbox:checked').each(function () {
			visibilityOptions.push($(this).val());
	    });
	    form.find("input[name='showrecipientto']").val(visibilityOptions.toString());
    });
});

//public functions:

function showAddCommentBox(id){
    $('#comment_box_' + id).show();
    $('#commentText_' + id).focus();
}

function hideAddCommentBox(id){
    $('#comment_box_' + id).hide();
}

function submitCommentForm(commentIdx){
    $('#form_commentedit-'+commentIdx).submit();
    return false;
}

function deleteComment(commentIdx){
    if (confirm("Are you sure you want to delete this comment?")){
        document.getElementById('commentedittype-'+commentIdx).value="delete";
        return submitCommentForm(commentIdx);
    } else {
        return false;
    }
}

function enableEdit(commentIdx, unusedParameter) {
	enableComment(commentIdx);
    return false;
}

function enableComment(commentIdx){
	$('#'+'commentBar-'+commentIdx).hide();
	$('#'+'plainCommentText'+commentIdx).hide();
	$("div[id='commentTextEdit"+commentIdx+"']").show();
	$("textarea[id='commentText"+commentIdx+"']").val($("#plainCommentText"+commentIdx).text());
    $("textarea[id='commentText"+commentIdx+"']").focus();
}

function disableComment(commentIdx){
	$('#'+'commentBar-'+commentIdx).show();
	$('#'+'plainCommentText'+commentIdx).show();
	$("div[id='commentTextEdit"+commentIdx+"']").hide();
}

function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

function checkComment(form){
    var formTextField = $(form).find('[name=commenttext]').val();
    if (isBlank(formTextField)) {
        setStatusMessage("Please enter a valid comment. The comment can't be empty.", true);
        $(window).scrollTop(0);
        return false;
    }
}