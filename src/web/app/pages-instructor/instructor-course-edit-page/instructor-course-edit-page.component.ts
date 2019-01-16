import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormArray, FormGroup } from '@angular/forms';
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
  googleId: string;
  name: string;
  email: string;
  role: string;
  isDisplayedToStudents: boolean;
  displayedName: string;
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

  formEditInstructors!: FormGroup;
  formInstructors!: FormArray;

  courseToEdit!: CourseAttributes;
  instructorList: InstructorAttributes[] = [];
  instructor!: InstructorAttributes;
  instructorToShowIndex: number = -1;
  sectionNames: string[] = [];
  feedbackNames: string[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private navigationService: NavigationService,
              private timezoneService: TimezoneService, private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService, private fb: FormBuilder) { }

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
          this.initEditInstructorsForm();
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Initialises the instructor course edit form with fields from the backend.
   */
  private initEditCourseForm(): void {
    this.formEditCourse = this.fb.group({
      id: [{ value: this.courseToEdit.id, disabled: true }],
      name: [{ value: this.courseToEdit.name, disabled: true }],
      timeZone: [{ value: this.courseToEdit.timeZone, disabled: true }],
    });
  }

  /**
   * Initialise the details panels with data from the backend for all instructors.
   */
  private initEditInstructorsForm(): void {
    this.formEditInstructors = this.fb.group({ formInstructors: [] });

    let control = this.fb.array([]);
    this.instructorList.forEach(instructor => {
      control.push(this.fb.group({
        googleId: [{ value: instructor.googleId, disabled: true }],
        name: [{ value: instructor.name, disabled: true }],
        email: [{ value: instructor.email, disabled: true }],
        isDisplayedToStudents: [{ value: instructor.isDisplayedToStudents, disabled: true }],
        displayedName: [{ value: instructor.displayedName, disabled: true }],
        role: [{ value: instructor.role }]
      }));
    });

    this.formEditInstructors.controls.formInstructors = control;
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

  /**
   * Toggles the edit instructor panel for a given instructor.
   */
  toggleIsEditingInstructor(control: FormGroup, index: number): void {
    const editBtnId: string = 'btn-edit-' + index;
    const cancelBtnId: string = 'btn-cancel-' + index;
    const saveBtnId: string = 'btn-save-' + index;
    const isEditBtnVisible: boolean = document!.getElementById(editBtnId)!.style!.display == 'inline-block';

    const viewRoleId: string = 'role-view-' + index;
    const editRoleId: string = 'role-edit-' + index;

    const googleId: string = 'googleId';
    const role: string = 'role';

    if (isEditBtnVisible) {
      document!.getElementById(editBtnId)!.style!.display = 'none';
      document!.getElementById(cancelBtnId)!.style!.display = 'inline-block';
      document!.getElementById(saveBtnId)!.style!.display = 'inline-block';

      document!.getElementById(viewRoleId)!.style!.display = 'none';
      document!.getElementById(editRoleId)!.style!.display = 'block';

      // Enable all form control elements except for the google id
      control!.enable();
      control!.get(googleId)!.disable();
      control!.get(role)!.setValue(this.instructorList[index].role);

    } else {
      document!.getElementById(editBtnId)!.style!.display = 'inline-block';
      document!.getElementById(cancelBtnId)!.style!.display = 'none';
      document!.getElementById(saveBtnId)!.style!.display = 'none';

      document!.getElementById(viewRoleId)!.style!.display = 'inline-block';
      document!.getElementById(editRoleId)!.style!.display = 'none';

      control!.disable();
      this.instructorList[index].role = control!.get(role)!.value;
    }
  }

  /**
   * Saves the updated instructor details.
   */
  onSubmitEditInstructor(instr: FormGroup, index: number): void {
    const instructor: InstructorAttributes = instr.value;

    const paramsMap: { [key: string]: string } = {
      courseid: this.courseToEdit.id,
      instructorname: instructor.name,
      instructoremail: instructor.email,
      instructorrole: instructor.role,
      instructorisdisplayed: String(instructor.isDisplayedToStudents),
      instructordisplayname: instructor.displayedName,
    };

    this.httpRequestService.put('/instructors/course/details/editInstructor', paramsMap)
        .subscribe((resp: MessageOutput) => {
          this.statusMessageService.showSuccessMessage(resp.message);
          this.toggleIsEditingInstructor(instr, index);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

}
