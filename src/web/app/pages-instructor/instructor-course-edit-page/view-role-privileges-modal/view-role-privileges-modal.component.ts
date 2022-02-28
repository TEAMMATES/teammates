import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { InstructorPermissionRole, InstructorPermissionSet } from '../../../../types/api-output';

/**
 * View privileges of a role modal.
 */
@Component({
  selector: 'tm-view-role-privileges-model',
  templateUrl: './view-role-privileges-modal.component.html',
  styleUrls: ['./view-role-privileges-modal.component.scss'],
})
export class ViewRolePrivilegesModalComponent {

  @Input()
  role: InstructorPermissionRole = InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM;

  @Input()
  instructorPrivilege: InstructorPermissionSet = {
    canModifyCourse: false,
    canModifySession: false,
    canModifyStudent: false,
    canModifyInstructor: false,
    canViewStudentInSections: false,
    canModifySessionCommentsInSections: false,
    canViewSessionInSections: false,
    canSubmitSessionInSections: false,
  };

  constructor(public activeModal: NgbActiveModal) { }

}
