import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import moment from 'moment-timezone';
import { CourseService } from '../../../../services/course.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { Course } from '../../../../types/api-output';
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

  user: string = '';

  @Input() isEnabled: boolean = true;
  @Output() courseAdded: EventEmitter<void> = new EventEmitter<void>();
  @ViewChild('newCourseMessageTemplate') newCourseMessageTemplate!: TemplateRef<any>;

  timezones: string[] = [];
  timezone: string = '';
  newCourseId: string = '';
  newCourseName: string = '';
  course!: Course;

  constructor(private route: ActivatedRoute,
              private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private timezoneService: TimezoneService) { }

  ngOnInit(): void {
    if (!this.isEnabled) {
      this.timezones = ['UTC', 'Other options ommitted...'];
      this.timezone = 'UTC';
      return;
    }
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
    });
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
    if (!this.newCourseId || !this.newCourseName) {
      this.statusMessageService.showErrorMessage(
          'Please make sure you have filled in both Course ID and Name before adding the course!');
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
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
    this.newCourseId = '';
    this.newCourseName = '';
    this.timezone = moment.tz.guess();
  }
}
