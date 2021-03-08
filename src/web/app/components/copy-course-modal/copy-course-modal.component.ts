import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {StatusMessageService} from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { FeedbackSession } from '../../../types/api-output';
import { COURSE_ID_MAX_LENGTH } from '../../../types/field-validator';

interface Timezone {
  id: string;
  offset: string;
}

const formatTwoDigits: Function = (n: number): string => {
  if (n < 10) {
    return `0${n}`;
  }
  return String(n);
};

/**
 * Copy current course modal.
 */
@Component({
  selector: 'tm-copy-course-modal',
  templateUrl: './copy-course-modal.component.html',
  styleUrls: ['./copy-course-modal.component.scss']
})
export class CopyCourseModalComponent implements OnInit {

  // const
  COURSE_ID_MAX_LENGTH: number = COURSE_ID_MAX_LENGTH;
  timezones: Timezone[] = [];
  newTimezone: string = '';
  newCourseId: string = '';
  newCourseName: string = '';
  oldCourseId: string = '';

  @Input() sessionsInCourse : FeedbackSession[] = [];

  chosenFeedbackSessions: Set<FeedbackSession> = new Set<FeedbackSession>();

  constructor(public activeModal: NgbActiveModal,
              private statusMessageService: StatusMessageService,
              private timezoneService: TimezoneService) { }

  ngOnInit(): void {
    for (const [id, offset] of Object.entries(this.timezoneService.getTzOffsets())) {
      const hourOffset: number = Math.floor(Math.abs(offset) / 60);
      const minOffset: number = Math.abs(offset) % 60;
      const sign: string = offset < 0 ? '-' : '+';
      this.timezones.push({
        id,
        offset: offset === 0 ? 'UTC' : `UTC ${sign}${formatTwoDigits(hourOffset)}:${formatTwoDigits(minOffset)}`,
      });
    }
    this.newTimezone = this.timezoneService.guessTimezone();
  }

  /**
   * Fires the copy event.
   */
  copy(): void {
    if (!this.newCourseId || !this.newCourseName) {
      this.statusMessageService.showErrorToast(
          'Please make sure you have filled in both Course ID and Name before adding the course!');
      return;
    }
    this.activeModal.close({
      newCourseId: this.newCourseId,
      newCourseName: this.newCourseName,
      newTimeZone: this.newTimezone,
      chosenFeedbackSessionList: Array.from(this.chosenFeedbackSessions)
    });
  }

  /**
   * Toggles selection of course to copy to in set.
   */
  select(session: FeedbackSession): void {
    this.chosenFeedbackSessions.has(session) ? this.chosenFeedbackSessions.delete(session) : this.chosenFeedbackSessions.add(session);
  }

  /**
   * Clear selected sessions
   */
  toggleSelection(): void {
    if (this.chosenFeedbackSessions.size != this.sessionsInCourse.length) {
      this.chosenFeedbackSessions = new Set(this.sessionsInCourse);
    } else {
      this.chosenFeedbackSessions = new Set<FeedbackSession>();
    }
  }

  /**
   * Auto-detects timezone for instructor.
   */
  onAutoDetectTimezone(): void {
    this.newTimezone = this.timezoneService.guessTimezone();
  }

}
