function validateEmail(event) {
    var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    var isValidEmail = emailRegex.test($('#email').val());

    if (!isValidEmail) {
        alert("Invalid Email Address");
        event.preventDefault();
        return false;
    } else {
        return true;
    }
}

$(document).ready(() => {
    $('#submitButton').on("click", function(event) {
        validateEmail(event);
    });
})

window.validateEmail = validateEmail;
