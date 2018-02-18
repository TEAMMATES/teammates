import {
    BootstrapContextualColors,
} from '../common/const';

import {
    makeCsrfTokenParam,
} from '../common/crypto';

import {
    scrollToTop,
} from '../common/scrollTo';

import {
    setStatusMessage,
} from '../common/statusMessage';

import {
    bindLinksInUnregisteredPage,
} from '../common/student';

function finaliseEditPictureForm() {
    const picture = $('#editableProfilePicture');
    const transformData = picture.guillotine('getData');
    const scaledWidth = picture.prop('naturalWidth') * transformData.scale;
    const scaledHeight = picture.prop('naturalHeight') * transformData.scale;

    $('#cropBoxLeftX').val(transformData.x);
    $('#cropBoxTopY').val(transformData.y);
    $('#cropBoxRightX').val(transformData.x + transformData.w);
    $('#cropBoxBottomY').val(transformData.y + transformData.h);
    $('#rotate').val(transformData.angle);
    $('#pictureWidth').val(scaledWidth);
    $('#pictureHeight').val(scaledHeight);
    $('#profilePictureEditForm').submit();
}

function finaliseUploadPictureForm() {
    if ($('#studentPhoto').val() === '') {
        return;
    }

    const initialSubmitMessage = $('#profileUploadPictureSubmit').html();
    $.ajax({
        url: `/page/studentProfileCreateFormUrl?${makeCsrfTokenParam()}&user=${$("input[name='user']").val()}`,
        beforeSend() {
            $('#profileUploadPictureSubmit').html('<img src="/images/ajax-loader.gif">');
        },
        error() {
            $('#profileUploadPictureSubmit').text(initialSubmitMessage);
            setStatusMessage('There seems to be a network error, please try again later', BootstrapContextualColors.DANGER);
            scrollToTop({ duration: '' });
        },
        success(data) {
            if (data.isError) {
                $('#profileUploadPictureSubmit').text(initialSubmitMessage);
                setStatusMessage('There seems to be a network error, please try again later',
                        BootstrapContextualColors.DANGER);
                scrollToTop({ duration: '' });
            } else {
                $('#profilePictureUploadForm').attr('enctype', 'multipart/form-data');
                // for IE compatibility
                $('#profilePictureUploadForm').attr('encoding', 'multipart/form-data');
                $('#profilePictureUploadForm').attr('action', data.formUrl);
                $('#profilePictureUploadForm').submit();
            }
        },
    });
}

$(document).ready(() => {
    bindLinksInUnregisteredPage('[data-unreg].navLinks');

    $('.form-control').on('click', function () {
        if ($(this).val() === $(this).attr('data-actual-value')) {
            $(this).select();
        }
    });

    $('#profileUploadPictureSubmit').on('click', () => {
        finaliseUploadPictureForm();
    });

    $('#profileEditPictureSubmit').on('click', () => {
        finaliseEditPictureForm();
    });

    $(window).load(() => {
        $('#studentPhoto').change(function () {
            const val = $(this).val();
            if (val === '') {
                $('#profileUploadPictureSubmit').prop('disabled', true);
                $('.filename-preview').val('No File Selected');
            } else {
                $('#profileUploadPictureSubmit').prop('disabled', false);
                const newVal = val.split('\\')
                        .pop()
                        .split('/')
                        .pop();
                $('.filename-preview').val(newVal);
            }
        });
        const picture = $('#editableProfilePicture');
        if (picture.length !== 0) {
            picture.guillotine({
                width: 150,
                height: 150,
            });
            picture.guillotine('fit');
            $('#profilePicEditRotateLeft').click(() => {
                picture.guillotine('rotateLeft');
            });
            $('#profilePicEditZoomIn').click(() => {
                picture.guillotine('zoomIn');
            });
            $('#profilePicEditZoomOut').click(() => {
                picture.guillotine('zoomOut');
            });
            $('#profilePicEditRotateRight').click(() => {
                picture.guillotine('rotateRight');
            });

            // Panning handlers based on approach outlined here
            // https://github.com/matiasgagliano/guillotine/issues/6#issuecomment-53178560
            //
            // It utilizes an internal method from the library (_offset)
            // to update the (top, left) offset values for the image.
            /* eslint-disable no-underscore-dangle */ // The method name is determined by external library (guillotine)
            $('#profilePicEditPanUp').click(() => {
                const data = picture.guillotine('getData');
                picture.guillotine('instance')._offset(data.x / data.w, (data.y - 10) / data.h);
            });
            $('#profilePicEditPanLeft').click(() => {
                const data = picture.guillotine('getData');
                picture.guillotine('instance')._offset((data.x - 10) / data.w, data.y / data.h);
            });
            $('#profilePicEditPanRight').click(() => {
                const data = picture.guillotine('getData');
                picture.guillotine('instance')._offset((data.x + 10) / data.w, data.y / data.h);
            });
            $('#profilePicEditPanDown').click(() => {
                const data = picture.guillotine('getData');
                picture.guillotine('instance')._offset(data.x / data.w, (data.y + 10) / data.h);
            });
            /* eslint-enable no-underscore-dangle */
            $('#pictureWidth').val(picture.prop('naturalWidth'));
            $('#pictureHeight').val(picture.prop('naturalHeight'));
            if ($('#profilePic').attr('data-edit') === 'true') {
                $('#studentPhotoUploader').modal({
                    show: true,
                });
            }
        }
    });
});
