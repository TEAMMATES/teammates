import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackTextQuestionDetails,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';
import { QuestionEditFormModel } from '../question-edit-form/question-edit-form-model';

/**
 * Displaying the feedback path panel.
 */
@Component({
  selector: 'tm-feedback-path-panel',
  templateUrl: './feedback-path-panel.component.html',
  styleUrls: ['./feedback-path-panel.component.scss'],
})
export class FeedbackPathPanelComponent implements OnInit {

  // enum
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;
  NumberOfEntitiesToGiveFeedbackToSetting: typeof NumberOfEntitiesToGiveFeedbackToSetting =
      NumberOfEntitiesToGiveFeedbackToSetting;

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
    recipientType: FeedbackParticipantType.OWN_TEAM_MEMBERS,

    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 1,

    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],

    commonVisibilitySettingName: '',

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
  commonFeedbackPaths: Map<FeedbackParticipantType, FeedbackParticipantType[]> = new Map();

  @Input()
  allowedFeedbackPaths: Map<FeedbackParticipantType, FeedbackParticipantType[]> = new Map();

  @Output()
  changeGiverRecipientTypeEvent: EventEmitter<{ giverType: FeedbackParticipantType,
    recipientType: FeedbackParticipantType }> = new EventEmitter<{ giverType: FeedbackParticipantType,
      recipientType: FeedbackParticipantType }>();

  @Output()
  triggerModelChangeEvent: EventEmitter<{ field: keyof QuestionEditFormModel,
    data: QuestionEditFormModel[keyof QuestionEditFormModel] }> = new EventEmitter<{ field: keyof QuestionEditFormModel,
      data: QuestionEditFormModel[keyof QuestionEditFormModel] }>();

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Handles the changing of the {@code giverType} and {@code recipientType} and reset the visibility settings.
   */
  changeGiverRecipientTypeHandler(giverType: FeedbackParticipantType, recipientType: FeedbackParticipantType): void {
    this.changeGiverRecipientTypeEvent.emit({ giverType, recipientType });
  }

  /**
   * Handles triggering of the change of the model for the form.
   */
  triggerModelChangeHandler(field: keyof QuestionEditFormModel,
    data: QuestionEditFormModel[keyof QuestionEditFormModel]): void {
    this.triggerModelChangeEvent.emit({ field, data });
  }
}
