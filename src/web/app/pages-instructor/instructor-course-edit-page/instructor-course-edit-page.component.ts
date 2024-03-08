import { Component, EventEmitter, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { forkJoin, Observable, of } from 'rxjs';
import { concatMap, finalize, map } from 'rxjs/operators';
import {
  CourseTabModel,
} from './copy-instructors-from-other-courses-modal/copy-instructors-from-other-courses-modal-model';
import {
  CopyInstructorsFromOtherCoursesModalComponent,
} from './copy-instructors-from-other-courses-modal/copy-instructors-from-other-courses-modal.component';
import {
  InstructorOverallPermission,
  InstructorSectionLevelPermission,
  InstructorSessionLevelPermission,
} from './custom-privilege-setting-panel/custom-privilege-setting-panel.component';
import { EditMode, InstructorEditPanel } from './instructor-edit-panel/instructor-edit-panel.component';
import { ViewRolePrivilegesModalComponent } from './view-role-privileges-modal/view-role-privileges-modal.component';
import { AuthService } from '../../../services/auth.service';
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
  Courses,
  FeedbackSession,
  FeedbackSessions,
  Instructor,
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
import { InstructorCreateRequest, Intent } from '../../../types/api-request';
import {
  DEFAULT_INSTRUCTOR_PRIVILEGE,
  DEFAULT_PRIVILEGE_COOWNER,
  DEFAULT_PRIVILEGE_MANAGER,
  DEFAULT_PRIVILEGE_OBSERVER,
  DEFAULT_PRIVILEGE_TUTOR,
} from '../../../types/default-instructor-privilege';
import { FormValidator } from '../../../types/form-validator';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import {
  CourseEditFormMode,
  CourseEditFormModel,
  DEFAULT_COURSE_EDIT_FORM_MODEL,
} from '../../components/course-edit-form/course-edit-form-model';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';
import {
  CoursesSectionQuestions,
} from '../../pages-help/instructor-help-page/instructor-help-courses-section/courses-section-questions';
import { Sections } from '../../pages-help/instructor-help-page/sections';

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
  styleUrls: ['./instructor-course-edit-page.component.scss'],
  animations: [collapseAnim],
})
export class InstructorCourseEditPageComponent implements OnInit {

  // enum
  EditMode: typeof EditMode = EditMode;
  FormValidator: typeof FormValidator = FormValidator;
  CoursesSectionQuestions: typeof CoursesSectionQuestions = CoursesSectionQuestions;
  Sections: typeof Sections = Sections;
  CourseEditFormMode: typeof CourseEditFormMode = CourseEditFormMode;

  courseId: string = '';
  currInstructorGoogleId: string = '';
  currInstructorCoursePrivilege: InstructorPermissionSet = {
    canModifyCourse: true,
    canModifySession: true,
    canModifyStudent: true,
    canModifyInstructor: true,
    canViewStudentInSections: true,
    canModifySessionCommentsInSections: true,
    canViewSessionInSections: true,
    canSubmitSessionInSections: true,
  };

  instructorDetailPanels: InstructorEditPanelDetail[] = [];

  isAddingNewInstructor: boolean = false;
  isCopyingInstructor: boolean = false;
  newInstructorPanel: InstructorEditPanel = {
    googleId: '',
    courseId: '',
    email: '',
    isDisplayedToStudents: true,
    displayedToStudentsAs: 'Instructor',
    name: '',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
    joinState: JoinState.NOT_JOINED,

    permission: {
      privilege: {
        canModifyCourse: false,
        canModifySession: false,
        canModifyStudent: false,
        canModifyInstructor: false,
        canViewStudentInSections: false,
        canModifySessionCommentsInSections: false,
        canViewSessionInSections: false,
        canSubmitSessionInSections: false,
      },
      sectionLevel: [],
    },

    isEditing: true,
    isSavingInstructorEdit: false,
  };

  courseFormModel: CourseEditFormModel = DEFAULT_COURSE_EDIT_FORM_MODEL();
  resetCourseFormEvent: EventEmitter<void> = new EventEmitter();

  // for fine-grain permission setting
  allSections: string[] = [];
  allSessions: string[] = [];

  isCourseLoading: boolean = false;
  hasCourseLoadingFailed: boolean = false;
  isInstructorsLoading: boolean = false;
  hasInstructorsLoadingFailed: boolean = false;
  isSavingNewInstructor: boolean = false;

  constructor(private route: ActivatedRoute,
              private navigationService: NavigationService,
              private studentService: StudentService,
              private instructorService: InstructorService,
              private feedbackSessionsService: FeedbackSessionsService,
              private statusMessageService: StatusMessageService,
              private courseService: CourseService,
              private authService: AuthService,
              private ngbModal: NgbModal,
              private simpleModalService: SimpleModalService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;

      this.loadCourseInfo();
      this.loadCurrInstructorInfo();

      // load all section and session name
      forkJoin([
        this.studentService.getStudentsFromCourse({ courseId: this.courseId }),
        this.feedbackSessionsService.getFeedbackSessionsForInstructor(this.courseId),
      ]).subscribe((vals: any[]) => {
        const students: Students = vals[0] as Students;
        const sessions: FeedbackSessions = vals[1] as FeedbackSessions;

        this.allSections =
            Array.from(new Set(students.students.map((value: Student) => value.sectionName)));
        this.allSessions =
            sessions.feedbackSessions.map((session: FeedbackSession) => session.feedbackSessionName);

        this.loadCourseInstructors();
      });
    });
  }

  /**
   * Loads the course being edited.
   */
  loadCourseInfo(): void {
    this.hasCourseLoadingFailed = false;
    this.isCourseLoading = true;
    this.courseService.getCourseAsInstructor(this.courseId).pipe(finalize(() => {
      this.isCourseLoading = false;
    })).subscribe({
      next: (resp: Course) => {
        this.courseFormModel.course = resp;
        this.courseFormModel.originalCourse = { ...resp };
        this.currInstructorCoursePrivilege = resp.privileges || DEFAULT_INSTRUCTOR_PRIVILEGE();
        this.courseFormModel.canModifyCourse = this.currInstructorCoursePrivilege.canModifyCourse;
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
    this.authService.getAuthUser().subscribe({
      next: (res: AuthInfo) => {
        this.currInstructorGoogleId = res.user === undefined ? '' : res.user.id;
      },
      error: (resp: ErrorMessageOutput) => {
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
        this.navigationService.navigateWithSuccessMessage('/web/instructor/courses',
            `The course ${course.courseId} has been deleted. You can restore it from the Recycle Bin manually.`);
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
    this.courseService.updateCourse(this.courseId, {
      courseName: this.courseFormModel.course.courseName,
      timeZone: this.courseFormModel.course.timeZone,
    }).pipe(finalize(() => {
      this.courseFormModel.isSaving = false;
    })).subscribe({
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
    this.instructorService.loadInstructors({
      courseId: this.courseId,
      intent: Intent.FULL_DETAIL,
    })
        .subscribe({
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
    /**
     * The non-null assertion operator (!) is used below in `isDisplayedToStudents`,
     * `displayedToStudentsAs` and `role`. These attributes should never be undefined and are only
     * typed as such to accommodate for a use case in SearchService.
     */
    return {
      googleId: i.googleId,
      courseId: i.courseId,
      email: i.email,
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      isDisplayedToStudents: i.isDisplayedToStudents!,
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      displayedToStudentsAs: i.displayedToStudentsAs!,
      name: i.name,
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      role: i.role!,
      joinState: i.joinState,

      permission: {
        privilege: {
          canModifyCourse: false,
          canModifySession: false,
          canModifyStudent: false,
          canModifyInstructor: false,
          canViewStudentInSections: false,
          canModifySessionCommentsInSections: false,
          canViewSessionInSections: false,
          canSubmitSessionInSections: false,
        },
        sectionLevel: [],
      },

      isEditing: false,
      isSavingInstructorEdit: false,
    };
  }

  /**
   * Shows the model of details permission for a role.
   */
  viewRolePrivilegeModel(role: InstructorPermissionRole): void {
    const modalRef: NgbModalRef = this.ngbModal.open(ViewRolePrivilegesModalComponent);
    modalRef.result.then(() => {}, () => {});
    let privilege: InstructorPermissionSet;
    switch (role) {
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER:
        privilege = DEFAULT_PRIVILEGE_COOWNER();
        break;
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_MANAGER:
        privilege = DEFAULT_PRIVILEGE_MANAGER();
        break;
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_OBSERVER:
        privilege = DEFAULT_PRIVILEGE_OBSERVER();
        break;
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_TUTOR:
        privilege = DEFAULT_PRIVILEGE_TUTOR();
        break;
      case InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM:
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
    panelDetail.editPanel = JSON.parse(JSON.stringify(panelDetail.originalPanel));
    panelDetail.editPanel.isSavingInstructorEdit = false;
    panelDetail.editPanel.isEditing = false;
  }

  /**
   * Saves instructor at index.
   */
  saveInstructor(index: number): void {
    const panelDetail: InstructorEditPanelDetail = this.instructorDetailPanels[index];
    panelDetail.editPanel.isSavingInstructorEdit = true;
    const reqBody: InstructorCreateRequest = {
      id: panelDetail.originalInstructor.joinState === JoinState.JOINED
          ? panelDetail.originalInstructor.googleId : undefined,
      name: panelDetail.editPanel.name,
      email: panelDetail.originalInstructor.joinState === JoinState.JOINED
          ? panelDetail.editPanel.email : panelDetail.originalInstructor.email,
      role: panelDetail.editPanel.role,
      displayName: panelDetail.editPanel.displayedToStudentsAs,
      isDisplayedToStudent: panelDetail.editPanel.isDisplayedToStudents,
    } as InstructorCreateRequest;

    this.instructorService.updateInstructor({
      courseId: panelDetail.originalInstructor.courseId,
      requestBody: reqBody,
    }).pipe(finalize(() => {
      panelDetail.editPanel.isSavingInstructorEdit = false;
    })).subscribe({
      next: (resp: Instructor) => {
        panelDetail.editPanel.isEditing = false;
        panelDetail.originalInstructor = { ...resp };
        const permission: InstructorOverallPermission = panelDetail.editPanel.permission;

        panelDetail.editPanel = this.getInstructorEditPanelModel(resp);
        panelDetail.editPanel.permission = permission;

        this.updatePrivilegeForInstructor(panelDetail.originalInstructor, panelDetail.editPanel.permission);

        this.statusMessageService.showSuccessToast(`The instructor ${resp.name} has been updated.`);

      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });

    panelDetail.originalPanel = JSON.parse(JSON.stringify(panelDetail.editPanel));
  }

  /**
   * Deletes instructor at index.
   */
  deleteInstructor(index: number): void {
    const panelDetail: InstructorEditPanelDetail = this.instructorDetailPanels[index];
    const isDeletingSelf: boolean = panelDetail.originalInstructor.googleId === this.currInstructorGoogleId;
    const modalContent: string = isDeletingSelf
        ? `Are you sure you want to delete your instructor role
        from the course <strong>${panelDetail.originalInstructor.courseId}</strong>?
        You will not be able to access the course anymore.`
        : `Are you sure you want to delete the instructor <strong>${panelDetail.originalInstructor.name}</strong>
        from the course <strong>${panelDetail.originalInstructor.courseId}</strong>?
        He/she will not be able to access the course anymore.`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        `Delete instructor <strong>${panelDetail.originalInstructor.name}</strong>?`,
        SimpleModalType.DANGER, modalContent);

    modalRef.result.then(() => {
      this.instructorService.deleteInstructor({
        courseId: panelDetail.originalInstructor.courseId,
        instructorEmail: panelDetail.originalInstructor.email,
      }).subscribe({
        next: () => {
          if (panelDetail.originalInstructor.googleId === this.currInstructorGoogleId) {
            this.navigationService.navigateWithSuccessMessage(
                '/web/instructor/courses', 'Instructor is successfully deleted.');
          } else {
            this.instructorDetailPanels.splice(index, 1);
            this.statusMessageService.showSuccessToast('Instructor is successfully deleted.');
          }
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
    }, () => {});
  }

  /**
   * Re-sends an invitation email to an instructor in the course.
   */
  resendReminderEmail(index: number): void {
    const panelDetail: InstructorEditPanelDetail = this.instructorDetailPanels[index];
    const modalContent: string = `Do you wish to re-send the invitation email to instructor
      ${panelDetail.originalInstructor.name} from course ${panelDetail.originalInstructor.courseId}?`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        'Re-send invitation email?', SimpleModalType.INFO, modalContent);

    modalRef.result.then(() => {
      this.courseService
          .remindInstructorForJoin(panelDetail.originalInstructor.courseId, panelDetail.originalInstructor.email)
          .subscribe({
            next: (resp: MessageOutput) => {
              this.statusMessageService.showSuccessToast(resp.message);
            },
            error: (resp: ErrorMessageOutput) => {
              this.statusMessageService.showErrorToast(resp.error.message);
            },
          });
    }, () => {});
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
    } as InstructorCreateRequest;

    this.instructorService.createInstructor({ courseId: this.courseId, requestBody: reqBody })
        .pipe(finalize(() => {
          this.isSavingNewInstructor = false;
        }))
        .subscribe({
          next: (resp: Instructor) => {
            const newDetailPanels: InstructorEditPanelDetail = {
              originalInstructor: { ...resp },
              originalPanel: this.getInstructorEditPanelModel(resp),
              editPanel: this.getInstructorEditPanelModel(resp),
            };
            newDetailPanels.editPanel.permission = this.newInstructorPanel.permission;
            newDetailPanels.originalPanel = JSON.parse(JSON.stringify(newDetailPanels.editPanel));

            this.instructorDetailPanels.push(newDetailPanels);
            this.statusMessageService.showSuccessToast(`The instructor ${resp.name} has been added successfully. `
            + `An email containing how to 'join' this course will be sent to ${resp.email} in a few minutes.`);

            this.updatePrivilegeForInstructor(newDetailPanels.originalInstructor, newDetailPanels.editPanel.permission);

            this.isAddingNewInstructor = false;
            this.newInstructorPanel = {
              googleId: '',
              courseId: '',
              email: '',
              isDisplayedToStudents: true,
              displayedToStudentsAs: '',
              name: '',
              role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
              joinState: JoinState.NOT_JOINED,

              permission: {
                privilege: {
                  canModifyCourse: true,
                  canModifySession: true,
                  canModifyStudent: true,
                  canModifyInstructor: true,
                  canViewStudentInSections: true,
                  canModifySessionCommentsInSections: true,
                  canViewSessionInSections: true,
                  canSubmitSessionInSections: true,
                },
                sectionLevel: [],
              },

              isEditing: true,
              isSavingInstructorEdit: false,
            };
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

    if (instructor.role !== InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM) {
      this.isInstructorsLoading = false;
      return;
    }

    this.instructorService.loadInstructorPrivilege({
      courseId: instructor.courseId,
      instructorEmail: instructor.email,
    }).subscribe((resp: InstructorPrivilege) => {
      permission.privilege = resp.privileges.courseLevel;

      this.allSections.forEach((sectionName: string) => {
        const sectionLevelPermission: InstructorSectionLevelPermission = {
          sectionNames: [sectionName],
          privilege: resp.privileges.sectionLevel[sectionName] || permission.privilege,
          sessionLevel: [],
        };

        this.allSessions.forEach((sessionName: string) => {
          const sessionLevelPermission: InstructorSessionLevelPermission = {
            sessionName,
            privilege: (resp.privileges.sessionLevel[sectionName] || {})[sessionName]
                || sectionLevelPermission.privilege,
          };
          sectionLevelPermission.sessionLevel.push(sessionLevelPermission);
        });
        permission.sectionLevel.push(sectionLevelPermission);
      });

      permission.sectionLevel = permission.sectionLevel
          .filter((sectionLevelPermission: InstructorSectionLevelPermission) => {
            // discard section level permission that is consistent with the overall permission
            if (sectionLevelPermission.privilege.canViewStudentInSections
                !== permission.privilege.canViewStudentInSections) {
              return true;
            }
            if (sectionLevelPermission.privilege.canModifySessionCommentsInSections
                !== permission.privilege.canModifySessionCommentsInSections) {
              return true;
            }
            if (sectionLevelPermission.privilege.canViewSessionInSections
                !== permission.privilege.canViewSessionInSections) {
              return true;
            }
            if (sectionLevelPermission.privilege.canSubmitSessionInSections
                !== permission.privilege.canSubmitSessionInSections) {
              return true;
            }

            return sectionLevelPermission.sessionLevel
                .some((sessionLevelPermission: InstructorSessionLevelPermission) => {
                  return sectionLevelPermission.privilege.canModifySessionCommentsInSections
                          !== sessionLevelPermission.privilege.canModifySessionCommentsInSections
                      || sectionLevelPermission.privilege.canViewSessionInSections
                          !== sessionLevelPermission.privilege.canViewSessionInSections
                      || sectionLevelPermission.privilege.canSubmitSessionInSections
                          !== sessionLevelPermission.privilege.canSubmitSessionInSections;
                });
          });

      permission.sectionLevel.forEach((sectionLevel: InstructorSectionLevelPermission) => {
        if (sectionLevel.sessionLevel.every((sessionLevel: InstructorSessionLevelPermission) => {
          return sectionLevel.privilege.canModifySessionCommentsInSections
                  === sessionLevel.privilege.canModifySessionCommentsInSections
              && sectionLevel.privilege.canViewSessionInSections
                  === sessionLevel.privilege.canViewSessionInSections
              && sectionLevel.privilege.canSubmitSessionInSections
                  === sessionLevel.privilege.canSubmitSessionInSections;
        })) {
          // session level is consistent with the section level, we can remove it.
          sectionLevel.sessionLevel = [];
        }
      });
      panel.originalPanel = JSON.parse(JSON.stringify(panel.editPanel));
    });
  }

  /**
   * Updates privilege for instructor
   */
  updatePrivilegeForInstructor(instructor: Instructor, permission: InstructorOverallPermission): void {
    if (instructor.role !== InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM) {
      return;
    }

    const privileges: InstructorPrivileges = {
      courseLevel: permission.privilege,
      sectionLevel: {},
      sessionLevel: {},
    };
    permission.sectionLevel.forEach((sectionLevel: InstructorSectionLevelPermission) => {
      sectionLevel.sectionNames.forEach((sectionName: string) => {
        privileges.sectionLevel[sectionName] = sectionLevel.privilege;
        privileges.sessionLevel[sectionName] = {};

        sectionLevel.sessionLevel.forEach((sessionLevel: InstructorSessionLevelPermission) => {
          privileges.sessionLevel[sectionName][sessionLevel.sessionName] = sessionLevel.privilege;
        });
      });
    });

    this.instructorService.updateInstructorPrivilege({
      courseId: instructor.courseId,
      instructorEmail: instructor.email,
      requestBody: { privileges },
    }).subscribe({
      next: () => {
        // privileges updated
        // filter out empty permission setting
        permission.sectionLevel = permission.sectionLevel.filter(
            (sectionLevel: InstructorSectionLevelPermission) => sectionLevel.sectionNames.length !== 0);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Copies instructors from existing courses.
   */
  copyInstructors(): void {
    this.isCopyingInstructor = true;
    const courseTabModels: CourseTabModel[] = [];

    forkJoin([
      this.courseService.getAllCoursesAsInstructor('active'),
      this.courseService.getAllCoursesAsInstructor('archived'),
    ]).subscribe({
      next: (values: Courses[]) => {
        const activeCourses: Courses = values[0];
        const archivedCourses: Courses = values[1];

        activeCourses.courses.forEach((course: Course) => {
          if (course.courseId !== this.courseId && course.institute === this.courseFormModel.course.institute) {
            const model: CourseTabModel = {
              courseId: course.courseId,
              courseName: course.courseName,
              creationTimestamp: course.creationTimestamp,
              isArchived: false,
              instructorCandidates: [],
              instructorCandidatesSortBy: SortBy.NONE,
              instructorCandidatesSortOrder: SortOrder.ASC,
              hasInstructorsLoaded: false,
              isTabExpanded: false,
              hasLoadingFailed: false,
            };
            courseTabModels.push(model);
          }
        });
        archivedCourses.courses.forEach((course: Course) => {
          if (course.courseId !== this.courseId && course.institute === this.courseFormModel.course.institute) {
            const model: CourseTabModel = {
              courseId: course.courseId,
              courseName: course.courseName,
              creationTimestamp: course.creationTimestamp,
              isArchived: true,
              instructorCandidates: [],
              instructorCandidatesSortBy: SortBy.NONE,
              instructorCandidatesSortOrder: SortOrder.ASC,
              hasInstructorsLoaded: false,
              isTabExpanded: false,
              hasLoadingFailed: false,
            };
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
    of(...instructors).pipe(
      concatMap((instructor: Instructor) => {
        return this.instructorService.createInstructor({
          courseId: this.courseId,
          requestBody: {
            name: instructor.name,
            email: instructor.email,
            role: instructor.role!,
            displayName: instructor.displayedToStudentsAs,
            isDisplayedToStudent: instructor.isDisplayedToStudents!,
          },
        });
      }),
      // always close the modal after it enters the last step no matter adding succeeds or fails
      finalize(() => {
        this.isCopyingInstructor = false;
        modalRef.componentInstance.isCopyingSelectedInstructors = false;
        modalRef.close();
      }),
    ).subscribe({
      next: (newInstructor: Instructor) => {
        const newDetailPanels: InstructorEditPanelDetail = {
          originalInstructor: { ...newInstructor },
          originalPanel: this.getInstructorEditPanelModel(newInstructor),
          editPanel: this.getInstructorEditPanelModel(newInstructor),
        };
        newDetailPanels.editPanel.permission = this.newInstructorPanel.permission;
        newDetailPanels.originalPanel = JSON.parse(JSON.stringify(newDetailPanels.editPanel));

        this.instructorDetailPanels.push(newDetailPanels);
      },
      error: (err: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(err.error.message);
      },
      complete: () => {
        this.statusMessageService.showSuccessToast('The selected instructor(s) have been added successfully. '
        + 'An email containing how to \'join\' this course will be sent to them in a few minutes.');
      },
    });
  }

  /**
   * Verifies that no two selected instructors have the same email addresses and any selected instructor's
   * email addresses already exists in the course. Shows an error toast and returns false if the verification fails.
   */
  verifyInstructorsToCopy(instructors: Instructor[]): Observable<boolean> {
    return forkJoin([
      this.instructorService.loadInstructors({
        courseId: this.courseId,
        intent: Intent.FULL_DETAIL,
      }),
    ]).pipe(
      map((values: [Instructors]) => {
        const allInstructorsAfterCopy: Instructor[] = instructors.concat(values[0].instructors);
        const emailSet: Set<string> = new Set();
        for (const instructor of allInstructorsAfterCopy) {
          if (emailSet.has(instructor.email)) {
            this.statusMessageService.showErrorToast(`An instructor with email address ${instructor.email} already `
            + 'exists in the course and/or you have selected more than one instructor with this email address.');
            return false;
          }
          emailSet.add(instructor.email);
        }
        return true;
      }),
    );
  }

}
