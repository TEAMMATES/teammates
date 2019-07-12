import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackQuestionsService } from '../../../services/feedback-questions.service';
import { FeedbackResponsesService } from '../../../services/feedback-responses.service';
import { VisibilityStateMachine } from '../../../services/visibility-state-machine';
import {
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackTextQuestionDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';
import { VisibilityControl } from '../../../types/visibility-control';
import { CommentTableMode } from '../comment-box/comment-table/comment-table-model';
import {
  FeedbackResponseRecipient,
  FeedbackResponseRecipientSubmissionFormModel,
  QuestionSubmissionFormMode,
  QuestionSubmissionFormModel,
} from './question-submission-form-model';

/**
 * The question submission form for a question.
 */
@Component({
  selector: 'tm-question-submission-form',
  templateUrl: './question-submission-form.component.html',
  styleUrls: ['./question-submission-form.component.scss'],
})
export class QuestionSubmissionFormComponent implements OnInit {

  // enum
  QuestionSubmissionFormMode: typeof QuestionSubmissionFormMode = QuestionSubmissionFormMode;
  CommentTableMode: typeof CommentTableMode = CommentTableMode;
  FeedbackQuestionType: typeof FeedbackQuestionType = FeedbackQuestionType;
  FeedbackParticipantType: typeof FeedbackParticipantType = FeedbackParticipantType;
  FeedbackVisibilityType: typeof FeedbackVisibilityType = FeedbackVisibilityType;

  @Input()
  formMode: QuestionSubmissionFormMode = QuestionSubmissionFormMode.FIXED_RECIPIENT;

  @Input()
  isDisabled: boolean = false;

  @Input()
  set formModel(model: QuestionSubmissionFormModel) {
    this.model = model;

    this.visibilityStateMachine =
        this.feedbackQuestionsService.getNewVisibilityStateMachine(model.giverType, model.recipientType);
    const visibilitySetting: {[TKey in VisibilityControl]: FeedbackVisibilityType[]} = {
      SHOW_RESPONSE: model.showResponsesTo,
      SHOW_GIVER_NAME: model.showGiverNameTo,
      SHOW_RECIPIENT_NAME: model.showRecipientNameTo,
    };
    this.visibilityStateMachine.applyVisibilitySettings(visibilitySetting);
  }

  @Output()
  formModelChange: EventEmitter<QuestionSubmissionFormModel> = new EventEmitter();

  model: QuestionSubmissionFormModel = {
    feedbackQuestionId: '',

    questionNumber: 0,
    questionBrief: '',
    questionDescription: '',

    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.STUDENTS,
    recipientList: [],
    recipientSubmissionForms: [],

    questionType: FeedbackQuestionType.TEXT,
    questionDetails: {
      recommendedLength: 0,
      questionText: '',
      questionType: FeedbackQuestionType.TEXT,
    } as FeedbackTextQuestionDetails,

    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 0,

    showGiverNameTo: [],
    showRecipientNameTo: [],
    showResponsesTo: [],
  };

  @Output() deleteCommentEvent: EventEmitter<any> = new EventEmitter();

  visibilityStateMachine: VisibilityStateMachine;

  isCommentTableExpanded: boolean[] = [];

  constructor(private feedbackQuestionsService: FeedbackQuestionsService,
              private  feedbackResponsesService: FeedbackResponsesService) {
    this.visibilityStateMachine =
        this.feedbackQuestionsService.getNewVisibilityStateMachine(
            this.model.giverType, this.model.recipientType);
  }

  ngOnInit(): void {
  }

  /**
   * Tracks submission form for each recipient by the index in the array.
   *
   * @see https://angular.io/api/common/NgForOf#properties
   */
  trackRecipientSubmissionFormByFn(index: number): any {
    return index;
  }

  /**
   * Gets recipient name in {@code FIXED_RECIPIENT} mode.
   */
  getRecipientName(recipientIdentifier: string): string {
    const recipient: FeedbackResponseRecipient | undefined =
        this.model.recipientList.find(
            (r: FeedbackResponseRecipient) => r.recipientIdentifier === recipientIdentifier);
    return recipient ? recipient.recipientName : 'Unknown';
  }

  /**
   * Checks whether the recipient is already selected in {@code FLEXIBLE_RECIPIENT} mode.
   */
  isRecipientSelected(recipient: FeedbackResponseRecipient): boolean {
    return this.model.recipientSubmissionForms.some(
        (recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel) =>
            recipientSubmissionFormModel.recipientIdentifier === recipient.recipientIdentifier);
  }

  /**
   * Triggers the change of the recipient submission form.
   */
  triggerRecipientSubmissionFormChange(index: number, field: string, data: any): void {
    const recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[] =
        this.model.recipientSubmissionForms.slice();
    recipientSubmissionForms[index] = {
      ...recipientSubmissionForms[index],
      [field]: data,
    };

    this.formModelChange.emit({
      ...this.model,
      recipientSubmissionForms,
    });
  }

  /**
   * Triggers deletion of a comment
   */
  triggerDeleteCommentEvent(index: number, commentId: number): void {
    this.deleteCommentEvent.emit({ commentId, recipientIndex: index });
  }

  /**
   * Checks whether to show the comments table or not.
   */
  isCommentsTableShown(questionType: FeedbackQuestionType,
                       recipientSubmissionFormModel: FeedbackResponseRecipientSubmissionFormModel): boolean {
    if (questionType !== FeedbackQuestionType.MCQ && questionType !== FeedbackQuestionType.MSQ) {
      return false;
    }

    return !this.feedbackResponsesService.isFeedbackResponseDetailsEmpty(
        questionType, recipientSubmissionFormModel.responseDetails);

  }
}
