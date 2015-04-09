var callbackFunction;


$(document).ready(function(){
	
	$(".navbar-fixed-top").css( "zIndex", 0);
	
	tinymce.init({
	    selector: "textarea",
	    theme: "modern",
	    fontsize_formats: "8pt 9pt 10pt 11pt 12pt 14pt 16pt 18pt 20pt 24pt 26pt 28pt 36pt 48pt 72pt",
	    font_formats: "Andale Mono=andale mono,times;"+
        "Arial=arial,helvetica,sans-serif;"+
        "Arial Black=arial black,avant garde;"+
        "Book Antiqua=book antiqua,palatino;"+
        "Comic Sans MS=comic sans ms,sans-serif;"+
        "Courier New=courier new,courier;"+
        "Georgia=georgia,palatino;"+
        "Helvetica=helvetica;"+
        "Impact=impact,chicago;"+
        "Symbol=symbol;"+
        "Tahoma=tahoma,arial,helvetica,sans-serif;"+
        "Terminal=terminal,monaco;"+
        "Times New Roman=times new roman,times;"+
        "Trebuchet MS=trebuchet ms,geneva;"+
        "Verdana=verdana,geneva;"+
        "Webdings=webdings;"+
        "Wingdings=wingdings,zapf dingbats",
	    
	    
	    document_base_url : $("#documentBaseUrl").text(),
	    relative_urls: false,
	    convert_urls: false,
	    plugins: [
			"advlist autolink lists link image charmap print preview hr anchor pagebreak",
			"searchreplace wordcount visualblocks visualchars code fullscreen",
			"insertdatetime nonbreaking save table contextmenu directionality",
			"emoticons template paste textcolor colorpicker textpattern"
	    ],
	    
	    
	   	    
	    toolbar1: "insertfile undo redo | styleselect | bold italic underline | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image",
	    toolbar2: "print preview | forecolor backcolor | fontsizeselect fontselect | emoticons | fullscreen",
	    
	    file_picker_callback: function(callback, value, meta) {

	        // Provide image and alt text for the image dialog
	        if (meta.filetype == 'image') {
	        	$("#adminEmailFile").click();
	        	callbackFunction = callback;
	        }
	    }
	    
	});
	
	
	$("#adminEmailFile").on("change paste keyup", function() {
		createImageUploadUrl();		
	 });
	
	$("#adminEmailGroupReceiverList").on("change paste keyup", function() {
		createGroupReceiverListUploadUrl();		
	 });
	
	$("#adminEmailGroupReceiverListUploadButton").on("click", function(){
		$("#adminEmailGroupReceiverList").click();
	});
	
	$("#composeSaveButton").on("click", function(){
		$("#adminEmailMainForm").attr("action", "/admin/adminEmailComposeSave");
		$("#composeSubmitButton").click();
	});
	
	$("#addressReceiverEmails").on("change keyup", function (e) {
		  if (e.which == 13) {
			  $("#addressReceiverEmails").val($("#addressReceiverEmails").val() + ",");
		  }
		});
	
	toggleSort($("#button_sort_date").parent(), 5);
});



function createGroupReceiverListUploadUrl(){
	
	$.ajax({
        type : 'POST',
        url :  "/admin/adminEmailCreateGroupReceiverListUploadUrl",
        beforeSend : function() {
        	showUploadingGif();
        },
        error : function() {
        	setErrorMessage("URL request failured, please try again.");
        },
        success : function(data) {
            setTimeout(function(){
                if (!data.isError) {                   
            	    $("#adminEmailReceiverListForm").attr("action", data.nextUploadUrl);          		
            	    setStatusMessage(data.ajaxStatus);   
            	    submitGroupReceiverListUploadFormAjax();
            	    
                } else {
                	setErrorMessage(data.ajaxStatus);
                }
                               

            },500);

        }
        
        
    });
}

function submitGroupReceiverListUploadFormAjax() {
    var formData = new FormData($("#adminEmailReceiverListForm")[0]);
    
    $.ajax({
        type : 'POST',
        enctype :"multipart/form-data",
        url :   $("#adminEmailReceiverListForm").attr("action"),
        data: formData,
        //Options to tell jQuery not to process data or worry about content-type.
          cache: false,
          contentType: false,
          processData: false,
          
        beforeSend : function() {
        	showUploadingGif();
        },
        error : function() {
        	setErrorMessage("Group receiver list upload failed, please try again.");
        	clearUploadGroupReceiverListInfo();
        },
        success : function(data) {
            setTimeout(function(){
                if (!data.isError) {
                   if(data.isFileUploaded){
                	   setStatusMessage(data.ajaxStatus);
                	   $("#groupReceiverListFileKey").val(data.groupReceiverListFileKey);  
                	   $("#groupReceiverListFileKey").show();
                	   $("#groupReceiverListFileSize").val(data.groupReceiverListFileSize);
                   } else {
                   	   setErrorMessage(data.ajaxStatus);
                   }
                   
                } else {
                	setErrorMessage(data.ajaxStatus);
                }
                               

            },500);
            
            
        }
        
        
    });
    clearUploadGroupReceiverListInfo();
};



function createImageUploadUrl(){
	
	$.ajax({
        type : 'POST',
        url :  "/admin/adminEmailCreateImageUploadUrl",
        beforeSend : function() {
        	showUploadingGif();
        },
        error : function() {
        	setErrorMessage("URL request failured, please try again.");
        },
        success : function(data) {
            setTimeout(function(){
                if (!data.isError) {                   
            	    $("#adminEmailFileForm").attr("action", data.nextUploadUrl);          		
            	    setStatusMessage(data.ajaxStatus);   
            	    submitImageUploadFormAjax();
            	    
                } else {
                	setErrorMessage(data.ajaxStatus);
                }
                               

            },500);

        }
        
        
    });
}

function submitImageUploadFormAjax() {
    var formData = new FormData($("#adminEmailFileForm")[0]);
    
    $.ajax({
        type : 'POST',
        enctype :"multipart/form-data",
        url :   $("#adminEmailFileForm").attr("action"),
        data: formData,
        //Options to tell jQuery not to process data or worry about content-type.
          cache: false,
          contentType: false,
          processData: false,
          
        beforeSend : function() {
        	showUploadingGif();
        },
        error : function() {
        	setErrorMessage("Image upload failed, please try again.");
        	clearUploadFileInfo();
        },
        success : function(data) {
            setTimeout(function(){
                if (!data.isError) {
                   if(data.isFileUploaded){
                	   url = data.fileSrcUrl;
                	   callbackFunction(url, {alt: 'My alt text'});
                	   setStatusMessage(data.ajaxStatus);
                   } else {
                   	   setErrorMessage(data.ajaxStatus);
                   }
                   
                } else {
                	setErrorMessage(data.ajaxStatus);
                }
                               

            },500);
            
            
        }
        
        
    });
    clearUploadFileInfo();
};




function setErrorMessage(error){
	$("#statusMessage").html(error);
	$("#statusMessage").attr("class", "alert alert-danger");
	$("#statusMessage").show();
}

function setStatusMessage(msg){
	$("#statusMessage").html(msg);
	$("#statusMessage").attr("class", "alert alert-warning");
	$("#statusMessage").show();
}

function showUploadingGif(){
	$("#statusMessage").html("Uploading...<span><img src='/images/ajax-loader.gif'/></span>");
	$("#statusMessage").attr("class", "alert alert-warning");
	$("#statusMessage").show();
}

function clearUploadFileInfo(){
	$("#adminEmailFileInput").html("<input type=\"file\" name=\"emailimagetoupload\" id=\"adminEmailFile\">");
	$("#adminEmailFile").on("change paste keyup", function() {
		createImageUploadUrl();
	 });
}

function clearUploadGroupReceiverListInfo(){
	$("#adminEmailGroupReceiverListInput").html("<input type=\"file\" name=\"emailgroupreceiverlisttoupload\" id=\"adminEmailGroupReceiverList\">");
	$("#adminEmailGroupReceiverList").on("change paste keyup", function() {
		createGroupReceiverListUploadUrl();
	 });
}





