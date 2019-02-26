import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to confirm permanent deletion of one course or all courses in the Recycle Bin.
 */
@Component({
  selector: 'tm-course-permanent-deletion-confirm-modal',
  templateUrl: './course-permanent-deletion-confirm-modal.component.html',
  styleUrls: ['./course-permanent-deletion-confirm-modal.component.scss'],
})
export class CoursePermanentDeletionConfirmModalComponent implements OnInit {

  @Input()
  courseId: string = '';

  @Input()
  isDeleteAll: boolean = false;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
