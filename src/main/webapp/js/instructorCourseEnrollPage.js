window.addEventListener('load', function (){
    var typingErrMsg = "Please use | character ( shift+\\ ) to seperate fields, or copy from your existing spreadsheet.";
    var notified = false;

    function isUserTyping(str){
        return str.indexOf("\t")==-1 && str.indexOf("|")==-1;
    }
  window.isUserTyping = isUserTyping;

    var ENTER_KEYCODE = 13;
    var enrolTextbox; 
    if ((enrolTextbox	 = $('#enrollstudents')).length){
        enrolTextbox = enrolTextbox[0];
        $(enrolTextbox).keydown(function(e) {
            var keycode = e.which || e.keyCode;
            if (keycode == ENTER_KEYCODE) {
                if (isUserTyping (e.target.value) && !notified){
                    notified = true;
                    alert(typingErrMsg);
                }
            }
        })
    };
})
