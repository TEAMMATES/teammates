import { Component, EventEmitter, Input, Output } from '@angular/core';

import { CommonVisibilitySetting } from '../../../services/feedback-questions.service';
import { VisibilityStateMachine } from '../../../services/visibility-state-machine';
import {
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackTextQuestionDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';
import { VisibilityControl } from '../../../types/visibility-control';
import { QuestionEditFormModel } from '../question-edit-form/question-edit-form-model';

/**
 * Displaying the visibility panel.
 */
@Component({
  selector: 'tm-visibility-panel',
  templateUrl: './visibility-panel.component.html',
  styleUrls: ['./visibility-panel.component.scss'],
})
export class VisibilityPanelComponent {

  // enum
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;
  NumberOfEntitiesToGiveFeedbackToSetting: typeof NumberOfEntitiesToGiveFeedbackToSetting =
        NumberOfEntitiesToGiveFeedbackToSetting;
  VisibilityControl: typeof VisibilityControl = VisibilityControl;
  FeedbackVisibilityType: typeof FeedbackVisibilityType = FeedbackVisibilityType;

  @Input()
  model: QuestionEditFormModel = {
    feedbackQuestionId: '',

    questionNumber: 0,
    questionBrief: '',
    questionDescription: '',

    isQuestionHasResponses: false,

    questionType: FeedbackQuestionType.TEXT,
    questionDetails: {
      questionType: FeedbackQuestionType.TEXT,
      questionText: '',
    } as FeedbackTextQuestionDetails,

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.STUDENTS_EXCLUDING_SELF,

    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
    customNumberOfEntitiesToGiveFeedbackTo: 1,

    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],

    commonVisibilitySettingName: 'Please select a visibility option',

    isUsingOtherFeedbackPath: false,
    isUsingOtherVisibilitySetting: false,
    isDeleting: false,
    isDuplicating: false,
    isEditable: false,
    isSaving: false,
    isCollapsed: false,
    isVisibilityChanged: false,
    isFeedbackPathChanged: false,
    isQuestionDetailsChanged: false,
  };

  @Input()
  isCustomFeedbackVisibilitySettingAllowed: boolean = false;

  @Input()
  commonFeedbackVisibilitySettings: CommonVisibilitySetting[] = [];

  @Input()
  visibilityStateMachine: VisibilityStateMachine =
    new VisibilityStateMachine(this.model.giverType, this.model.recipientType);

  @Output()
  customVisibilitySetting: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Output()
  triggerModelChangeBatch: EventEmitter<Partial<QuestionEditFormModel>> =
    new EventEmitter<Partial<QuestionEditFormModel>>();

  @Output()
  visibilityStateMachineChange: EventEmitter<VisibilityStateMachine> = new EventEmitter<VisibilityStateMachine>();

  visibilityTypeAriaLabels: Map<FeedbackVisibilityType, string> = new Map([
    [FeedbackVisibilityType.RECIPIENT, 'Recipient(s)'],
    [FeedbackVisibilityType.GIVER_TEAM_MEMBERS, 'Giver\'s Team Members'],
    [FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS, 'Recipient\'s Team Members'],
    [FeedbackVisibilityType.STUDENTS, 'Other Students'],
    [FeedbackVisibilityType.INSTRUCTORS, 'Instructors'],
  ]);

  visibilityControlAriaLabels: Map<VisibilityControl, string> = new Map([
    [VisibilityControl.SHOW_RESPONSE, 'Answer'],
    [VisibilityControl.SHOW_GIVER_NAME, 'Giver\'s Name'],
    [VisibilityControl.SHOW_RECIPIENT_NAME, 'Recipient\'s Name'],
  ]);

  triggerCustomVisibilitySetting(): void {
    this.customVisibilitySetting.emit(true);
  }

  getCheckboxAriaLabel(visibilityType: FeedbackVisibilityType, visibilityControl: VisibilityControl): string {
    const group: string = this.visibilityTypeAriaLabels.get(visibilityType) || '';
    const groupVisibility: string = this.visibilityControlAriaLabels.get(visibilityControl) || '';
    return `${group} can see ${groupVisibility}`;
  }

  /**
   * Applies the common visibility setting.
   */
  applyCommonVisibilitySettings(commonSettings: CommonVisibilitySetting): void {
    this.triggerModelChangeBatch.emit({
      showResponsesTo: commonSettings.visibilitySettings.SHOW_RESPONSE,
      showGiverNameTo: commonSettings.visibilitySettings.SHOW_GIVER_NAME,
      showRecipientNameTo: commonSettings.visibilitySettings.SHOW_RECIPIENT_NAME,
      commonVisibilitySettingName: commonSettings.name,
      isUsingOtherVisibilitySetting: false,
    });
  }

  /**
   * Modifies visibility control of visibility type based on {@code isAllowed}.
   */
  modifyVisibilityControl(
      isAllowed: boolean, visibilityType: FeedbackVisibilityType, visibilityControl: VisibilityControl): void {
    if (isAllowed) {
      this.visibilityStateMachine.allowToSee(visibilityType, visibilityControl);
      this.visibilityStateMachineChange.emit(this.visibilityStateMachine);
    } else {
      this.visibilityStateMachine.disallowToSee(visibilityType, visibilityControl);
      this.visibilityStateMachineChange.emit(this.visibilityStateMachine);
    }
    this.triggerModelChangeBatch.emit({
      showResponsesTo:
          this.visibilityStateMachine.getVisibilityTypesUnderVisibilityControl(VisibilityControl.SHOW_RESPONSE),
      showGiverNameTo:
          this.visibilityStateMachine.getVisibilityTypesUnderVisibilityControl(VisibilityControl.SHOW_GIVER_NAME),
      showRecipientNameTo:
          this.visibilityStateMachine.getVisibilityTypesUnderVisibilityControl(VisibilityControl.SHOW_RECIPIENT_NAME),
    });
  }
}
