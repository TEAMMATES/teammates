import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackTextQuestionDetails,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';
import { QuestionEditFormModel } from '../question-edit-form/question-edit-form-model';

const FEEDBACK_PATH_PROPERTIES: Set<string> = new Set<string>([
  'giverType',
  'recipientType',
  'isUsingOtherFeedbackPath',
  'numberOfEntitiesToGiveFeedbackToSetting',
  'customNumberOfEntitiesToGiveFeedbackTo',
]);

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
  modelChange : EventEmitter<QuestionEditFormModel> = new EventEmitter<QuestionEditFormModel>();

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: keyof QuestionEditFormModel,
                     data: QuestionEditFormModel[keyof QuestionEditFormModel]): void {
    this.modelChange.emit({
      ...this.model,
      [field]: data,
      ...(!this.model.isFeedbackPathChanged && FEEDBACK_PATH_PROPERTIES.has(field)
        && { isFeedbackPathChanged: true }),
    });
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChangeBatch(obj: Partial<QuestionEditFormModel>): void {
    this.modelChange.emit({
      ...this.model,
      ...obj,
      ...(!this.model.isFeedbackPathChanged
        && Object.keys(obj).some((key: string) => FEEDBACK_PATH_PROPERTIES.has(key))
        && { isFeedbackPathChanged: true }),
    });
  }

  /**
     * Change the {@code giverType} and {@code recipientType} and reset the visibility settings.
     */
    changeGiverRecipientType(giverType: FeedbackParticipantType, recipientType: FeedbackParticipantType): void {
      // check if current recipientType is allowed for giverType,
      // if not, set default recipientType to the first allowed type as default.
      /* tslint:disable-next-line: no-non-null-assertion */
      const allowedRecipientTypes: FeedbackParticipantType[] = this.allowedFeedbackPaths.get(giverType)!;

      let newRecipientType: FeedbackParticipantType = recipientType;
      if (allowedRecipientTypes.indexOf(recipientType) === -1) {
        newRecipientType = allowedRecipientTypes[0];
      }
      if (this.model.giverType === giverType && this.model.recipientType === newRecipientType) {
        // do not reset the visibility settings if reverting feedback path to preset template provided
        if (this.model.isUsingOtherFeedbackPath) {
          // remove the custom feedback if selecting a common feedback path
          this.modelChange.emit({
            ...this.model,
            isUsingOtherFeedbackPath: true,
          });
        }
      } else {
        this.modelChange.emit({
          ...this.model,
          giverType,
          recipientType: newRecipientType,
          commonVisibilitySettingName: 'Please select a visibility option',
          isUsingOtherFeedbackPath: false,
          isUsingOtherVisibilitySetting: false,
          showResponsesTo: [],
          showGiverNameTo: [],
          showRecipientNameTo: [],
        });
      }
    }

}
