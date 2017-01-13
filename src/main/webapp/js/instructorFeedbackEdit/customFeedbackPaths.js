var CustomFeedbackPaths = {

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
    STUDENT_PARTICIPANT_TYPE_SUFFIX: ' (Student)',
    INSTRUCTOR_PARTICIPANT_TYPE_SUFFIX: ' (Instructor)',
    TEAM_PARTICIPANT_TYPE_SUFFIX: ' (Team)',

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
        CustomFeedbackPaths.sessionCreator = $('#session-creator-data').data('session-creator');
        CustomFeedbackPaths.studentEmailToTeamNameMap = $('#students-data').data('students');
        CustomFeedbackPaths.instructorEmails = $('#instructors-data').data('instructors');
        CustomFeedbackPaths.teamNameToStudentEmailsMap = [[]];
        CustomFeedbackPaths.studentEmails = [];
        CustomFeedbackPaths.teamNames = [];
        
        for (var studentEmail in CustomFeedbackPaths.studentEmailToTeamNameMap) {
            if (CustomFeedbackPaths.studentEmailToTeamNameMap.hasOwnProperty(studentEmail)) {
                CustomFeedbackPaths.studentEmails.push(studentEmail);
                
                var teamName = CustomFeedbackPaths.studentEmailToTeamNameMap[studentEmail];
                if (!CustomFeedbackPaths.teamNames.includes(teamName)) {
                    CustomFeedbackPaths.teamNames.push(teamName);
                }
                
                var studentEmailsList = CustomFeedbackPaths.teamNameToStudentEmailsMap[teamName];

                studentEmailsList = studentEmailsList || [];

                studentEmailsList.push(studentEmail);
                CustomFeedbackPaths.teamNameToStudentEmailsMap[teamName] = studentEmailsList;
            }
        }
        
        // Empty string added to provide an empty option in spreadsheet dropdown
        // It prevents a feedback participant from being selected upon clicking away from dropdown
        CustomFeedbackPaths.allPossibleFeedbackGivers = [''];
        var i;
        for (i = 0; i < CustomFeedbackPaths.studentEmails.length; i++) {
            CustomFeedbackPaths.allPossibleFeedbackGivers.push(
                    CustomFeedbackPaths.studentEmails[i] + CustomFeedbackPaths.STUDENT_PARTICIPANT_TYPE_SUFFIX);
        }
        for (i = 0; i < CustomFeedbackPaths.instructorEmails.length; i++) {
            CustomFeedbackPaths.allPossibleFeedbackGivers.push(
                    CustomFeedbackPaths.instructorEmails[i]
                    + CustomFeedbackPaths.INSTRUCTOR_PARTICIPANT_TYPE_SUFFIX);
        }
        for (i = 0; i < CustomFeedbackPaths.teamNames.length; i++) {
            CustomFeedbackPaths.allPossibleFeedbackGivers.push(
                    CustomFeedbackPaths.teamNames[i] + CustomFeedbackPaths.TEAM_PARTICIPANT_TYPE_SUFFIX);
        }

        CustomFeedbackPaths.allPossibleFeedbackRecipients = CustomFeedbackPaths.allPossibleFeedbackGivers.slice();
        CustomFeedbackPaths.allPossibleFeedbackRecipients.push('Class');
    },
    
    initializeFeedbackPathsSpreadsheets: function() {
        $('.form_question').each(function() {
            CustomFeedbackPaths.generateFeedbackPathsSpreadsheet($(this));
        });
    },
    
    generateFeedbackPathsSpreadsheet: function($questionForm) {
        var $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        var giverType = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_GIVERTYPE + '"]').val();
        var recipientType = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_RECIPIENTTYPE + '"]').val();
        var data;
        if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM
                && recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM) {
            data = JSON.parse($questionForm.find('.custom-feedback-paths-spreadsheet-data-input').val());
        } else {
            data = CustomFeedbackPaths.getDataForFeedbackPathsSpreadsheet(giverType, recipientType);
        }
        var columns = CustomFeedbackPaths.getColumnsForFeedbackPathsSpreadsheet(giverType, recipientType);
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
            stretchH: 'all',
            afterChange: function() {
                CustomFeedbackPaths.updateCustomFeedbackPathsSpreadsheetDataInput($questionForm);
            }
        });
    },
    
    updateCustomFeedbackPathsSpreadsheetDataInput: function($questionForm) {
        var $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        var data = $container.handsontable('getData');
        
        var dataWithoutPartiallyFilledOrEmptyRows = [];

        for (var i = 0; i < data.length; i++) {
            if (!data[i].includes('') && !data[i].includes(null)) {
                dataWithoutPartiallyFilledOrEmptyRows.push(data[i]);
            }
        }
        
        var $customFeedbackPathsSpreadsheetDataInput = $questionForm.find('.custom-feedback-paths-spreadsheet-data-input');
        $customFeedbackPathsSpreadsheetDataInput.attr(
                'value', JSON.stringify(dataWithoutPartiallyFilledOrEmptyRows));
    },
    
    updateFeedbackPathsSpreadsheet: function($questionForm) {
        var $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        var giverType = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_GIVERTYPE + '"]').val();
        var recipientType = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_RECIPIENTTYPE + '"]').val();
        var data = CustomFeedbackPaths.getDataForFeedbackPathsSpreadsheet(giverType, recipientType);
        var columns = CustomFeedbackPaths.getColumnsForFeedbackPathsSpreadsheet(giverType, recipientType);
        var hotInstance = $container.handsontable('getInstance');
        hotInstance.updateSettings({
            data: data,
            columns: columns
        });
    },
    
    updateColumnsForFeedbackPathsSpreadsheet: function($questionForm) {
        var columns = CustomFeedbackPaths.getColumnsForFeedbackPathsSpreadsheet(
                              CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM,
                              CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM);
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
        case CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF:
            CustomFeedbackPaths.populateGiverToRecipientsMapForGiver(
                    giverToRecipientsMap, [CustomFeedbackPaths.sessionCreator]);
            break;
        case CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS:
            CustomFeedbackPaths.populateGiverToRecipientsMapForGiver(
                    giverToRecipientsMap, CustomFeedbackPaths.studentEmails);
            break;
        case CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS:
            CustomFeedbackPaths.populateGiverToRecipientsMapForGiver(
                    giverToRecipientsMap, CustomFeedbackPaths.instructorEmails);
            break;
        case CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_TEAMS:
            CustomFeedbackPaths.populateGiverToRecipientsMapForGiver(
                    giverToRecipientsMap, CustomFeedbackPaths.teamNames);
            break;
        default:
            // no change
        }
        
        switch (recipientType) {
        case CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF:
            CustomFeedbackPaths.populateGiverToRecipientsMapForRecipientAsSelf(giverToRecipientsMap);
            break;
        case CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS:
            CustomFeedbackPaths.populateGiverToRecipientsMapForRecipientAsStudents(giverToRecipientsMap);
            break;
        case CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS:
            CustomFeedbackPaths.populateGiverToRecipientsMapForRecipientAsInstructors(giverToRecipientsMap);
            break;
        case CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_TEAMS:
            CustomFeedbackPaths.populateGiverToRecipientsMapForRecipientAsTeams(giverToRecipientsMap, giverType);
            break;
        case CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM:
            CustomFeedbackPaths.populateGiverToRecipientsMapForRecipientAsOwnTeam(giverToRecipientsMap, giverType);
            break;
        case CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS:
            CustomFeedbackPaths.populateGiverToRecipientsMapForRecipientAsOwnTeamMembers(giverToRecipientsMap, giverType);
            break;
        case CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS_INCLUDING_SELF:
            CustomFeedbackPaths.populateGiverToRecipientsMapForRecipientAsOwnTeamMembersIncludingSelf(
                    giverToRecipientsMap, giverType);
            break;
        case CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_NONE:
            CustomFeedbackPaths.populateGiverToRecipientsMapForRecipientAsNobodySpecific(giverToRecipientsMap);
            break;
        default:
            // no change
        }
        return CustomFeedbackPaths.getFeedbackPathsDataUsingGiverToRecipientsMap(
                giverToRecipientsMap, giverType, recipientType);
    },
    
    getFeedbackPathsDataUsingGiverToRecipientsMap: function(giverToRecipientsMap, giverType, recipientType) {
        var giverSuffix = '';
        var isGiverAStudent =
                giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS;
        var isGiverAnInstructor =
                giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF
                || giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS;
        var isGiverATeam =
                giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_TEAMS;
        
        if (isGiverAStudent) {
            giverSuffix = CustomFeedbackPaths.STUDENT_PARTICIPANT_TYPE_SUFFIX;
        } else if (isGiverAnInstructor) {
            giverSuffix = CustomFeedbackPaths.INSTRUCTOR_PARTICIPANT_TYPE_SUFFIX;
        } else if (isGiverATeam) {
            giverSuffix = CustomFeedbackPaths.TEAM_PARTICIPANT_TYPE_SUFFIX;
        }
        
        var recipientSuffix = '';
        var isRecipientAStudent =
                recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF
                && giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS
                || recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS
                || recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS
                || recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS_INCLUDING_SELF;
        var isRecipientAnInstructor =
                recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF
                && (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF
                        || giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS)
                || recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS;
        var isRecipientATeam =
                recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF
                && giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_TEAMS
                || recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_TEAMS
                || recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM;
        
        if (isRecipientAStudent) {
            recipientSuffix = CustomFeedbackPaths.STUDENT_PARTICIPANT_TYPE_SUFFIX;
        } else if (isRecipientAnInstructor) {
            recipientSuffix = CustomFeedbackPaths.INSTRUCTOR_PARTICIPANT_TYPE_SUFFIX;
        } else if (isRecipientATeam) {
            recipientSuffix = CustomFeedbackPaths.TEAM_PARTICIPANT_TYPE_SUFFIX;
        }
        
        var data = [];
        for (var giver in giverToRecipientsMap) {
            if (giverToRecipientsMap.hasOwnProperty(giver)) {
                for (var i = 0; i < giverToRecipientsMap[giver].length; i++) {
                    var dataRow = [];
                    dataRow.push(giver + giverSuffix);
                    dataRow.push(giverToRecipientsMap[giver][i] + recipientSuffix);
                    data.push(dataRow);
                }
            }
        }
        
        // data should minimally contain one empty row
        data.push(['', '']);
        return data;
    },
    
    getColumnsForFeedbackPathsSpreadsheet: function(giverType, recipientType) {
        var columns = [{}, {}];
        if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM
                && recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM) {
            columns = [{
                type: 'dropdown',
                source: CustomFeedbackPaths.allPossibleFeedbackGivers,
                readOnly: false
            }, {
                type: 'dropdown',
                source: CustomFeedbackPaths.allPossibleFeedbackRecipients,
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
                for (var i = 0; i < CustomFeedbackPaths.studentEmails.length; i++) {
                    if (CustomFeedbackPaths.studentEmails[i] !== giver) {
                        giverToRecipientsMap[giver].push(CustomFeedbackPaths.studentEmails[i]);
                    }
                }
            }
        }
    },
    
    populateGiverToRecipientsMapForRecipientAsInstructors: function(giverToRecipientsMap) {
        for (var giver in giverToRecipientsMap) {
            if (giverToRecipientsMap.hasOwnProperty(giver)) {
                for (var i = 0; i < CustomFeedbackPaths.instructorEmails.length; i++) {
                    if (CustomFeedbackPaths.instructorEmails[i] !== giver) {
                        giverToRecipientsMap[giver].push(CustomFeedbackPaths.instructorEmails[i]);
                    }
                }
            }
        }
    },
    
    populateGiverToRecipientsMapForRecipientAsTeams: function(giverToRecipientsMap, giverType) {
        for (var giver in giverToRecipientsMap) {
            if (giverToRecipientsMap.hasOwnProperty(giver)) {
                for (var i = 0; i < CustomFeedbackPaths.teamNames.length; i++) {
                    if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
                        if (CustomFeedbackPaths.teamNames[i] !== CustomFeedbackPaths.studentEmailToTeamNameMap[giver]) {
                            giverToRecipientsMap[giver].push(CustomFeedbackPaths.teamNames[i]);
                        }
                    } else if (CustomFeedbackPaths.teamNames[i] !== giver) {
                        giverToRecipientsMap[giver].push(CustomFeedbackPaths.teamNames[i]);
                    }
                }
            }
        }
    },
    
    populateGiverToRecipientsMapForRecipientAsOwnTeam: function(giverToRecipientsMap, giverType) {
        for (var giver in giverToRecipientsMap) {
            if (giverToRecipientsMap.hasOwnProperty(giver)) {
                if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
                    giverToRecipientsMap[giver].push(CustomFeedbackPaths.studentEmailToTeamNameMap[giver]);
                }
            }
        }
    },
    
    populateGiverToRecipientsMapForRecipientAsOwnTeamMembers: function(giverToRecipientsMap, giverType) {
        if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
            for (var giver in giverToRecipientsMap) {
                if (giverToRecipientsMap.hasOwnProperty(giver)) {
                    var giverTeamName = CustomFeedbackPaths.studentEmailToTeamNameMap[giver];
                    var giverTeamMembers = CustomFeedbackPaths.teamNameToStudentEmailsMap[giverTeamName];
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
                if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
                    var giverTeamName = CustomFeedbackPaths.studentEmailToTeamNameMap[giver];
                    giverToRecipientsMap[giver] = CustomFeedbackPaths.teamNameToStudentEmailsMap[giverTeamName];
                } else if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_TEAMS) {
                    giverToRecipientsMap[giver] = CustomFeedbackPaths.teamNameToStudentEmailsMap[giver];
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
            CustomFeedbackPaths.removeCustomOptionsIfNecessary($questionForm);
            CustomFeedbackPaths.updateFeedbackPathsSpreadsheet($questionForm);
        });
        
        $('.form_question').on('click', '.add-rows-button', function() {
            var $questionForm = $(this).closest('.form_question');
            CustomFeedbackPaths.addRowsToFeedbackPathsSpreadsheet($questionForm);
        });
        
        $('.form_question').on('click', '.customize-button', function() {
            var $questionForm = $(this).closest('.form_question');
            $questionForm.find('div[class*="numberOfEntitiesElements"]').hide();
            $questionForm.find('.feedback-path-dropdown-option-other').click();
            CustomFeedbackPaths.appendCustomOptionsIfNecessary($questionForm);
            enableAllRows($questionForm);
            CustomFeedbackPaths.updateColumnsForFeedbackPathsSpreadsheet($questionForm);
            getVisibilityMessage(this);
        });
    },
    
    removeCustomOptionsIfNecessary: function($questionForm) {
        var $giverSelect = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_GIVERTYPE + '"]');
        var $recipientSelect = $questionForm.find('select[id^="' + FEEDBACK_QUESTION_RECIPIENTTYPE + '"]');
        var giverType = $giverSelect.val();
        var recipientType = $recipientSelect.val();
        
        var isChangingParticipantTypeFromCustomToPredefined =
                giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM
                || recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM;
        if (isChangingParticipantTypeFromCustomToPredefined) {
            $giverSelect.find('option[value="' + CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM + '"]').remove();
            $recipientSelect.find('option[value="' + CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM + '"]').remove();
            hideInvalidRecipientTypeOptions($giverSelect);
            updateVisibilityCheckboxesDiv($questionForm);
        }
    },
    
    appendCustomOptionsIfNecessary: function($questionForm) {
        $questionForm.find('.participantSelect').each(function() {
            if ($(this).val() !== CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM) {
                CustomFeedbackPaths.appendCustomOptionToParticipantSelect($(this));
            }
        });
    },
    
    appendCustomOptionToParticipantSelect: function($participantSelect) {
        var $customFeedbackParticipantTypeOption =
                $('<option></option>').attr('value', CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM)
                                      .text('Custom');
        $participantSelect.append($customFeedbackParticipantTypeOption)
                          .val(CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM);
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
