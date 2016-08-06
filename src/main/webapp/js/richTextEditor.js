/* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
function RichTextEditor(params) {

    // ******************************************************************************************************
    // Private properties
    // ******************************************************************************************************

    var MANDATORY_INIT_PARAMS = ['selector'];
    var initParams = {};

    var MANDATORY_ADDITIONAL_PARAMS = ['uploadImageId', 'uploadImageInputName', 'createImageUploadUrl'];
    var additionalParams = {};

    // ******************************************************************************************************

    // ******************************************************************************************************
    // Private functions
    // ******************************************************************************************************

    var prepareInitParams = function() {
        checkMandatoryParams(MANDATORY_INIT_PARAMS, initParams);
    };

    var prepareAdditionalParams = function() {
        if (!additionalParams.uploadImageId) {
            additionalParams.uploadImageId = 'uploadImage';
        }
        if (!additionalParams.uploadImageInputName) {
            additionalParams.uploadImageInputName = 'imagetoupload';
        }
        if (!additionalParams.createImageUploadUrl) {
            additionalParams.createImageUploadUrl = '/page/createImageUploadUrl';
        }

        checkMandatoryParams(MANDATORY_ADDITIONAL_PARAMS, additionalParams);
    };

    var checkMandatoryParams = function(mandatoryParamsList, params) {
        $.each(mandatoryParamsList, function(i, key) {
            if (typeof params[key] === 'undefined') {
                throw Error('Parameter ' + key + ' is mandatory');
            }
        });
    };

    var getDefaultConfiguration = function() {
        return {
            theme: 'modern',
            fontsize_formats: '8pt 9pt 10pt 11pt 12pt 14pt 16pt 18pt 20pt 24pt 26pt 28pt 36pt 48pt 72pt',
            font_formats: 'Andale Mono=andale mono,times;'
                          + 'Arial=arial,helvetica,sans-serif;'
                          + 'Arial Black=arial black,avant garde;'
                          + 'Book Antiqua=book antiqua,palatino;'
                          + 'Comic Sans MS=comic sans ms,sans-serif;'
                          + 'Courier New=courier new,courier;'
                          + 'Georgia=georgia,palatino;'
                          + 'Helvetica=helvetica;'
                          + 'Impact=impact,chicago;'
                          + 'Symbol=symbol;'
                          + 'Tahoma=tahoma,arial,helvetica,sans-serif;'
                          + 'Terminal=terminal,monaco;'
                          + 'Times New Roman=times new roman,times;'
                          + 'Trebuchet MS=trebuchet ms,geneva;'
                          + 'Verdana=verdana,geneva;'
                          + 'Webdings=webdings;'
                          + 'Wingdings=wingdings,zapf dingbats',
                
            relative_urls: false,
            convert_urls: false,
            remove_linebreaks: false,
            file_browser_callback_types: 'file image media',
            file_picker_callback: function(callback, value, meta) {
                if (meta.filetype === 'image') {
                    $('#' + additionalParams.uploadImageId).click();
                    callbackFunction = callback;
                }
            },
            plugins: [
                'advlist autolink lists link image charmap print preview hr anchor pagebreak',
                'searchreplace wordcount visualblocks visualchars code fullscreen',
                'insertdatetime nonbreaking save table contextmenu directionality',
                'emoticons template paste textcolor colorpicker textpattern'
            ],

            toolbar1: 'insertfile undo redo | styleselect | bold italic underline | '
                    + 'alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image',
            toolbar2: 'print preview | forecolor backcolor | fontsizeselect fontselect | emoticons | fullscreen',

            init_instance_callback: 'initEditorCallback'

        };
    };

    var createImageUploadUrl = function() {
        $.ajax({
            type: 'POST',
            url: additionalParams.createImageUploadUrl,
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
                        $('#' + additionalParams.uploadImageId + 'Form').attr('action', data.nextUploadUrl);
                        setStatusMessage(data.ajaxStatus);
                        submitImageUploadFormAjax();
                    }
                }, 500);
            }
        });
    };

    var submitImageUploadFormAjax = function() {
        var uploadImageId = additionalParams.uploadImageId;
        var formData = new FormData($('#' + uploadImageId + 'Form')[0]);

        $.ajax({
            type: 'POST',
            enctype: 'multipart/form-data',
            url: $('#' + uploadImageId + 'Form').attr('action'),
            data: formData,
            // Options to tell jQuery not to process data or worry about content-type.
            cache: false,
            contentType: false,
            processData: false,

            beforeSend: function() {
                showUploadingGif();
            },
            error: function() {
                setErrorMessage('Image upload failed, please try again.');
                clearUploadFileInfo();
            },
            success: function(data) {
                setTimeout(function() {
                    if (data.isError) {
                        setErrorMessage(data.ajaxStatus);
                    } else if (data.isFileUploaded) {
                        url = location.origin + data.fileSrcUrl;
                        callbackFunction(url, { alt: 'PLACEHOLDER_IMAGE_UPLOAD_ALT_TEXT' });
                        setStatusMessage(data.ajaxStatus, StatusType.SUCCESS);
                    } else {
                        setErrorMessage(data.ajaxStatus);
                    }
                }, 500);
                
            }

        });
        clearUploadFileInfo();
    };

    var injectImageUploadForm = function() {
        var uploadImageId = additionalParams.uploadImageId;
        var uploadImageInputName = additionalParams.uploadImageInputName;

        if ($('#' + uploadImageId + 'Form').length) {
            return;
        }

        $('body').append($('<div id="uploadFileBlock" style="display: none;"></div>'));
        $('#uploadFileBlock').append(
                '<form id="' + uploadImageId + 'Form" action="" method="POST" enctype="multipart/form-data">');
        $('#' + uploadImageId + 'Form').append('<span id="' + uploadImageId + 'Input" />');
        $('#' + uploadImageId + 'Input').append(
                '<input type="file" id="' + uploadImageId + '" name="' + uploadImageInputName + '" />');

        $('#' + uploadImageId).on('change paste keyup', function() {
            createImageUploadUrl();
        });
    };

    var showUploadingGif = function() {
        setStatusMessage("Uploading...<span><img src='/images/ajax-loader.gif'/></span>", StatusType.WARNING);
    };

    var setErrorMessage = function(message) {
        setStatusMessage(message, StatusType.DANGER);
    };

    var clearUploadFileInfo = function() {
        var uploadImageId = additionalParams.uploadImageId;
        var uploadImageInputName = additionalParams.uploadImageInputName;

        $('#' + uploadImageId + 'Input').html(
                '<input type="file" name="' + uploadImageInputName + '" id="' + uploadImageId + '">');

        $('#' + uploadImageId).on('change paste keyup', function() {
            createImageUploadUrl();
        });
    };

    // ******************************************************************************************************

    this.init = function() {
        tinymce.init($.extend(getDefaultConfiguration(), initParams));
        injectImageUploadForm();
    };

    if (typeof tinymce === 'undefined') {
        throw Error('TinyMCE library is not included');
    }

    initParams = params.initParams || {};
    additionalParams = params.additionalParams || {};

    prepareInitParams();
    prepareAdditionalParams();
}
/* eslint-enable camelcase */

function setPlaceholderText(editor) {
    if (editor.getContent() === '') {
        tinymce.DOM.addClass(editor.bodyElement, 'empty');
    } else {
        tinymce.DOM.removeClass(editor.bodyElement, 'empty');
    }
}

function initEditorCallback(editor) {
    tinymce.DOM.addClass(editor.bodyElement, 'content-editor');
    setPlaceholderText(editor);

    editor.on('selectionchange', function() {
        setPlaceholderText(editor);
    });
}

/**
 * Destroys an instance of TinyMCE rich-text editor.
 */
function destroyEditor(id) {
    if (typeof tinyMCE === 'undefined') {
        return;
    }
    var currentEditor = tinyMCE.get(id);
    if (currentEditor) {
        currentEditor.destroy();
    }
}
