/**
* Returns an id-value pair dictionary of the selected input elements of the given form.
* Required params: formSelector
* Optional params: tinyMCEDivId (defaults to null)
*                  favourableSelector (defaults to ':input:enabled')
*                  unfavourableSelector (defaults to 'button,[type="hidden"]')
*/
function backupFormHtml(formSelector, tinyMCEDivId, favourableSelector, unfavourableSelector) {
    const allFieldsDict = {}; // A dictionary to store id-value pairs of all the necessary input elements.

    // Selecting all the form elements that need to be backed up.
    const $allFields = $(formSelector)
                     .find(favourableSelector || ':input:enabled')
                     .not(unfavourableSelector || 'button,[type="hidden"]');

    if (typeof tinyMCEDivId !== "undefined") {
        allFieldsDict['instructions'] = tinymce.get('instructions').getContent();
    }

    for (let i = 0; i < $allFields.length; i += 1) {
        const field = $allFields.get(i); // .get() returns a jQuery object.
        if (field.type === 'radio' || field.type === 'checkbox') {
            // Set the value to true or false based on if the radio button/checkbox is checked or not.
            allFieldsDict[field.id] = field.checked;
        } else {
            // All other types of input stored with their full values.
            allFieldsDict[field.id] = field.value;
        }
    }

    console.log(allFieldsDict);
    return allFieldsDict;
}

/**
* Changes the values of the fields in the given form to match those stored in allFieldsDict.
* Required params: formSelector
                   allFieldsDict
* Optional params: tinyMCEDivId (defaults to null)
*/
function restoreFormHtml(formSelector, allFieldsDict, tinyMCEDivId) {
    for (const fieldId in allFieldsDict) {
        const field = $(formSelector).find(`:input[id=${fieldId}]`); // jQuery selector returns a DOM element.
        if (field.prop('type') === 'radio' || field.prop('type') === 'checkbox') {
            // Change the checked property of the DOM element for radio button/checkbox
            field.prop('checked', allFieldsDict[fieldId]);
        } else {
            // For all other input types, change the value of the DOM element.
            field.val(allFieldsDict[fieldId]);
        }
    }
}

export {
    backupFormHtml,
    restoreFormHtml,
};
