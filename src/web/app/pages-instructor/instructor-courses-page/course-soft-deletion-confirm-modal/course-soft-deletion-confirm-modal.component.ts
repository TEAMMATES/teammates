import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to confirm soft deletion of a course.
 */
@Component({
  selector: 'tm-course-soft-deletion-confirm-modal',
  templateUrl: './course-soft-deletion-confirm-modal.component.html',
  styleUrls: ['./course-soft-deletion-confirm-modal.component.scss'],
})
export class CourseSoftDeletionConfirmModalComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
