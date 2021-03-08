import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FeedbackSession } from '../../../types/api-output';
import { COURSE_ID_MAX_LENGTH } from '../../../types/field-validator';

/**
 * Copy current course modal.
 */
@Component({
  selector: 'tm-copy-course-modal',
  templateUrl: './copy-course-modal.component.html',
  styleUrls: ['./copy-course-modal.component.scss'],
})
export class CopyCourseModalComponent implements OnInit {

  // const
  COURSE_ID_MAX_LENGTH: number = COURSE_ID_MAX_LENGTH;

  newCourseId: string = '';

  @Input()
  sessionsInCourse: FeedbackSession[] = [];

  chosenFeedbackSessions: Set<FeedbackSession> = new Set<FeedbackSession>();

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

  /**
   * Fires the copy event.
   */
  copy(): void {
    this.activeModal.close({
      newCourseId: this.newCourseId,
      chosenFeedbackSessionList: Array.from(this.chosenFeedbackSessions),
    });
  }

  /**
   * Toggles selection of course to copy to in set.
   */
  select(session: FeedbackSession): void {
    this.chosenFeedbackSessions.has(session) ? this.chosenFeedbackSessions.delete(session) :
        this.chosenFeedbackSessions.add(session);
  }

  /**
   * Clear selected sessions
   */
  clearSessions(): void {
    if (this.chosenFeedbackSessions.size > 0) {
      this.chosenFeedbackSessions = new Set<FeedbackSession>();
    }
  }

  /**
   * Select all sessions
   */
  addAllSessions(): void {
    if (this.chosenFeedbackSessions.size !== this.sessionsInCourse.length) {
      this.chosenFeedbackSessions = new Set(this.sessionsInCourse);
    }
  }

}
