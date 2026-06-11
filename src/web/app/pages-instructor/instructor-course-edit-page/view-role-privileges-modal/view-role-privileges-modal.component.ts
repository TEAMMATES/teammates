import { Component, Input, inject } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { InstructorPermissionRole, InstructorPermissionSet } from '../../../../types/api-output';
import { InstructorRoleNamePipe } from '../../../components/teammates-common/instructor-role-name.pipe';

/**
 * View privileges of a role modal.
 */
@Component({
  selector: 'tm-view-role-privileges-model',
  templateUrl: './view-role-privileges-modal.component.html',
  imports: [InstructorRoleNamePipe],
})
export class ViewRolePrivilegesModalComponent {
  activeModal = inject(NgbActiveModal);

  @Input()
  role: InstructorPermissionRole = InstructorPermissionRole.CUSTOM;

  @Input()
  instructorPrivilege: InstructorPermissionSet = {
    canModifyCourse: false,
    canModifySession: false,
    canModifyStudent: false,
    canModifyInstructor: false,
    canViewStudent: false,
    canModifySessionComments: false,
    canViewSession: false,
    canSubmitSession: false,
  };
}
