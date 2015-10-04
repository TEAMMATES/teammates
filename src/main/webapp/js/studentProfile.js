$(function() {
    $('.form-control').on('click', function() {
        if ($(this).val() == $(this).attr('data-actual-value')) {
            $(this).select();
        }
    });
    
    $(window).load(function() {
        var picture = $('#editableProfilePicture');
        if (picture.length !== 0) {
            picture.guillotine({
                width: 150,
                height: 150,
                init: {
                    scale: 0.1
                }
            });
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
