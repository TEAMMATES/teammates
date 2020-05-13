import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import moment from 'moment-timezone';
import { CourseService } from '../../../../services/course.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { Course } from '../../../../types/api-output';
import { FormValidator } from '../../../../types/form-validator';
import { ErrorMessageOutput } from '../../../error-message-output';

/**
 * The actual component
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

  timezones: string[] = [];
  timezone: string = '';
  newCourseId: string = '';
  newCourseName: string = '';
  course!: Course;
  FormValidator: typeof FormValidator = FormValidator;

  constructor(private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private timezoneService: TimezoneService) { }

  ngOnInit(): void {
    if (!this.isEnabled) {
      this.timezones = ['UTC', 'Other options ommitted...'];
      this.timezone = 'UTC';
      return;
    }
    this.timezones = Object.keys(this.timezoneService.getTzOffsets());
    this.timezone = moment.tz.guess();
  }

  /**
   * Auto-detects timezone for instructor.
   */
  onAutoDetectTimezone(): void {
    if (!this.isEnabled) {
      return;
    }
    this.timezone = moment.tz.guess();
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
    this.courseService.createCourse({
      courseName: this.newCourseName,
      timeZone: this.timezone,
      courseId: this.newCourseId,
    }).subscribe((course: Course) => {
      this.courseAdded.emit();
      this.course = course;
      this.statusMessageService.showSuccessMessageTemplate(this.newCourseMessageTemplate);
      this.form.reset({ timezone: moment.tz.guess() });
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Handles closing of the edit form.
   */
  closeEditFormHandler(): void {
    this.closeCourseFormEvent.emit();
  }
}
