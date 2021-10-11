import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

import { QuestionEditFormModel } from '../question-edit-form/question-edit-form-model';

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

/**
 * Displaying the visibility panel.
 */
@Component({
  selector: 'tm-visibility-panel',
  templateUrl: './visibility-panel.component.html',
  styleUrls: ['./visibility-panel.component.scss'],
})
export class VisibilityPanelComponent implements OnInit {

  // enum
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;
  NumberOfEntitiesToGiveFeedbackToSetting: typeof NumberOfEntitiesToGiveFeedbackToSetting =
        NumberOfEntitiesToGiveFeedbackToSetting;
  VisibilityControl: typeof VisibilityControl = VisibilityControl;
  FeedbackVisibilityType: typeof FeedbackVisibilityType = FeedbackVisibilityType;

  @Input()
  isCustomFeedbackVisibilitySettingAllowed: boolean = false;

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
    recipientType: FeedbackParticipantType.STUDENTS,

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
  visibilityControls: VisibilityControl = VisibilityControl.SHOW_RESPONSE;

  @Input()
  feedbackVisibilityTypes: FeedbackVisibilityType = FeedbackVisibilityType.INSTRUCTORS;

  @Input()
  commonFeedbackVisibilitySettings: CommonVisibilitySetting[] = [];

  @Input()
  visibilityStateMachine: VisibilityStateMachine =
    new VisibilityStateMachine(this.model.giverType, this.model.recipientType);

  @Output()
  applyCommonVisibilitySettingsEvent: EventEmitter<CommonVisibilitySetting> =
    new EventEmitter<CommonVisibilitySetting>();

  @Output()
  triggerModelChangeEvent: EventEmitter<{ field: keyof QuestionEditFormModel,
    data: QuestionEditFormModel[keyof QuestionEditFormModel] }> = new EventEmitter<{ field: keyof QuestionEditFormModel,
      data: QuestionEditFormModel[keyof QuestionEditFormModel] }>();

  @Output()
  modifyVisibilityControlEvent: EventEmitter<{ isAllowed: boolean, visibilityType: FeedbackVisibilityType,
    visibilityControl: VisibilityControl }> = new EventEmitter<{ isAllowed: boolean,
      visibilityType: FeedbackVisibilityType, visibilityControl: VisibilityControl }>();

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Handles application of the common visibility setting.
   */
  applyCommonVisibilitySettingsHandler(commonSettings: CommonVisibilitySetting): void {
    this.applyCommonVisibilitySettingsEvent.emit(commonSettings);
  }

  /**
   * Handles the triggering of the change of the model for the form.
   */
  triggerModelChangeHandler(field: keyof QuestionEditFormModel,
    data: QuestionEditFormModel[keyof QuestionEditFormModel]): void {
    this.triggerModelChangeEvent.emit({ field, data });
  }

  /**
   * Handles modifying of visibility control of visibility type based on {@code isAllowed}.
   */
  modifyVisibilityControlHandler(isAllowed: boolean, visibilityType: FeedbackVisibilityType,
    visibilityControl: VisibilityControl): void {
    this.modifyVisibilityControlEvent.emit({ isAllowed, visibilityType, visibilityControl });
  }
}
