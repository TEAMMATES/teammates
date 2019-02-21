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
import { CourseLevelPrivileges, DefaultPrivileges, Privileges, Role, } from './instructor-privileges-model';
import { ResendReminderModalComponent } from './resend-reminder-modal/resend-reminder-modal.component';
import { ViewPrivilegesModalComponent } from './view-privileges-modal/view-privileges-modal.component';

interface Course {
  id: string;
  name: string;
  creationDate: string;
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
  defaultPrivileges!: DefaultPrivileges;

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
      sectionLevel: instructor.privileges.sectionLevel,
      sessionLevel: instructor.privileges.sessionLevel,

      isEditable: false,
      isSaving: false,
    };
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
  editInstructorHandler(instructorEditFormModel: InstructorEditFormModel, index: number): void {
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

      /**
       // Append custom section level privileges
       const tuneSectionGroupPermissions: (FormArray | null) = (instr.controls.tunePermissions as FormGroup)
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

       editedInstructor.privileges.sectionLevel = newSectionLevelPrivileges;
       editedInstructor.privileges.sessionLevel = newSessionLevelPrivileges;
       }*/
    }

    this.httpRequestService.post('/instructors/course/details/editInstructor', paramsMap)
        .subscribe((updatedInstructor: Instructor) => {
          this.instructorEditFormModels[index] = this.getInstructorEditFormModel(updatedInstructor);
          this.instructorEditFormModels[index].isSaving = false;
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

  /**
   * Adds an additional panel to modify custom section privileges for a given instructor.
  addTuneSectionGroupPermissionsPanel(instr: FormGroup, index: number): void {
    const instructor: InstructorAttributes = this.instructorList[index];
    const newSection: FormGroup = this.fb.group({
      permissionsForSection: this.fb.group({
        canviewstudentinsection: instructor.privileges.courseLevel.canviewstudentinsection,
        cansubmitsessioninsection: instructor.privileges.courseLevel.cansubmitsessioninsection,
        canviewsessioninsection: instructor.privileges.courseLevel.canviewsessioninsection,
        canmodifysessioncommentinsection: instructor.privileges.courseLevel.canmodifysessioncommentinsection,
      }),
      permissionsForSessions: this.fb.group({}),
    });

    this.sectionNames.forEach((sectionName: string) => {
      newSection.addControl(sectionName, this.fb.control(false));
    });

    const sectionPrivileges: FormGroup = newSection.controls.permissionsForSection as FormGroup;
    sectionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
      if (!isAbleToSubmit) {
        sectionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
      }
    });

    sectionPrivileges.controls.canmodifysessioncommentinsection.valueChanges.subscribe((isAbleToModify: boolean) => {
      if (isAbleToModify) {
        sectionPrivileges.controls.canviewsessioninsection.setValue(true);
      }
    });

    this.feedbackNames.forEach((feedback: string) => {
      const defaultSessionPrivileges: FormGroup = this.fb.group({
        canviewsessioninsection: false,
        cansubmitsessioninsection: false,
        canmodifysessioncommentinsection: false,
      });

      defaultSessionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
        if (!isAbleToSubmit) {
          defaultSessionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
        }
      });

      defaultSessionPrivileges.controls.canmodifysessioncommentinsection.valueChanges
          .subscribe((isAbleToModify: boolean) => {
            if (isAbleToModify) {
              defaultSessionPrivileges.controls.canviewsessioninsection.setValue(true);
            }
          });

      (newSection.controls.permissionsForSessions as FormGroup).addControl(feedback, defaultSessionPrivileges);
    });

    ((instr.controls.tunePermissions as FormGroup).controls.tuneSectionGroupPermissions as FormArray).push(newSection);
  }

  /**
   * Adds a default tune section group permission panel.
  addEmptyTuneSectionGroupPermissionsPanel(instr: FormGroup): void {
    const newSection: FormGroup = this.fb.group({
      permissionsForSection: this.fb.group({
        canviewstudentinsection: false,
        cansubmitsessioninsection: false,
        canviewsessioninsection: false,
        canmodifysessioncommentinsection: false,
      }),
      permissionsForSessions: this.fb.group({}),
    });

    this.sectionNames.forEach((sectionName: string) => {
      newSection.addControl(sectionName, this.fb.control(false));
    });

    const sectionPrivileges: FormGroup = newSection.controls.permissionsForSection as FormGroup;
    sectionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
      if (!isAbleToSubmit) {
        sectionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
      }
    });

    sectionPrivileges.controls.canmodifysessioncommentinsection.valueChanges.subscribe((isAbleToModify: boolean) => {
      if (isAbleToModify) {
        sectionPrivileges.controls.canviewsessioninsection.setValue(true);
      }
    });

    this.feedbackNames.forEach((feedback: string) => {
      const defaultSessionPrivileges: FormGroup = this.fb.group({
        canviewsessioninsection: false,
        cansubmitsessioninsection: false,
        canmodifysessioncommentinsection: false,
      });

      defaultSessionPrivileges.controls.canviewsessioninsection.valueChanges.subscribe((isAbleToSubmit: boolean) => {
        if (!isAbleToSubmit) {
          defaultSessionPrivileges.controls.canmodifysessioncommentinsection.setValue(false);
        }
      });

      defaultSessionPrivileges.controls.canmodifysessioncommentinsection.valueChanges
          .subscribe((isAbleToModify: boolean) => {
            if (isAbleToModify) {
              defaultSessionPrivileges.controls.canviewsessioninsection.setValue(true);
            }
          });

      (newSection.controls.permissionsForSessions as FormGroup).addControl(feedback, defaultSessionPrivileges);
    });

    ((instr.controls.tunePermissions as FormGroup).controls.tuneSectionGroupPermissions as FormArray).push(newSection);
  }

  /**
   * Removes a panel to modify custom section privileges for a given instructor.
  removeTuneSectionGroupPermissionsPanel(instr: FormGroup, index: number): void {
    ((instr.controls.tunePermissions as FormGroup).controls.tuneSectionGroupPermissions as FormArray).removeAt(index);
  }

  /**
   * Hides session level permissions for a section panel.
  hideSessionLevelPermissions(panelIdx: number, sectionIdx: number): void {
    const table: (HTMLElement | null) = document.getElementById(`tune-session-permissions-${panelIdx}-${sectionIdx}`);
    const hideLink: (HTMLElement | null) = document.getElementById(`hide-link-${panelIdx}-${sectionIdx}`);
    const showLink: (HTMLElement | null) = document.getElementById(`show-link-${panelIdx}-${sectionIdx}`);

    if (table != null && hideLink != null && showLink != null) {
      table.style.display = 'none';
      hideLink.style.display = 'none';
      showLink.style.display = 'block';
    }
  }

  /**
   * Shows session level permissions for a section panel.
  showSessionLevelPermissions(panelIdx: number, sectionIdx: number): void {
    const table: (HTMLElement | null) = document.getElementById(`tune-session-permissions-${panelIdx}-${sectionIdx}`);
    const hideLink: (HTMLElement | null) = document.getElementById(`hide-link-${panelIdx}-${sectionIdx}`);
    const showLink: (HTMLElement | null) = document.getElementById(`show-link-${panelIdx}-${sectionIdx}`);

    if (table != null && hideLink != null && showLink != null) {
      table.style.display = 'block';
      hideLink.style.display = 'block';
      showLink.style.display = 'none';
    }
  }*/

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
