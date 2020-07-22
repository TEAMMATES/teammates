import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { InstructorPermissionRole, InstructorPrivilege, JoinState } from '../../../../types/api-output';
import {
  InstructorOverallPermission,
} from '../custom-privilege-setting-panel/custom-privilege-setting-panel.component';

/**
 * Model for edit instructor panel.
 */
export interface InstructorEditPanel {
  googleId?: string;
  courseId: string;
  email: string;
  isDisplayedToStudents: boolean;
  displayedToStudentsAs: string;
  name: string;
  role: InstructorPermissionRole;
  joinState: JoinState;

  permission: InstructorOverallPermission;

  isEditing: boolean;
  isSavingInstructorEdit: boolean;
}

/**
 * Mode of the editing panel.
 */
export enum EditMode {
  /**
   * Editing existing instructor mode.
   */
  EDIT,

  /**
   * Adding new instructor mode.
   */
  ADD,
}

/**
 * Edit instructor panel.
 */
@Component({
  selector: 'tm-instructor-edit-panel',
  templateUrl: './instructor-edit-panel.component.html',
  styleUrls: ['./instructor-edit-panel.component.scss'],
})
export class InstructorEditPanelComponent implements OnInit {

  // enum
  JoinState: typeof JoinState = JoinState;
  InstructorPermissionRole: typeof InstructorPermissionRole = InstructorPermissionRole;
  EditMode: typeof EditMode = EditMode;

  @Input()
  editMode: EditMode = EditMode.EDIT;

  @Input()
  instructorIndex: number = 0;

  @Input()
  instructor: InstructorEditPanel = {
    googleId: '',
    courseId: '',
    email: '',
    isDisplayedToStudents: true,
    displayedToStudentsAs: '',
    name: '',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
    joinState: JoinState.JOINED,

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
  @Output()
  instructorChange: EventEmitter<InstructorEditPanel> = new EventEmitter();

  @Input()
  currInstructorCoursePrivilege: InstructorPrivilege = {
    canModifyCourse: true,
    canModifySession: true,
    canModifyStudent: true,
    canModifyInstructor: true,
    canViewStudentInSections: true,
    canModifySessionCommentsInSections: true,
    canViewSessionInSections: true,
    canSubmitSessionInSections: true,
  };

  @Input()
  allSections: string[] = [];

  @Input()
  allSessions: string[] = [];

  @Input()
  isSavingNewInstructor: boolean = false;

  @Output()
  sendRemindJoinEmail: EventEmitter<void> = new EventEmitter();
  @Output()
  cancelEditing: EventEmitter<void> = new EventEmitter();
  @Output()
  deleteInstructor: EventEmitter<void> = new EventEmitter();
  @Output()
  saveInstructor: EventEmitter<void> = new EventEmitter();
  @Output()
  viewRolePrivilegeModel: EventEmitter<InstructorPermissionRole> = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Triggers the change of the model for the form.
   */
  triggerModelChange(field: string, data: any): void {
    this.instructorChange.emit({
      ...this.instructor,
      [field]: data,
    });
  }

}
