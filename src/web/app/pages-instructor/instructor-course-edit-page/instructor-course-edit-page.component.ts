import { Component, OnInit } from '@angular/core';
import { FormArray, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { HttpRequestService } from '../../../services/http-request.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../message-output';
import {TimezoneService} from "../../../services/timezone.service";

import moment from 'moment-timezone';

interface CourseAttributes {
  id: string;
  name: string;
  timeZone: string;
}

interface InstructorAttributes {
  googleid: string;
  name: string;
  email: string;
  role: string;
  displayedname: string;
}

interface CourseEditDetails {
  courseToEdit: CourseAttributes;
  instructorList: InstructorAttributes[];
  instructor: InstructorAttributes;
  instructorToShowIndex: number;
  sectionNames: string[];
  feedbackNames: string[];
}

/**
 * Instructor course edit page.
 */
@Component({
  selector: 'tm-instructor-course-edit-page',
  templateUrl: './instructor-course-edit-page.component.html',
  styleUrls: ['./instructor-course-edit-page.component.scss'],
})
export class InstructorCourseEditPageComponent implements OnInit {

  user: string = '';
  timezones: string[] = [];

  formEditCourse!: FormGroup;

  courseToEdit!: CourseAttributes;
  instructorList: InstructorAttributes[] = [];
  instructor!: InstructorAttributes;
  instructorToShowIndex: number = -1;
  sectionNames: string[] = [];
  feedbackNames: string[] = [];

  constructor(private route: ActivatedRoute, private timezoneService: TimezoneService,
              private httpRequestService: HttpRequestService, private statusMessageService: StatusMessageService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.getCourseEditDetails(queryParams.courseid);
    });

    this.timezones = Object.keys(this.timezoneService.getTzOffsets());
  }

  /**
   * Gets details related to the specified course.
   */
  getCourseEditDetails(courseid: string): void {
    const paramMap: { [key: string]: string } = { courseid };
    this.httpRequestService.get('/instructors/course/details', paramMap)
        .subscribe((resp: CourseEditDetails) => {
          this.courseToEdit = resp.courseToEdit;
          this.instructorList = resp.instructorList;
          this.instructor = resp.instructor;
          this.instructorToShowIndex = resp.instructorToShowIndex;
          this.sectionNames = resp.sectionNames;
          this.feedbackNames = resp.feedbackNames;

          this.initEditCourseForm();
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Initialises the instructor course edit form with fields from the backend.
   */
  private initEditCourseForm(): void {
    this.formEditCourse = new FormGroup({
      courseid: new FormControl(this.courseToEdit.id),
      coursename: new FormControl(this.courseToEdit.name),
      coursetimezone: new FormControl(this.courseToEdit.timeZone)
    });
  }

}
