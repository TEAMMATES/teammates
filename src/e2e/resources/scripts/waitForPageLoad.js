var numOfTestabilityToRemain = arguments[0];
var callback = arguments[arguments.length - 1];
if (document.readyState !== 'complete') {
  callback('document not ready');
} else {
  try {
    if (!window.getAllAngularTestabilities) {
      callback('complete');
    }
    var testabilities = window.getAllAngularTestabilities();
    var count = testabilities.length;
    if (count <= numOfTestabilityToRemain) {
      callback('complete');
    }
    var decrement = function () {
      count--;
      if (count <= numOfTestabilityToRemain) {
        callback('complete');
      }
    };
    testabilities.forEach(function (testability) {
      testability.whenStable(decrement);
    });
  } catch (err) {
    callback(err.message);
  }
}
