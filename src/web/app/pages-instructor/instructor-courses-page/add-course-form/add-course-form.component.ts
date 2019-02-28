import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import moment from 'moment-timezone';
import { HttpRequestService } from '../../../../services/http-request.service';
import { StatusMessageService } from '../../../../services/status-message.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { MessageOutput } from '../../../../types/api-output';
import { ErrorMessageOutput } from '../../../error-message-output';

/**
 * Contains properties and methods that are used in the template
 */
interface AddCourseFormComponentInterface {
  timezones: string[];
  timezone: string;
  newCourseId: string;
  newCourseName: string;

  onAutoDetectTimezone(): void;
  onSubmit(): void;
}

/**
 * An example component to be shown in the help page
 */
@Component({
  selector: 'tm-example-add-course-form',
  templateUrl: './add-course-form.component.html',
  styleUrls: ['./add-course-form.component.scss'],
})
export class ExampleAddCourseFormComponent implements AddCourseFormComponentInterface {
  timezones: string[] = ['UTC', 'Other options ommitted...'];
  timezone: string = 'UTC';
  newCourseId: string = '';
  newCourseName: string = '';

  /**
   * Mock
   */
  onAutoDetectTimezone(): void { }
  /**
   * Mock
   */
  onSubmit(): void { }
}

/**
 * The actual component
 */
@Component({
  selector: 'tm-add-course-form',
  templateUrl: './add-course-form.component.html',
  styleUrls: ['./add-course-form.component.scss'],
})
export class AddCourseFormComponent implements OnInit, AddCourseFormComponentInterface {

  user: string = '';

  @Output() courseAdded: EventEmitter<void> = new EventEmitter<void>();

  timezones: string[] = [];
  timezone: string = '';
  newCourseId: string = '';
  newCourseName: string = '';

  constructor(private route: ActivatedRoute,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private timezoneService: TimezoneService) { }

  ngOnInit(): void {
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
    this.timezone = moment.tz.guess();
  }

  /**
   * Submits the data to add the new course.
   */
  onSubmit(): void {
    if (!this.newCourseId || !this.newCourseName) {
      this.statusMessageService.showErrorMessage(
          'Please make sure you have filled in both Course ID and Name before adding the course!');
      return;
    }
    const paramMap: { [key: string]: string } = {
      courseid: this.newCourseId,
      coursename: this.newCourseName,
      coursetimezone: this.timezone,
      user: this.user,
    };
    this.newCourseId = '';
    this.newCourseName = '';
    this.timezone = moment.tz.guess();
    this.httpRequestService.post('/instructor/courses', paramMap).subscribe((resp: MessageOutput) => {
      this.courseAdded.emit();
      this.statusMessageService.showSuccessMessage(resp.message);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

}
