import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Course } from '../../../course';
import { FeedbackSession } from '../../../feedback-session';

/**
 * Modal for creating new feedback session by copying from other feedback sessions.
 */
@Component({
  selector: 'tm-copy-from-other-sessions-modal',
  templateUrl: './copy-from-other-sessions-modal.component.html',
  styleUrls: ['./copy-from-other-sessions-modal.component.scss'],
})
export class CopyFromOtherSessionsModalComponent implements OnInit {

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

  ngOnInit(): void {
  }

}
