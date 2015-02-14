
var DFDs = {};

var deferredIndex = 0;
var url;
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
			"insertdatetime media nonbreaking save table contextmenu directionality",
			"emoticons template paste textcolor colorpicker textpattern"
	    ],
	    
	    
	   	    
	    toolbar1: "insertfile undo redo | styleselect | bold italic underline | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image",
	    toolbar2: "print preview media | forecolor backcolor | fontsizeselect fontselect | emoticons | fullscreen",
	    
	    file_picker_callback: function(callback, value, meta) {
	        // Provide file and text for the link dialog
//	        if (meta.filetype == 'file') {
//	        	
//	            //callback('mypage.html', {text: 'My text'});
//	            $("#adminEmailFile").click();
//	            callbackFunction = callback;
//	        }

	        // Provide image and alt text for the image dialog
	        if (meta.filetype == 'image') {
        	
	        	$("#adminEmailFile").click();
	        	callbackFunction = callback;
	        }

	        // Provide alternative source and posted for the media dialog
	        if (meta.filetype == 'media') {
	            callback('movie.mp4', {source2: 'alt.ogg', poster: 'image.jpg'});
	        }
	    }
	    
	});
	
	
	$("#adminEmailFile").on("change paste keyup", function() {
		submitFormAjax();
		clearUploadFileInfo();
		
	 });
	
	
	
});


function submitFormAjax() {
    var formData = new FormData($("#adminEmailFileForm")[0]);
    
    $.ajax({
        type : 'POST',
        enctype :"multipart/form-data",
        url :   $("#adminEmailFileForm").attr("action"),
//         xhr: function() {  // Custom XMLHttpRequest
//             var myXhr = $.ajaxSettings.xhr();
//             return myXhr;
//         },
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
        	
        },
        success : function(data) {
            setTimeout(function(){
                if (!data.isError) {
                   if(data.isFileUploaded){
                	   url = data.fileSrcUrl;
                	   $("#adminEmailFileForm").attr("action", data.nextUploadUrl);
                	   callbackFunction(url, {alt: 'My alt text'});
                	   $("#statusMessage").html(data.ajaxStatus);
                   }
                   
                } else {
                	setErrorMessage(data.ajaxStatus);
                }
                               

            },500);
        }
        
        
    });
};

function setErrorMessage(error){
	$("#statusMessage").html("Image upload failed, please try again.");
	$("#statusMessage").attr("class", "alert alert-danger");
	$("#statusMessage").show();
}

function showUploadingGif(){
	$("#statusMessage").html("Uploading...<span><img src='/images/ajax-loader.gif'/></span>");
	$("#statusMessage").attr("class", "alert alert-warning");
	$("#statusMessage").show();
}

function clearUploadFileInfo(){
	var originalHtml = $("#adminEmailFileInput").html();
	$("#adminEmailFileInput").html(originalHtml);
	$("#adminEmailFile").on("change paste keyup", function() {
		submitFormAjax();
		clearUploadFileInfo();
		
	 });
}

