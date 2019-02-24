import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to confirm permanent deletion of all courses.
 */
@Component({
  selector: 'tm-course-permanent-deletion-confirm-modal',
  templateUrl: './courses-permanent-deletion-confirm-modal.component.html',
  styleUrls: ['./courses-permanent-deletion-confirm-modal.component.scss'],
})
export class CoursesPermanentDeletionConfirmModalComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
