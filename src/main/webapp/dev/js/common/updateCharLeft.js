/**
 * Updates the number of characters left in the text area
 * @param textArea - Text area for which the number of characters are to be counted
 * @param letterCountId - Id of Label to display length of text area
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
 * @param letterCountId - Id of area for letter count display
 */
function insertLetterCountArea(textArea, letterCountId) {
    const maxLength = textArea.attr('maxlength');
    if ($(`#${letterCountId}`).length === 0) {
        const letterCountAreaTemplate = `<div class="col-md-6 padding-0">
            <span id="${letterCountId}">${maxLength}</span> characters left </div>`;
        $(letterCountAreaTemplate).insertAfter(textArea);
    }
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
