/* global prepareDatepickers:false linkAjaxForResponseRate:false readyFeedbackPage:false richTextEditorBuilder:false */

$(document).ready(() => {
    prepareDatepickers();
    linkAjaxForResponseRate();

    if (typeof richTextEditorBuilder !== 'undefined') {
        /* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
        richTextEditorBuilder.initEditor('#instructions', {
            inline: true,
            readonly: false,
            fixed_toolbar_container: '#richtext-toolbar-container',
        });
        /* eslint-enable camelcase */
    }

    readyFeedbackPage();
});
