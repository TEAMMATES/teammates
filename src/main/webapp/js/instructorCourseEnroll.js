/**
 * Highlight the enrollstudents textarea at specified positions.
 * @param start
 * @param end
 */
function highlightError(start, end){
	document.getElementById("enrollstudents").selectionStart = start;
	document.getElementById("enrollstudents").selectionEnd = end;
}

/**
 * Checks enrollment input, highlight the problematic line, if any.
 * Returns the result of the validation.
 * @param input
 * @returns {Boolean}
 */
function checkEnrollmentInput(input) {
	input = input.replace(/\t/g,"|");
	var entries = input.split("\n");
	var fields;
	var totalLen = 0;

	var entriesLength = entries.length;
	for ( var x = 0; x < entriesLength; x++) {
		if (entries[x] != "") {
			// Separate the fields
			fields = entries[x].split("|");
			var fieldsLength = fields.length;

			var error = false;
			// Make sure that all fields are present and valid
			if (fieldsLength<3) {
				setStatusMessage(DISPLAY_ENROLLMENT_FIELDS_MISSING,true);
				error = true;
			} else if(fieldsLength>4){
				setStatusMessage(DISPLAY_ENROLLMENT_FIELDS_EXTRA,true);
				error = true;
			} else if (!isStudentNameValid(fields[1].trim())) {
				setStatusMessage(DISPLAY_STUDENT_NAME_INVALID,true);
				error = true;
			} else if (!isStudentEmailValid(fields[2].trim())) {
				setStatusMessage(DISPLAY_STUDENT_EMAIL_INVALID,true);
				error = true;
			} else if (!isStudentTeamNameValid(fields[0].trim())) {
				setStatusMessage(DISPLAY_STUDENT_TEAMNAME_INVALID,true);
				error = true;
			}
			if(error){
				highlightError(totalLen, totalLen+entries[x].length+1);
				return false;
			}
		}
		totalLen += entries[x].length+1;
	}
	return true;
}