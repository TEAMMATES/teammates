$(function() {
    $('.form-control').on('click', function() {
        if ($(this).val() == $(this).attr('data-actual-value')) {
            $(this).select();
        }
    });
    
    $(window).load(function() {
        $('#studentPhoto').change(function() {
            if ($(this).val() === "") {
                $('#profileUploadPictureSubmit').prop('disabled', true);
                $('.filename-preview').val('No File Selected');
            } else {
                $('#profileUploadPictureSubmit').prop('disabled', false);
                $('.filename-preview').val($(this).val().split('\\').pop().split('/').pop());
            }
        });
        var picture = $('#editableProfilePicture');
        if (picture.length !== 0) {
            picture.guillotine({
                width: 150,
                height: 150
            });
            picture.guillotine('fit');
            $('#profilePicEditRotateLeft').click(function() {
                 picture.guillotine('rotateLeft');
            });
            $('#profilePicEditZoomIn').click(function() {
                 picture.guillotine('zoomIn');
            });
            $('#profilePicEditZoomOut').click(function() {
                 picture.guillotine('zoomOut');
            });
            $('#profilePicEditRotateRight').click(function() {
                 picture.guillotine('rotateRight');
            });

            // Panning handlers based on approach outlined here
            // https://github.com/matiasgagliano/guillotine/issues/6#issuecomment-53178560
            //
            // It utilizes an internal method from the library (_offset)
            // to update the (top, left) offset values for the image.
            $('#profilePicEditPanUp').click(function() {
                var data = picture.guillotine('getData');
                picture.guillotine('instance')._offset(data.x / data.w, (data.y - 10) / data.h);
            });
            $('#profilePicEditPanLeft').click(function() {
                var data = picture.guillotine('getData');
                picture.guillotine('instance')._offset((data.x - 10) / data.w, data.y / data.h);
            });
            $('#profilePicEditPanRight').click(function() {
                var data = picture.guillotine('getData');
                picture.guillotine('instance')._offset((data.x + 10) / data.w, data.y / data.h);
            });
            $('#profilePicEditPanDown').click(function() {
                var data = picture.guillotine('getData');
                picture.guillotine('instance')._offset(data.x / data.w, (data.y + 10) / data.h);
            });
            $('#pictureWidth').val(picture.prop('naturalWidth'));
            $('#pictureHeight').val(picture.prop('naturalHeight'));
            if ($('#profilePic').attr('data-edit') == "true") {
                $('#studentPhotoUploader').modal({
                    show: true
                });
            }
        }
    });
});

function finaliseEditPictureForm(event) {
    var picture = $('#editableProfilePicture'),
        transformData = picture.guillotine('getData'),
        scaledWidth = picture.prop('naturalWidth') * transformData.scale,
        scaledHeight = picture.prop('naturalHeight') * transformData.scale;

    $('#cropBoxLeftX').val(transformData.x);
    $('#cropBoxTopY').val(transformData.y);
    $('#cropBoxRightX').val(transformData.x + transformData.w);
    $('#cropBoxBottomY').val(transformData.y + transformData.h);
    $('#rotate').val(transformData.angle);
    $('#pictureWidth').val(scaledWidth);
    $('#pictureHeight').val(scaledHeight);
    $('#profilePictureEditForm').submit();
}

function finaliseUploadPictureForm(event) {
    if ($('#studentPhoto').val() === "") {
        return;
    }

    initialSubmitMessage = $('#profileUploadPictureSubmit').html();
    $.ajax({
        url: "/page/studentProfileCreateFormUrl?user=" + $("input[name='user']").val(),
        beforeSend : function() {
            $('#profileUploadPictureSubmit').html("<img src='../images/ajax-loader.gif'/>");
        },
        error: function() {
            $('#profileUploadPictureSubmit').Text(initialSubmitMessage);
            $('#statusMessage').css("display", "block")
                    .attr('class', 'alert alert-danger')
                    .html('There seems to be a network error, please try again later');
            scrollToTop({duration: ''});
        },
        success: function(data) {
            if (!data.isError) {
                $('#profilePictureUploadForm').attr('enctype','multipart/form-data');
                // for IE compatibility
                $('#profilePictureUploadForm').attr('encoding','multipart/form-data');
                $('#profilePictureUploadForm').attr('action', data.formUrl);
                $('#profilePictureUploadForm').submit();
            } else {
                $('#profileUploadPictureSubmit').Text(initialSubmitMessage);
                $('#statusMessage').css("display", "block")
                        .attr('class', 'alert alert-danger')
                        .html('There seems to be a network error, please try again later');
                scrollToTop({duration: ''});
            }
        }
    });
}
