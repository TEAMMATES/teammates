import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { CourseService } from '../../../../services/course.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { Course } from '../../../../types/api-output';
import { FormValidator } from '../../../../types/form-validator';
import { ErrorMessageOutput } from '../../../error-message-output';

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
 * Instructor add new course form
 */
@Component({
  selector: 'tm-add-course-form',
  templateUrl: './add-course-form.component.html',
  styleUrls: ['./add-course-form.component.scss'],
})
export class AddCourseFormComponent implements OnInit {

  @Input() isEnabled: boolean = true;
  @Output() courseAdded: EventEmitter<void> = new EventEmitter<void>();
  @Output() closeCourseFormEvent: EventEmitter<void> = new EventEmitter<void>();
  @ViewChild('newCourseMessageTemplate', { static: false }) newCourseMessageTemplate!: TemplateRef<any>;
  @ViewChild('courseForm', { static: false }) form!: FormGroup;

  timezones: Timezone[] = [];
  timezone: string = '';
  newCourseId: string = '';
  newCourseName: string = '';
  course!: Course;
  FormValidator: typeof FormValidator = FormValidator;
  isAddingCourse: boolean = false;

  constructor(private statusMessageService: StatusMessageService,
              private courseService: CourseService,
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

    this.timezone = this.timezoneService.guessTimezone();
  }

  /**
   * Auto-detects timezone for instructor.
   */
  onAutoDetectTimezone(): void {
    if (!this.isEnabled) {
      return;
    }
    this.timezone = this.timezoneService.guessTimezone();
  }

  /**
   * Submits the data to add the new course.
   */
  onSubmit(): void {
    if (!this.isEnabled) {
      return;
    }
    if (this.form.invalid) {
      Object.values(this.form.controls).forEach((control: any) => control.markAsTouched());
      return;
    }

    this.isAddingCourse = true;
    this.courseService.createCourse({
      courseName: this.newCourseName,
      timeZone: this.timezone,
      courseId: this.newCourseId,
    }).pipe(finalize(() => this.isAddingCourse = false)).subscribe(() => {
      this.courseAdded.emit();
      this.statusMessageService.showSuccessToast('The course has been added.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
    this.newCourseId = '';
    this.newCourseName = '';
    this.timezone = this.timezoneService.guessTimezone();
  }

  /**
   * Handles closing of the edit form.
   */
  closeEditFormHandler(): void {
    this.closeCourseFormEvent.emit();
  }
}
