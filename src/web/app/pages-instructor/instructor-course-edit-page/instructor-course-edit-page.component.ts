import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { MessageOutput } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { Instructor } from '../../Instructor';
import { CourseEditFormModel } from './course-edit-form/course-edit-form-model';
import { DeleteInstructorModalComponent } from './delete-instructor-modal/delete-instructor-modal.component';
import { InstructorEditFormModel } from "./instructor-edit-form/instructor-edit-form-model";
import {
  CourseLevelPrivileges, Privileges, Role, SectionLevelPrivileges, SessionLevelPrivileges,
} from './instructor-privileges-model';
import { ResendReminderModalComponent } from './resend-reminder-modal/resend-reminder-modal.component';
import { ViewPrivilegesModalComponent } from './view-privileges-modal/view-privileges-modal.component';
import {
  InstructorEditSectionPrivilegesFormModel,
  InstructorEditSessionPrivilegesFormModel
} from "./instructor-edit-form/instructor-edit-section-privileges-form/instructor-edit-section-privileges-form-model";

interface Course {
  id: string;
  name: string;
  timeZone: string;
}

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

  // models
  courseEditFormModel: CourseEditFormModel = {
    courseId: '',
    courseName: '',
    timeZone: 'UTC',

    isEditable: false,
    isSaving: false,
  };

  // to get the original question model
  instructorFormModels: Map<string, Instructor> = new Map();

  instructorEditFormModels: InstructorEditFormModel[] = [];

  user: string = '';
  instructor!: Instructor;
  instructorToShowIndex: number = -1;
  sectionNames: string[] = [];
  feedbackNames: string[] = [];

  constructor(private route: ActivatedRoute,
              private router: Router,
              private navigationService: NavigationService,
              private httpRequestService: HttpRequestService,
              private statusMessageService: StatusMessageService,
              private modalService: NgbModal) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.getCourseEditDetails(queryParams.courseid);
    });
  }

  /******************************************************************************
   * COURSE DETAILS RELATED FUNCTIONS
   ******************************************************************************/

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

          this.courseEditFormModel = this.getCourseEditFormModel(resp.courseToEdit);

          resp.instructorList.forEach((instructor: Instructor) => {
            this.instructorEditFormModels.push(this.getInstructorEditFormModel(instructor));
            this.instructorFormModels.set(instructor.googleId, instructor);
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

  /******************************************************************************
   * EDIT INSTRUCTOR RELATED FUNCTIONS
   ******************************************************************************/

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
      instructorEditSectionPrivilegesFormModels:
          this.getInstructorEditSectionPrivilegesFormModels(instructor.privileges),

      isEditable: false,
      isSaving: false,
    };
  }

  /**
   * Converts an instructor's privileges to a section privilege form model.
   */
  private getInstructorEditSectionPrivilegesFormModels(privileges: Privileges): InstructorEditSectionPrivilegesFormModel[] {
    let instructorEditSectionPrivilegesFormModels: InstructorEditSectionPrivilegesFormModel[] = [];

    Object.keys(privileges.sectionLevel).forEach((section: string) => {
      const instructorEditSectionPrivilegesFormModel: InstructorEditSectionPrivilegesFormModel = {
        sections: this.getSectionMap(section),
        sectionLevel: privileges.sectionLevel[section],
        instructorEditSessionPrivilegesFormModels:
            this.getInstructorEditSessionPrivilegesFormModels(privileges, section),

        isSessionPrivilegesVisible: true,
      };
      instructorEditSectionPrivilegesFormModels.push(instructorEditSectionPrivilegesFormModel);
    });

    return instructorEditSectionPrivilegesFormModels;
  }

  /**
   * Gets a section map where only the input section is marked as having special privileges.
   */
  private getSectionMap(section: string): { [section: string]: boolean } {
    let sectionMap: { [section: string]: boolean } = {};

    this.sectionNames.forEach((sectionName: string) => {
      if (sectionName == section) {
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
  private getInstructorEditSessionPrivilegesFormModels(privileges: Privileges,
                                                      section: string): InstructorEditSessionPrivilegesFormModel[] {
    let instructorEditSessionPrivilegesFormModels: InstructorEditSessionPrivilegesFormModel[] = [];

    this.feedbackNames.forEach((session: string) => {
      let sessionLevelPrivileges: SessionLevelPrivileges = privileges.sessionLevel[section][session];

      // Use section level privileges by default if there are no special session permissions
      if (sessionLevelPrivileges == null) {
        sessionLevelPrivileges = privileges.sectionLevel[section];
      }

      const instructorEditSessionPrivilegesFormModel: InstructorEditSessionPrivilegesFormModel = {
        sessionName: session,
        sessionLevel: sessionLevelPrivileges,
      };
      instructorEditSessionPrivilegesFormModels.push(instructorEditSessionPrivilegesFormModel);
    });

    return instructorEditSessionPrivilegesFormModels;
  }

  /**
   * Tracks the instructor edit form by instructor google id.
   */
  trackInstructorEditFormByFn(_: any, item: InstructorEditFormModel): any {
    return item.googleId;
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
    };

    const instructorIsDisplayed: string = 'instructorisdisplayed';
    if (instructorEditFormModel.isDisplayedToStudents) {
      paramsMap[instructorIsDisplayed] = 'true';
    }

    if (instructorEditFormModel.role == Role.CUSTOM) {
      paramsMap = this.addCourseLevelParams(instructorEditFormModel, paramsMap);
      paramsMap = this.addSectionAndSessionLevelParams(instructorEditFormModel, paramsMap);
    }

    this.httpRequestService.post('/instructors/course/details/editInstructor', paramsMap)
        .subscribe((updatedInstructor: Instructor) => {
          console.log('updated: ');
          console.log(updatedInstructor);
          this.instructorEditFormModels[instructorIndex] = this.getInstructorEditFormModel(updatedInstructor);
          this.instructorEditFormModels[instructorIndex].isSaving = false;
          this.instructorFormModels.set(updatedInstructor.googleId, updatedInstructor);

          if (updatedInstructor.googleId == this.instructor.googleId) {
            this.instructor = updatedInstructor;
          }

          this.statusMessageService
              .showSuccessMessage(`The changes to the instructor ${instructorEditFormModel.name} has been updated.`);
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(resp.error.message);
        });
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

    instructorEditFormModel.instructorEditSectionPrivilegesFormModels.forEach(
        (instructorEditSectionPrivilegesFormModel: InstructorEditSectionPrivilegesFormModel,
         sectionGroupIdx: number) => {

          let hasSpecialPrivileges: boolean =
              this.checkSpecialPrivileges(instructorEditFormModel, instructorEditSectionPrivilegesFormModel);

          // Skip models without any special privileges
          if (!hasSpecialPrivileges) {
            return;
          }

          paramsMap[`issectiongroup${sectionGroupIdx}sessionsset`] = 'true';

          // Mark section as special if it has been checked in a section group
          Object.keys(instructorEditSectionPrivilegesFormModel.sections)
              .forEach((section: string, sectionIdx: number) => {
                if (instructorEditSectionPrivilegesFormModel.sections[section]) {
                  paramsMap[`issectiongroup${sectionIdx}set`] = 'true';
                  paramsMap[`sectiongroup${sectionGroupIdx}section${sectionIdx}`] = section;
                }
              });

          // Include section permissions for a section group
          Object.keys(instructorEditSectionPrivilegesFormModel.sectionLevel).forEach((permission: string) => {
            if (instructorEditSectionPrivilegesFormModel.sectionLevel[permission]) {
              paramsMap[`${permission}sectiongroup${sectionGroupIdx}`] = 'true';
            }
          });

          // Append custom session level privileges
          const instructorEditSessionPrivilegesFormModels: InstructorEditSessionPrivilegesFormModel[] =
              instructorEditSectionPrivilegesFormModel.instructorEditSessionPrivilegesFormModels;

          instructorEditSessionPrivilegesFormModels.forEach(
              (instructorEditSessionPrivilegesFormModel: InstructorEditSessionPrivilegesFormModel) => {
                const sessionName: string = instructorEditSessionPrivilegesFormModel.sessionName;

                Object.keys(instructorEditSessionPrivilegesFormModel.sessionLevel)
                    .forEach((permission: string) => {
                      if (instructorEditSessionPrivilegesFormModel.sessionLevel[permission]) {
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
                                 instructorEditSectionPrivilegesFormModel: InstructorEditSectionPrivilegesFormModel): boolean {

    let hasSpecialSectionPrivileges: boolean = false;
    let hasSpecialSessionPrivileges: boolean = false;

    const courseLevelAsSectionLevel: SectionLevelPrivileges = {
      canviewstudentinsection: instructorEditFormModel.courseLevel.canviewstudentinsection,
      canviewsessioninsection: instructorEditFormModel.courseLevel.canviewsessioninsection,
      cansubmitsessioninsection: instructorEditFormModel.courseLevel.cansubmitsessioninsection,
      canmodifysessioncommentinsection: instructorEditFormModel.courseLevel.canmodifysessioncommentinsection,
    };

    if (instructorEditSectionPrivilegesFormModel.sectionLevel != courseLevelAsSectionLevel) {
      hasSpecialSectionPrivileges = true;
    }

    const sectionLevelAsSessionLevel: SessionLevelPrivileges = {
      canviewsessioninsection: instructorEditSectionPrivilegesFormModel.sectionLevel.canviewsessioninsection,
      cansubmitsessioninsection: instructorEditSectionPrivilegesFormModel.sectionLevel.cansubmitsessioninsection,
      canmodifysessioncommentinsection: instructorEditSectionPrivilegesFormModel.sectionLevel.canmodifysessioncommentinsection,
    };

    instructorEditSectionPrivilegesFormModel.instructorEditSessionPrivilegesFormModels.forEach(
        (instructorEditSessionPrivilegesFormModel: InstructorEditSessionPrivilegesFormModel) => {
          if (instructorEditSessionPrivilegesFormModel.sessionLevel != sectionLevelAsSessionLevel) {
            hasSpecialSessionPrivileges = true;
          }
        });

    return hasSpecialSectionPrivileges || hasSpecialSessionPrivileges;
  }

  /******************************************************************************
   * ADD INSTRUCTOR RELATED FUNCTIONS
   *****************************************************************************

   /**
   * Toggles the add instructor form.
  toggleIsAddingInstructor(): void {
      this.isAddingInstructor = !this.isAddingInstructor;

    if (this.isAddingInstructor) {
      this.initAddInstructorForm();
    }
  }

  /**
   * Initialises a new form for adding an instructor to the current course.
  private initAddInstructorForm(): void {
    this.formAddInstructor = this.fb.group({
      googleId: [''],
      name: [''],
      email: [''],
      isDisplayedToStudents: [{ value: true }],
      displayedName: ['Instructor'],
      role: ['Co-owner'],
      privileges: this.defaultPrivileges.coowner,
      tunePermissions: this.fb.group({
        permissionsForCourse: this.fb.group({
          canmodifycourse: true,
          canmodifyinstructor: true,
          canmodifysession: true,
          canmodifystudent: true,
          canviewstudentinsection: true,
          canviewsessioninsection: true,
          cansubmitsessioninsection: true,
          canmodifysessioncommentinsection: true,
        }),
        tuneSectionGroupPermissions: this.fb.array([]),
      }),
    });

    // Listen for changes to custom privileges
    const roleControl: (AbstractControl | null) = this.formAddInstructor.get('role');
    const permissionsControl: (FormGroup | null) = this.formAddInstructor.get('tunePermissions') as FormGroup;

    if (roleControl != null && permissionsControl != null) {
      roleControl.valueChanges.subscribe((selectedRole: string) => {
        const panelId: string = `tune-permissions-${this.instructorList.length}`;
        const panel: (HTMLElement | null) = document.getElementById(panelId);

        if (selectedRole === 'Custom' && panel != null) {
          panel.style.display = 'block';
          permissionsControl.controls.permissionsForCourse.reset();
          permissionsControl.controls.tuneSectionGroupPermissions = this.fb.array([]);
        } else if (panel != null) {
          panel.style.display = 'none';
        }
      });
    }
  }

  /**
   * Adds a new instructor to the current course.
  onSubmitAddInstructor(formAddInstructor: FormGroup): void {
    // Create a copy of the added instructor
    const addedInstructor: InstructorAttributes = {
      googleId: formAddInstructor.controls.googleId.value,
      name: formAddInstructor.controls.name.value,
      email: formAddInstructor.controls.email.value,
      role: formAddInstructor.controls.role.value,
      isDisplayedToStudents: formAddInstructor.controls.isDisplayedToStudents.value,
      displayedName: formAddInstructor.controls.displayedName.value,
      privileges: this.defaultPrivileges[formAddInstructor.controls.role.value],
    };

    const paramsMap: { [key: string]: string } = {
      courseid: this.courseEditFormModel.courseId,
      instructorname: addedInstructor.name,
      instructoremail: addedInstructor.email,
      instructorrole: addedInstructor.role,
      instructordisplayname: addedInstructor.displayedName,
    };

    const instructorIsDisplayed: string = 'instructorisdisplayed';
    if (addedInstructor.isDisplayedToStudents) {
      paramsMap[instructorIsDisplayed] = 'true';
    }

    if (formAddInstructor.controls.role.value === 'Custom') {
      const tuneCoursePermissions: (FormGroup | null) = (formAddInstructor.controls.tunePermissions as FormGroup)
          .controls.permissionsForCourse as FormGroup;

      // Append custom course level privileges
      Object.keys(tuneCoursePermissions.controls).forEach((permission: string) => {
        if (tuneCoursePermissions.controls[permission].value) {
          paramsMap[permission] = 'true';
        }
      });
      addedInstructor.privileges.courseLevel = tuneCoursePermissions.value;

      // Append custom section level privileges
      const tuneSectionGroupPermissions: (FormArray | null) = (formAddInstructor.controls.tunePermissions as FormGroup)
          .controls.tuneSectionGroupPermissions as FormArray;

      const newSectionLevelPrivileges: { [key: string]: SectionLevelPrivileges } = {};
      const newSessionLevelPrivileges: { [section: string]: { [session: string]: SessionLevelPrivileges } } = {};

      tuneSectionGroupPermissions.controls.forEach((sectionGroupPermissions: AbstractControl, panelIdx: number) => {
        const specialSections: string[] = [];

        // Mark section as special if it has been checked in a section group
        this.sectionNames.forEach((section: string, sectionIdx: number) => {
          if ((sectionGroupPermissions as FormGroup).controls[section].value) {
            paramsMap[`issectiongroup${sectionIdx}set`] = 'true';
            paramsMap[`sectiongroup${panelIdx}section${sectionIdx}`] = section;
            specialSections.push(section);
          }
        });

        // Include section permissions for a section group
        const permissionsInSection: (FormGroup | null) = (sectionGroupPermissions as FormGroup)
            .controls.permissionsForSection as FormGroup;
        Object.keys(permissionsInSection.controls).forEach((permission: string) => {
          if (permissionsInSection.controls[permission].value) {
            paramsMap[`${permission}sectiongroup${panelIdx}`] = 'true';
          }
        });

        // Save new section level privileges
        specialSections.forEach((section: string) => {
          newSectionLevelPrivileges[section] = permissionsInSection.value;
        });

        // Append custom session level privileges
        const permissionsForSessions: (FormGroup | null) = (sectionGroupPermissions as FormGroup)
            .controls.permissionsForSessions as FormGroup;
        const specialSessionsAndSessionPermissions: { [session: string]: SessionLevelPrivileges } = {};

        // Mark session as special if a session has different permissions from the section permissions
        const sectionLevelSessionPrivileges: SessionLevelPrivileges = {
          canviewsessioninsection: permissionsInSection.controls.canviewsessioninsection.value,
          cansubmitsessioninsection: permissionsInSection.controls.cansubmitsessioninsection.value,
          canmodifysessioncommentinsection: permissionsInSection.controls.canmodifysessioncommentinsection.value,
        };

        this.feedbackNames.forEach((feedback: string) => {
          const permissionsForSession: (FormGroup | null) = permissionsForSessions.controls[feedback] as FormGroup;
          if (permissionsForSession.value !== sectionLevelSessionPrivileges) {
            Object.keys(permissionsForSession.controls).forEach((permission: string) => {
              if (permissionsForSession.controls[permission].value) {
                paramsMap[`${permission}sectiongroup${panelIdx}feedback${feedback}`] = 'true';
              }
            });
            specialSessionsAndSessionPermissions[feedback] = permissionsForSession.value;
          }
        });

        if (Object.keys(specialSessionsAndSessionPermissions).length > 0) {
          paramsMap[`issectiongroup${panelIdx}sessionsset`] = 'true';
        }

        // Save new section level privileges
        specialSections.forEach((section: string) => {
          newSessionLevelPrivileges[section] = specialSessionsAndSessionPermissions;
        });
      });

      addedInstructor.privileges.sectionLevel = newSectionLevelPrivileges;
      addedInstructor.privileges.sessionLevel = newSessionLevelPrivileges;
    }

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
  private addToInstructorList(instructor: InstructorAttributes): void {
    this.instructorList.push(instructor);
    this.initEditInstructorsForm();
  }

  /******************************************************************************
   * MODAL RELATED FUNCTIONS
   ******************************************************************************/

  /**
   * Opens a modal to show the privileges for a given role and its associated privileges.
   */
  viewPrivilegesHandler(role: Role, privileges: Privileges): void {
    const modalRef: NgbModalRef = this.modalService.open(ViewPrivilegesModalComponent);

    modalRef.componentInstance.model = privileges.courseLevel;
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
      const paramsMap: { [key: string]: string } = {
        courseid: this.courseEditFormModel.courseId,
        instructoremail: instructorToResend.email,
      };

      this.httpRequestService.post('/instructors/course/details/sendReminders', paramsMap)
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
              this.statusMessageService.showSuccessMessage(resp.message);
            }
          }, (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorMessage(resp.error.message);
          });
    }, () => {});
  }
}
