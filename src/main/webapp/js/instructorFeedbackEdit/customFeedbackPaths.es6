/* global
enableAllRows:false, hideInvalidRecipientTypeOptions:false, getVisibilityMessage:false, updateVisibilityCheckboxesDiv:false

FEEDBACK_QUESTION_GIVERTYPE:false, FEEDBACK_QUESTION_RECIPIENTTYPE:false
*/

const CustomFeedbackPaths = {

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

    initializeCustomFeedbackPathsData() {
        CustomFeedbackPaths.sessionCreator = $('#session-creator-data').data('session-creator');
        CustomFeedbackPaths.studentEmailToTeamNameMap = $('#students-data').data('students');
        CustomFeedbackPaths.instructorEmails = $('#instructors-data').data('instructors');
        CustomFeedbackPaths.teamNameToStudentEmailsMap = [[]];
        CustomFeedbackPaths.studentEmails = [];
        CustomFeedbackPaths.teamNames = [];

        Object.keys(CustomFeedbackPaths.studentEmailToTeamNameMap).forEach((studentEmail) => {
            CustomFeedbackPaths.studentEmails.push(studentEmail);

            const teamName = CustomFeedbackPaths.studentEmailToTeamNameMap[studentEmail];
            if (!CustomFeedbackPaths.teamNames.includes(teamName)) {
                CustomFeedbackPaths.teamNames.push(teamName);
            }

            const studentEmailsList =
                    CustomFeedbackPaths.teamNameToStudentEmailsMap[teamName] || [];

            studentEmailsList.push(studentEmail);
            CustomFeedbackPaths.teamNameToStudentEmailsMap[teamName] = studentEmailsList;
        });

        // Empty string added to provide an empty option in spreadsheet dropdown
        // It prevents a feedback participant from being selected upon clicking away from dropdown
        CustomFeedbackPaths.allPossibleFeedbackGivers = [''];
        let i;
        for (i = 0; i < CustomFeedbackPaths.studentEmails.length; i += 1) {
            CustomFeedbackPaths.allPossibleFeedbackGivers.push(
                    CustomFeedbackPaths.studentEmails[i] + CustomFeedbackPaths.STUDENT_PARTICIPANT_TYPE_SUFFIX);
        }
        for (i = 0; i < CustomFeedbackPaths.instructorEmails.length; i += 1) {
            CustomFeedbackPaths.allPossibleFeedbackGivers.push(
                    CustomFeedbackPaths.instructorEmails[i]
                    + CustomFeedbackPaths.INSTRUCTOR_PARTICIPANT_TYPE_SUFFIX);
        }
        for (i = 0; i < CustomFeedbackPaths.teamNames.length; i += 1) {
            CustomFeedbackPaths.allPossibleFeedbackGivers.push(
                    CustomFeedbackPaths.teamNames[i] + CustomFeedbackPaths.TEAM_PARTICIPANT_TYPE_SUFFIX);
        }

        CustomFeedbackPaths.allPossibleFeedbackRecipients = CustomFeedbackPaths.allPossibleFeedbackGivers.slice();
        CustomFeedbackPaths.allPossibleFeedbackRecipients.push('Class');
    },

    generateFeedbackPathsSpreadsheet($questionForm) {
        const $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        const giverType = $questionForm.find(`select[id^="${FEEDBACK_QUESTION_GIVERTYPE}"]`).val();
        const recipientType = $questionForm.find(`select[id^="${FEEDBACK_QUESTION_RECIPIENTTYPE}"]`).val();
        let data;
        if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM
                && recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM) {
            data = JSON.parse($questionForm.find('.custom-feedback-paths-spreadsheet-data-input').val());
        } else {
            data = CustomFeedbackPaths.getDataForFeedbackPathsSpreadsheet(giverType, recipientType);
        }
        const columns = CustomFeedbackPaths.getColumnsForFeedbackPathsSpreadsheet(giverType, recipientType);
        $container.handsontable({
            data,
            minRows: 15,
            minCols: 2,
            minSpareRows: 1,
            rowHeaders: true,
            colHeaders: ['Feedback giver', 'Feedback recipient'],
            columns,
            manualColumnResize: true,
            manualRowResize: true,
            stretchH: 'all',
            afterChange() {
                CustomFeedbackPaths.updateCustomFeedbackPathsSpreadsheetDataInput($questionForm);
            },
        });
    },

    updateCustomFeedbackPathsSpreadsheetDataInput($questionForm) {
        const $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        const data = $container.handsontable('getData');

        const dataWithoutPartiallyFilledOrEmptyRows = [];

        for (let i = 0; i < data.length; i += 1) {
            if (!data[i].includes('') && !data[i].includes(null)) {
                dataWithoutPartiallyFilledOrEmptyRows.push(data[i]);
            }
        }

        const $customFeedbackPathsSpreadsheetDataInput = $questionForm.find('.custom-feedback-paths-spreadsheet-data-input');
        $customFeedbackPathsSpreadsheetDataInput.attr(
                'value', JSON.stringify(dataWithoutPartiallyFilledOrEmptyRows));
    },

    updateFeedbackPathsSpreadsheet($questionForm) {
        const $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        const giverType = $questionForm.find(`select[id^="${FEEDBACK_QUESTION_GIVERTYPE}"]`).val();
        const recipientType = $questionForm.find(`select[id^="${FEEDBACK_QUESTION_RECIPIENTTYPE}"]`).val();
        const data = CustomFeedbackPaths.getDataForFeedbackPathsSpreadsheet(giverType, recipientType);
        const columns = CustomFeedbackPaths.getColumnsForFeedbackPathsSpreadsheet(giverType, recipientType);
        const hotInstance = $container.handsontable('getInstance');
        if (hotInstance === undefined) {
            return;
        }
        hotInstance.updateSettings({
            data,
            columns,
        });
    },

    updateColumnsForFeedbackPathsSpreadsheet($questionForm) {
        const columns = CustomFeedbackPaths.getColumnsForFeedbackPathsSpreadsheet(
                              CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM,
                              CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM);
        const $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        const hotInstance = $container.handsontable('getInstance');
        hotInstance.updateSettings({
            columns,
        });
        hotInstance.validateCells();
    },

    getDataForFeedbackPathsSpreadsheet(giverType, recipientType) {
        const giverToRecipientsMap = {};
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

    getFeedbackPathsDataUsingGiverToRecipientsMap(giverToRecipientsMap, giverType, recipientType) {
        let giverSuffix = '';
        const isGiverAStudent =
                giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS;
        const isGiverAnInstructor =
                giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF
                || giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS;
        const isGiverATeam =
                giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_TEAMS;

        if (isGiverAStudent) {
            giverSuffix = CustomFeedbackPaths.STUDENT_PARTICIPANT_TYPE_SUFFIX;
        } else if (isGiverAnInstructor) {
            giverSuffix = CustomFeedbackPaths.INSTRUCTOR_PARTICIPANT_TYPE_SUFFIX;
        } else if (isGiverATeam) {
            giverSuffix = CustomFeedbackPaths.TEAM_PARTICIPANT_TYPE_SUFFIX;
        }

        let recipientSuffix = '';
        const isRecipientAStudent =
                recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF
                && giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS
                || recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS
                || recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS
                || recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_OWN_TEAM_MEMBERS_INCLUDING_SELF;
        const isRecipientAnInstructor =
                recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF
                && (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_SELF
                        || giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS)
                || recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_INSTRUCTORS;
        const isRecipientATeam =
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

        const data = [];
        Object.keys(giverToRecipientsMap).forEach((giver) => {
            for (let i = 0; i < giverToRecipientsMap[giver].length; i += 1) {
                const dataRow = [];
                dataRow.push(giver + giverSuffix);
                dataRow.push(giverToRecipientsMap[giver][i] + recipientSuffix);
                data.push(dataRow);
            }
        });

        // data should minimally contain one empty row
        data.push(['', '']);
        return data;
    },

    getColumnsForFeedbackPathsSpreadsheet(giverType, recipientType) {
        let columns = [{}, {}];
        if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM
                && recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM) {
            columns = [{
                type: 'dropdown',
                source: CustomFeedbackPaths.allPossibleFeedbackGivers,
                readOnly: false,
            }, {
                type: 'dropdown',
                source: CustomFeedbackPaths.allPossibleFeedbackRecipients,
                readOnly: false,
            }];
        } else {
            columns = [{ readOnly: true }, { readOnly: true }];
        }
        return columns;
    },

    populateGiverToRecipientsMapForGiver(giverToRecipientsMap, giverList) {
        for (let i = 0; i < giverList.length; i += 1) {
            giverToRecipientsMap[giverList[i]] = [];
        }
    },

    populateGiverToRecipientsMapForRecipientAsSelf(giverToRecipientsMap) {
        Object.keys(giverToRecipientsMap).forEach((giver) => {
            giverToRecipientsMap[giver].push(giver);
        });
    },

    populateGiverToRecipientsMapForRecipientAsStudents(giverToRecipientsMap) {
        Object.keys(giverToRecipientsMap).forEach((giver) => {
            for (let i = 0; i < CustomFeedbackPaths.studentEmails.length; i += 1) {
                if (CustomFeedbackPaths.studentEmails[i] !== giver) {
                    giverToRecipientsMap[giver].push(CustomFeedbackPaths.studentEmails[i]);
                }
            }
        });
    },

    populateGiverToRecipientsMapForRecipientAsInstructors(giverToRecipientsMap) {
        Object.keys(giverToRecipientsMap).forEach((giver) => {
            for (let i = 0; i < CustomFeedbackPaths.instructorEmails.length; i += 1) {
                if (CustomFeedbackPaths.instructorEmails[i] !== giver) {
                    giverToRecipientsMap[giver].push(CustomFeedbackPaths.instructorEmails[i]);
                }
            }
        });
    },

    populateGiverToRecipientsMapForRecipientAsTeams(giverToRecipientsMap, giverType) {
        Object.keys(giverToRecipientsMap).forEach((giver) => {
            for (let i = 0; i < CustomFeedbackPaths.teamNames.length; i += 1) {
                if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
                    if (CustomFeedbackPaths.teamNames[i] !== CustomFeedbackPaths.studentEmailToTeamNameMap[giver]) {
                        giverToRecipientsMap[giver].push(CustomFeedbackPaths.teamNames[i]);
                    }
                } else if (CustomFeedbackPaths.teamNames[i] !== giver) {
                    giverToRecipientsMap[giver].push(CustomFeedbackPaths.teamNames[i]);
                }
            }
        });
    },

    populateGiverToRecipientsMapForRecipientAsOwnTeam(giverToRecipientsMap, giverType) {
        Object.keys(giverToRecipientsMap).forEach((giver) => {
            if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
                giverToRecipientsMap[giver].push(CustomFeedbackPaths.studentEmailToTeamNameMap[giver]);
            }
        });
    },

    populateGiverToRecipientsMapForRecipientAsOwnTeamMembers(giverToRecipientsMap, giverType) {
        if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
            Object.keys(giverToRecipientsMap).forEach((giver) => {
                const giverTeamName = CustomFeedbackPaths.studentEmailToTeamNameMap[giver];
                const giverTeamMembers = CustomFeedbackPaths.teamNameToStudentEmailsMap[giverTeamName];
                for (let i = 0; i < giverTeamMembers.length; i += 1) {
                    if (giverTeamMembers[i] !== giver) {
                        giverToRecipientsMap[giver].push(giverTeamMembers[i]);
                    }
                }
            });
        }
    },

    populateGiverToRecipientsMapForRecipientAsOwnTeamMembersIncludingSelf(giverToRecipientsMap, giverType) {
        Object.keys(giverToRecipientsMap).forEach((giver) => {
            if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_STUDENTS) {
                const giverTeamName = CustomFeedbackPaths.studentEmailToTeamNameMap[giver];
                giverToRecipientsMap[giver] = CustomFeedbackPaths.teamNameToStudentEmailsMap[giverTeamName];
            } else if (giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_TEAMS) {
                giverToRecipientsMap[giver] = CustomFeedbackPaths.teamNameToStudentEmailsMap[giver];
            }
        });
    },

    populateGiverToRecipientsMapForRecipientAsNobodySpecific(giverToRecipientsMap) {
        Object.keys(giverToRecipientsMap).forEach((giver) => {
            giverToRecipientsMap[giver].push('Class');
        });
    },

    bindEventHandlers() {
        $('.form_question').on('change', '.participantSelect', function () {
            const $questionForm = $(this).closest('.form_question');
            CustomFeedbackPaths.removeCustomOptionsIfNecessary($questionForm);
            CustomFeedbackPaths.updateFeedbackPathsSpreadsheet($questionForm);
        });

        $('.form_question').on('click', '.add-rows-button', function () {
            const $questionForm = $(this).closest('.form_question');
            CustomFeedbackPaths.addRowsToFeedbackPathsSpreadsheet($questionForm);
        });

        $('.form_question').on('click', '.customize-button', function () {
            const $questionForm = $(this).closest('.form_question');
            $questionForm.find('div[class*="numberOfEntitiesElements"]').hide();
            $questionForm.find('.feedback-path-dropdown-option-other').click();
            CustomFeedbackPaths.appendCustomOptionsIfNecessary($questionForm);
            enableAllRows($questionForm);
            CustomFeedbackPaths.updateColumnsForFeedbackPathsSpreadsheet($questionForm);
            getVisibilityMessage(this);
        });
    },

    removeCustomOptionsIfNecessary($questionForm) {
        const $giverSelect = $questionForm.find(`select[id^="${FEEDBACK_QUESTION_GIVERTYPE}"]`);
        const $recipientSelect = $questionForm.find(`select[id^="${FEEDBACK_QUESTION_RECIPIENTTYPE}"]`);
        const giverType = $giverSelect.val();
        const recipientType = $recipientSelect.val();

        const isChangingParticipantTypeFromCustomToPredefined =
                giverType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM
                || recipientType === CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM;
        if (isChangingParticipantTypeFromCustomToPredefined) {
            $giverSelect.find(`option[value="${CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM}"]`).remove();
            $recipientSelect.find(`option[value="${CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM}"]`).remove();
            hideInvalidRecipientTypeOptions($giverSelect);
            updateVisibilityCheckboxesDiv($questionForm);
        }
    },

    appendCustomOptionsIfNecessary($questionForm) {
        $questionForm.find('.participantSelect').each(function () {
            if ($(this).val() !== CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM) {
                CustomFeedbackPaths.appendCustomOptionToParticipantSelect($(this));
            }
        });
    },

    appendCustomOptionToParticipantSelect($participantSelect) {
        const $customFeedbackParticipantTypeOption =
                $('<option></option>').attr('value', CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM)
                                      .text('Custom');
        $participantSelect.append($customFeedbackParticipantTypeOption)
                          .val(CustomFeedbackPaths.FEEDBACK_PARTICIPANT_TYPE_CUSTOM);
    },

    addRowsToFeedbackPathsSpreadsheet($questionForm) {
        const $container = $questionForm.find('.custom-feedback-paths-spreadsheet');
        const numRowsToAdd = parseInt($questionForm.find('.add-rows-input').val(), 10);
        const hotInstance = $container.handsontable('getInstance');
        if (numRowsToAdd > 0) {
            hotInstance.alter('insert_row', hotInstance.countRows(), numRowsToAdd);
        }
    },
};
