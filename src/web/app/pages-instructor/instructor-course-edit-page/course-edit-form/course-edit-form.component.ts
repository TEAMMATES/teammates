import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import moment from 'moment-timezone';
import { TimezoneService } from '../../../../services/timezone.service';
import { CourseEditFormModel } from './course-edit-form-model';

/**
 * Form to edit course details.
 */
@Component({
  selector: 'tm-course-edit-form',
  templateUrl: './course-edit-form.component.html',
  styleUrls: ['./course-edit-form.component.scss'],
})
export class CourseEditFormComponent implements OnInit {

  TIMEZONE_OPTIONS: string[] = [];
  DETECTED_TIMEZONE: string = '';

  @Input()
  model: CourseEditFormModel = {
    courseId: '',
    courseName: '',
    timeZone: 'UTC',

    isEditable: true,
    isSaving: false,
  };

  @Output()
  modelChange: EventEmitter<CourseEditFormModel> = new EventEmitter();

  @Output()
  editCourseEvent: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  deleteCourseEvent: EventEmitter<void> = new EventEmitter<void>();

  constructor(private timezoneService: TimezoneService) { }

  ngOnInit(): void {
    this.TIMEZONE_OPTIONS = Object.keys(this.timezoneService.getTzOffsets());
    this.DETECTED_TIMEZONE = moment.tz.guess();
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.modelChange.emit({
      ...this.model,
      [field]: data,
    });
  }

  /**
   * Handles delete course button click event.
   */
  deleteHandler(): void {
    this.deleteCourseEvent.emit();
  }

  /**
   * Handles submit button click event.
   */
  submitFormHandler(): void {
    this.editCourseEvent.emit();
  }
}
