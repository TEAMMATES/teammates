import { Component, EventEmitter, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CopyCourseModalResult } from './copy-course-modal-model';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { Course, FeedbackSession } from '../../../types/api-output';
import { COURSE_ID_MAX_LENGTH, COURSE_NAME_MAX_LENGTH } from '../../../types/field-validator';

interface Timezone {
  id: string;
  offset: string;
}

const zeroPad: (num: number) => string = (num: number) => String(num).padStart(2, '0');

/**
 * Copy course modal.
 */
@Component({
  selector: 'tm-copy-course-modal',
  templateUrl: './copy-course-modal.component.html',
  styleUrls: ['./copy-course-modal.component.scss'],
})
export class CopyCourseModalComponent implements OnInit {

  // const
  readonly COURSE_ID_MAX_LENGTH: number = COURSE_ID_MAX_LENGTH;
  readonly COURSE_NAME_MAX_LENGTH: number = COURSE_NAME_MAX_LENGTH;

  @Input()
  courseToFeedbackSession: Record<string, FeedbackSession[]> = {};

  @Input()
  activeCourses: Course[] = [];

  @Input()
  allCourses: Course[] = [];

  fetchFeedbackSessionsEvent: EventEmitter<string> = new EventEmitter<string>();

  isCopyFromOtherSession: boolean = false;
  newCourseIdIsConflicting: boolean = false;
  institutes: string[] = [];
  timezones: Timezone[] = [];
  newTimezone: string = '';
  newCourseId: string = '';
  newCourseName: string = '';
  newCourseInstitute: string = '';
  oldCourseId: string = '';
  oldCourseName: string = '';

  selectedFeedbackSessions: Set<FeedbackSession> = new Set<FeedbackSession>();

  constructor(public activeModal: NgbActiveModal,
              private statusMessageService: StatusMessageService,
              private timezoneService: TimezoneService) {}

  ngOnInit(): void {
    this.timezones = Object.entries(this.timezoneService.getTzOffsets())
      .map(([id, offset]: [string, number]) => {
        const hourOffset: number = Math.floor(Math.abs(offset) / 60);
        const minOffset: number = Math.abs(offset) % 60;
        const sign: string = offset < 0 ? '-' : '+';
        return { id, offset: offset === 0 ? 'UTC' : `UTC ${sign}${zeroPad(hourOffset)}:${zeroPad(minOffset)}` };
      });
    this.institutes = Array.from(new Set(this.allCourses.map((course: Course) => course.institute)));
    if (this.institutes.length) {
      this.newCourseInstitute = this.institutes[0];
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

    this.newCourseIdIsConflicting = this.allCourses
      .filter((course: Course) => course.courseId === this.newCourseId).length > 0;
    if (this.newCourseIdIsConflicting) {
      this.statusMessageService.showErrorToast(
        `The course ID ${this.newCourseId} already exists.`);
      return;
    }

    const result: CopyCourseModalResult = {
      newCourseId: this.newCourseId,
      newCourseName: this.newCourseName,
      newCourseInstitute: this.newCourseInstitute,
      oldCourseId: this.oldCourseId,
      newTimeZone: this.newTimezone,
      selectedFeedbackSessionList: this.selectedFeedbackSessions,
      totalNumberOfSessions: this.selectedFeedbackSessions.size,
    };

    this.activeModal.close(result);
  }

  /**
   * Toggles selection of a feedback session.
   */
  toggleSelection(session: FeedbackSession): void {
    if (this.selectedFeedbackSessions.has(session)) {
      this.selectedFeedbackSessions.delete(session);
    } else {
      this.selectedFeedbackSessions.add(session);
    }
  }

  /**
   * Select all sessions or clear all sessions
   */
  toggleSelectionForAll(): void {
    if (this.selectedFeedbackSessions.size === this.courseToFeedbackSession[this.oldCourseId]?.length) {
      this.selectedFeedbackSessions.clear();
    } else {
      this.selectedFeedbackSessions = new Set(this.courseToFeedbackSession[this.oldCourseId]);
    }
  }

  /**
   * Auto-detects timezone for instructor.
   */
  onAutoDetectTimezone(): void {
    this.newTimezone = this.timezoneService.guessTimezone();
  }

  /**
   * Clears all selected feedback sessions of previously chosen course.
   */
  onSelectCourseChange(): void {
    this.selectedFeedbackSessions.clear();

    if (!this.courseToFeedbackSession[this.oldCourseId]) {
      this.fetchFeedbackSessionsEvent.emit(this.oldCourseId);
    }
  }
}
