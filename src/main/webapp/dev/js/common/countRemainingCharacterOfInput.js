/**
 * Computes the number of characters left in the text area
 * @param textInput - The text input for which the number of characters are to be counted
 * @param remainingCharactersElementId - The ID of the HTML element that displays the value of the remaining characters
 */
function computeRemainingCharactersCount(textInput, remainingCharactersElementId) {
    const maxLength = textInput.attr('maxLength');
    const currentLength = textInput.val().length;
    $(`#${remainingCharactersElementId}`).text(maxLength - currentLength);
}
/**
 * Inserts the remaining characters count for the text input into the designated HTML element
 * @param textInput - The text input for which the number of characters are to be counted
 * @param remainingCharactersElementId - The ID of the HTML element that displays the value of the remaining characters
 */
function insertRemainingCharactersCount(textInput, remainingCharactersElementId) {
    const maxLength = textInput.attr('maxlength');
    if ($(`#${remainingCharactersElementId}`).length === 0) {
        const charactersLeftSpan = `<span id="${remainingCharactersElementId}">${maxLength}</span>`;
        const remainingCharactersTemplate =
                `<div class="row"><div class="col-md-12">${charactersLeftSpan} characters left </div></div>`;
        $(remainingCharactersTemplate).insertAfter(textInput);
    }
}

function countRemainingCharacterOfInput(textInputId) {
    const testInput = $(`#${textInputId}`);
    const letterCountAreaId = `charLeft-${textInputId}`;

    insertRemainingCharactersCount(testInput, letterCountAreaId);

    computeRemainingCharactersCount(testInput, letterCountAreaId);
    testInput.on('input', () => computeRemainingCharactersCount(testInput, letterCountAreaId));
}

export {
    countRemainingCharacterOfInput,
};
