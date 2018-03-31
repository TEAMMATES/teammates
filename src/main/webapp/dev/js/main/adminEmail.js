import {
    BootstrapContextualColors,
} from '../common/const';

import {
    makeCsrfTokenParam,
} from '../common/crypto';

import {
    richTextEditorBuilder,
} from '../common/richTextEditor';

import {
    toggleSort,
} from '../common/sortBy';

import {
    setStatusMessage,
} from '../common/statusMessage';

// Form input placeholders
const PLACEHOLDER_IMAGE_UPLOAD_ALT_TEXT = 'Please enter an alt text for the image';

let callbackFunction;

function setErrorMessage(message) {
    setStatusMessage(message, BootstrapContextualColors.DANGER);
}

function showUploadingGif() {
    setStatusMessage("Uploading...<span><img src='/images/ajax-loader.gif'/></span>", BootstrapContextualColors.WARNING);
}

function createGroupReceiverListUploadUrl() {
    $.ajax({
        type: 'POST',
        url: `/admin/adminEmailCreateGroupReceiverListUploadUrl?${makeCsrfTokenParam()}`,
        beforeSend() {
            showUploadingGif();
        },
        error() {
            setErrorMessage('URL request failured, please try again.');
        },
        success(data) {
            setTimeout(() => {
                if (data.isError) {
                    setErrorMessage(data.ajaxStatus);
                } else {
                    $('#adminEmailReceiverListForm').attr('action', data.nextUploadUrl);
                    setStatusMessage(data.ajaxStatus);
                    submitGroupReceiverListUploadFormAjax(); // eslint-disable-line no-use-before-define
                }
            }, 500);
        },
    });
}

function clearUploadGroupReceiverListInfo() {
    $('#adminEmailGroupReceiverListInput').html('<input type="file" name="emailgroupreceiverlisttoupload" '
                                                     + 'id="adminEmailGroupReceiverList">');
    $('#adminEmailGroupReceiverList').on('change paste keyup', () => {
        createGroupReceiverListUploadUrl();
    });
}

function submitGroupReceiverListUploadFormAjax() {
    const formData = new FormData($('#adminEmailReceiverListForm')[0]);

    $.ajax({
        type: 'POST',
        enctype: 'multipart/form-data',
        url: $('#adminEmailReceiverListForm').attr('action'),
        data: formData,
        // Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false,

        beforeSend() {
            showUploadingGif();
        },
        error() {
            setErrorMessage('Group receiver list upload failed, please try again.');
            clearUploadGroupReceiverListInfo();
        },
        success(data) {
            setTimeout(() => {
                if (data.isError) {
                    setErrorMessage(data.ajaxStatus);
                } else if (data.isFileUploaded) {
                    setStatusMessage(data.ajaxStatus, BootstrapContextualColors.SUCCESS);
                    $('#groupReceiverListFileKey').val(data.groupReceiverListFileKey);
                    $('#groupReceiverListFileKey').show();
                    $('#groupReceiverListFileSize').val(data.groupReceiverListFileSize);
                } else {
                    setErrorMessage(data.ajaxStatus);
                }
            }, 500);
        },

    });
    clearUploadGroupReceiverListInfo();
}

function clearUploadFileInfo() {
    $('#adminEmailFileInput').html('<input type="file" name="emailimagetoupload" id="adminEmailFile">');
    $('#adminEmailFile').on('change paste keyup', () => {
        createImageUploadUrl(); // eslint-disable-line no-use-before-define
    });
}

function submitImageUploadFormAjax() {
    const formData = new FormData($('#adminEmailFileForm')[0]);

    $.ajax({
        type: 'POST',
        enctype: 'multipart/form-data',
        url: $('#adminEmailFileForm').attr('action'),
        data: formData,
        // Options to tell jQuery not to process data or worry about content-type.
        cache: false,
        contentType: false,
        processData: false,

        beforeSend() {
            showUploadingGif();
        },
        error() {
            setErrorMessage('Image upload failed, please try again.');
            clearUploadFileInfo();
        },
        success(data) {
            setTimeout(() => {
                if (data.isError) {
                    setErrorMessage(data.ajaxStatus);
                } else if (data.isFileUploaded) {
                    const url = data.fileSrcUrl;
                    callbackFunction(url, { alt: PLACEHOLDER_IMAGE_UPLOAD_ALT_TEXT });
                    setStatusMessage(data.ajaxStatus, BootstrapContextualColors.SUCCESS);
                } else {
                    setErrorMessage(data.ajaxStatus);
                }
            }, 500);
        },

    });
    clearUploadFileInfo();
}

function createImageUploadUrl() {
    $.ajax({
        type: 'POST',
        url: `/admin/adminEmailCreateImageUploadUrl?${makeCsrfTokenParam()}`,
        beforeSend() {
            showUploadingGif();
        },
        error() {
            setErrorMessage('URL request failured, please try again.');
        },
        success(data) {
            setTimeout(() => {
                if (data.isError) {
                    setErrorMessage(data.ajaxStatus);
                } else {
                    $('#adminEmailFileForm').attr('action', data.nextUploadUrl);
                    setStatusMessage(data.ajaxStatus);
                    submitImageUploadFormAjax();
                }
            }, 500);
        },

    });
}

$(document).ready(() => {
    /* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
    richTextEditorBuilder.initEditor('textarea', {
        document_base_url: $('#documentBaseUrl').text(),
        file_picker_callback(callback, value, meta) {
            // Provide image and alt text for the image dialog
            if (meta.filetype === 'image') {
                $('#adminEmailFile').click();
                callbackFunction = callback;
            }
        },
    });
    /* eslint-enable camelcase */

    $('#adminEmailFile').on('change paste keyup', () => {
        createImageUploadUrl();
    });

    $('#adminEmailGroupReceiverList').on('change paste keyup', () => {
        createGroupReceiverListUploadUrl();
    });

    $('#adminEmailGroupReceiverListUploadButton').on('click', () => {
        $('#adminEmailGroupReceiverList').click();
    });

    $('#composeSubmitButton').on('click', () => {
        $('#adminEmailMainForm').submit();
    });

    $('#composeSaveButton').on('click', () => {
        $('#adminEmailMainForm').attr('action', `/admin/adminEmailComposeSave?${makeCsrfTokenParam()}`);
        $('#composeSubmitButton').click();
    });

    $('#addressReceiverEmails').on('change keyup', (e) => {
        if (e.which === 13) {
            $('#addressReceiverEmails').val(`${$('#addressReceiverEmails').val()},`);
        }
    });

    toggleSort($('#button_sort_date').parent());
});
