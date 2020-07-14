import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Instructor, InstructorPermissionRole, JoinState } from '../../../../types/api-output';

/**
 * Delete instructor confirm modal.
 */
@Component({
  selector: 'tm-delete-instructor-confirm-model',
  templateUrl: './delete-instructor-confirm-modal.component.html',
  styleUrls: ['./delete-instructor-confirm-modal.component.scss'],
})
export class DeleteInstructorConfirmModalComponent implements OnInit {

  @Input()
  instructorToDelete: Instructor = {
    googleId: '',
    courseId: '',
    email: '',
    isDisplayedToStudents: true,
    displayedToStudentsAs: '',
    name: '',
    role: InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
    joinState: JoinState.JOINED,
  };

  @Input()
  isDeletingSelf: boolean = false;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
