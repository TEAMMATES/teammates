import { Component, EventEmitter, Input, Output } from '@angular/core';
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
export class FeedbackPathPanelComponent {

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
  customFeedbackPath: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Output()
  customNumberOfEntitiesToGiveFeedbackTo: EventEmitter<number> = new EventEmitter<number>();

  @Output()
  numberOfEntitiesToGiveFeedbackToSetting: EventEmitter<NumberOfEntitiesToGiveFeedbackToSetting> =
    new EventEmitter<NumberOfEntitiesToGiveFeedbackToSetting>();

  @Output()
  triggerModelChangeBatch: EventEmitter<Partial<QuestionEditFormModel>> =
    new EventEmitter<Partial<QuestionEditFormModel>>();

  subMenuStatuses: Map<FeedbackParticipantType, boolean> = new Map();

  triggerCustomNumberOfEntities(data: number): void {
    this.customNumberOfEntitiesToGiveFeedbackTo.emit(data);
  }

  triggerNumberOfEntitiesSetting(data: NumberOfEntitiesToGiveFeedbackToSetting): void {
    this.numberOfEntitiesToGiveFeedbackToSetting.emit(data);
  }

  triggerCustomFeedbackPath(): void {
    this.customFeedbackPath.emit(true);
  }

  toggleSubMenu(menu: FeedbackParticipantType): void {
    this.subMenuStatuses.set(menu, !this.subMenuStatuses.get(menu));
  }

  resetMenu(): void {
    this.subMenuStatuses.forEach((_, key) => this.subMenuStatuses.set(key, false));
  }

  isSubMenuOpen(menu: FeedbackParticipantType): boolean {
    let subMenuState: boolean | undefined = this.subMenuStatuses.get(menu);
    if (subMenuState === undefined) {
      subMenuState = false;
    }
    return subMenuState;
  }

  /**
   * Change the {@code giverType} and {@code recipientType} and reset the visibility settings.
   */
  changeGiverRecipientType(giverType: FeedbackParticipantType, recipientType: FeedbackParticipantType): void {
    // check if current recipientType is allowed for giverType,
    // if not, set default recipientType to the first allowed type as default.
    // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
    const allowedRecipientTypes: FeedbackParticipantType[] = this.allowedFeedbackPaths.get(giverType)!;
    let newRecipientType: FeedbackParticipantType = recipientType;
    if (allowedRecipientTypes.indexOf(recipientType) === -1) {
      newRecipientType = allowedRecipientTypes[0];
    }
    if (this.model.giverType === giverType && this.model.recipientType === newRecipientType) {
      // do not reset the visibility settings if reverting feedback path to preset template provided
      if (this.model.isUsingOtherFeedbackPath) {
        // remove the custom feedback if selecting a common feedback path
        this.triggerModelChangeBatch.emit({
          isUsingOtherFeedbackPath: false,
        });
      }
    } else {
      this.triggerModelChangeBatch.emit({
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
