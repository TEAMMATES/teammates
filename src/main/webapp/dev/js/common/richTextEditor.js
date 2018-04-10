/* global tinymce:false */

/* eslint-disable camelcase */ // The property names are determined by external library (tinymce)
const richTextEditorBuilder = {
    getDefaultConfiguration() {
        return {
            theme: 'modern',
            fontsize_formats: '8pt 9pt 10pt 11pt 12pt 14pt 16pt 18pt 20pt 24pt 26pt 28pt 36pt 48pt 72pt',
            font_formats: 'Andale Mono=andale mono,times;'
                          + 'Arial=arial,helvetica,sans-serif;'
                          + 'Arial Black=arial black,avant garde;'
                          + 'Book Antiqua=book antiqua,palatino;'
                          + 'Comic Sans MS=comic sans ms,sans-serif;'
                          + 'Courier New=courier new,courier;'
                          + 'Georgia=georgia,palatino;'
                          + 'Helvetica=helvetica;'
                          + 'Impact=impact,chicago;'
                          + 'Symbol=symbol;'
                          + 'Tahoma=tahoma,arial,helvetica,sans-serif;'
                          + 'Terminal=terminal,monaco;'
                          + 'Times New Roman=times new roman,times;'
                          + 'Trebuchet MS=trebuchet ms,geneva;'
                          + 'Verdana=verdana,geneva;'
                          + 'Webdings=webdings;'
                          + 'Wingdings=wingdings,zapf dingbats',

            relative_urls: false,
            convert_urls: false,
            remove_linebreaks: false,
            plugins: [
                'advlist autolink lists link image charmap print preview hr anchor pagebreak',
                'searchreplace wordcount visualblocks visualchars code fullscreen',
                'insertdatetime nonbreaking save table contextmenu directionality',
                'emoticons template paste textcolor colorpicker textpattern',
            ],

            toolbar1: 'insertfile undo redo | styleselect | bold italic underline | '
                    + 'alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image',
            toolbar2: 'print preview | forecolor backcolor | fontsizeselect fontselect | emoticons | fullscreen',

            init_instance_callback: 'initEditorCallback',

        };
    },

    initEditor(selector, opts) {
        tinymce.init($.extend(this.getDefaultConfiguration(), {
            selector,
        }, opts));
    },

};
/* eslint-enable camelcase */

function setPlaceholderText(editor) {
    if (editor.getContent() === '') {
        tinymce.DOM.addClass(editor.bodyElement, 'empty');
    } else {
        tinymce.DOM.removeClass(editor.bodyElement, 'empty');
    }
}

function initEditorCallback(editor) {
    tinymce.DOM.addClass(editor.bodyElement, 'content-editor');
    setPlaceholderText(editor);

    editor.on('selectionchange', () => {
        setPlaceholderText(editor);
    });
}

window.initEditorCallback = initEditorCallback;

/**
 * Destroys an instance of TinyMCE rich-text editor.
 */
function destroyEditor(id) {
    if (typeof tinymce === 'undefined') {
        return;
    }
    const currentEditor = tinymce.get(id);
    if (currentEditor) {
        currentEditor.destroy();
    }
}

export {
    destroyEditor,
    richTextEditorBuilder,
};
