var id = arguments[0];
var callback = arguments[arguments.length - 1];
var nTries = 25;

var loadTinyMceInstance = function () {
  var tinyMceInstance = tinyMCE.get(id);
  if (tinyMceInstance && tinyMceInstance.initialized || nTries < 0) {
    callback(tinyMceInstance.getContent());
  } else {
    nTries--;
    setTimeout(loadTinyMceInstance, 200);
  }
};

loadTinyMceInstance();
