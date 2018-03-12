package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.pagedata.PageData;

public class FeedbackSessionsAdditionalSettingsFormSegment {
    private boolean isSessionVisibleDateButtonChecked;
    private String sessionVisibleDateValue;
    private boolean isSessionVisibleDateDisabled;
    private List<ElementTag> sessionVisibleTimeOptions;
    private boolean isSessionVisibleAtOpenChecked;
    private boolean isSessionVisiblePrivateChecked;

    private boolean isResponseVisibleDateChecked;
    private String responseVisibleDateValue;
    private boolean isResponseVisibleDateDisabled;
    private List<ElementTag> responseVisibleTimeOptions;
    private boolean isResponseVisibleImmediatelyChecked;
    private boolean isResponseVisiblePublishManuallyChecked;
    private boolean isResponseVisibleNeverChecked;

    private boolean isSendClosingEmailChecked;
    private boolean isSendOpeningEmailChecked;
    private boolean isSendPublishedEmailChecked;

    public boolean isSessionVisibleDateButtonChecked() {
        return isSessionVisibleDateButtonChecked;
    }

    public String getSessionVisibleDateValue() {
        return sessionVisibleDateValue;
    }

    public boolean isSessionVisibleDateDisabled() {
        return isSessionVisibleDateDisabled;
    }

    public List<ElementTag> getSessionVisibleTimeOptions() {
        return sessionVisibleTimeOptions;
    }

    public boolean isSessionVisibleAtOpenChecked() {
        return isSessionVisibleAtOpenChecked;
    }

    public boolean isSessionVisiblePrivateChecked() {
        return isSessionVisiblePrivateChecked;
    }

    public boolean isResponseVisibleDateChecked() {
        return isResponseVisibleDateChecked;
    }

    public String getResponseVisibleDateValue() {
        return responseVisibleDateValue;
    }

    public boolean isResponseVisibleDateDisabled() {
        return isResponseVisibleDateDisabled;
    }

    public List<ElementTag> getResponseVisibleTimeOptions() {
        return responseVisibleTimeOptions;
    }

    public boolean isResponseVisibleImmediatelyChecked() {
        return isResponseVisibleImmediatelyChecked;
    }

    public boolean isResponseVisiblePublishManuallyChecked() {
        return isResponseVisiblePublishManuallyChecked;
    }

    public boolean isResponseVisibleNeverChecked() {
        return isResponseVisibleNeverChecked;
    }

    public boolean isSendClosingEmailChecked() {
        return isSendClosingEmailChecked;
    }

    public boolean isSendOpeningEmailChecked() {
        return isSendOpeningEmailChecked;
    }

    public boolean isSendPublishedEmailChecked() {
        return isSendPublishedEmailChecked;
    }

    public void setSendPublishedEmailChecked(boolean isSendPublishedEmailChecked) {
        this.isSendPublishedEmailChecked = isSendPublishedEmailChecked;
    }

    public static FeedbackSessionsAdditionalSettingsFormSegment getDefaultFormSegment() {
        FeedbackSessionsAdditionalSettingsFormSegment additionalSettings =
                new FeedbackSessionsAdditionalSettingsFormSegment();

        additionalSettings.isSessionVisibleAtOpenChecked = true;
        additionalSettings.isSessionVisibleDateButtonChecked = false;
        additionalSettings.sessionVisibleDateValue = "";
        additionalSettings.isSessionVisibleDateDisabled = true;
        additionalSettings.sessionVisibleTimeOptions = PageData.getTimeOptionsAsElementTags(null);
        additionalSettings.isSessionVisiblePrivateChecked = false;

        additionalSettings.isResponseVisibleDateChecked = false;
        additionalSettings.responseVisibleDateValue = "";
        additionalSettings.isResponseVisibleDateDisabled = true;
        additionalSettings.responseVisibleTimeOptions = PageData.getTimeOptionsAsElementTags(null);
        additionalSettings.isResponseVisibleImmediatelyChecked = false;
        additionalSettings.isResponseVisiblePublishManuallyChecked = true;
        additionalSettings.isResponseVisibleNeverChecked = false;

        additionalSettings.isSendClosingEmailChecked = true;
        additionalSettings.isSendOpeningEmailChecked = true;
        additionalSettings.isSendPublishedEmailChecked = true;

        return additionalSettings;
    }

    public static FeedbackSessionsAdditionalSettingsFormSegment getFormSegmentWithExistingValues(
                                                                    FeedbackSessionAttributes feedbackSession) {
        FeedbackSessionsAdditionalSettingsFormSegment additionalSettings =
                new FeedbackSessionsAdditionalSettingsFormSegment();

        setSessionVisibleSettings(feedbackSession, additionalSettings);
        setResponseVisibleSettings(feedbackSession, additionalSettings);
        setEmailSettings(feedbackSession, additionalSettings);

        return additionalSettings;
    }

    private static void setEmailSettings(FeedbackSessionAttributes feedbackSession,
                                         FeedbackSessionsAdditionalSettingsFormSegment additionalSettings) {

        additionalSettings.isSendClosingEmailChecked = feedbackSession.isClosingEmailEnabled();
        additionalSettings.isSendOpeningEmailChecked = feedbackSession.isOpeningEmailEnabled();
        additionalSettings.isSendPublishedEmailChecked = feedbackSession.isPublishedEmailEnabled();
    }

    private static void setResponseVisibleSettings(FeedbackSessionAttributes feedbackSession,
                                                   FeedbackSessionsAdditionalSettingsFormSegment additionalSettings) {
        boolean hasResultVisibleDate = !TimeHelper.isSpecialTime(feedbackSession.getResultsVisibleFromTime());

        additionalSettings.isResponseVisibleDateChecked = hasResultVisibleDate;

        additionalSettings.responseVisibleDateValue = hasResultVisibleDate
                                                    ? TimeHelper.formatDateForSessionsForm(
                                                            feedbackSession.getResultsVisibleFromTimeLocal())
                                                    : "";
        additionalSettings.responseVisibleTimeOptions =
                                        PageData.getTimeOptionsAsElementTags(
                                                  hasResultVisibleDate
                                                ? feedbackSession.getResultsVisibleFromTimeLocal()
                                                : null);
        additionalSettings.isResponseVisibleDateDisabled = !hasResultVisibleDate;

        additionalSettings.isResponseVisibleImmediatelyChecked =
                                      Const.TIME_REPRESENTS_FOLLOW_VISIBLE.equals(
                                                                      feedbackSession.getResultsVisibleFromTime());

        additionalSettings.isResponseVisiblePublishManuallyChecked =
                                          Const.TIME_REPRESENTS_LATER.equals(feedbackSession.getResultsVisibleFromTime())
                                       || Const.TIME_REPRESENTS_NOW.equals(feedbackSession.getResultsVisibleFromTime());

        additionalSettings.isResponseVisibleNeverChecked = Const.TIME_REPRESENTS_NEVER.equals(
                                                                   feedbackSession.getResultsVisibleFromTime());
    }

    private static void setSessionVisibleSettings(FeedbackSessionAttributes feedbackSession,
                                                  FeedbackSessionsAdditionalSettingsFormSegment additionalSettings) {
        boolean hasSessionVisibleDate = !TimeHelper.isSpecialTime(feedbackSession.getSessionVisibleFromTime());

        additionalSettings.isSessionVisibleAtOpenChecked =
                                        Const.TIME_REPRESENTS_FOLLOW_OPENING.equals(
                                             feedbackSession.getSessionVisibleFromTime());
        additionalSettings.isSessionVisiblePrivateChecked =
                                        Const.TIME_REPRESENTS_NEVER.equals(
                                            feedbackSession.getSessionVisibleFromTime());

        additionalSettings.isSessionVisibleDateButtonChecked = hasSessionVisibleDate;
        additionalSettings.sessionVisibleDateValue = hasSessionVisibleDate
                                                   ? TimeHelper.formatDateForSessionsForm(
                                                           feedbackSession.getSessionVisibleFromTimeLocal())
                                                   : "";
        additionalSettings.isSessionVisibleDateDisabled = !hasSessionVisibleDate;
        additionalSettings.sessionVisibleTimeOptions = PageData.getTimeOptionsAsElementTags(
                                                            hasSessionVisibleDate
                                                            ? feedbackSession.getSessionVisibleFromTimeLocal()
                                                            : null);
    }

}
