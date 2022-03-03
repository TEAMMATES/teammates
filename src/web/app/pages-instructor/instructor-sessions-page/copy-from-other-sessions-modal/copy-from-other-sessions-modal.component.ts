import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Course, FeedbackSession } from '../../../../types/api-output';
import { FEEDBACK_SESSION_NAME_MAX_LENGTH } from '../../../../types/field-validator';

/**
 * Modal for creating new feedback session by copying from other feedback sessions.
 */
@Component({
  selector: 'tm-copy-from-other-sessions-modal',
  templateUrl: './copy-from-other-sessions-modal.component.html',
  styleUrls: ['./copy-from-other-sessions-modal.component.scss'],
})
export class CopyFromOtherSessionsModalComponent {

  // const
  FEEDBACK_SESSION_NAME_MAX_LENGTH: number = FEEDBACK_SESSION_NAME_MAX_LENGTH;

  @Input()
  courseCandidates: Course[] = [];

  @Input()
  existingFeedbackSession: FeedbackSession[] = [];

  copyToCourseId: string = '';
  newFeedbackSessionName: string = '';
  copyFromFeedbackSession: FeedbackSession | undefined;

  constructor(public activeModal: NgbActiveModal) {}

  /**
   * Copies the selected feedback session.
   */
  copy(): void {
    this.activeModal.close({
      fromFeedbackSession: this.copyFromFeedbackSession,
      newFeedbackSessionName: this.newFeedbackSessionName,
      copyToCourseId: this.copyToCourseId,
    });
  }

}
