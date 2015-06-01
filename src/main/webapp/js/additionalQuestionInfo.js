// Toggle the visibility of additional question information for the specified question.
function toggleAdditionalQuestionInfo(identifier) {
    var $questionButton = $('#questionAdditionalInfoButton-' + identifier);

    if ($questionButton.text() == $questionButton.attr('data-more')) {
        $questionButton.text($questionButton.attr('data-less'));
    } else {
        $questionButton.text($questionButton.attr('data-more'));
    }

    $('#questionAdditionalInfo-' + identifier).toggle();
}
