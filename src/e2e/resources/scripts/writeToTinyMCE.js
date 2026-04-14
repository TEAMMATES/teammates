var id = arguments[0];
var content = arguments[1];
var callback = arguments[arguments.length - 1];
var nTries = 25;

var loadTinyMceInstance = function () {
    var tinyMceInstance = typeof window.tinymce !== 'undefined' ? window.tinymce.get(id) : null;
    if (tinyMceInstance && tinyMceInstance.initialized && tinyMceInstance.getBody()) {
        tinyMceInstance.setContent(content);
        tinyMceInstance.fire('input');
        tinyMceInstance.fire('change');
        callback('done');
    } else if (nTries < 0) {
        callback('error: TinyMCE failed to load in time');
    } else {
        nTries--;
        setTimeout(loadTinyMceInstance, 200);
    }
};

loadTinyMceInstance();
