$(document).ready(function(){
	//show on hover for comment
	commentToolBarAppearOnHover();
	
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
	
	//Binding for "Display All" giver option
	$('#giver_all').click(function(){
		//use giver_all checkbox to control its children checkboxes.
		if($('#giver_all').is(':checked')){
			$("input[id^=giver_check]").prop("checked", true);
		} else {
			$("input[id^=giver_check]").prop("checked", false);
		}
		
		filterGiver();
	});
	
	//Binding for changes in the giver checkboxes.
    $("input[id^=giver_check]").change(function(){
    	//based on the selected checkboxes, check/uncheck giver_all checkbox
    	if($("input[id^='giver_check']:checked").length == $("input[id^='giver_check']").length){
        	$("#giver_all").prop("checked", true);
    	} else{
        	$("#giver_all").prop("checked", false);
    	}
    	
    	filterGiver();
    });
    
    function filterGiver(){
		//hide the panel accordingly based on panel_check checkbox
	    $("input[id=giver_check-by-you]").each(function(){
	        if(this.checked){
	            $(".giver_display-by-you").each(function(){showCommentAndItsPanel(this);});
	        } else{
	            $(".giver_display-by-you").each(function(){hideCommentAndItsPanel(this);});
	        }
	    });
	    
	    $("input[id=giver_check-by-others]").each(function(){
	        if(this.checked){
	            $(".giver_display-by-others").each(function(){showCommentAndItsPanel(this);});
	        } else{
	            $(".giver_display-by-others").each(function(){hideCommentAndItsPanel(this);});
	        }
	    });
	}
    
    function showCommentAndItsPanel(comment){
    	var classNameForCommentsInFeedbackResponse = "list-group-item list-group-item-warning giver_display-by";
    	var classNameForCommentsInStudentRecords = "panel panel-info student-record-comments giver_display-by";
    	
    	$(comment).show();
    	
    	//to show feedback question + feedback session panel
    	//if not all list elements are hidden within fbQuestion, then show fbQuestion
    	if($(comment).prop("class").toString().contains(classNameForCommentsInFeedbackResponse)){
            if($(comment).parent().find('li[style*="display:none"]').length != $(comment).parent().find('li').length){
            	var commentListRegionForFeedbackResponse = $(comment).parent().parent().parent();
            	//a response in instructorCommentsPage (html) is made up of 4 rows as the followings
            	commentListRegionForFeedbackResponse.show();
            	commentListRegionForFeedbackResponse.prev().show();
            	commentListRegionForFeedbackResponse.prev().prev().show();
            	commentListRegionForFeedbackResponse.prev().prev().prev().show();
            	
            	var feedbackQuestion = commentListRegionForFeedbackResponse.parent().parent().parent();
            	if(feedbackQuestion.find('tr[style*="display:none"]').length != feedbackQuestion.find('tr').length){
            		feedbackQuestion.show();
            		
            		//if not all questions are hidden within fbSession, then show the fbsession
            		var feedbackSessionPanel = feedbackQuestion.parent().parent().parent();
            		if(feedbackSessionPanel.find('div[class="panel panel-info"][style*="display:none"]').length != feedbackSessionPanel.find('div[class="panel panel-info"]').length){
            			feedbackSessionPanel.show();
            		}
            	}
            }
    	}
        //to show student comments
        if ($(comment).prop("class").toString().contains(classNameForCommentsInStudentRecords)){
        	var studentCommentRowParent = $(comment).parent().parent().parent();
        	//if not all student comments are hidden, then show the student comments panel
        	if(studentCommentRowParent.find('div[class*="giver_display-by"][style*="display:none"]').length != studentCommentRowParent.find('div[class*="giver_display-by"]').length)
    		{
        		studentCommentRowParent.show();
    		}
        }
        //if not everything is hidden, hide no-commnet-panel
        if ($("div[id^='panel_display'][style*='display:none']").length != $("div[id^='panel_display']").length){
        	$('#no-comment-panel').hide();
        }
    }
    
    function hideCommentAndItsPanel(comment){
    	var classNameForCommentsInFeedbackResponse = "list-group-item list-group-item-warning giver_display-by";
    	var classNameForCommentsInStudentRecords = "panel panel-info student-record-comments giver_display-by";
    	
    	$(comment).hide();
    	//hide comment's add form in commentListRegionForFeedbackResponse
    	$("li[id^='showResponseCommentAddForm']").hide();
    	
    	//to hide feedback question + feedback session panel
    	//if all list elements are hidden within fbQuestion, then hide fbQuestion
    	if($(comment).prop("class").toString().contains(classNameForCommentsInFeedbackResponse)){
            if($(comment).parent().find('li:hidden').length == $(comment).parent().find('li').length){
            	var commentListRegionForFeedbackResponse = $(comment).parent().parent().parent();
            	//a response in instructorCommentsPage (html) is made up of 4 rows as the followings
            	commentListRegionForFeedbackResponse.hide();
            	commentListRegionForFeedbackResponse.prev().hide();
            	commentListRegionForFeedbackResponse.prev().prev().hide();
            	commentListRegionForFeedbackResponse.prev().prev().prev().hide();
            	
            	var feedbackQuestion = commentListRegionForFeedbackResponse.parent().parent().parent();
            	if(feedbackQuestion.find('tr:hidden').length == feedbackQuestion.find('tr').length){
            		feedbackQuestion.hide();
            		
            		//if all questions are hidden within fbSession, then hide the fbsession
            		var feedbackSessionPanel = feedbackQuestion.parent().parent().parent();
            		if(feedbackSessionPanel.find('div[class="panel panel-info"]:hidden').length == feedbackSessionPanel.find('div[class="panel panel-info"]').length){
            			feedbackSessionPanel.hide();
            		}
            	}
            }
    	}
    	//to hide student comments
    	if ($(comment).prop("class").toString().contains(classNameForCommentsInStudentRecords)){
        	var studentCommentRowParent = $(comment).parent().parent().parent();
        	//if all student comments are hidden, then hide the student comments panel
        	if(studentCommentRowParent.find('div[class*="giver_display-by"]:hidden').length == studentCommentRowParent.find('div[class*="giver_display-by"]').length)
    		{
        		studentCommentRowParent.hide();
    		}
        }
    	//if everything is hidden, display no-commnet-panel
    	if ($("div[id^=panel_display]:hidden").length == $("div[id^=panel_display]").length){
    		$('#no-comment-panel').show();
    	}
    }
	
	//Binding for changes in the panel checkboxes.
    $("input[id^=panel_check]").change(function(){
    	//based on the selected panel_check checkboxes, check/uncheck panel_all checkbox
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
    	} else {
    		$('#no-comment-panel').hide();
    	}
		
		//hide the panel accordingly based on panel_check checkbox
	    $("input[id^='panel_check']").each(function(){
	        var $courseIdx = $(this).attr("id").split('-')[1];
	        if(this.checked){
	            $("#panel_display-" + $courseIdx).show();
	        } else{
	            $("#panel_display-" + $courseIdx).hide();
	        }
	    });
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
        if(!url.contains("?")){
            window.location.href = url + "?" + paramValuePair;
        } else if(!url.contains(param)){
            window.location.href = url + "&" + paramValuePair;
        } else if(url.contains(paramValuePair)){
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
    
    /**
     * Check whether a string contains the substr or not
     */
    String.prototype.contains = function(substr) { return this.indexOf(substr) != -1; };
});

function commentToolBarAppearOnHover(){
	$('.comments > .list-group-item').hover(
			function(){
			$("a[type='button']", this).show();
		}, function(){
			$("a[type='button']", this).hide();
		});
}

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

function enableEdit(commentIdx){
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