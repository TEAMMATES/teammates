import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { ErrorMessageOutput, MessageOutput } from '../../message-output';

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
  privileges: { [key: string]: { [key: string]: boolean} };
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

  isEditingCourse: boolean = false;

  formEditCourse!: FormGroup;

  courseToEdit!: CourseAttributes;
  instructorList: InstructorAttributes[] = [];
  instructor!: InstructorAttributes;
  instructorToShowIndex: number = -1;
  sectionNames: string[] = [];
  feedbackNames: string[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private navigationService: NavigationService,
              private timezoneService: TimezoneService, private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService) { }

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
      id: new FormControl({ value: this.courseToEdit.id, disabled: true }),
      name: new FormControl({ value: this.courseToEdit.name, disabled: true }),
      timeZone: new FormControl({ value: this.courseToEdit.timeZone, disabled: true }),
    });
  }

  /**
   * Toggles the edit course form depending on whether the edit button is clicked.
   */
  toggleIsEditingCourse(): void {
    this.isEditingCourse = !this.isEditingCourse;

    const name: string = 'name';
    const timeZone: string = 'timeZone';

    if (!this.isEditingCourse) {
      this.formEditCourse.controls[name].disable();
      this.formEditCourse.controls[timeZone].disable();
    } else {
      this.formEditCourse.controls[name].enable();
      this.formEditCourse.controls[timeZone].enable();
    }
  }

  /**
   * Deletes the current course and redirects to 'Courses' page if action is successful.
   */
  deleteCourse(): void {
    const paramsMap: { [key: string]: string } = { courseid: this.courseToEdit.id, next: '/web/instructor/courses' };

    this.httpRequestService.delete('/instructors/course/delete', paramsMap)
        .subscribe((resp: MessageOutput) => {
          this.navigationService.navigateWithSuccessMessage(this.router, '/web/instructor/courses', resp.message);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Saves the updated course details.
   */
  onSubmitEditCourse(editedCourseDetails: CourseAttributes): void {
    const paramsMap: { [key: string]: string } = {
      courseid: this.courseToEdit.id,
      coursename: editedCourseDetails.name,
      coursetimezone: editedCourseDetails.timeZone,
    };

    this.httpRequestService.put('/instructors/course/details/save', paramsMap)
        .subscribe((resp: MessageOutput) => {
          this.statusMessageService.showSuccessMessage(resp.message);
          this.toggleIsEditingCourse();
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }
}
