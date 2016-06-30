var FEEDBACK_PARTICIPANT_TYPE_CUSTOM = 'CUSTOM';
var FEEDBACK_PARTICIPANT_TYPE_SELF = 'SELF';

var studentEmailToTeamNameMap;
var teamNameToStudentEmailsMap;
var instructorEmails;
var studentEmails;
var teamNames;

$(document).ready(function() {
    initialiseCustomFeedbackPathsData();
    initialiseFeedbackPathsSpreadsheets();
    bindEventHandlers();
});

function initialiseCustomFeedbackPathsData() {
    studentEmailToTeamNameMap = $('#students-data').data('students');
    teamNameToStudentEmailsMap = [[]];
    studentEmails = [];
    teamNames = [];
    
    for (studentEmail in studentEmailToTeamNameMap) {
        if ({}.hasOwnProperty.call(studentEmailToTeamNameMap, studentEmail)) {
            studentEmails.push(studentEmail);
            
            var teamName = studentEmailToTeamNameMap[studentEmail];
            if (!teamNames.includes(teamName)) {
                teamNames.push(teamName);
            }
            
            var studentEmailsList = teamNameToStudentEmailsMap[teamName];
            if (studentEmailsList === undefined) {
                studentEmailsList = [];
            }
            studentEmailsList.push(studentEmail);
            teamNameToStudentEmailsMap[teamName] = studentEmailsList;
        }
    }
    
    instructorEmails = $('#instructors-data').data('instructors');
}

function initialiseFeedbackPathsSpreadsheets() {
    $('.form_question').each(function() {
        var giverType = $('select[id^="' + FEEDBACK_QUESTION_GIVERTYPE + '"]').val();
        var recipientType = $('select[id^="' + FEEDBACK_QUESTION_RECIPIENTTYPE + '"]').val();
        generateFeedbackPathsSpreadsheet($(this).find('.custom-feedback-paths-spreadsheet'), giverType, recipientType);
    });
}

function generateFeedbackPathsSpreadsheet($container, giverType, recipientType) {
    var data = [];
    populateFeedbackPathsData(data, giverType, recipientType);
    $container.handsontable({
        data: data,
        minRows: 10,
        minCols: 2,
        minSpareRows: 1,
        rowHeaders: true,
        colHeaders: true,
        manualColumnResize: true,
        manualRowResize: true,
        stretchH: 'all'
    });
}

function populateFeedbackPathsData(data, giverType, recipientType) {
    var giverToRecipientsMatrix = [[]];
    switch (giverType) {
    case FEEDBACK_PARTICIPANT_TYPE_SELF:
        populateGiverToRecipientsMatrixForGiverAsSelf(giverToRecipientsMatrix);
        break;
    case 'STUDENTS':
        populateGiverToRecipientsMatrixForGiverAsStudents(giverToRecipientsMatrix);
        break;
    case 'INSTRUCTORS':
        populateGiverToRecipientsMatrixForGiverAsInstructors(giverToRecipientsMatrix);
        break;
    case 'TEAMS':
        populateGiverToRecipientsMatrixForGiverAsTeams(giverToRecipientsMatrix);
        break;
    default:
        // no change
    }
    
    switch (recipientType) {
    case FEEDBACK_PARTICIPANT_TYPE_SELF:
        populateGiverToRecipientsMatrixForRecipientAsSelf(giverToRecipientsMatrix);
        break;
    case 'STUDENTS':
        populateGiverToRecipientsMatrixForRecipientAsStudents(giverToRecipientsMatrix);
        break;
    case 'INSTRUCTORS':
        populateGiverToRecipientsMatrixForRecipientAsInstructors(giverToRecipientsMatrix);
        break;
    case 'TEAMS':
        populateGiverToRecipientsMatrixForRecipientAsTeams(giverToRecipientsMatrix, giverType);
        break;
    case 'OWN_TEAM':
        populateGiverToRecipientsMatrixForRecipientAsOwnTeam(giverToRecipientsMatrix, giverType);
        break;
    case 'OWN_TEAM_MEMBERS':
        populateGiverToRecipientsMatrixForRecipientAsOwnTeamMembers(giverToRecipientsMatrix, giverType);
        break;
    case 'OWN_TEAM_MEMBERS_INCLUDING_SELF':
        populateGiverToRecipientsMatrixForRecipientAsOwnTeamMembersIncludingSelf(giverToRecipientsMatrix, giverType);
        break;
    case 'NONE':
        populateGiverToRecipientsMatrixForRecipientAsNobodySpecific(giverToRecipientsMatrix);
        break;
    default:
        // no change
    }
    populateFeedbackPathsDataUsingGiverToRecipientsMatrix(data, giverToRecipientsMatrix);
}

function populateFeedbackPathsDataUsingGiverToRecipientsMatrix(data, giverToRecipientsMatrix) {
    var i;
    var j;
    for (i = 0; i < giverToRecipientsMatrix.length; i++) {
        if (giverToRecipientsMatrix[i].length === 1) {
            data.push([giverToRecipientsMatrix[i][0]]);
        } else {
            for (j = 1; j < giverToRecipientsMatrix[i].length; j++) {
                data.push([giverToRecipientsMatrix[i][0], giverToRecipientsMatrix[i][j]]);
            }
        }
    }
}

function populateGiverToRecipientsMatrixForGiverAsSelf(giverToRecipientsMatrix) {
    giverToRecipientsMatrix[0][0] = $('#session-creator-data').data('session-creator');
}

function populateGiverToRecipientsMatrixForGiverAsStudents(giverToRecipientsMatrix) {
    var i;
    for (i = 0; i < studentEmails.length; i++) {
        giverToRecipientsMatrix[i] = [];
        giverToRecipientsMatrix[i][0] = studentEmails[i];
    }
}

function populateGiverToRecipientsMatrixForGiverAsInstructors(giverToRecipientsMatrix) {
    var i;
    for (i = 0; i < instructorEmails.length; i++) {
        giverToRecipientsMatrix[i] = [];
        giverToRecipientsMatrix[i][0] = instructorEmails[i];
    }
}

function populateGiverToRecipientsMatrixForGiverAsTeams(giverToRecipientsMatrix) {
    var i;
    for (i = 0; i < teamNames.length; i++) {
        giverToRecipientsMatrix[i] = [];
        giverToRecipientsMatrix[i][0] = teamNames[i];
    }
}

function populateGiverToRecipientsMatrixForRecipientAsSelf(giverToRecipientsMatrix) {
    var i;
    for (i = 0; i < giverToRecipientsMatrix.length; i++) {
        giverToRecipientsMatrix[i].push(giverToRecipientsMatrix[i][0]);
    }
}

function populateGiverToRecipientsMatrixForRecipientAsStudents(giverToRecipientsMatrix) {
    var i;
    var j;
    for (i = 0; i < giverToRecipientsMatrix.length; i++) {
        for (j = 0; j < studentEmails.length; j++) {
            if (giverToRecipientsMatrix[i][0] !== studentEmails[j]) {
                giverToRecipientsMatrix[i].push(studentEmails[j]);
            }
        }
    }
}

function populateGiverToRecipientsMatrixForRecipientAsInstructors(giverToRecipientsMatrix) {
    var i;
    var j;
    for (i = 0; i < giverToRecipientsMatrix.length; i++) {
        for (j = 0; j < instructorEmails.length; j++) {
            if (giverToRecipientsMatrix[i][0] !== instructorEmails[j]) {
                giverToRecipientsMatrix[i].push(instructorEmails[j]);
            }
        }
    }
}

function populateGiverToRecipientsMatrixForRecipientAsTeams(giverToRecipientsMatrix, giverType) {
    var i;
    var j;
    if (giverType === 'STUDENTS') {
        for (i = 0; i < giverToRecipientsMatrix.length; i++) {
            for (j = 0; j < teamNames.length; j++) {
                var giverStudentEmail = giverToRecipientsMatrix[i][0];
                if (studentEmailToTeamNameMap[giverStudentEmail] !== teamNames[j]) {
                    giverToRecipientsMatrix[i].push(teamNames[j]);
                }
            }
        }
    } else {
        for (i = 0; i < giverToRecipientsMatrix.length; i++) {
            for (j = 0; j < teamNames.length; j++) {
                if (giverToRecipientsMatrix[i][0] !== teamNames[j]) {
                    giverToRecipientsMatrix[i].push(teamNames[j]);
                }
            }
        }
    }
}

function populateGiverToRecipientsMatrixForRecipientAsOwnTeam(giverToRecipientsMatrix, giverType) {
    var i;
    if (giverType === FEEDBACK_PARTICIPANT_TYPE_SELF || giverType === 'INSTRUCTORS') {
        for (i = 0; i < giverToRecipientsMatrix.length; i++) {
            giverToRecipientsMatrix[i].push('Instructors');
        }
    } else if (giverType === 'STUDENTS') {
        for (i = 0; i < giverToRecipientsMatrix.length; i++) {
            var giverStudentEmail = giverToRecipientsMatrix[i][0];
            giverToRecipientsMatrix[i].push(studentEmailToTeamNameMap[giverStudentEmail]);
        }
    }
}

function populateGiverToRecipientsMatrixForRecipientAsOwnTeamMembers(giverToRecipientsMatrix, giverType) {
    if (giverType === 'STUDENTS') {
        var i;
        var j;
        for (i = 0; i < giverToRecipientsMatrix.length; i++) {
            var giverStudentEmail = giverToRecipientsMatrix[i][0];
            var giverTeamName = studentEmailToTeamNameMap[giverStudentEmail];
            var giverTeamMembers = teamNameToStudentEmailsMap[giverTeamName];
            for (j = 0; j < giverTeamMembers.length; j++) {
                if (giverTeamMembers[j] !== giverStudentEmail) {
                    giverToRecipientsMatrix[i].push(giverTeamMembers[j]);
                }
            }
        }
    }
}

function populateGiverToRecipientsMatrixForRecipientAsOwnTeamMembersIncludingSelf(giverToRecipientsMatrix, giverType) {
    var i;
    var giverTeamName;
    var giverTeamMembers;
    if (giverType === 'STUDENTS') {
        for (i = 0; i < giverToRecipientsMatrix.length; i++) {
            var giverStudentEmail = giverToRecipientsMatrix[i][0];
            giverTeamName = studentEmailToTeamNameMap[giverStudentEmail];
            giverTeamMembers = teamNameToStudentEmailsMap[giverTeamName];
            giverToRecipientsMatrix[i] = giverToRecipientsMatrix[i].concat(giverTeamMembers);
        }
    } else if (giverType === 'TEAMS') {
        for (i = 0; i < giverToRecipientsMatrix.length; i++) {
            giverTeamName = giverToRecipientsMatrix[i][0];
            giverTeamMembers = teamNameToStudentEmailsMap[giverTeamName];
            giverToRecipientsMatrix[i] = giverToRecipientsMatrix[i].concat(giverTeamMembers);
        }
    }
}

function populateGiverToRecipientsMatrixForRecipientAsNobodySpecific(giverToRecipientsMatrix) {
    var i;
    for (i = 0; i < giverToRecipientsMatrix.length; i++) {
        giverToRecipientsMatrix[i].push('%GENERAL%');
    }
}

function bindEventHandlers() {
    $('.form_question').on('change', '.participantSelect', function() {
        var $questionForm = $(this).closest('.form_question');
        var $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        var $giverSelect = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_GIVERTYPE + '"]');
        var $recipientSelect = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_RECIPIENTTYPE + '"]');
        var giverType = $giverSelect.val();
        var recipientType = $recipientSelect.val();
        
        if (giverType === FEEDBACK_PARTICIPANT_TYPE_CUSTOM
                || recipientType === FEEDBACK_PARTICIPANT_TYPE_CUSTOM) {
            if (this.id.includes(FEEDBACK_QUESTION_GIVERTYPE)) {
                $recipientSelect.val(FEEDBACK_PARTICIPANT_TYPE_SELF);
                recipientType = FEEDBACK_PARTICIPANT_TYPE_SELF;
            } else if (this.id.includes(FEEDBACK_QUESTION_RECIPIENTTYPE)) {
                $giverSelect.val(FEEDBACK_PARTICIPANT_TYPE_SELF);
                giverType = FEEDBACK_PARTICIPANT_TYPE_SELF;
            }
            $giverSelect.find('option[value="' + FEEDBACK_PARTICIPANT_TYPE_CUSTOM + '"]').remove();
            $recipientSelect.find('option[value="' + FEEDBACK_PARTICIPANT_TYPE_CUSTOM + '"]').remove();
        }
        
        generateFeedbackPathsSpreadsheet($container, giverType, recipientType);
    });
    
    $('.form_question').on('change', '.custom-feedback-paths-spreadsheet textarea', function() {
        var $questionForm = $(this).closest('.form_question');
        $questionForm.find('.participantSelect').each(function() {
            var customFeedbackParticipantTypeText = 'Custom';
            var $customFeedbackParticipantTypeOption =
                    $('<option></option>').attr('value', FEEDBACK_PARTICIPANT_TYPE_CUSTOM)
                                          .text(customFeedbackParticipantTypeText);
            $(this).append($customFeedbackParticipantTypeOption)
                   .val(FEEDBACK_PARTICIPANT_TYPE_CUSTOM);
        });
        enableAllVisibilityOptionsRowsForQuestion($questionForm);
    });
    
    $('.form_question').on('click', '.add-rows-button', function() {
        var $questionForm = $(this).closest('.form_question');
        var $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        var numRowsToAdd = parseInt($questionForm.find('.add-rows-input').val());
        var hotInstance = $container.handsontable('getInstance');
        if (numRowsToAdd > 0) {
            hotInstance.alter('insert_row', hotInstance.countRows(), numRowsToAdd);
        }
    });
}

function enableAllVisibilityOptionsRowsForQuestion($questionForm) {
    enableRow($questionForm.children(':first'), 1);
    enableRow($questionForm.children(':first'), 2);
    enableRow($questionForm.children(':first'), 3);
    enableRow($questionForm.children(':first'), 4);
    enableRow($questionForm.children(':first'), 5);
}
