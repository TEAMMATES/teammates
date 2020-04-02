import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { InstructorPrivilege } from '../../../../types/api-output';

/**
 * View privileges of a role modal.
 */
@Component({
  selector: 'tm-view-role-privileges-model',
  templateUrl: './view-role-privileges-modal.component.html',
  styleUrls: ['./view-role-privileges-modal.component.scss'],
})
export class ViewRolePrivilegesModalComponent implements OnInit {

  @Input()
  instructorPrivilege: InstructorPrivilege = {
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

  ngOnInit(): void {
  }

}
