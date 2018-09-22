/* global bootbox:true */

/**
 * Ensures that a modal dialog is "opened" when clicking the selector.
 * It will not be actually opened, but rather will sent the contents of
 * the header and the body message to a designated space.
 *
 * <p>Button actions are not checked and are left for proper UI testing.
 *
 * @param {Object} assert The assert Object from the QUnit test function
 * @param {String} selector The selector of the object to be clicked
 * @param {String} header The expected header text of the modal dialog
 * @param {String} message The expected body message of the modal dialog
 */
function ensureCorrectModal(assert, selector, header, message) {
    $(selector).click();

    assert.equal($('#test-bootbox-modal-stub-title').html(), header, 'Header text should be correct.');
    assert.equal($('#test-bootbox-modal-stub-message').html(), message, 'Message text should be correct.');
}

/**
 * Clears the button used for testing bootbox event binding.
 */
function clearBootboxButtonClickEvent() {
    $(document).off('click', '#test-bootbox-button');
}

/**
 * Clears the div used for testing bootbox event binding.
 */
function clearBootboxModalStub() {
    $('#test-bootbox-modal-stub').html('');
}

const jQueryObjectStubForBootbox = {
    find() {
        return this;
    },
    addClass() {
        return this;
    },
    modal() {
        return this;
    },
};

bootbox.dialog = function (params) {
    $('#test-bootbox-modal-stub').html(
            `<div id="test-bootbox-modal-stub-title">${params.title}</div>`
            + `<div id="test-bootbox-modal-stub-message">${params.message}</div>`,
    );
    return jQueryObjectStubForBootbox;
};

$.fn.ready = function () {
    // do not call the document ready functions as they are page-specific
};

$.ajax = function () {
    // do not actually make the AJAX request
};

export {
    clearBootboxButtonClickEvent,
    clearBootboxModalStub,
    ensureCorrectModal,
};
