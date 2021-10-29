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

const VISIBILITY_PROPERTIES: Set<string> = new Set<string>([
  'isUsingOtherVisibilitySetting',
  'showResponsesTo',
  'showGiverNameTo',
  'showRecipientNameTo',
  'commonVisibilitySettingName',
]);

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
  set formModel(model: QuestionEditFormModel) {
    this.model = model;

    const visibilitySetting: {[TKey in VisibilityControl]: FeedbackVisibilityType[]} = {
      SHOW_RESPONSE: model.showResponsesTo,
      SHOW_GIVER_NAME: model.showGiverNameTo,
      SHOW_RECIPIENT_NAME: model.showRecipientNameTo,
    };
    this.visibilityStateMachine.applyVisibilitySettings(visibilitySetting);

    if (!model.commonVisibilitySettingName && !model.isUsingOtherVisibilitySetting) {
      // find if the visibility settings is in the common visibility settings
      this.model.isUsingOtherVisibilitySetting = true;
      for (const commonVisibilityOption of this.commonFeedbackVisibilitySettings) {
        if (this.isSameSet(visibilitySetting.SHOW_RESPONSE, commonVisibilityOption.visibilitySettings.SHOW_RESPONSE)
            && this.isSameSet(visibilitySetting.SHOW_GIVER_NAME,
                commonVisibilityOption.visibilitySettings.SHOW_GIVER_NAME)
            && this.isSameSet(visibilitySetting.SHOW_RECIPIENT_NAME,
                commonVisibilityOption.visibilitySettings.SHOW_RECIPIENT_NAME)) {
          this.model.commonVisibilitySettingName = commonVisibilityOption.name;
          this.model.isUsingOtherVisibilitySetting = false;
          break;
        }
      }
    }
  }

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
  formModelChange: EventEmitter<QuestionEditFormModel> = new EventEmitter<QuestionEditFormModel>();

  constructor() { }

  private isSameSet(setA: FeedbackVisibilityType[], setB: FeedbackVisibilityType[]): boolean {
    return setA.length === setB.length && setA.every((ele: FeedbackVisibilityType) => setB.includes(ele));
  }

  ngOnInit(): void {
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: keyof QuestionEditFormModel,
                     data: QuestionEditFormModel[keyof QuestionEditFormModel]): void {
    this.formModelChange.emit({
      ...this.model,
      [field]: data,
      ...(!this.model.isVisibilityChanged && VISIBILITY_PROPERTIES.has(field)
        && { isVisibilityChanged: true }),
    });
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChangeBatch(obj: Partial<QuestionEditFormModel>): void {
    this.formModelChange.emit({
      ...this.model,
      ...obj,
      ...(!this.model.isVisibilityChanged
          && Object.keys(obj).some((key: string) => VISIBILITY_PROPERTIES.has(key))
          && { isVisibilityChanged: true }),
    });
  }

  /**
   * Applies the common visibility setting.
   */
  applyCommonVisibilitySettings(commonSettings: CommonVisibilitySetting): void {
    this.triggerModelChangeBatch({
      showResponsesTo: commonSettings.visibilitySettings.SHOW_RESPONSE,
      showGiverNameTo: commonSettings.visibilitySettings.SHOW_GIVER_NAME,
      showRecipientNameTo: commonSettings.visibilitySettings.SHOW_RECIPIENT_NAME,
      commonVisibilitySettingName: commonSettings.name,
      isUsingOtherVisibilitySetting: false,
      isVisibilityChanged: true,
    });
  }

  /**
   * Modifies visibility control of visibility type based on {@code isAllowed}.
   */
  modifyVisibilityControl(
      isAllowed: boolean, visibilityType: FeedbackVisibilityType, visibilityControl: VisibilityControl): void {
    if (isAllowed) {
      this.visibilityStateMachine.allowToSee(visibilityType, visibilityControl);
    } else {
      this.visibilityStateMachine.disallowToSee(visibilityType, visibilityControl);
    }
    this.triggerModelChangeBatch({
      showResponsesTo:
          this.visibilityStateMachine.getVisibilityTypesUnderVisibilityControl(VisibilityControl.SHOW_RESPONSE),
      showGiverNameTo:
          this.visibilityStateMachine.getVisibilityTypesUnderVisibilityControl(VisibilityControl.SHOW_GIVER_NAME),
      showRecipientNameTo:
          this.visibilityStateMachine.getVisibilityTypesUnderVisibilityControl(VisibilityControl.SHOW_RECIPIENT_NAME),
    });
  }
}
