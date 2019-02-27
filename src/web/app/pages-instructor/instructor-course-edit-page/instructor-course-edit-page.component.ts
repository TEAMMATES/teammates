import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { CourseService } from '../../../services/course.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { Course, MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { Instructor } from '../../Instructor';
import {
  CourseLevelPrivileges, DefaultPrivileges, Privileges, Role, SectionLevelPrivileges, SessionLevelPrivileges,
} from '../../instructor-privilege';
import { CourseEditFormModel } from './course-edit-form/course-edit-form-model';
import { DeleteInstructorModalComponent } from './delete-instructor-modal/delete-instructor-modal.component';
import { InstructorEditFormMode, InstructorEditFormModel } from './instructor-edit-form/instructor-edit-form-model';
import {
  InstructorSectionPrivilegesFormFormModel,
  InstructorSessionPrivilegesFormFormModel,
} from './instructor-edit-form/instructor-section-privileges-form/instructor-section-privileges-form-model';
import { ResendReminderModalComponent } from './resend-reminder-modal/resend-reminder-modal.component';
import { ViewPrivilegesModalComponent } from './view-privileges-modal/view-privileges-modal.component';

interface CourseEditDetails {
  courseToEdit: Course;
  instructorList: Instructor[];
  instructor: Instructor;
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

  // enums
  Role: typeof Role = Role;
  InstructorEditFormMode: typeof InstructorEditFormMode = InstructorEditFormMode;

  // models
  courseEditFormModel: CourseEditFormModel = {
    courseId: '',
    courseName: '',
    timeZone: 'UTC',

    isEditable: false,
    isSaving: false,
  };

  instructorAddFormModel: InstructorEditFormModel = {
    googleId: '',
    name: '',
    email: '',
    role: Role.COOWNER,
    isDisplayedToStudents: true,
    displayedName: 'Instructor',
    courseLevel: DefaultPrivileges.COOWNER.value.courseLevel,
    instructorSectionPrivilegesFormModels: [],

    isEditable: false,
    isSaving: false,
  };

  // to get the original question model
  instructorFormModels: Instructor[] = [];
  instructorEditFormModels: InstructorEditFormModel[] = [];

  user: string = '';
  instructor!: Instructor;
  instructorToShowIndex: number = -1;
  sectionNames: string[] = [];
  feedbackNames: string[] = [];
  course!: Course;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private navigationService: NavigationService,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private modalService: NgbModal) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.getCourseEditDetails(queryParams.courseid);
    });
  }

  /**
   * Gets details related to the specified course.
   */
  getCourseEditDetails(courseid: string): void {
    const paramMap: { [key: string]: string } = { courseid };
    this.httpRequestService.get('/instructors/course/details', paramMap)
        .subscribe((resp: CourseEditDetails) => {
          this.instructor = resp.instructor;
          this.instructorToShowIndex = resp.instructorToShowIndex;
          this.sectionNames = resp.sectionNames;
          this.feedbackNames = resp.feedbackNames;
          this.course = resp.courseToEdit;

          this.courseEditFormModel = this.getCourseEditFormModel(resp.courseToEdit);

          resp.instructorList.forEach((instructor: Instructor) => {
            this.instructorEditFormModels.push(this.getInstructorEditFormModel(instructor));
            this.instructorFormModels.push(instructor);
          });
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Gets the {@code courseEditFormModel} with {@link Course} entity.
   */
  private getCourseEditFormModel(course: Course): CourseEditFormModel {
    return {
      courseId: course.id,
      courseName: course.name,
      timeZone: course.timeZone,

      isEditable: false,
      isSaving: false,
    };
  }

  /**
   * Handles editing course details event.
   */
  editCourseHandler(): void {
    this.courseEditFormModel.isSaving = true;

    const paramsMap: { [key: string]: string } = {
      courseid: this.courseEditFormModel.courseId,
      coursename: this.courseEditFormModel.courseName,
      coursetimezone: this.courseEditFormModel.timeZone,
    };

    this.httpRequestService.put('/instructors/course/details/save', paramsMap)
        .subscribe((course: Course) => {
          this.courseEditFormModel = this.getCourseEditFormModel(course);

          this.statusMessageService.showSuccessMessage(`Updated course [${course.id}] details: `
              + `Name: ${course.name}, Time zone: ${course.timeZone}`);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
          this.courseEditFormModel = this.getCourseEditFormModel(this.course);
        });
  }

  /**
   * Handles deleting course event.
   */
  deleteCourseHandler(): void {
    const paramsMap: { [key: string]: string } = { courseid: this.courseEditFormModel.courseId };

    this.httpRequestService.delete('/instructors/course/delete', paramsMap)
        .subscribe((resp: MessageOutput) => {
          this.navigationService.navigateWithSuccessMessage(this.router, '/web/instructor/courses', resp.message);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
  }

  /**
   * Converts an instructor to an instructor edit form model.
   */
  private getInstructorEditFormModel(instructor: Instructor): InstructorEditFormModel {
    return {
      googleId: instructor.googleId,
      name: instructor.name,
      email: instructor.email,
      role: instructor.role,
      isDisplayedToStudents: instructor.isDisplayedToStudents,
      displayedName: instructor.displayedName,
      courseLevel: instructor.privileges.courseLevel,
      instructorSectionPrivilegesFormModels:
          this.getInstructorSectionPrivilegesFormFormModels(instructor.privileges),

      isEditable: false,
      isSaving: false,
    };
  }

  /**
   * Converts an instructor's privileges to a section privilege form model.
   */
  private getInstructorSectionPrivilegesFormFormModels(
      privileges: Privileges): InstructorSectionPrivilegesFormFormModel[] {
    const instructorSectionPrivilegesFormModels: InstructorSectionPrivilegesFormFormModel[] = [];

    Object.keys(privileges.sectionLevel).forEach((section: string) => {
      const instructorSectionPrivilegesFormModel: InstructorSectionPrivilegesFormFormModel = {
        sections: this.getSectionMap(section),
        sectionLevel: privileges.sectionLevel[section],
        instructorSessionPrivilegesFormFormModels:
            this.getInstructorSessionPrivilegesFormFormModels(privileges, section),

        isSessionPrivilegesVisible: true,
      };
      instructorSectionPrivilegesFormModels.push(instructorSectionPrivilegesFormModel);
    });

    return instructorSectionPrivilegesFormModels;
  }

  /**
   * Gets a section map where only the input section is marked as having special privileges.
   */
  private getSectionMap(section: string): { [section: string]: boolean } {
    const sectionMap: { [section: string]: boolean } = {};

    this.sectionNames.forEach((sectionName: string) => {
      if (sectionName === section) {
        sectionMap[sectionName] = true;
      } else {
        sectionMap[sectionName] = false;
      }
    });
    return sectionMap;
  }

  /**
   * Converts an instructor's privileges to a session privilege form model.
   */
  private getInstructorSessionPrivilegesFormFormModels(privileges: Privileges,
                                                      section: string): InstructorSessionPrivilegesFormFormModel[] {
    const instructorSessionPrivilegesFormFormModels: InstructorSessionPrivilegesFormFormModel[] = [];

    this.feedbackNames.forEach((session: string) => {
      let sessionLevelPrivileges: SessionLevelPrivileges = privileges.sessionLevel[section][session];

      // Use section level privileges by default if there are no special session permissions
      if (sessionLevelPrivileges === null) {
        sessionLevelPrivileges = privileges.sectionLevel[section];
      }

      const instructorSessionPrivilegesFormFormModel: InstructorSessionPrivilegesFormFormModel = {
        sessionName: session,
        sessionLevel: sessionLevelPrivileges,
      };
      instructorSessionPrivilegesFormFormModels.push(instructorSessionPrivilegesFormFormModel);
    });

    return instructorSessionPrivilegesFormFormModels;
  }

  /**
   * Saves the updated instructor details.
   */
  editInstructorHandler(instructorEditFormModel: InstructorEditFormModel, instructorIndex: number): void {
    instructorEditFormModel.isSaving = true;

    let paramsMap: { [key: string]: string } = {
      courseid: this.courseEditFormModel.courseId,
      instructorid: instructorEditFormModel.googleId,
      instructorname: instructorEditFormModel.name,
      instructoremail: instructorEditFormModel.email,
      instructorrole: instructorEditFormModel.role,
      instructordisplayname: instructorEditFormModel.displayedName,
      instructorisdisplayed: instructorEditFormModel.isDisplayedToStudents.toString(),
    };
    paramsMap = this.addAdditionalParams(instructorEditFormModel, paramsMap);

    this.httpRequestService.post('/instructors/course/details/editInstructor', paramsMap)
        .subscribe((updatedInstructor: Instructor) => {
          this.instructorFormModels[instructorIndex] = updatedInstructor;
          this.instructorEditFormModels[instructorIndex] = this.getInstructorEditFormModel(updatedInstructor);

          if (updatedInstructor.googleId === this.instructor.googleId) {
            this.instructor = updatedInstructor;
          }

          this.statusMessageService
              .showSuccessMessage(`The changes to the instructor ${instructorEditFormModel.name} has been updated.`);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
          this.cancelEditHandler(InstructorEditFormMode.EDIT, instructorIndex);
        });
  }

  /**
   * Adds additional parameters to the params map for editing and adding instructors.
   */
  private addAdditionalParams(instructorEditFormModel: InstructorEditFormModel,
                              paramsMap: { [key: string]: string }): { [key: string]: string } {
    let currentMap: { [key: string]: string } = paramsMap;

    if (instructorEditFormModel.role === Role.CUSTOM) {
      currentMap = this.addCourseLevelParams(instructorEditFormModel, currentMap);
      currentMap = this.addSectionAndSessionLevelParams(instructorEditFormModel, currentMap);
    }

    return currentMap;
  }

  /**
   * Adds course level permissions to be saved to the parameters map.
   */
  private addCourseLevelParams(instructorEditFormModel: InstructorEditFormModel,
                               paramsMap: { [key: string]: string }): { [key: string]: string } {
    const courseLevelPrivileges: CourseLevelPrivileges = instructorEditFormModel.courseLevel;

    // Append custom course level privileges
    Object.keys(courseLevelPrivileges).forEach((permission: string) => {
      if (courseLevelPrivileges[permission]) {
        paramsMap[permission] = 'true';
      }
    });

    return paramsMap;
  }

  /**
   * Adds section and session level permissions to be saved to the parameters map.
   */
  private addSectionAndSessionLevelParams(instructorEditFormModel: InstructorEditFormModel,
                                          paramsMap: { [key: string]: string }): { [key: string]: string } {

    instructorEditFormModel.instructorSectionPrivilegesFormModels.forEach(
        (instructorSectionPrivilegesFormModel: InstructorSectionPrivilegesFormFormModel,
         sectionGroupIdx: number) => {

          const hasSpecialPrivileges: boolean =
              this.checkSpecialPrivileges(instructorEditFormModel, instructorSectionPrivilegesFormModel);

          // Skip models without any special privileges
          if (!hasSpecialPrivileges) {
            return;
          }

          paramsMap[`issectiongroup${sectionGroupIdx}sessionsset`] = 'true';

          // Mark section as special if it has been checked in a section group
          Object.keys(instructorSectionPrivilegesFormModel.sections)
              .forEach((section: string, sectionIdx: number) => {
                if (instructorSectionPrivilegesFormModel.sections[section]) {
                  paramsMap[`issectiongroup${sectionIdx}set`] = 'true';
                  paramsMap[`sectiongroup${sectionGroupIdx}section${sectionIdx}`] = section;
                }
              });

          // Include section permissions for a section group
          Object.keys(instructorSectionPrivilegesFormModel.sectionLevel).forEach((permission: string) => {
            if (instructorSectionPrivilegesFormModel.sectionLevel[permission]) {
              paramsMap[`${permission}sectiongroup${sectionGroupIdx}`] = 'true';
            }
          });

          // Append custom session level privileges
          const instructorSessionPrivilegesFormFormModels: InstructorSessionPrivilegesFormFormModel[] =
              instructorSectionPrivilegesFormModel.instructorSessionPrivilegesFormFormModels;

          instructorSessionPrivilegesFormFormModels.forEach(
              (instructorSessionPrivilegesFormFormModel: InstructorSessionPrivilegesFormFormModel) => {
                const sessionName: string = instructorSessionPrivilegesFormFormModel.sessionName;

                Object.keys(instructorSessionPrivilegesFormFormModel.sessionLevel)
                    .forEach((permission: string) => {
                      if (instructorSessionPrivilegesFormFormModel.sessionLevel[permission]) {
                        paramsMap[`${permission}sectiongroup${sectionGroupIdx}feedback${sessionName}`] = 'true';
                      }
                    });
              });
        });

    return paramsMap;
  }

  /**
   * Checks if an instructor edit section privilege card has special privileges to be saved.
   */
  private checkSpecialPrivileges(instructorEditFormModel: InstructorEditFormModel,
                                 instructorSectionPrivilegesFormModel:
                                     InstructorSectionPrivilegesFormFormModel): boolean {

    let hasSpecialSectionPrivileges: boolean = false;
    let hasSpecialSessionPrivileges: boolean = false;

    const courseLevelAsSectionLevel: SectionLevelPrivileges = {
      canviewstudentinsection: instructorEditFormModel.courseLevel.canviewstudentinsection,
      canviewsessioninsection: instructorEditFormModel.courseLevel.canviewsessioninsection,
      cansubmitsessioninsection: instructorEditFormModel.courseLevel.cansubmitsessioninsection,
      canmodifysessioncommentinsection: instructorEditFormModel.courseLevel.canmodifysessioncommentinsection,
    };

    if (instructorSectionPrivilegesFormModel.sectionLevel !== courseLevelAsSectionLevel) {
      hasSpecialSectionPrivileges = true;
    }

    const sectionLevelAsSessionLevel: SessionLevelPrivileges = {
      canviewsessioninsection: instructorSectionPrivilegesFormModel.sectionLevel.canviewsessioninsection,
      cansubmitsessioninsection: instructorSectionPrivilegesFormModel.sectionLevel.cansubmitsessioninsection,
      canmodifysessioncommentinsection: instructorSectionPrivilegesFormModel.
          sectionLevel.canmodifysessioncommentinsection,
    };

    instructorSectionPrivilegesFormModel.instructorSessionPrivilegesFormFormModels.forEach(
        (instructorSessionPrivilegesFormFormModel: InstructorSessionPrivilegesFormFormModel) => {
          if (instructorSessionPrivilegesFormFormModel.sessionLevel !== sectionLevelAsSessionLevel) {
            hasSpecialSessionPrivileges = true;
          }
        });

    return hasSpecialSectionPrivileges || hasSpecialSessionPrivileges;
  }

  /**
   * Handles add instructor button click event.
   */
  addInstructorHandler(instructorAddForm: InstructorEditFormModel): void {
    let paramsMap: { [key: string]: string } = {
      courseid: this.courseEditFormModel.courseId,
      instructorname: instructorAddForm.name,
      instructoremail: instructorAddForm.email,
      instructorrole: instructorAddForm.role,
      instructordisplayname: instructorAddForm.displayedName,
    };

    const instructorIsDisplayed: string = 'instructorisdisplayed';
    if (instructorAddForm.isDisplayedToStudents) {
      paramsMap[instructorIsDisplayed] = 'true';
    }

    paramsMap = this.addAdditionalParams(instructorAddForm, paramsMap);

    this.httpRequestService.put('/instructors/course/details/addInstructor', paramsMap)
        .subscribe((addedInstructor: Instructor) => {
          this.instructorEditFormModels.push(this.getInstructorEditFormModel(addedInstructor));
          this.instructorFormModels.push(addedInstructor);

          this.statusMessageService.showSuccessMessage(`The instructor ${addedInstructor.name} has been added ` +
              `successfully. An email containing how to 'join' this course will be sent to ${addedInstructor.email}` +
              ' in a few minutes.');
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
    this.resetInstructorAddFormModel();
  }

  /**
   * Resets the add instructor form to a new form.
   */
  private resetInstructorAddFormModel(): void {
    this.instructorAddFormModel = {
      googleId: '',
      name: '',
      email: '',
      role: Role.COOWNER,
      isDisplayedToStudents: true,
      displayedName: 'Instructor',
      courseLevel: DefaultPrivileges.COOWNER.value.courseLevel,
      instructorSectionPrivilegesFormModels: [],

      isEditable: false,
      isSaving: false,
    };
  }

  /**
   * Handles the cancel edit button click event.
   */
  cancelEditHandler(formMode: InstructorEditFormMode, index: number): void {
    if (formMode === InstructorEditFormMode.ADD) {
      this.resetInstructorAddFormModel();
    } else {
      const instructor: Instructor = this.instructorFormModels[index];
      this.instructorEditFormModels[index] = this.getInstructorEditFormModel(instructor);
    }
  }

  /**
   * Opens a modal to show the privileges for a given role and its associated privileges.
   */
  viewPrivilegesHandler(role: Role, courseLevel: CourseLevelPrivileges): void {
    const modalRef: NgbModalRef = this.modalService.open(ViewPrivilegesModalComponent);

    modalRef.componentInstance.model = courseLevel;
    modalRef.componentInstance.instructorrole = role;
  }

  /**
   * Opens a modal to confirm resending an invitation email to an instructor.
   */
  resendReminderHandler(index: number): void {
    const modalRef: NgbModalRef = this.modalService.open(ResendReminderModalComponent);

    const instructorToResend: InstructorEditFormModel = this.instructorEditFormModels[index];
    modalRef.componentInstance.instructorname = instructorToResend.name;
    modalRef.componentInstance.courseId = this.courseEditFormModel.courseId;

    modalRef.result.then(() => {
      this.courseService.remindInstructorForJoin(this.courseEditFormModel.courseId, instructorToResend.email)
          .subscribe((resp: MessageOutput) => {
            this.statusMessageService.showSuccessMessage(resp.message);
          }, (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorMessage(resp.error.message);
          });
      }, () => {});
  }

  /**
   * Opens a modal to confirm deleting an instructor.
   */
  deleteInstructorHandler(index: number): void {
    const modalRef: NgbModalRef = this.modalService.open(DeleteInstructorModalComponent);

    const instructorToDelete: InstructorEditFormModel = this.instructorEditFormModels[index];
    modalRef.componentInstance.courseId = this.courseEditFormModel.courseId;
    modalRef.componentInstance.idToDelete = instructorToDelete.googleId;
    modalRef.componentInstance.nameToDelete = instructorToDelete.name;
    modalRef.componentInstance.currentId = this.instructor.googleId;

    modalRef.result.then(() => {
      const paramsMap: { [key: string]: string } = {
        courseid: this.courseEditFormModel.courseId,
        instructorid: this.instructor.googleId,
        instructoremail: instructorToDelete.email,
      };

      this.httpRequestService.delete('/instructors/course/details/deleteInstructor', paramsMap)
          .subscribe((resp: MessageOutput) => {
            if (instructorToDelete.googleId === this.instructor.googleId) {
              this.navigationService.navigateWithSuccessMessage(this.router, '/web/instructor/courses', resp.message);
            } else {
              this.instructorEditFormModels.splice(index, 1);
              this.instructorFormModels.splice(index, 1);
              this.statusMessageService.showSuccessMessage(resp.message);
            }
          }, (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorMessage(resp.error.message);
          });
    }, () => {});
  }
}
