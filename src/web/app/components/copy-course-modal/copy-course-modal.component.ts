import { Component, EventEmitter, Input, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { CopyCourseModalResult } from './copy-course-modal-model';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { Course, FeedbackSession, Institute } from '../../../types/api-output';
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
  imports: [FormsModule, NgbTooltip],
})
export class CopyCourseModalComponent implements OnInit {
  activeModal = inject(NgbActiveModal);
  private statusMessageService = inject(StatusMessageService);
  private timezoneService = inject(TimezoneService);

  // const
  readonly COURSE_ID_MAX_LENGTH: number;
  readonly COURSE_NAME_MAX_LENGTH: number;

  @Input()
  courseToFeedbackSession: Record<string, FeedbackSession[]> = {};

  @Input()
  activeCourses: Course[] = [];

  @Input()
  allCourses: Course[] = [];

  @Input()
  institutes: Institute[] = [];

  fetchFeedbackSessionsEvent: EventEmitter<string> = new EventEmitter<string>();

  isCopyFromOtherSession = false;
  newCourseIdIsConflicting = false;
  newCourseIdTouched = false;
  newCourseNameTouched = false;
  timezones: Timezone[] = [];
  newTimezone = '';
  newCourseId = '';
  newCourseName = '';
  newCourseInstituteId = '';
  oldCourseId = '';
  oldCourseName = '';

  selectedFeedbackSessions: Set<FeedbackSession> = new Set<FeedbackSession>();

  constructor() {
    this.COURSE_ID_MAX_LENGTH = COURSE_ID_MAX_LENGTH;
    this.COURSE_NAME_MAX_LENGTH = COURSE_NAME_MAX_LENGTH;
  }

  ngOnInit(): void {
    this.timezones = Object.entries(this.timezoneService.getTzOffsets()).map(([id, offset]: [string, number]) => {
      const hourOffset: number = Math.floor(Math.abs(offset) / 60);
      const minOffset: number = Math.abs(offset) % 60;
      const sign: string = offset < 0 ? '-' : '+';
      return { id, offset: offset === 0 ? 'UTC' : `UTC ${sign}${zeroPad(hourOffset)}:${zeroPad(minOffset)}` };
    });
    if (this.institutes.length) {
      this.newCourseInstituteId = this.institutes[0].id;
    }
    this.newTimezone = this.timezoneService.guessTimezone();
  }

  /**
   * Fires the copy event.
   */
  copy(): void {
    if (!this.newCourseId || !this.newCourseName) {
      this.statusMessageService.showErrorToast(
        'Please make sure you have filled in both Course ID and Name before adding the course!',
      );
      return;
    }

    this.newCourseIdIsConflicting =
      this.allCourses.filter((course: Course) => course.courseId === this.newCourseId).length > 0;
    if (this.newCourseIdIsConflicting) {
      this.statusMessageService.showErrorToast(`The course ID ${this.newCourseId} already exists.`);
      return;
    }

    const result: CopyCourseModalResult = {
      newCourseId: this.newCourseId,
      newCourseName: this.newCourseName,
      newCourseInstituteId: this.newCourseInstituteId,
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
