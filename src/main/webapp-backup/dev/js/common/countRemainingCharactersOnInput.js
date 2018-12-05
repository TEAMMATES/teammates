/**
 * Computes the number of characters left in the text input
 * @param $textInput - The text input for which the number of characters are to be counted
 * @param remainingCharactersElementId - The ID of the HTML element that displays the value of the remaining characters
 */
function computeRemainingCharactersCount($textInput, remainingCharactersElementId) {
    const maxLength = $textInput.attr('maxLength');
    const currentLength = $textInput.val().length;
    $(`#${remainingCharactersElementId}`).text(maxLength - currentLength);
}
/**
 * Inserts the remaining characters count for the text input into the designated HTML element
 * @param $textInput - The text input for which the number of characters are to be counted
 * @param remainingCharactersElementId - The ID of the HTML element that displays the value of the remaining characters
 */
function insertRemainingCharactersCount($textInput, remainingCharactersElementId) {
    const maxLength = $textInput.attr('maxlength');
    if ($(`#${remainingCharactersElementId}`).length === 0) {
        const charactersLeftSpan = `<span id="${remainingCharactersElementId}">${maxLength}</span>`;
        const remainingCharactersTemplate = `<div>${charactersLeftSpan} characters left </div>`;
        $(remainingCharactersTemplate).insertAfter($textInput);
    }
}

/**
 * Computes the number of remaining characters left in the text input upon 'input' event
 * @param textInputId - The ID of the text input for which the number of characters are to be counted
 */
function countRemainingCharactersOnInput(textInputId) {
    const $textInput = $(`#${textInputId}`);
    const remainingCharactersElementId = `charLeft-${textInputId}`;

    insertRemainingCharactersCount($textInput, remainingCharactersElementId);

    computeRemainingCharactersCount($textInput, remainingCharactersElementId);
    $textInput.on('input', e => computeRemainingCharactersCount($(e.currentTarget), remainingCharactersElementId));
}

export {
    countRemainingCharactersOnInput,
};
