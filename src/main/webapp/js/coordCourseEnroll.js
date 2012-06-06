// Display messages
var DISPLAY_ENROLLMENT_FIELDS_EXTRA = "There are too many fields.";
var DISPLAY_ENROLLMENT_FIELDS_MISSING = "There are missing fields.";
var DISPLAY_STUDENT_EMAIL_INVALID = "The e-mail address is invalid.";
var DISPLAY_STUDENT_NAME_INVALID = "Name should only consist of alphanumerics and not<br />more than 40 characters.";
var DISPLAY_STUDENT_TEAMNAME_INVALID = "Team name should contain less than 25 characters.";

function checkEnrollmentInput(input) {
	input = replaceAll(input,"|","\t");
	var entries = input.split("\n");
	var fields;

	var entriesLength = entries.length;
	for ( var x = 0; x < entriesLength; x++) {
		if (entries[x] != "") {
			// Separate the fields
			fields = entries[x].split("\t");
			var fieldsLength = fields.length;

			// Make sure that all fields are present
			if (fieldsLength<3) {
				setStatusMessage(DISPLAY_ENROLLMENT_FIELDS_MISSING,true);
				return false;
			} else if(fieldsLength>4){
				setStatusMessage(DISPLAY_ENROLLMENT_FIELDS_EXTRA,true);
				return false;
			}

			// Check that fields are correct
			if (!isStudentNameValid(trim(fields[1]))) {
				setStatusMessage(DISPLAY_STUDENT_NAME_INVALID,true);
				return false;
			} else if (!isStudentEmailValid(trim(fields[2]))) {
				setStatusMessage(DISPLAY_STUDENT_EMAIL_INVALID,true);
				return false;
			} else if (!isStudentTeamNameValid(trim(fields[0]))) {
				setStatusMessage(DISPLAY_STUDENT_TEAMNAME_INVALID,true);
				return false;
			}
		}
	}
	return true;
}