// Form input placeholders
var PLACEHOLDER_IMAGE_UPLOAD_ALT_TEXT = 'Please enter an alt text for the image';

var callbackFunction;

$(document).ready(function() {
    
    $('.navbar-fixed-top').css('zIndex', 0);

    var richTextEditor = new RichTextEditor({
        initParams: {
            selector: 'textarea'
        },
        additionalParams: {
            uploadImageId: 'adminEmailFile',
            uploadImageInputName: 'emailimagetoupload',
            createImageUploadUrl: '/admin/adminEmailCreateImageUploadUrl'
        }
    });
    richTextEditor.init();

    $('#adminEmailGroupReceiverList').on('change paste keyup', function() {
        createGroupReceiverListUploadUrl();
    });
    
    $('#adminEmailGroupReceiverListUploadButton').on('click', function() {
        $('#adminEmailGroupReceiverList').click();
    });
    
    $('#composeSaveButton').on('click', function() {
        $('#adminEmailMainForm').attr('action', '/admin/adminEmailComposeSave');
        $('#composeSubmitButton').click();
    });
    
    $('#addressReceiverEmails').on('change keyup', function(e) {
        if (e.which === 13) {
            $('#addressReceiverEmails').val($('#addressReceiverEmails').val() + ',');
        }
    });
    
    toggleSort($('#button_sort_date').parent());
});

function createGroupReceiverListUploadUrl() {
    
    $.ajax({
        type: 'POST',
        url: '/admin/adminEmailCreateGroupReceiverListUploadUrl',
        beforeSend: function() {
            showUploadingGif();
        },
        error: function() {
            setErrorMessage('URL request failured, please try again.');
        },
        success: function(data) {
            setTimeout(function() {
                if (data.isError) {
                    setErrorMessage(data.ajaxStatus);
                } else {
                    $('#adminEmailReceiverListForm').attr('action', data.nextUploadUrl);
                    setStatusMessage(data.ajaxStatus);
                    submitGroupReceiverListUploadFormAjax();
                }
            }, 500);
        }
    });
}

function submitGroupReceiverListUploadFormAjax() {
    var formData = new FormData($('#adminEmailReceiverListForm')[0]);
    
    $.ajax({
        type: 'POST',
        enctype: 'multipart/form-data',
        url: $('#adminEmailReceiverListForm').attr('action'),
        data: formData,
        // Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false,
          
        beforeSend: function() {
            showUploadingGif();
        },
        error: function() {
            setErrorMessage('Group receiver list upload failed, please try again.');
            clearUploadGroupReceiverListInfo();
        },
        success: function(data) {
            setTimeout(function() {
                if (data.isError) {
                    setErrorMessage(data.ajaxStatus);
                } else if (data.isFileUploaded) {
                    setStatusMessage(data.ajaxStatus, StatusType.SUCCESS);
                    $('#groupReceiverListFileKey').val(data.groupReceiverListFileKey);
                    $('#groupReceiverListFileKey').show();
                    $('#groupReceiverListFileSize').val(data.groupReceiverListFileSize);
                } else {
                    setErrorMessage(data.ajaxStatus);
                }
            }, 500);
        }
        
    });
    clearUploadGroupReceiverListInfo();
}

function setErrorMessage(message) {
    setStatusMessage(message, StatusType.DANGER);
}

function showUploadingGif() {
    setStatusMessage("Uploading...<span><img src='/images/ajax-loader.gif'/></span>", StatusType.WARNING);
}

function clearUploadGroupReceiverListInfo() {
    $('#adminEmailGroupReceiverListInput').html('<input type="file" name="emailgroupreceiverlisttoupload" '
                                                     + 'id="adminEmailGroupReceiverList">');
    $('#adminEmailGroupReceiverList').on('change paste keyup', function() {
        createGroupReceiverListUploadUrl();
    });
}
