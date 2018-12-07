const FeedbackPath = {
    attachEvents() {
        const allDropdownOptions = $('.feedback-path-dropdown-option');
        FeedbackPath.attachEventsForAllOptions(allDropdownOptions);

        const commonOptions = allDropdownOptions.not('.feedback-path-dropdown-option-other');
        FeedbackPath.attachEventsForCommonOptions(commonOptions);

        const otherOption = $('.feedback-path-dropdown-option-other');
        FeedbackPath.attachEventsForOtherOption(otherOption);
    },

    attachEventsForAllOptions(allDropdownOptions) {
        allDropdownOptions.on('click', (event) => {
            const clickedElem = $(event.currentTarget);
            const containingForm = FeedbackPath.getContainingForm(clickedElem);

            FeedbackPath.setDropdownText(clickedElem.data('pathDescription'), containingForm);
        });
    },

    attachEventsForCommonOptions(commonOptions) {
        commonOptions.on('click', (event) => {
            const clickedElem = $(event.currentTarget);
            const containingForm = FeedbackPath.getContainingForm(clickedElem);

            FeedbackPath.updateInputTags(
                    clickedElem.data('giverType'), clickedElem.data('recipientType'), containingForm);
            FeedbackPath.hideOtherOption(containingForm);
        });
    },

    attachEventsForOtherOption(otherOption) {
        otherOption.on('click', (event) => {
            const clickedElem = $(event.currentTarget);
            const containingForm = FeedbackPath.getContainingForm(clickedElem);

            FeedbackPath.showOtherOption(containingForm);
        });
    },

    showOtherOption(containingForm) {
        containingForm.find('.feedback-path-others').show();
    },

    hideOtherOption(containingForm) {
        containingForm.find('.feedback-path-others').hide();
        containingForm.find('[class*= numberOfEntitiesElements]').hide();
    },

    updateInputTags(giverType, recipientType, containingForm) {
        containingForm.find('[id^=givertype]').val(giverType);
        containingForm.find('[id^=givertype]').trigger('change');

        containingForm.find('[id^=recipienttype]').val(recipientType);
        containingForm.find('[id^=recipienttype]').trigger('change');
    },

    getDropdownText(containingForm) {
        const feedbackPathDropdown = containingForm.find('.feedback-path-dropdown');
        return feedbackPathDropdown.find('button').html();
    },

    setDropdownText(text, containingForm) {
        const feedbackPathDropdown = containingForm.find('.feedback-path-dropdown');
        feedbackPathDropdown.find('button').html(text);
    },

    getContainingForm(elem) {
        return elem.closest('form');
    },

    isCommonOptionSelected(containingForm) {
        return containingForm.find('.feedback-path-dropdown > button').html().trim() !== 'Predefined combinations:';
    },
};

export {
    FeedbackPath,
};
