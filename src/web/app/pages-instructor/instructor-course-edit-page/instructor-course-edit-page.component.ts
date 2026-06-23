import { Component, EventEmitter, Input, OnInit, inject } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal';
import { forkJoin, Observable, of } from 'rxjs';
import { concatMap, finalize, map } from 'rxjs/operators';
import { CourseTabModel } from './copy-instructors-from-other-courses-modal/copy-instructors-from-other-courses-modal-model';
import { CopyInstructorsFromOtherCoursesModalComponent } from './copy-instructors-from-other-courses-modal/copy-instructors-from-other-courses-modal.component';
import {
  InstructorOverallPermission,
  InstructorSectionLevelPermission,
  InstructorSessionLevelPermission,
} from './custom-privilege-setting-panel/custom-privilege-setting-panel.component';
import {
  EditMode,
  InstructorEditPanel,
  InstructorEditPanelComponent,
} from './instructor-edit-panel/instructor-edit-panel.component';
import { ViewRolePrivilegesModalComponent } from './view-role-privileges-modal/view-role-privileges-modal.component';
import { CourseService } from '../../../services/course.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import {
  AuthInfo,
  Course,
  CourseView,
  FeedbackSessionView,
  FeedbackSessions,
  Instructor,
  InstructorCoursePermissions,
  InstructorPermissionRole,
  InstructorPermissionSet,
  InstructorPrivilege,
  InstructorPrivileges,
  Instructors,
  JoinState,
  MessageOutput,
  Student,
  Students,
} from '../../../types/api-output';
import { InstructorCreateRequest, InstructorUpdateRequest } from '../../../types/api-request';
import {
  DEFAULT_INSTRUCTOR_PRIVILEGE,
  DEFAULT_PRIVILEGE_COOWNER,
  DEFAULT_PRIVILEGE_MANAGER,
  DEFAULT_PRIVILEGE_OBSERVER,
  DEFAULT_PRIVILEGE_TUTOR,
} from '../../../types/default-instructor-privilege';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { AjaxLoadingComponent } from '../../components/ajax-loading/ajax-loading.component';
import {
  CourseEditFormMode,
  CourseEditFormModel,
  DEFAULT_COURSE_EDIT_FORM_MODEL,
} from '../../components/course-edit-form/course-edit-form-model';
import { CourseEditFormComponent } from '../../components/course-edit-form/course-edit-form.component';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { TeammatesRouterDirective } from '../../components/teammates-router/teammates-router.directive';
import { ErrorMessageOutput } from '../../error-message-output';
import { CoursesSectionQuestions } from '../../pages-help/instructor-help-page/instructor-help-courses-section/courses-section-questions';
import { Sections } from '../../pages-help/instructor-help-page/sections';
import { AuthService } from '../../../services/auth.service';

interface InstructorEditPanelDetail {
  originalInstructor: Instructor;
  originalPanel: InstructorEditPanel;
  editPanel: InstructorEditPanel;
}

/**
 * Instructor course edit page.
 */
@Component({
  selector: 'tm-instructor-course-edit-page',
  templateUrl: './instructor-course-edit-page.component.html',
  imports: [
    LoadingRetryComponent,
    LoadingSpinnerDirective,
    CourseEditFormComponent,
    TeammatesRouterDirective,
    InstructorEditPanelComponent,
    AjaxLoadingComponent,
    NgbCollapse,
  ],
})
export class InstructorCourseEditPageComponent implements OnInit {
  private readonly navigationService = inject(NavigationService);
  private readonly studentService = inject(StudentService);
  private readonly instructorService = inject(InstructorService);
  private readonly feedbackSessionsService = inject(FeedbackSessionsService);
  private readonly statusMessageService = inject(StatusMessageService);
  private readonly courseService = inject(CourseService);
  private readonly authService = inject(AuthService);
  private readonly ngbModal = inject(NgbModal);
  private readonly simpleModalService = inject(SimpleModalService);

  // enum
  EditMode!: typeof EditMode;
  CoursesSectionQuestions!: typeof CoursesSectionQuestions;
  Sections!: typeof Sections;
  CourseEditFormMode!: typeof CourseEditFormMode;

  @Input({ required: true }) courseId!: string;
  authInfo: AuthInfo | null = null;
  currInstructorCoursePrivilege?: InstructorCoursePermissions;

  instructorDetailPanels: InstructorEditPanelDetail[] = [];

  isAddingNewInstructor = false;
  isCopyingInstructor = false;
  newInstructorPanel: InstructorEditPanel = this.getDefaultInstructorPanel({
    isEditing: true,
  });

  courseFormModel: CourseEditFormModel = DEFAULT_COURSE_EDIT_FORM_MODEL();
  resetCourseFormEvent: EventEmitter<void> = new EventEmitter();

  // for fine-grain permission setting
  allSections: { id: string; name: string }[] = [];
  allSessions: { id: string; name: string }[] = [];

  isCourseLoading = false;
  hasCourseLoadingFailed = false;
  isInstructorsLoading = false;
  hasInstructorsLoadingFailed = false;
  isSavingNewInstructor = false;

  constructor() {
    this.EditMode = EditMode;
    this.CoursesSectionQuestions = CoursesSectionQuestions;
    this.Sections = Sections;
    this.CourseEditFormMode = CourseEditFormMode;
  }

  ngOnInit(): void {
    this.loadCourseInfo();
    this.loadCurrInstructorInfo();

    // load all section and session name
    forkJoin([
      this.studentService.getStudents({ courseIds: [this.courseId] }),
      this.feedbackSessionsService.getFeedbackSessionsForInstructor(this.courseId),
    ]).subscribe((vals) => {
      const students: Students = vals[0];
      const sessions: FeedbackSessions = vals[1];

      this.allSections = Array.from(
        new Map(students.students.map((s: Student) => [s.sectionId, s.sectionName])).entries(),
      ).map(([id, name]) => ({ id, name }));
      this.allSessions = sessions.feedbackSessions.map((sv: FeedbackSessionView) => ({
        id: sv.feedbackSession.feedbackSessionId,
        name: sv.feedbackSession.feedbackSessionName,
      }));

      this.loadCourseInstructors();
    });
  }

  /**
   * Loads the course being edited.
   */
  loadCourseInfo(): void {
    this.hasCourseLoadingFailed = false;
    this.isCourseLoading = true;
    this.courseService
      .getCourseAsInstructor(this.courseId)
      .pipe(
        finalize(() => {
          this.isCourseLoading = false;
        }),
      )
      .subscribe({
        next: (resp: CourseView) => {
          this.courseFormModel.course = resp.course;
          this.courseFormModel.originalCourse = { ...resp.course };
          this.currInstructorCoursePrivilege = resp.instructorPermissions;
          this.courseFormModel.canModifyCourse = this.currInstructorCoursePrivilege?.canModifyCourse ?? false;
        },
        error: (resp: ErrorMessageOutput) => {
          this.hasCourseLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Loads the information of the current logged-in instructor.
   */
  loadCurrInstructorInfo(): void {
    this.hasInstructorsLoadingFailed = false;
    this.isInstructorsLoading = true;
    this.authService
      .getAuthUser()
      .pipe(
        finalize(() => {
          this.isInstructorsLoading = false;
        }),
      )
      .subscribe({
        next: (authInfo) => {
          this.authInfo = authInfo;
        },
        error: (resp: ErrorMessageOutput) => {
          this.hasInstructorsLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Deletes the current course and redirects to 'Courses' page if action is successful.
   */
  deleteCourse(): void {
    this.courseService.binCourse(this.courseId).subscribe({
      next: (course: Course) => {
        this.navigationService.navigateWithSuccessMessage(
          '/web/instructor/courses',
          `The course ${course.courseId} has been deleted. You can restore it from the Recycle Bin manually.`,
        );
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Saves the updated course details.
   */
  onSaveCourse(): void {
    this.courseFormModel.isSaving = true;
    this.courseService
      .updateCourse(this.courseId, {
        courseName: this.courseFormModel.course.courseName,
        timeZone: this.courseFormModel.course.timeZone,
      })
      .pipe(
        finalize(() => {
          this.courseFormModel.isSaving = false;
        }),
      )
      .subscribe({
        next: (resp: Course) => {
          this.statusMessageService.showSuccessToast('The course has been edited.');
          this.courseFormModel.isEditing = false;
          this.courseFormModel.course = resp;
          this.courseFormModel.originalCourse = { ...resp };
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    this.resetCourseFormEvent.emit();
  }

  /**
   * Loads all instructors in the course.
   */
  loadCourseInstructors(): void {
    this.hasInstructorsLoadingFailed = false;
    this.isInstructorsLoading = true;
    this.instructorService.loadInstructors({ courseId: this.courseId }).subscribe({
      next: (resp: Instructors) => {
        this.instructorDetailPanels = resp.instructors.map((i: Instructor) => ({
          originalInstructor: { ...i },
          originalPanel: this.getInstructorEditPanelModel(i),
          editPanel: this.getInstructorEditPanelModel(i),
          isSavingInstructorEdit: false,
        }));
        this.instructorDetailPanels.forEach((panel: InstructorEditPanelDetail) => {
          this.loadPermissionForInstructor(panel);
        });
      },
      error: (resp: ErrorMessageOutput) => {
        this.hasInstructorsLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Gets the default edit panel model of an instructor.
   */
  getInstructorEditPanelModel(i: Instructor): InstructorEditPanel {
    return this.getDefaultInstructorPanel({
      id: i.userId,
      courseId: i.courseId,
      email: i.email,
      isDisplayedToStudents: i.isDisplayedToStudents,
      displayedToStudentsAs: i.displayedToStudentsAs,
      name: i.name,
      role: i.role,
      joinState: i.joinState,
    });
  }

  /**
   * Gets a default InstructorEditPanel with optional overrides.
   *
   * @param overrides Properties to overwrite the base model.
   * @param defaultPrivileges Boolean to set all nested privileges to true or false.
   */
  private getDefaultInstructorPanel(
    overrides: Partial<InstructorEditPanel> = {},
    defaultPrivileges = false,
  ): InstructorEditPanel {
    return {
      id: '',
      courseId: '',
      email: '',
      isDisplayedToStudents: true,
      displayedToStudentsAs: 'Instructor',
      name: '',
      role: InstructorPermissionRole.COOWNER,
      joinState: JoinState.NOT_JOINED,

      permission: {
        privilege: {
          canModifyCourse: defaultPrivileges,
          canModifySession: defaultPrivileges,
          canModifyStudent: defaultPrivileges,
          canModifyInstructor: defaultPrivileges,
          canViewStudent: defaultPrivileges,
          canModifySessionComments: defaultPrivileges,
          canViewSession: defaultPrivileges,
          canSubmitSession: defaultPrivileges,
        },
        sectionLevel: [],
      },

      isEditing: false,
      isSavingInstructorEdit: false,
      ...overrides,
    };
  }

  /**
   * Gets the default CourseTabModel with optional overrides.
   *
   * @param overrides Properties to overwrite the base model.
   */
  private getDefaultCourseTab(overrides: Partial<CourseTabModel> = {}): CourseTabModel {
    return {
      courseId: '',
      courseName: '',
      creationTimestamp: 0,
      instructorCandidates: [],
      instructorCandidatesSortBy: SortBy.NONE,
      instructorCandidatesSortOrder: SortOrder.ASC,
      hasInstructorsLoaded: false,
      isTabExpanded: false,
      hasLoadingFailed: false,
      ...overrides,
    };
  }

  /**
   * Shows the model of details permission for a role.
   */
  viewRolePrivilegeModel(role: InstructorPermissionRole): void {
    const modalRef: NgbModalRef = this.ngbModal.open(ViewRolePrivilegesModalComponent);
    modalRef.result.then(
      () => {},
      () => {},
    );
    let privilege: InstructorPermissionSet;
    switch (role) {
      case InstructorPermissionRole.COOWNER:
        privilege = DEFAULT_PRIVILEGE_COOWNER();
        break;
      case InstructorPermissionRole.MANAGER:
        privilege = DEFAULT_PRIVILEGE_MANAGER();
        break;
      case InstructorPermissionRole.OBSERVER:
        privilege = DEFAULT_PRIVILEGE_OBSERVER();
        break;
      case InstructorPermissionRole.TUTOR:
        privilege = DEFAULT_PRIVILEGE_TUTOR();
        break;
      case InstructorPermissionRole.CUSTOM:
      default:
        privilege = DEFAULT_INSTRUCTOR_PRIVILEGE();
    }
    modalRef.componentInstance.instructorPrivilege = privilege;
    modalRef.componentInstance.role = role;
  }

  /**
   * Cancels editing an instructor.
   */
  cancelEditingInstructor(index: number): void {
    const panelDetail: InstructorEditPanelDetail = this.instructorDetailPanels[index];
    panelDetail.editPanel = structuredClone(panelDetail.originalPanel);
    panelDetail.editPanel.isSavingInstructorEdit = false;
    panelDetail.editPanel.isEditing = false;
  }

  /**
   * Saves instructor at index.
   */
  saveInstructor(index: number): void {
    const panelDetail: InstructorEditPanelDetail = this.instructorDetailPanels[index];
    panelDetail.editPanel.isSavingInstructorEdit = true;
    const reqBody: InstructorUpdateRequest = {
      name: panelDetail.editPanel.name,
      email:
        panelDetail.originalInstructor.joinState === JoinState.JOINED
          ? panelDetail.editPanel.email
          : panelDetail.originalInstructor.email,
      role: panelDetail.editPanel.role,
      displayName: panelDetail.editPanel.displayedToStudentsAs,
      isDisplayedToStudent: panelDetail.editPanel.isDisplayedToStudents,
      privileges:
        panelDetail.editPanel.role === InstructorPermissionRole.CUSTOM
          ? this.toInstructorPrivileges(panelDetail.editPanel.permission)
          : undefined,
    };

    this.instructorService
      .updateInstructor({ instructorId: panelDetail.originalInstructor.userId }, reqBody)
      .pipe(
        finalize(() => {
          panelDetail.editPanel.isSavingInstructorEdit = false;
        }),
      )
      .subscribe({
        next: (resp: Instructor) => {
          panelDetail.editPanel.isEditing = false;
          panelDetail.originalInstructor = { ...resp };
          const permission: InstructorOverallPermission = panelDetail.editPanel.permission;

          panelDetail.editPanel = this.getInstructorEditPanelModel(resp);
          panelDetail.editPanel.permission = permission;

          this.statusMessageService.showSuccessToast(`The instructor ${resp.name} has been updated.`);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });

    panelDetail.originalPanel = structuredClone(panelDetail.editPanel);
  }

  /**
   * Deletes instructor at index.
   */
  deleteInstructor(index: number): void {
    const panelDetail: InstructorEditPanelDetail = this.instructorDetailPanels[index];
    const isDeletingSelf: boolean =
      !!panelDetail.originalInstructor.accountId &&
      panelDetail.originalInstructor.accountId === this.authInfo?.user?.accountId;
    const modalContent: string = isDeletingSelf
      ? `Are you sure you want to delete your instructor role
        from the course <strong>${panelDetail.originalInstructor.courseId}</strong>?
        You will not be able to access the course anymore.`
      : `Are you sure you want to delete the instructor <strong>${panelDetail.originalInstructor.name}</strong>
        from the course <strong>${panelDetail.originalInstructor.courseId}</strong>?
        He/she will not be able to access the course anymore.`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Delete instructor <strong>${panelDetail.originalInstructor.name}</strong>?`,
      SimpleModalType.DANGER,
      modalContent,
    );

    modalRef.result.then(
      () => {
        this.instructorService
          .deleteInstructor({
            userId: panelDetail.originalInstructor.userId,
          })
          .subscribe({
            next: () => {
              if (isDeletingSelf) {
                this.navigationService.navigateWithSuccessMessage(
                  '/web/instructor/courses',
                  'Instructor is successfully deleted.',
                );
              } else {
                this.instructorDetailPanels.splice(index, 1);
                this.statusMessageService.showSuccessToast('Instructor is successfully deleted.');
              }
            },
            error: (resp: ErrorMessageOutput) => {
              this.statusMessageService.showErrorToast(resp.error.message);
            },
          });
      },
      () => {},
    );
  }

  /**
   * Re-sends an invitation email to an instructor in the course.
   */
  resendReminderEmail(index: number): void {
    const panelDetail: InstructorEditPanelDetail = this.instructorDetailPanels[index];
    const modalContent = `Do you wish to re-send the invitation email to instructor
      ${panelDetail.originalInstructor.name} from course ${panelDetail.originalInstructor.courseId}?`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      'Re-send invitation email?',
      SimpleModalType.INFO,
      modalContent,
    );

    modalRef.result.then(
      () => {
        this.courseService.remindUserForJoin(panelDetail.originalInstructor.userId).subscribe({
          next: (resp: MessageOutput) => {
            this.statusMessageService.showSuccessToast(resp.message);
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
      },
      () => {},
    );
  }

  /**
   * Adds new instructor.
   */
  addNewInstructor(): void {
    this.isSavingNewInstructor = true;
    const reqBody: InstructorCreateRequest = {
      name: this.newInstructorPanel.name,
      email: this.newInstructorPanel.email,
      role: this.newInstructorPanel.role,
      displayName: this.newInstructorPanel.displayedToStudentsAs,
      isDisplayedToStudent: this.newInstructorPanel.isDisplayedToStudents,
      privileges:
        this.newInstructorPanel.role === InstructorPermissionRole.CUSTOM
          ? this.toInstructorPrivileges(this.newInstructorPanel.permission)
          : undefined,
    };

    this.instructorService
      .createInstructor({ courseId: this.courseId }, reqBody)
      .pipe(
        finalize(() => {
          this.isSavingNewInstructor = false;
        }),
      )
      .subscribe({
        next: (resp: Instructor) => {
          const newDetailPanels: InstructorEditPanelDetail = {
            originalInstructor: { ...resp },
            originalPanel: this.getInstructorEditPanelModel(resp),
            editPanel: this.getInstructorEditPanelModel(resp),
          };
          newDetailPanels.editPanel.permission = this.newInstructorPanel.permission;
          newDetailPanels.originalPanel = structuredClone(newDetailPanels.editPanel);

          this.instructorDetailPanels.push(newDetailPanels);
          this.statusMessageService.showSuccessToast(
            `The instructor ${resp.name} has been added successfully. ` +
              `An email containing how to 'join' this course will be sent to ${resp.email} in a few minutes.`,
          );

          this.isAddingNewInstructor = false;

          this.newInstructorPanel = this.getDefaultInstructorPanel(
            {
              displayedToStudentsAs: '', // override the default 'Instructor'
              isEditing: true, // keeping the form open
            },
            true,
          );
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Loads permission for instructor.
   */
  loadPermissionForInstructor(panel: InstructorEditPanelDetail): void {
    const instructor: Instructor = panel.originalInstructor;
    const permission: InstructorOverallPermission = panel.editPanel.permission;

    if (instructor.role !== InstructorPermissionRole.CUSTOM) {
      this.isInstructorsLoading = false;
      return;
    }

    this.instructorService
      .loadInstructorPrivilege({
        userId: instructor.userId,
      })
      .subscribe((resp: InstructorPrivilege) => {
        permission.privilege = resp.privileges.courseLevel;

        this.allSections.forEach((section: { id: string; name: string }) => {
          const sectionLevelPermission: InstructorSectionLevelPermission = {
            sections: [section],
            privilege: resp.privileges.sectionLevel[section.id] || permission.privilege,
            sessionLevel: [],
          };

          this.allSessions.forEach((session: { id: string; name: string }) => {
            const sessionLevelPermission: InstructorSessionLevelPermission = {
              sessionId: session.id,
              sessionName: session.name,
              privilege: resp.privileges.sessionLevel[section.id]?.[session.id] || sectionLevelPermission.privilege,
            };
            sectionLevelPermission.sessionLevel.push(sessionLevelPermission);
          });
          permission.sectionLevel.push(sectionLevelPermission);
        });

        permission.sectionLevel = permission.sectionLevel.filter(
          (sectionLevelPermission: InstructorSectionLevelPermission) => {
            // discard section level permission that is consistent with the overall permission
            if (sectionLevelPermission.privilege.canViewStudent !== permission.privilege.canViewStudent) {
              return true;
            }
            if (
              sectionLevelPermission.privilege.canModifySessionComments !==
              permission.privilege.canModifySessionComments
            ) {
              return true;
            }
            if (sectionLevelPermission.privilege.canViewSession !== permission.privilege.canViewSession) {
              return true;
            }
            if (sectionLevelPermission.privilege.canSubmitSession !== permission.privilege.canSubmitSession) {
              return true;
            }

            return sectionLevelPermission.sessionLevel.some(
              (sessionLevelPermission: InstructorSessionLevelPermission) => {
                return (
                  sectionLevelPermission.privilege.canModifySessionComments !==
                    sessionLevelPermission.privilege.canModifySessionComments ||
                  sectionLevelPermission.privilege.canViewSession !== sessionLevelPermission.privilege.canViewSession ||
                  sectionLevelPermission.privilege.canSubmitSession !==
                    sessionLevelPermission.privilege.canSubmitSession
                );
              },
            );
          },
        );

        permission.sectionLevel.forEach((sectionLevel: InstructorSectionLevelPermission) => {
          if (
            sectionLevel.sessionLevel.every((sessionLevel: InstructorSessionLevelPermission) => {
              return (
                sectionLevel.privilege.canModifySessionComments === sessionLevel.privilege.canModifySessionComments &&
                sectionLevel.privilege.canViewSession === sessionLevel.privilege.canViewSession &&
                sectionLevel.privilege.canSubmitSession === sessionLevel.privilege.canSubmitSession
              );
            })
          ) {
            // session level is consistent with the section level, we can remove it.
            sectionLevel.sessionLevel = [];
          }
        });
        panel.originalPanel = structuredClone(panel.editPanel);
      });
  }

  /**
   * Converts the edit panel permission model into API request privileges.
   */
  private toInstructorPrivileges(permission: InstructorOverallPermission): InstructorPrivileges {
    const privileges: InstructorPrivileges = {
      courseLevel: permission.privilege,
      sectionLevel: {},
      sessionLevel: {},
    };
    permission.sectionLevel.forEach((sectionLevel: InstructorSectionLevelPermission) => {
      sectionLevel.sections.forEach((section: { id: string; name: string }) => {
        privileges.sectionLevel[section.id] = sectionLevel.privilege;
        privileges.sessionLevel[section.id] = {};

        sectionLevel.sessionLevel.forEach((sessionLevel: InstructorSessionLevelPermission) => {
          privileges.sessionLevel[section.id][sessionLevel.sessionId] = sessionLevel.privilege;
        });
      });
    });

    return privileges;
  }

  /**
   * Copies instructors from existing courses.
   */
  copyInstructors(): void {
    this.isCopyingInstructor = true;
    const courseTabModels: CourseTabModel[] = [];

    this.courseService.getAllCoursesAsInstructor('active').subscribe({
      next: (activeCourses) => {
        activeCourses.courses.forEach((course) => {
          if (course.courseId !== this.courseId && course.institute === this.courseFormModel.course.institute) {
            const model: CourseTabModel = this.getDefaultCourseTab({
              courseId: course.courseId,
              courseName: course.courseName,
              creationTimestamp: course.creationTimestamp,
            });
            courseTabModels.push(model);
          }
        });
      },
      error: (err: ErrorMessageOutput) => {
        this.isCopyingInstructor = false;
        this.statusMessageService.showErrorToast(err.error.message);
      },
      complete: () => {
        const modalRef: NgbModalRef = this.ngbModal.open(CopyInstructorsFromOtherCoursesModalComponent);
        modalRef.componentInstance.courses = courseTabModels;

        modalRef.dismissed.subscribe(() => {
          this.isCopyingInstructor = false;
        });

        modalRef.componentInstance.copyClickedEvent.subscribe((instructors: Instructor[]) => {
          this.verifyInstructorsToCopy(instructors).subscribe((hasCheckPassed: boolean) => {
            if (!hasCheckPassed) {
              modalRef.componentInstance.isCopyingSelectedInstructors = false;
              return;
            }

            this.addNewInstructors(instructors, modalRef);
          });
        });
      },
    });
  }

  /**
   * Adds new instructors.
   */
  addNewInstructors(instructors: Instructor[], modalRef: NgbModalRef): void {
    of(...instructors)
      .pipe(
        concatMap((instructor: Instructor) => {
          return this.instructorService.createInstructor(
            { courseId: this.courseId },
            {
              name: instructor.name,
              email: instructor.email,
              role: instructor.role!,
              displayName: instructor.displayedToStudentsAs,
              isDisplayedToStudent: instructor.isDisplayedToStudents!,
            },
          );
        }),
        // always close the modal after it enters the last step no matter adding succeeds or fails
        finalize(() => {
          this.isCopyingInstructor = false;
          modalRef.componentInstance.isCopyingSelectedInstructors = false;
          modalRef.close();
        }),
      )
      .subscribe({
        next: (newInstructor: Instructor) => {
          const newDetailPanels: InstructorEditPanelDetail = {
            originalInstructor: { ...newInstructor },
            originalPanel: this.getInstructorEditPanelModel(newInstructor),
            editPanel: this.getInstructorEditPanelModel(newInstructor),
          };
          newDetailPanels.editPanel.permission = this.newInstructorPanel.permission;
          newDetailPanels.originalPanel = structuredClone(newDetailPanels.editPanel);

          this.instructorDetailPanels.push(newDetailPanels);
        },
        error: (err: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(err.error.message);
        },
        complete: () => {
          this.statusMessageService.showSuccessToast(
            'The selected instructor(s) have been added successfully. ' +
              "An email containing how to 'join' this course will be sent to them in a few minutes.",
          );
        },
      });
  }

  /**
   * Verifies that no two selected instructors have the same email addresses and any selected instructor's
   * email addresses already exists in the course. Shows an error toast and returns false if the verification fails.
   */
  verifyInstructorsToCopy(instructors: Instructor[]): Observable<boolean> {
    return forkJoin([this.instructorService.loadInstructors({ courseId: this.courseId })]).pipe(
      map((values: [Instructors]) => {
        const allInstructorsAfterCopy: Instructor[] = instructors.concat(values[0].instructors);
        const emailSet: Set<string> = new Set();
        for (const instructor of allInstructorsAfterCopy) {
          if (emailSet.has(instructor.email)) {
            this.statusMessageService.showErrorToast(
              `An instructor with email address ${instructor.email} already ` +
                'exists in the course and/or you have selected more than one instructor with this email address.',
            );
            return false;
          }
          emailSet.add(instructor.email);
        }
        return true;
      }),
    );
  }
}
