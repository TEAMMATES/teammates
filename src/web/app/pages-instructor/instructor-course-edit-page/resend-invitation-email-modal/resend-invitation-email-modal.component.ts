import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Instructor, InstructorPermissionRole, JoinState } from '../../../../types/api-output';

/**
 * Resend invitation email modal.
 */
@Component({
  selector: 'tm-resend-invitation-email-modal',
  templateUrl: './resend-invitation-email-modal.component.html',
  styleUrls: ['./resend-invitation-email-modal.component.scss'],
})
export class ResendInvitationEmailModalComponent implements OnInit {

  @Input()
  instructorToResend: Instructor = {
    googleId: '',
    courseId: '',
    email: '',
    isDisplayedToStudents: true,
    displayedToStudentsAs: '',
    name: '',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
    joinState: JoinState.JOINED,
  };

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
