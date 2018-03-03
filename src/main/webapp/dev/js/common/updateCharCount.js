/**
 * Updates the number of characters left in the text area
 * @param textArea - Text area for which char are to be counted
 * @param wordsCountId - Id of Label to display length of text area
 */
function updateCharLeftCount(textArea, letterCountId) {
    const letterCountArea = $(`#${letterCountId}`);
    const maxLength = textArea.attr('maxLength');
    const charLength = textArea.val().length;
    letterCountArea.text(maxLength - charLength);
}
/**
 * Create the div to display the number of characters left in textArea
 * @param textArea - Text area for which char are to be counted
 * @param wordsCountId - Id of Label to display length of text area
 */
function insertLetterCountArea(textArea, letterCountAreaId) {
    const maxLength = textArea.attr('maxlength');
    const letterCountAreaTemplate = `<div class="col-md-6 padding-0"> 
            <span id="${letterCountAreaId}">${maxLength}</span> characters left </div>`;

    $(letterCountAreaTemplate).insertAfter(textArea);
}

function updateCharLeft(textAreaId) {
    const feedbackSessionName = $(`#${textAreaId}`);
    const letterCountAreaId = `charLeft-${textAreaId}`;

    insertLetterCountArea(feedbackSessionName, letterCountAreaId);

    updateCharLeftCount(feedbackSessionName, letterCountAreaId);
    feedbackSessionName.on('keyup', () => updateCharLeftCount(feedbackSessionName, letterCountAreaId));
    feedbackSessionName.on('keydown', () => updateCharLeftCount(feedbackSessionName, letterCountAreaId));
}

export {
    updateCharLeft,
};
