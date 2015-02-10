
var DFDs = {};

var deferredIndex = 0;
var url;
var callbackFunction;

$(document).ready(function(){
	
	$(".navbar-fixed-top").css( "zIndex", 0);
	
	tinymce.init({
	    selector: "textarea",
	    theme: "modern",
	    plugins: [
			"advlist autolink lists link image charmap print preview hr anchor pagebreak",
			"searchreplace wordcount visualblocks visualchars code fullscreen",
			"insertdatetime media nonbreaking save table contextmenu directionality",
			"emoticons template paste textcolor colorpicker textpattern"
	    ],
	    
	    document_base_url : "",
	    
	    toolbar1: "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image",
	    toolbar2: "print preview media | forecolor backcolor emoticons",
	    
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
//             button.html("<img src='/images/ajax-loader.gif'/>");
        },
        error : function() {
//             setFormErrorMessage(olderButton, "Failed to load older logs. Please try again.");
//             button.html("Retry");           
        },
        success : function(data) {
            setTimeout(function(){
                if (!data.isError) {
                   if(data.isFileUploaded){
                	   url = data.fileSrcUrl;
                	   $("adminEmailFileForm").attr("action", data.nextUploadUrl);
                	   callbackFunction(url, {alt: 'My alt text'});
                   }
                   
                } else {
//                     setFormErrorMessage(button, data.errorMessage);
                }
                               
//                 $("#statusMessage").html(data.statusForAjax);

            },500);
        }
        
        
    });
};



