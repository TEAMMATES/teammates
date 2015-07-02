package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.ui.controller.PageData;

public class AdditionalSettingsFormSegment {
    private boolean isSessionVisibleDateButtonChecked;
    private String sessionVisibleDateValue;
    private boolean isSessionVisibleDateDisabled;
    private List<ElementTag> sessionVisibleTimeOptions;
    private boolean sessionVisibleAtOpenChecked;
    private boolean sessionVisiblePrivateChecked;
    
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
    
    
    public AdditionalSettingsFormSegment() {
    }

    public void setSessionVisibleDateButtonChecked(boolean isSessionVisibleDateButtonChecked) {
        this.isSessionVisibleDateButtonChecked = isSessionVisibleDateButtonChecked;
    }

    public void setSessionVisibleDateValue(String sessionVisibleDateValue) {
        this.sessionVisibleDateValue = sessionVisibleDateValue;
    }

    public void setSessionVisibleDateDisabled(boolean isSessionVisibleDateDisabled) {
        this.isSessionVisibleDateDisabled = isSessionVisibleDateDisabled;
    }

    public void setSessionVisibleTimeOptions(List<ElementTag> sessionVisibleTimeOptions) {
        this.sessionVisibleTimeOptions = sessionVisibleTimeOptions;
    }

    public void setSessionVisibleAtOpenChecked(boolean sessionVisibleAtOpenChecked) {
        this.sessionVisibleAtOpenChecked = sessionVisibleAtOpenChecked;
    }

    public void setSessionVisiblePrivateChecked(boolean isSessionVisiblePrivateChecked) {
        this.sessionVisiblePrivateChecked = isSessionVisiblePrivateChecked;
    }

    public void setResponseVisibleDateChecked(boolean isResponseVisibleDateChecked) {
        this.isResponseVisibleDateChecked = isResponseVisibleDateChecked;
    }

    public void setResponseVisibleDateValue(String responseVisibleDateValue) {
        this.responseVisibleDateValue = responseVisibleDateValue;
    }
    
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
        return sessionVisibleAtOpenChecked;
    }

    public boolean isSessionVisiblePrivateChecked() {
        return sessionVisiblePrivateChecked;
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
    
    public void setResponseVisibleDateDisabled(boolean isResponseVisibleDisabled) {
        this.isResponseVisibleDateDisabled = isResponseVisibleDisabled;
    }

    public void setResponseVisibleTimeOptions(List<ElementTag> responseVisibleTimeOptions) {
        this.responseVisibleTimeOptions = responseVisibleTimeOptions;
    }

    public void setResponseVisibleImmediatelyChecked(boolean isResponseVisibleImmediatelyChecked) {
        this.isResponseVisibleImmediatelyChecked = isResponseVisibleImmediatelyChecked;
    }

    public void setResponseVisiblePublishManuallyChecked(boolean isResponseVisiblePublishManuallyChecked) {
        this.isResponseVisiblePublishManuallyChecked = isResponseVisiblePublishManuallyChecked;
    }

    public void setResponseVisibleNeverChecked(boolean isResponseVisibleNeverChecked) {
        this.isResponseVisibleNeverChecked = isResponseVisibleNeverChecked;
    }

    public boolean isSendClosingEmailChecked() {
        return isSendClosingEmailChecked;
    }

    public void setSendClosingEmailChecked(boolean isSendClosingEmailChecked) {
        this.isSendClosingEmailChecked = isSendClosingEmailChecked;
    }

    public boolean isSendOpeningEmailChecked() {
        return isSendOpeningEmailChecked;
    }

    public void setSendOpeningEmailChecked(boolean isSendOpeningEmailChecked) {
        this.isSendOpeningEmailChecked = isSendOpeningEmailChecked;
    }

    public boolean isSendPublishedEmailChecked() {
        return isSendPublishedEmailChecked;
    }

    public void setSendPublishedEmailChecked(boolean isSendPublishedEmailChecked) {
        this.isSendPublishedEmailChecked = isSendPublishedEmailChecked;
    }
    
    public static AdditionalSettingsFormSegment getDefaultFormSegment(PageData data) {
        AdditionalSettingsFormSegment additionalSettings = new AdditionalSettingsFormSegment(); 
        
        additionalSettings.setSessionVisibleAtOpenChecked(true);
        additionalSettings.setSessionVisibleDateButtonChecked(false);
        additionalSettings.setSessionVisibleDateValue("");
        additionalSettings.setSessionVisibleDateDisabled(true);
        additionalSettings.setSessionVisibleTimeOptions(data.getTimeOptionsAsElementTags(null));
        additionalSettings.setSessionVisiblePrivateChecked(false);

        
        additionalSettings.setResponseVisibleDateChecked(false);
        additionalSettings.setResponseVisibleDateValue("");
        additionalSettings.setResponseVisibleDateDisabled(true);
        additionalSettings.setResponseVisibleTimeOptions(data.getTimeOptionsAsElementTags(null));
        additionalSettings.setResponseVisibleImmediatelyChecked(false);
        additionalSettings.setResponseVisiblePublishManuallyChecked(true);
        additionalSettings.setResponseVisibleNeverChecked(false);
        
        
        additionalSettings.setSendClosingEmailChecked(true);
        additionalSettings.setSendOpeningEmailChecked(true);
        additionalSettings.setSendPublishedEmailChecked(true);
        
        return additionalSettings;
    }
    
    public static AdditionalSettingsFormSegment getFormSegmentWithExistingValues(PageData data, 
                                                                                 FeedbackSessionAttributes newFeedbackSession) {
        AdditionalSettingsFormSegment additionalSettings = new AdditionalSettingsFormSegment();
        
        setSessionVisibleSettings(data, newFeedbackSession, additionalSettings);
        setResponseVisibleSettings(data, newFeedbackSession, additionalSettings);
        setEmailSettings(newFeedbackSession, additionalSettings);
        
        return additionalSettings;
    }

    private static void setEmailSettings(FeedbackSessionAttributes newFeedbackSession,
                                    AdditionalSettingsFormSegment additionalSettings) {
        additionalSettings.setSendClosingEmailChecked(newFeedbackSession.isClosingEmailEnabled);
        additionalSettings.setSendOpeningEmailChecked(newFeedbackSession.isOpeningEmailEnabled);
        additionalSettings.setSendPublishedEmailChecked(newFeedbackSession.isPublishedEmailEnabled);
    }

    private static void setResponseVisibleSettings(PageData data,
                                                    FeedbackSessionAttributes newFeedbackSession,
                                                    AdditionalSettingsFormSegment additionalSettings) {
        boolean hasResultVisibleDate = !TimeHelper.isSpecialTime(newFeedbackSession.resultsVisibleFromTime);
        additionalSettings.setResponseVisibleDateChecked(hasResultVisibleDate);
        additionalSettings.setResponseVisibleDateValue(hasResultVisibleDate ? 
                                                       TimeHelper.formatDate(newFeedbackSession.resultsVisibleFromTime) :
                                                       "");
        additionalSettings.setResponseVisibleTimeOptions(data.getTimeOptionsAsElementTags(
                                                            hasResultVisibleDate ?
                                                            newFeedbackSession.resultsVisibleFromTime :
                                                            null));
        additionalSettings.setResponseVisibleDateDisabled(!hasResultVisibleDate);
        additionalSettings.setResponseVisibleImmediatelyChecked(
                                      Const.TIME_REPRESENTS_FOLLOW_VISIBLE.equals(
                                      newFeedbackSession.resultsVisibleFromTime));
        additionalSettings.setResponseVisiblePublishManuallyChecked(
                                          Const.TIME_REPRESENTS_LATER.equals(newFeedbackSession.resultsVisibleFromTime) 
                                          || Const.TIME_REPRESENTS_NOW.equals(newFeedbackSession.resultsVisibleFromTime));
        additionalSettings.setResponseVisibleNeverChecked(Const.TIME_REPRESENTS_NEVER.equals(newFeedbackSession.resultsVisibleFromTime));
    }

    private static void setSessionVisibleSettings(PageData data,
                                    FeedbackSessionAttributes newFeedbackSession,
                                    AdditionalSettingsFormSegment additionalSettings) {
        boolean hasSessionVisibleDate = !TimeHelper.isSpecialTime(newFeedbackSession.sessionVisibleFromTime);
        
        additionalSettings.setSessionVisibleAtOpenChecked(
                                        Const.TIME_REPRESENTS_FOLLOW_OPENING.equals(
                                             newFeedbackSession.sessionVisibleFromTime));
        additionalSettings.setSessionVisiblePrivateChecked( 
                                        Const.TIME_REPRESENTS_NEVER.equals(
                                            newFeedbackSession.sessionVisibleFromTime));
        
        additionalSettings.setSessionVisibleDateButtonChecked(hasSessionVisibleDate);
        additionalSettings.setSessionVisibleDateValue(hasSessionVisibleDate ? 
                                                      TimeHelper.formatDate(newFeedbackSession.sessionVisibleFromTime) :
                                                      "");
        additionalSettings.setSessionVisibleDateDisabled(!hasSessionVisibleDate);
        additionalSettings.setSessionVisibleTimeOptions(data.getTimeOptionsAsElementTags(
                                                            hasSessionVisibleDate ?
                                                            newFeedbackSession.sessionVisibleFromTime :
                                                            null));
    }
    
}