import { EventEmitter, Input, Output } from '@angular/core';
import {
  FeedbackParticipantType,
  FeedbackQuestion,
  FeedbackQuestionType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';
import { CommentTableModel } from '../comment-box/comment-table/comment-table.component';

/**
 * Abstract component for instructor related views.
 */
export abstract class InstructorResponsesViewBase {
  @Input() question: FeedbackQuestion = {
    feedbackQuestionId: '',
    questionNumber: 1,
    questionBrief: '',
    questionDescription: '',
    questionDetails: {
      questionType: FeedbackQuestionType.TEXT,
      questionText: '',
    },
    questionType: FeedbackQuestionType.TEXT,
    giverType: FeedbackParticipantType.STUDENTS,
    recipientType: FeedbackParticipantType.STUDENTS,
    numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED,
    customNumberOfEntitiesToGiveFeedbackTo: 0,
    showResponsesTo: [],
    showGiverNameTo: [],
    showRecipientNameTo: [],
  };

  @Input() instructorCommentTableModel: Record<string, CommentTableModel> = {};
  @Input() isExpandAll: boolean = false;

  @Output() instructorCommentTableModelChange: EventEmitter<Record<string, CommentTableModel>> = new EventEmitter();
  @Output() saveNewCommentEvent: EventEmitter<string> = new EventEmitter();
  @Output() deleteCommentEvent: EventEmitter<{
    responseId: string,
    index: number,
  }> = new EventEmitter();
  @Output() updateCommentEvent: EventEmitter<{
    responseId: string,
    index: number,
  }> = new EventEmitter();

  constructor() {

  }

  /**
   * Triggers the delete comment event.
   */
  triggerDeleteCommentEvent(responseId: string, index: number): void {
    this.deleteCommentEvent.emit({
      responseId,
      index,
    });
  }

  /**
   * Triggers the update comment event.
   */
  triggerUpdateCommentEvent(responseId: string, index: number): void {
    this.updateCommentEvent.emit({
      responseId,
      index,
    });
  }

  /**
   * Triggers the add new comment event.
   */
  triggerSaveNewCommentEvent(responseId: string): void {
    this.saveNewCommentEvent.emit(responseId);
  }

  /**
   * Triggers the change of the instructor comment table model for a single response.
   */
  triggerModelChangeForSingleResponse(responseId: string, model: CommentTableModel): void {
    // for performance consideration, we will not generate a copy
    this.instructorCommentTableModel[responseId] = model;
    this.instructorCommentTableModelChange.emit(this.instructorCommentTableModel);
  }

  /**
   * Triggers the change of the model.
   */
  triggerModelChange(instructorCommentTableModel: Record<string, CommentTableModel>): void {
    this.instructorCommentTableModelChange.emit(instructorCommentTableModel);
  }
}
