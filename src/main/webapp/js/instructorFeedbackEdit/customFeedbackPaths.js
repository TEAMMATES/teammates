var customFeedbackPaths = {

    FEEDBACK_PARTICIPANT_TYPE_CUSTOM: 'CUSTOM',
    FEEDBACK_PARTICIPANT_TYPE_SELF: 'SELF',
    FEEDBACK_PARTICIPANT_TYPE_STUDENTS: 'STUDENTS',
    FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS: 'INSTRUCTORS',
    FEEDBACK_PARTICIPANT_TYPE_TEAMS: 'TEAMS',
    FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM: 'OWN_TEAM',
    FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS: 'OWN_TEAM_MEMBERS',
    FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS_INCLUDING_SELF: 'OWN_TEAM_MEMBERS_INCLUDING_SELF',
    FEEDBACK_PARTICIPANT_TYPE_NONE: 'NONE',
    TEAM_NAME_INSTRUCTORS: 'Instructors',

    // These variables are read-only and are not changed after they have been initialized
    // They are used to derive the data that is used to fill the spreadsheets
    sessionCreator: null,
    studentEmailToTeamNameMap: null,
    instructorEmails: null,
    teamNameToStudentEmailsMap: null,
    studentEmails: null,
    teamNames: null,
    allPossibleFeedbackGivers: null,
    allPossibleFeedbackRecipients: null,

    initializeCustomFeedbackPathsData: function() {
        customFeedbackPaths.sessionCreator = $('#session-creator-data').data('session-creator');
        customFeedbackPaths.studentEmailToTeamNameMap = $('#students-data').data('students');
        customFeedbackPaths.instructorEmails = $('#instructors-data').data('instructors');
        customFeedbackPaths.teamNameToStudentEmailsMap = [[]];
        customFeedbackPaths.studentEmails = [];
        customFeedbackPaths.teamNames = [];
        
        for (var studentEmail in customFeedbackPaths.studentEmailToTeamNameMap) {
            if (customFeedbackPaths.studentEmailToTeamNameMap.hasOwnProperty(studentEmail)) {
                customFeedbackPaths.studentEmails.push(studentEmail);
                
                var teamName = customFeedbackPaths.studentEmailToTeamNameMap[studentEmail];
                if (!customFeedbackPaths.teamNames.includes(teamName)) {
                    customFeedbackPaths.teamNames.push(teamName);
                }
                
                var studentEmailsList = customFeedbackPaths.teamNameToStudentEmailsMap[teamName];
                if (studentEmailsList === undefined) {
                    studentEmailsList = [];
                }
                studentEmailsList.push(studentEmail);
                customFeedbackPaths.teamNameToStudentEmailsMap[teamName] = studentEmailsList;
            }
        }
        
        // Empty string added to provide an empty option in spreadsheet dropdown
        // It prevents a feedback participant from being selected upon clicking away from dropdown
        customFeedbackPaths.allPossibleFeedbackGivers = [''];
        customFeedbackPaths.allPossibleFeedbackGivers =
                customFeedbackPaths.allPossibleFeedbackGivers.concat(customFeedbackPaths.studentEmails);
        for (var i = 0; i < customFeedbackPaths.instructorEmails.length; i++) {
            if (!customFeedbackPaths.allPossibleFeedbackGivers.includes(customFeedbackPaths.instructorEmails[i])) {
                customFeedbackPaths.allPossibleFeedbackGivers.push(customFeedbackPaths.instructorEmails[i]);
            }
        }
        customFeedbackPaths.allPossibleFeedbackGivers =
                customFeedbackPaths.allPossibleFeedbackGivers.concat(customFeedbackPaths.teamNames);
        customFeedbackPaths.allPossibleFeedbackRecipients = customFeedbackPaths.allPossibleFeedbackGivers.slice();
        customFeedbackPaths.allPossibleFeedbackRecipients.push(customFeedbackPaths.TEAM_NAME_INSTRUCTORS);
        customFeedbackPaths.allPossibleFeedbackRecipients.push('Class');
    },
    
    initializeFeedbackPathsSpreadsheets: function() {
        $('.form_question').each(function() {
            customFeedbackPaths.generateFeedbackPathsSpreadsheet($(this));
        });
    },
    
    generateFeedbackPathsSpreadsheet: function($questionForm) {
        var $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        var giverType = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_GIVERTYPE + '"]').val();
        var recipientType = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_RECIPIENTTYPE + '"]').val();
        var data = customFeedbackPaths.getDataForFeedbackPathsSpreadsheet(giverType, recipientType);
        var columns = customFeedbackPaths.getColumnsForFeedbackPathsSpreadsheet(giverType, recipientType);
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
    },
    
    updateFeedbackPathsSpreadsheet: function($questionForm) {
        var $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        var giverType = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_GIVERTYPE + '"]').val();
        var recipientType = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_RECIPIENTTYPE + '"]').val();
        var data = customFeedbackPaths.getDataForFeedbackPathsSpreadsheet(giverType, recipientType);
        var columns = customFeedbackPaths.getColumnsForFeedbackPathsSpreadsheet(giverType, recipientType);
        var hotInstance = $container.handsontable('getInstance');
        hotInstance.updateSettings({
            data: data,
            columns: columns
        });
    },
    
    updateColumnsForFeedbackPathsSpreadsheet: function($questionForm) {
        var columns = customFeedbackPaths.getColumnsForFeedbackPathsSpreadsheet(
                              customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM,
                              customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM);
        var $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        var hotInstance = $container.handsontable('getInstance');
        hotInstance.updateSettings({
            columns: columns
        });
        hotInstance.validateCells();
    },
    
    getDataForFeedbackPathsSpreadsheet: function(giverType, recipientType) {
        var giverToRecipientsMap = {};
        switch (giverType) {
        case customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF:
            customFeedbackPaths.populateGiverToRecipientsMapForGiver(
                    giverToRecipientsMap, [customFeedbackPaths.sessionCreator]);
            break;
        case customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS:
            customFeedbackPaths.populateGiverToRecipientsMapForGiver(
                    giverToRecipientsMap, customFeedbackPaths.studentEmails);
            break;
        case customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS:
            customFeedbackPaths.populateGiverToRecipientsMapForGiver(
                    giverToRecipientsMap, customFeedbackPaths.instructorEmails);
            break;
        case customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_TEAMS:
            customFeedbackPaths.populateGiverToRecipientsMapForGiver(
                    giverToRecipientsMap, customFeedbackPaths.teamNames);
            break;
        default:
            // no change
        }
        
        switch (recipientType) {
        case customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF:
            customFeedbackPaths.populateGiverToRecipientsMapForRecipientAsSelf(giverToRecipientsMap);
            break;
        case customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS:
            customFeedbackPaths.populateGiverToRecipientsMapForRecipientAsStudents(giverToRecipientsMap);
            break;
        case customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS:
            customFeedbackPaths.populateGiverToRecipientsMapForRecipientAsInstructors(giverToRecipientsMap);
            break;
        case customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_TEAMS:
            customFeedbackPaths.populateGiverToRecipientsMapForRecipientAsTeams(giverToRecipientsMap, giverType);
            break;
        case customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM:
            customFeedbackPaths.populateGiverToRecipientsMapForRecipientAsOwnTeam(giverToRecipientsMap, giverType);
            break;
        case customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS:
            customFeedbackPaths.populateGiverToRecipientsMapForRecipientAsOwnTeamMembers(giverToRecipientsMap, giverType);
            break;
        case customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS_INCLUDING_SELF:
            customFeedbackPaths.populateGiverToRecipientsMapForRecipientAsOwnTeamMembersIncludingSelf(
                    giverToRecipientsMap, giverType);
            break;
        case customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_NONE:
            customFeedbackPaths.populateGiverToRecipientsMapForRecipientAsNobodySpecific(giverToRecipientsMap);
            break;
        default:
            // no change
        }
        return customFeedbackPaths.getFeedbackPathsDataUsingGiverToRecipientsMap(giverToRecipientsMap);
    },
    
    getFeedbackPathsDataUsingGiverToRecipientsMap: function(giverToRecipientsMap) {
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
    },
    
    getColumnsForFeedbackPathsSpreadsheet: function(giverType, recipientType) {
        var columns = [{}, {}];
        if (giverType === customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM
                && recipientType === customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM) {
            columns = [{
                type: 'dropdown',
                source: customFeedbackPaths.allPossibleFeedbackGivers,
                readOnly: false
            }, {
                type: 'dropdown',
                source: customFeedbackPaths.allPossibleFeedbackRecipients,
                readOnly: false
            }];
        } else {
            columns = [{ readOnly: true }, { readOnly: true }];
        }
        return columns;
    },
    
    populateGiverToRecipientsMapForGiver: function(giverToRecipientsMap, giverList) {
        for (var i = 0; i < giverList.length; i++) {
            giverToRecipientsMap[giverList[i]] = [];
        }
        
    },
    
    populateGiverToRecipientsMapForRecipientAsSelf: function(giverToRecipientsMap) {
        for (var giver in giverToRecipientsMap) {
            if (giverToRecipientsMap.hasOwnProperty(giver)) {
                giverToRecipientsMap[giver].push(giver);
            }
        }
    },
    
    populateGiverToRecipientsMapForRecipientAsStudents: function(giverToRecipientsMap) {
        for (var giver in giverToRecipientsMap) {
            if (giverToRecipientsMap.hasOwnProperty(giver)) {
                for (var i = 0; i < customFeedbackPaths.studentEmails.length; i++) {
                    if (customFeedbackPaths.studentEmails[i] !== giver) {
                        giverToRecipientsMap[giver].push(customFeedbackPaths.studentEmails[i]);
                    }
                }
            }
        }
    },
    
    populateGiverToRecipientsMapForRecipientAsInstructors: function(giverToRecipientsMap) {
        for (var giver in giverToRecipientsMap) {
            if (giverToRecipientsMap.hasOwnProperty(giver)) {
                for (var i = 0; i < customFeedbackPaths.instructorEmails.length; i++) {
                    if (customFeedbackPaths.instructorEmails[i] !== giver) {
                        giverToRecipientsMap[giver].push(customFeedbackPaths.instructorEmails[i]);
                    }
                }
            }
        }
    },
    
    populateGiverToRecipientsMapForRecipientAsTeams: function(giverToRecipientsMap, giverType) {
        for (var giver in giverToRecipientsMap) {
            if (giverToRecipientsMap.hasOwnProperty(giver)) {
                for (var i = 0; i < customFeedbackPaths.teamNames.length; i++) {
                    if (giverType === customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
                        if (customFeedbackPaths.teamNames[i] !== customFeedbackPaths.studentEmailToTeamNameMap[giver]) {
                            giverToRecipientsMap[giver].push(customFeedbackPaths.teamNames[i]);
                        }
                    } else if (customFeedbackPaths.teamNames[i] !== giver) {
                        giverToRecipientsMap[giver].push(customFeedbackPaths.teamNames[i]);
                    }
                }
            }
        }
    },
    
    populateGiverToRecipientsMapForRecipientAsOwnTeam: function(giverToRecipientsMap, giverType) {
        for (var giver in giverToRecipientsMap) {
            if (giverToRecipientsMap.hasOwnProperty(giver)) {
                if (giverType === customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF
                        || giverType === customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS) {
                    giverToRecipientsMap[giver].push(customFeedbackPaths.TEAM_NAME_INSTRUCTORS);
                } else if (giverType === customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
                    giverToRecipientsMap[giver].push(customFeedbackPaths.studentEmailToTeamNameMap[giver]);
                }
            }
        }
    },
    
    populateGiverToRecipientsMapForRecipientAsOwnTeamMembers: function(giverToRecipientsMap, giverType) {
        if (giverType === customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
            for (var giver in giverToRecipientsMap) {
                if (giverToRecipientsMap.hasOwnProperty(giver)) {
                    var giverTeamName = customFeedbackPaths.studentEmailToTeamNameMap[giver];
                    var giverTeamMembers = customFeedbackPaths.teamNameToStudentEmailsMap[giverTeamName];
                    for (var i = 0; i < giverTeamMembers.length; i++) {
                        if (giverTeamMembers[i] !== giver) {
                            giverToRecipientsMap[giver].push(giverTeamMembers[i]);
                        }
                    }
                }
            }
        }
    },
    
    populateGiverToRecipientsMapForRecipientAsOwnTeamMembersIncludingSelf: function(giverToRecipientsMap, giverType) {
        for (var giver in giverToRecipientsMap) {
            if (giverToRecipientsMap.hasOwnProperty(giver)) {
                if (giverType === customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
                    var giverTeamName = customFeedbackPaths.studentEmailToTeamNameMap[giver];
                    giverToRecipientsMap[giver] = customFeedbackPaths.teamNameToStudentEmailsMap[giverTeamName];
                } else if (giverType === customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_TEAMS) {
                    giverToRecipientsMap[giver] = customFeedbackPaths.teamNameToStudentEmailsMap[giver];
                }
            }
        }
    },
    
    populateGiverToRecipientsMapForRecipientAsNobodySpecific: function(giverToRecipientsMap) {
        for (var giver in giverToRecipientsMap) {
            if (giverToRecipientsMap.hasOwnProperty(giver)) {
                giverToRecipientsMap[giver].push('Class');
            }
        }
    },
    
    bindEventHandlers: function() {
        $('.form_question').on('change', '.participantSelect', function() {
            var $questionForm = $(this).closest('.form_question');
            customFeedbackPaths.removeCustomOptionsIfNecessary($questionForm);
            customFeedbackPaths.updateFeedbackPathsSpreadsheet($questionForm);
        });
        
        $('.form_question').on('click', '.add-rows-button', function() {
            var $questionForm = $(this).closest('.form_question');
            customFeedbackPaths.addRowsToFeedbackPathsSpreadsheet($questionForm);
        });
        
        $('.form_question').on('click', '.customize-button', function() {
            var $questionForm = $(this).closest('.form_question');
            $questionForm.find('div[class*="numberOfEntitiesElements"]').hide();
            customFeedbackPaths.appendCustomOptionsIfNecessary($questionForm);
            enableAllRows($questionForm);
            customFeedbackPaths.updateColumnsForFeedbackPathsSpreadsheet($questionForm);
        });
    },
    
    removeCustomOptionsIfNecessary: function($questionForm) {
        var $giverSelect = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_GIVERTYPE + '"]');
        var $recipientSelect = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_RECIPIENTTYPE + '"]');
        var giverType = $giverSelect.val();
        var recipientType = $recipientSelect.val();
        
        var isChangingParticipantTypeFromCustomToPredefined =
                giverType === customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM
                || recipientType === customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM;
        if (isChangingParticipantTypeFromCustomToPredefined) {
            $giverSelect.find('option[value="' + customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM + '"]').remove();
            $recipientSelect.find('option[value="' + customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM + '"]').remove();
        }
    },
    
    appendCustomOptionsIfNecessary: function($questionForm) {
        $questionForm.find('.participantSelect').each(function() {
            if ($(this).val() !== customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM) {
                customFeedbackPaths.appendCustomOptionToParticipantSelect($(this));
            }
        });
    },
    
    appendCustomOptionToParticipantSelect: function($participantSelect) {
        var $customFeedbackParticipantTypeOption =
                $('<option></option>').attr('value', customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM)
                                      .text('Custom');
        $participantSelect.append($customFeedbackParticipantTypeOption)
                          .val(customFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM);
    },
    
    addRowsToFeedbackPathsSpreadsheet: function($questionForm) {
        var $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        var numRowsToAdd = parseInt($questionForm.find('.add-rows-input').val());
        var hotInstance = $container.handsontable('getInstance');
        if (numRowsToAdd > 0) {
            hotInstance.alter('insert_row', hotInstance.countRows(), numRowsToAdd);
        }
    }
};
