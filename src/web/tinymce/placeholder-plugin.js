// This is a plugin for TinyMCE to enable placeholder feature.
// Part of code is adapted from https://github.com/mohan/tinymce-placeholder
tinymce.PluginManager.add('placeholder', function(editor) {
  editor.on('init', function() {
    const placeHolderDiv = new PlaceHolderDiv;

    onBlur();

    tinymce.DOM.bind(placeHolderDiv.el, 'click', onFocus);
    editor.on('focus', onFocus);
    editor.on('blur', onBlur);
    editor.on('change', onBlur);
    editor.on('setContent', onBlur);
    editor.on('keydown', onKeydown);

    function onFocus() {
      editor.execCommand('mceFocus', false);
    }

    function onBlur() {
      if (editor.getContent() === '') {
        placeHolderDiv.show();
      } else {
        placeHolderDiv.hide();
      }
    }

    function onKeydown() {
      placeHolderDiv.hide();
    }
  });

  const PlaceHolderDiv = function() {
    const contentAreaParent = tinymce.dom.DomQuery(editor.getElement()).parent()[0];
    const placeholderText = contentAreaParent.getAttribute('placeholder');
    const placeholderAttrs = { style: { position: 'absolute', top: '0.375rem', left: '0.75rem', color: '#888' } };

    tinymce.DOM.setStyle(contentAreaParent, 'position', 'relative');

    this.el = editor.dom.add(contentAreaParent, 'div', placeholderAttrs, placeholderText);
  };

  PlaceHolderDiv.prototype.hide = function() {
    tinymce.DOM.setStyle(this.el, 'display', 'none');
  };

  PlaceHolderDiv.prototype.show = function() {
    tinymce.DOM.setStyle(this.el, 'display', '');
  };
});
