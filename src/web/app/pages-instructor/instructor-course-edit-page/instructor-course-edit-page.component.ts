import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

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

interface CourseLevelPrivileges {
  canmodifycourse: boolean;
  canmodifyinstructor: boolean;
  canmodifysession: boolean;
  canmodifystudent: boolean;
  canviewstudentinsection: boolean;
  canviewsessioninsection: boolean;
  cansubmitsessioninsection: boolean;
  canmodifysessioncommentinsection: boolean;
}

interface Privileges {
  courseLevel: CourseLevelPrivileges;
}

interface InstructorAttributes {
  googleId: string;
  name: string;
  email: string;
  role: string;
  isDisplayedToStudents: boolean;
  displayedName: string;
  privileges: Privileges;
}

interface InstructorPrivileges {
  coowner: Privileges;
  manager: Privileges;
  observer: Privileges;
  tutor: Privileges;
  custom: Privileges;
}

interface CourseEditDetails {
  courseToEdit: CourseAttributes;
  instructorList: InstructorAttributes[];
  instructor: InstructorAttributes;
  instructorToShowIndex: number;
  sectionNames: string[];
  feedbackNames: string[];
  instructorPrivileges: InstructorPrivileges;
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
  instructorPrivileges!: InstructorPrivileges;

  isAddingInstructor: boolean = false;
  formAddInstructor!: FormGroup;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private navigationService: NavigationService,
              private timezoneService: TimezoneService,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private fb: FormBuilder,
              private ngbModal: NgbModal) { }

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
          this.instructorPrivileges = resp.instructorPrivileges;

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
   * Initialises the details panels with data from the backend for all instructors.
   */
  private initEditInstructorsForm(): void {
    this.formEditInstructors = this.fb.group({ formInstructors: [] });

    const control: FormArray = this.fb.array([]);
    this.instructorList.forEach((instructor: InstructorAttributes) => {

      const instructorPrivileges: Privileges = this.getPrivilegesForRole(instructor.role);

      control.push(this.fb.group({
        googleId: [{ value: instructor.googleId, disabled: true }],
        name: [{ value: instructor.name, disabled: true }],
        email: [{ value: instructor.email, disabled: true }],
        isDisplayedToStudents: [{ value: instructor.isDisplayedToStudents, disabled: true }],
        displayedName: [{ value: instructor.displayedName, disabled: true }],
        role: [{ value: instructor.role }],
        privileges: [{ value: instructorPrivileges }],
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
   * Checks if the current instructor has a valid google id.
   */
  hasGoogleId(index: number): boolean {
    const googleId: string = this.instructorList[index].googleId;
    return googleId != null && googleId != '';
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
          this.updateCourseDetails(editedCourseDetails.name, editedCourseDetails.timeZone);
          this.toggleIsEditingCourse();
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Updates the stored course attributes entity.
   */
  updateCourseDetails(editedCourseName: string, editedCourseTimezone: string): void {
    this.courseToEdit.name = editedCourseName;
    this.courseToEdit.timeZone = editedCourseTimezone;
  }

  /**
   * Toggles the edit instructor panel for a given instructor.
   */
  toggleIsEditingInstructor(control: FormGroup, index: number): void {
    const editBtnId: string = `btn-edit-${index}`;
    const cancelBtnId: string = `btn-cancel-${index}`;
    const saveBtnId: string = `btn-save-${index}`;

    const editBtn: (HTMLElement | null) = document.getElementById(editBtnId);
    const cancelBtn: (HTMLElement | null) = document.getElementById(cancelBtnId);
    const saveBtn: (HTMLElement | null) = document.getElementById(saveBtnId);

    let isEditBtnVisible: boolean = true;
    if (editBtn != null) {
      isEditBtnVisible = editBtn.style.display === 'inline-block';
    }

    const viewRoleId: string = `role-view-${index}`;
    const editRoleId: string = `role-edit-${index}`;

    const viewRole: (HTMLElement | null) = document.getElementById(viewRoleId);
    const editRole: (HTMLElement | null) = document.getElementById(editRoleId);

    const googleId: string = 'googleId';
    const role: string = 'role';

    const idControl: (AbstractControl | null) = control.get(googleId);
    const roleControl: (AbstractControl | null) = control.get(role);

    if (editBtn != null && cancelBtn != null && saveBtn != null && viewRole != null && editRole != null
        && idControl != null && roleControl != null) {
      if (isEditBtnVisible) {
        editBtn.style.display = 'none';
        cancelBtn.style.display = 'inline-block';
        saveBtn.style.display = 'inline-block';

        viewRole.style.display = 'none';
        editRole.style.display = 'block';

        // Enable all form control elements except for the google id
        control.enable();
        idControl.disable();
        roleControl.setValue(this.instructorList[index].role);

      } else {
        editBtn.style.display = 'inline-block';
        cancelBtn.style.display = 'none';
        saveBtn.style.display = 'none';

        viewRole.style.display = 'inline-block';
        editRole.style.display = 'none';

        control.disable();
        this.instructorList[index].role = roleControl.value;
      }
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

    this.httpRequestService.post('/instructors/course/details/editInstructor', paramsMap)
        .subscribe((resp: MessageOutput) => {
          this.statusMessageService.showSuccessMessage(resp.message);
          this.updateInstructorPrivileges(index, instructor.role);
          this.toggleIsEditingInstructor(instr, index);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Updates the stored instructor and instructor list entities.
   */
  updateInstructorPrivileges(index: number, editedRole: string): void {
    const editedInstructor: InstructorAttributes = this.instructorList[index];
    const newPrivileges: Privileges = this.getPrivilegesForRole(editedRole);

    // If there is only one instructor, the instructor can modify instructors by default
    if (this.instructorList.length === 1) {
      newPrivileges.courseLevel.canmodifyinstructor = true;
    }

    // Update stored instructor entity if necessary
    if (this.instructor.googleId === editedInstructor.googleId) {
      this.instructor.privileges = newPrivileges;
      this.updateElementsForPrivileges();
    }

    this.instructorList[index].privileges = newPrivileges;
  }

  /**
   * Gets the privileges for a particular role.
   */
  private getPrivilegesForRole(role: string): Privileges {
    if (role === 'Co-owner') {
      return this.instructorPrivileges.coowner;
    }

    if (role === 'Manager') {
      return this.instructorPrivileges.manager;
    }

    if (role === 'Observer') {
      return this.instructorPrivileges.observer;
    }

    if (role === 'Tutor') {
      return this.instructorPrivileges.tutor;
    }

    return this.instructorPrivileges.custom;
  }

  /**
   * Updates elements and buttons related to the current instructor's privileges.
   */
  private updateElementsForPrivileges(): void {
    const courseBtns: HTMLCollectionOf<Element> = document.getElementsByClassName('btn-course');
    for (const courseBtn of courseBtns as any) {
      (courseBtn as HTMLInputElement).disabled = !this.instructor.privileges.courseLevel.canmodifycourse;
    }

    const instrBtns: HTMLCollectionOf<Element> = document.getElementsByClassName('btn-instr');
    for (const instrBtn of instrBtns as any) {
      (instrBtn as HTMLInputElement).disabled = !this.instructor.privileges.courseLevel.canmodifyinstructor;
    }
  }

  /**
   * Toggles the add instructor form.
   */
  toggleIsAddingInstructor(): void {
    this.isAddingInstructor = !this.isAddingInstructor;

    if (this.isAddingInstructor) {
      this.initAddInstructorForm();
    }
  }

  /**
   * Initialises a new form for adding an instructor to the current course.
   */
  private initAddInstructorForm(): void {
    this.formAddInstructor = this.fb.group({
      name: [''],
      email: [''],
      isDisplayedToStudents: [{ value: true }],
      displayedName: ['Instructor'],
      role: ['Co-owner'],
    });
  }

  /**
   * Adds a new instructor to the current course.
   */
  onSubmitAddInstructor(addedInstructor: InstructorAttributes): void {
    const paramsMap: { [key: string]: string } = {
      courseid: this.courseToEdit.id,
      instructorname: addedInstructor.name,
      instructoremail: addedInstructor.email,
      instructorrole: addedInstructor.role,
      instructorisdisplayed: addedInstructor.isDisplayedToStudents ? 'true' : '',
      instructordisplayname: addedInstructor.displayedName,
    };

    this.httpRequestService.put('/instructors/course/details/addInstructor', paramsMap)
        .subscribe((resp: MessageOutput) => {
          this.statusMessageService.showSuccessMessage(resp.message);
          this.addToInstructorList(addedInstructor);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });

    this.toggleIsAddingInstructor();
  }

  /**
   * Updates the stored instructor list and forms.
   */
  private addToInstructorList(instructor: InstructorAttributes): void {
    const formInstructors: string = 'formInstructors';
    const newPrivileges: Privileges = this.getPrivilegesForRole(instructor.role);
    instructor.privileges = newPrivileges;

    this.instructorList.push(instructor);

    (this.formEditInstructors.controls[formInstructors] as FormArray).push(this.fb.group({
      googleId: [{ value: '', disabled: true }],
      name: [{ value: instructor.name, disabled: true }],
      email: [{ value: instructor.email, disabled: true }],
      isDisplayedToStudents: [{ value: instructor.isDisplayedToStudents, disabled: true }],
      displayedName: [{ value: instructor.displayedName, disabled: true }],
      role: [{ value: instructor.role }],
    }));
  }

  /**
   * Opens a modal to confirm deleting an instructor.
   */
  onSubmitDeleteInstructor(deleteInstructorModal: NgbModal, index: number): void {
    this.ngbModal.open(deleteInstructorModal);

    const instructorToDelete: InstructorAttributes = this.instructorList[index];
    const modalId: string = 'delete-instr-modal';
    const courseId: string = this.courseToEdit.id;
    let modalContent: string = '';

    const modal: (HTMLElement | null) = document.getElementById(modalId);

    // Display different text depending on who is being deleted
    if (instructorToDelete.googleId === this.instructor.googleId) {
      modalContent = 'Are you sure you want to delete your instructor role from the '
      + `course ${courseId}? You will not be able to access the course anymore.`;
    } else {
      modalContent = `Are you sure you want to delete the instructor ${instructorToDelete.name} from the course `
          + `${courseId}? He/she will not be able to access the course anymore.`;
    }

    if (modal != null) {
      modal.innerText = modalContent;
    }
  }

  /**
   * Deletes an instructor from the given course.
   */
  deleteInstructor(index: number): void {
    const instructorToDelete: InstructorAttributes = this.instructorList[index];
    const paramsMap: { [key: string]: string } = {
      courseid: this.courseToEdit.id,
      instructorid: this.instructor.googleId,
      instructoremail: instructorToDelete.email,
    };

    this.httpRequestService.delete('/instructors', paramsMap)
        .subscribe((resp: MessageOutput) => {
          if (instructorToDelete.googleId === this.instructor.googleId) {
            this.navigationService.navigateWithSuccessMessage(this.router, '/web/instructor/courses', resp.message);
          } else {
            this.removeFromInstructorList(index);
            this.statusMessageService.showSuccessMessage(resp.message);
          }
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Removes a deleted instructor from the stored instructor lists.
   */
  private removeFromInstructorList(index: number): void {
    const formInstructors: string = 'formInstructors';

    this.instructorList.splice(index, 1);
    (this.formEditInstructors.controls[formInstructors] as FormArray).removeAt(index);
  }

  /**
   * Opens a modal to show the privileges for a given instructor.
   */
  viewInstructorRole(viewInstructorRoleModal: NgbModal, index: number): boolean {
    this.ngbModal.open(viewInstructorRoleModal);

    const instructorToView: InstructorAttributes =  this.instructorList[index];
    this.initViewInstructorRole(instructorToView.role, instructorToView.privileges.courseLevel);
    return false;
  }

  /**
   * Opens a modal to show the privileges for a given role.
   */
  viewRolePrivileges(viewInstructorRoleModal: NgbModal, role: string) {
    this.ngbModal.open(viewInstructorRoleModal);

    const privileges: Privileges = this.getPrivilegesForRole(role);
    this.initViewInstructorRole(role, privileges.courseLevel);
    return false;
  }

  /**
   * Initialises the modal showing privileges for given privileges.
   */
  initViewInstructorRole(role: string, courseLevelPrivileges: CourseLevelPrivileges): void {
    const modalTitleId: string = 'role-title';
    const modifyCourseId: string = 'canmodifycourse';
    const modifyInstructorId: string = 'canmodifyinstructor';
    const modifySessionId: string = 'canmodifysession';
    const modifyStudentId: string = 'canmodifystudent';
    const viewStudentInSectionId: string = 'canviewstudentinsection';
    const submitSessionInSectionId: string = 'cansubmitsessioninsection';
    const viewSessionInSectionId: string = 'canviewsessioninsection';
    const modifySessionCommentInSectionId: string = 'canmodifysessioncommentinsection';

    const modalTitle: (HTMLElement | null) = document.getElementById(modalTitleId);
    const canModifyCourse: (HTMLInputElement | null) = document.getElementById(modifyCourseId) as HTMLInputElement;
    const canModifyInstructor: (HTMLInputElement | null) = document.getElementById(modifyInstructorId) as HTMLInputElement;
    const canModifySession: (HTMLInputElement | null) = document.getElementById(modifySessionId) as HTMLInputElement;
    const canModifyStudent: (HTMLInputElement | null) = document.getElementById(modifyStudentId) as HTMLInputElement;
    const canViewStudentInSection: (HTMLInputElement | null) = document.getElementById(viewStudentInSectionId) as HTMLInputElement;
    const canSubmitSessionInSection: (HTMLInputElement | null) = document.getElementById(submitSessionInSectionId) as HTMLInputElement;
    const canViewSessionInSection: (HTMLInputElement | null) = document.getElementById(viewSessionInSectionId) as HTMLInputElement;
    const canModifySessionCommentInSection: (HTMLInputElement | null) =
        document.getElementById(modifySessionCommentInSectionId) as HTMLInputElement;

    if (modalTitle != null && canModifyCourse != null && canModifyInstructor != null && canModifySession != null
        && canModifyStudent != null && canViewStudentInSection != null && canSubmitSessionInSection != null
        && canViewSessionInSection != null && canModifySessionCommentInSection != null) {

      modalTitle.innerText = `Permissions for ${role}`;

      canModifyCourse.checked = courseLevelPrivileges.canmodifycourse;
      canModifyInstructor.checked = courseLevelPrivileges.canmodifyinstructor;
      canModifySession.checked = courseLevelPrivileges.canmodifysession;
      canModifyStudent.checked = courseLevelPrivileges.canmodifystudent;
      canViewStudentInSection.checked = courseLevelPrivileges.canviewstudentinsection;
      canSubmitSessionInSection.checked = courseLevelPrivileges.cansubmitsessioninsection;
      canViewSessionInSection.checked = courseLevelPrivileges.canviewsessioninsection;
      canModifySessionCommentInSection.checked = courseLevelPrivileges.canmodifysessioncommentinsection;
    }

  }

}
