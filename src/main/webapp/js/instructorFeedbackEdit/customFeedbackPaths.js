var FEEDBACK_PARTICIPANT_TYPE_CUSTOM = 'CUSTOM';
var FEEDBACK_PARTICIPANT_TYPE_SELF = 'SELF';
var FEEDBACK_PARTICIPANT_TYPE_STUDENTS = 'STUDENTS';
var FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS = 'INSTRUCTORS';
var FEEDBACK_PARTICIPANT_TYPE_TEAMS = 'TEAMS';
var FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM = 'OWN_TEAM';
var FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS = 'OWN_TEAM_MEMBERS';
var FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS_INCLUDING_SELF = 'OWN_TEAM_MEMBERS_INCLUDING_SELF';
var FEEDBACK_PARTICIPANT_TYPE_NONE = 'NONE';
var TEAM_NAME_INSTRUCTORS = 'Instructors';

var sessionCreator;
var studentEmailToTeamNameMap;
var instructorEmails;
var teamNameToStudentEmailsMap;
var studentEmails;
var teamNames;
var allPossibleFeedbackGivers;
var allPossibleFeedbackRecipients;

$(document).ready(function() {
    initialiseCustomFeedbackPathsData();
    initialiseFeedbackPathsSpreadsheets();
    bindEventHandlers();
});

function initialiseCustomFeedbackPathsData() {
    sessionCreator = $('#session-creator-data').data('session-creator');
    studentEmailToTeamNameMap = $('#students-data').data('students');
    instructorEmails = $('#instructors-data').data('instructors');
    teamNameToStudentEmailsMap = [[]];
    studentEmails = [];
    teamNames = [];
    
    for (var studentEmail in studentEmailToTeamNameMap) {
        if (studentEmailToTeamNameMap.hasOwnProperty(studentEmail)) {
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
    
    allPossibleFeedbackGivers = studentEmails.slice();
    for (var i = 0; i < instructorEmails.length; i++) {
        if (!allPossibleFeedbackGivers.includes(instructorEmails[i])) {
            allPossibleFeedbackGivers.push(instructorEmails[i]);
        }
    }
    allPossibleFeedbackGivers = allPossibleFeedbackGivers.concat(teamNames);
    allPossibleFeedbackRecipients = allPossibleFeedbackGivers.slice();
    allPossibleFeedbackRecipients.push(TEAM_NAME_INSTRUCTORS);
    allPossibleFeedbackRecipients.push('%GENERAL%');
}

function initialiseFeedbackPathsSpreadsheets() {
    $('.form_question').each(function() {
        var $questionForm = $(this);
        var giverType = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_GIVERTYPE + '"]').val();
        var recipientType = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_RECIPIENTTYPE + '"]').val();
        generateFeedbackPathsSpreadsheet($questionForm.find('.custom-feedback-paths-spreadsheet'), giverType, recipientType);
    });
}

function generateFeedbackPathsSpreadsheet($container, giverType, recipientType) {
    var data = getDataForFeedbackPathsSpreadsheet(giverType, recipientType);
    var columns = getColumnsForFeedbackPathsSpreadsheet(giverType, recipientType);
    $container.handsontable({
        data: data,
        minRows: 15,
        minCols: 2,
        minSpareRows: 1,
        rowHeaders: true,
        colHeaders: ['Feedback giver', 'Feedback recipient'],
        columns: columns,
        manualColumnResize: true,
        manualRowResize: true,
        stretchH: 'all'
    });
}

function updateFeedbackPathsSpreadsheetForQuestionTable($questionTable) {
    var $container = $questionTable.find('.custom-feedback-paths-spreadsheet');
    var giverType = $questionTable.find('select[id^="' + FEEDBACK_QUESTION_GIVERTYPE + '"]').val();
    var recipientType = $questionTable.find('select[id^="' + FEEDBACK_QUESTION_RECIPIENTTYPE + '"]').val();
    updateFeedbackPathsSpreadsheet($container, giverType, recipientType);
}

function updateFeedbackPathsSpreadsheet($container, giverType, recipientType) {
    var data = getDataForFeedbackPathsSpreadsheet(giverType, recipientType);
    var columns = getColumnsForFeedbackPathsSpreadsheet(giverType, recipientType);
    var hotInstance = $container.handsontable('getInstance');
    hotInstance.updateSettings({
        data: data,
        columns: columns
    });
}

function getDataForFeedbackPathsSpreadsheet(giverType, recipientType) {
    var giverToRecipientsMap = {};
    switch (giverType) {
    case FEEDBACK_PARTICIPANT_TYPE_SELF:
        populateGiverToRecipientsMapForGiver(giverToRecipientsMap, [sessionCreator]);
        break;
    case FEEDBACK_PARTICIPANT_TYPE_STUDENTS:
        populateGiverToRecipientsMapForGiver(giverToRecipientsMap, studentEmails);
        break;
    case FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS:
        populateGiverToRecipientsMapForGiver(giverToRecipientsMap, instructorEmails);
        break;
    case FEEDBACK_PARTICIPANT_TYPE_TEAMS:
        populateGiverToRecipientsMapForGiver(giverToRecipientsMap, teamNames);
        break;
    default:
        // no change
    }
    
    switch (recipientType) {
    case FEEDBACK_PARTICIPANT_TYPE_SELF:
        populateGiverToRecipientsMapForRecipientAsSelf(giverToRecipientsMap);
        break;
    case FEEDBACK_PARTICIPANT_TYPE_STUDENTS:
        populateGiverToRecipientsMapForRecipientAsStudents(giverToRecipientsMap);
        break;
    case FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS:
        populateGiverToRecipientsMapForRecipientAsInstructors(giverToRecipientsMap);
        break;
    case FEEDBACK_PARTICIPANT_TYPE_TEAMS:
        populateGiverToRecipientsMapForRecipientAsTeams(giverToRecipientsMap, giverType);
        break;
    case FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM:
        populateGiverToRecipientsMapForRecipientAsOwnTeam(giverToRecipientsMap, giverType);
        break;
    case FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS:
        populateGiverToRecipientsMapForRecipientAsOwnTeamMembers(giverToRecipientsMap, giverType);
        break;
    case FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS_INCLUDING_SELF:
        populateGiverToRecipientsMapForRecipientAsOwnTeamMembersIncludingSelf(
                giverToRecipientsMap, giverType);
        break;
    case FEEDBACK_PARTICIPANT_TYPE_NONE:
        populateGiverToRecipientsMapForRecipientAsNobodySpecific(giverToRecipientsMap);
        break;
    default:
        // no change
    }
    return getFeedbackPathsDataUsingGiverToRecipientsMap(giverToRecipientsMap);
}

function getFeedbackPathsDataUsingGiverToRecipientsMap(giverToRecipientsMap) {
    var data = [];
    for (var giver in giverToRecipientsMap) {
        if (giverToRecipientsMap.hasOwnProperty(giver)) {
            for (var i = 0; i < giverToRecipientsMap[giver].length; i++) {
                data.push([giver, giverToRecipientsMap[giver][i]]);
            }
        }
    }
    
    // data should minimally contain one empty row
    data.push(['', '']);
    return data;
}

function getColumnsForFeedbackPathsSpreadsheet(giverType, recipientType) {
    var columns = [{}, {}];
    if (giverType === FEEDBACK_PARTICIPANT_TYPE_CUSTOM
            && recipientType === FEEDBACK_PARTICIPANT_TYPE_CUSTOM) {
        columns = [{
            type: 'dropdown',
            source: allPossibleFeedbackGivers,
            readOnly: false
        }, {
            type: 'dropdown',
            source: allPossibleFeedbackRecipients,
            readOnly: false
        }];
    } else {
        columns = [{ readOnly: true }, { readOnly: true }];
    }
    return columns;
}

function populateGiverToRecipientsMapForGiver(giverToRecipientsMap, giverList) {
    for (var i = 0; i < giverList.length; i++) {
        giverToRecipientsMap[giverList[i]] = [];
    }
    
}

function populateGiverToRecipientsMapForRecipientAsSelf(giverToRecipientsMap) {
    for (var giver in giverToRecipientsMap) {
        if (giverToRecipientsMap.hasOwnProperty(giver)) {
            giverToRecipientsMap[giver].push(giver);
        }
    }
}

function populateGiverToRecipientsMapForRecipientAsStudents(giverToRecipientsMap) {
    for (var giver in giverToRecipientsMap) {
        if (giverToRecipientsMap.hasOwnProperty(giver)) {
            for (var i = 0; i < studentEmails.length; i++) {
                if (studentEmails[i] !== giver) {
                    giverToRecipientsMap[giver].push(studentEmails[i]);
                }
            }
        }
    }
}

function populateGiverToRecipientsMapForRecipientAsInstructors(giverToRecipientsMap) {
    for (var giver in giverToRecipientsMap) {
        if (giverToRecipientsMap.hasOwnProperty(giver)) {
            for (var i = 0; i < instructorEmails.length; i++) {
                if (instructorEmails[i] !== giver) {
                    giverToRecipientsMap[giver].push(instructorEmails[i]);
                }
            }
        }
    }
}

function populateGiverToRecipientsMapForRecipientAsTeams(giverToRecipientsMap, giverType) {
    for (var giver in giverToRecipientsMap) {
        if (giverToRecipientsMap.hasOwnProperty(giver)) {
            for (var i = 0; i < teamNames.length; i++) {
                if (giverType === FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
                    if (teamNames[i] !== studentEmailToTeamNameMap[giver]) {
                        giverToRecipientsMap[giver].push(teamNames[i]);
                    }
                } else if (teamNames[i] !== giver) {
                    giverToRecipientsMap[giver].push(teamNames[i]);
                }
            }
        }
    }
}

function populateGiverToRecipientsMapForRecipientAsOwnTeam(giverToRecipientsMap, giverType) {
    for (var giver in giverToRecipientsMap) {
        if (giverToRecipientsMap.hasOwnProperty(giver)) {
            if (giverType === FEEDBACK_PARTICIPANT_TYPE_SELF || giverType === FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS) {
                giverToRecipientsMap[giver].push(TEAM_NAME_INSTRUCTORS);
            } else if (giverType === FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
                giverToRecipientsMap[giver].push(studentEmailToTeamNameMap[giver]);
            }
        }
    }
}

function populateGiverToRecipientsMapForRecipientAsOwnTeamMembers(giverToRecipientsMap, giverType) {
    if (giverType === FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
        for (var giver in giverToRecipientsMap) {
            if (giverToRecipientsMap.hasOwnProperty(giver)) {
                var giverTeamName = studentEmailToTeamNameMap[giver];
                var giverTeamMembers = teamNameToStudentEmailsMap[giverTeamName];
                for (var i = 0; i < giverTeamMembers.length; i++) {
                    if (giverTeamMembers[i] !== giver) {
                        giverToRecipientsMap[giver].push(giverTeamMembers[i]);
                    }
                }
            }
        }
    }
}

function populateGiverToRecipientsMapForRecipientAsOwnTeamMembersIncludingSelf(giverToRecipientsMap, giverType) {
    for (var giver in giverToRecipientsMap) {
        if (giverToRecipientsMap.hasOwnProperty(giver)) {
            if (giverType === FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
                var giverTeamName = studentEmailToTeamNameMap[giver];
                giverToRecipientsMap[giver] = teamNameToStudentEmailsMap[giverTeamName];
            } else if (giverType === FEEDBACK_PARTICIPANT_TYPE_TEAMS) {
                giverToRecipientsMap[giver] = teamNameToStudentEmailsMap[giver];
            }
        }
    }
}

function populateGiverToRecipientsMapForRecipientAsNobodySpecific(giverToRecipientsMap) {
    for (var giver in giverToRecipientsMap) {
        if (giverToRecipientsMap.hasOwnProperty(giver)) {
            giverToRecipientsMap[giver].push('%GENERAL%');
        }
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
            $giverSelect.find('option[value="' + FEEDBACK_PARTICIPANT_TYPE_CUSTOM + '"]').remove();
            $recipientSelect.find('option[value="' + FEEDBACK_PARTICIPANT_TYPE_CUSTOM + '"]').remove();
        }
        
        giverType = $giverSelect.val();
        recipientType = $recipientSelect.val();
        updateFeedbackPathsSpreadsheet($container, giverType, recipientType);
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
    
    $('.form_question').on('click', '.customize-button', function() {
        var $questionForm = $(this).closest('.form_question');
        $questionForm.find('div[class*="numberOfEntitiesElements"]').hide();
        $questionForm.find('.participantSelect').each(function() {
            if ($(this).val() !== FEEDBACK_PARTICIPANT_TYPE_CUSTOM) {
                var $customFeedbackParticipantTypeOption =
                        $('<option></option>').attr('value', FEEDBACK_PARTICIPANT_TYPE_CUSTOM)
                                              .text('Custom');
                $(this).append($customFeedbackParticipantTypeOption)
                       .val(FEEDBACK_PARTICIPANT_TYPE_CUSTOM);
            }
        });
        enableAllVisibilityOptionsRowsForQuestion($questionForm);
        var columns = getColumnsForFeedbackPathsSpreadsheet(FEEDBACK_PARTICIPANT_TYPE_CUSTOM,
                                                            FEEDBACK_PARTICIPANT_TYPE_CUSTOM);
        var $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        var hotInstance = $container.handsontable('getInstance');
        hotInstance.updateSettings({
            columns: columns
        });
        hotInstance.validateCells();
    });
}

function enableAllVisibilityOptionsRowsForQuestion($questionForm) {
    enableRow($questionForm.children(':first'), 1);
    enableRow($questionForm.children(':first'), 2);
    enableRow($questionForm.children(':first'), 3);
    enableRow($questionForm.children(':first'), 4);
    enableRow($questionForm.children(':first'), 5);
}
