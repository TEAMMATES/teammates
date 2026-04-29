import { Directive, EventEmitter, Input, Output } from '@angular/core';
import { InstructorSessionResultSectionType } from './instructor-session-result-section-type.enum';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
} from '../../../types/api-output';
import { CommentTableModel } from '../../components/comment-box/comment-table/comment-table.model';

/**
 * Abstract component for all different view type components of instructor sessions result page.
 */
@Directive()
export abstract class InstructorSessionResultView {

  @Input() section = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  @Input() groupByTeam = true;
  @Input() showStatistics = true;
  @Input() indicateMissingResponses = true;
  @Input() session: FeedbackSession = {
    feedbackSessionId: '',
    courseId: '',
    timeZone: '',
    feedbackSessionName: '',
    instructions: '',
    submissionStartTimestamp: 0,
    submissionEndTimestamp: 0,
    gracePeriod: 0,
    sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
    responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
    submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
    publishStatus: FeedbackSessionPublishStatus.NOT_PUBLISHED,
    isClosingSoonEmailEnabled: true,
    isPublishedEmailEnabled: true,
    createdAtTimestamp: 0,
  };
  @Input() instructorCommentTableModel: Record<string, CommentTableModel> = {};

  @Input() isExpandAll = false;

  @Output() toggleAndLoadTab: EventEmitter<string> = new EventEmitter<string>();
  @Output() loadTab: EventEmitter<string> = new EventEmitter<string>();

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

  constructor(protected viewType: InstructorSessionResultViewType) {}

  /**
   * Triggers the change of {@code instructorCommentTableModel}
   */
  triggerInstructorCommentTableModelChange(model: Record<string, CommentTableModel>): void {
    this.instructorCommentTableModelChange.emit(model);
  }

  /**
   * Triggers the delete comment event.
   */
  triggerDeleteCommentEvent($event: { responseId: string, index: number }): void {
    this.deleteCommentEvent.emit($event);
  }

  /**
   * Triggers the update comment event.
   */
  triggerUpdateCommentEvent($event: { responseId: string, index: number }): void {
    this.updateCommentEvent.emit($event);
  }

  /**
   * Triggers the add new comment event.
   */
  triggerSaveNewCommentEvent(responseId: string): void {
    this.saveNewCommentEvent.emit(responseId);
  }

}
