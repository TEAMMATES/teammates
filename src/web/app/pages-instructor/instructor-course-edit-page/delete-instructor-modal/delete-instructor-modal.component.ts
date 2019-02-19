import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to delete an instructor from the current course.
 */
@Component({
  selector: 'tm-delete-instructor-modal',
  templateUrl: './delete-instructor-modal.component.html',
  styleUrls: ['./delete-instructor-modal.component.scss'],
})
export class DeleteInstructorModalComponent implements OnInit {

  @Input()
  courseId: string = '';

  @Input()
  idToDelete: string = '';

  @Input()
  nameToDelete: string = '';

  @Input()
  currentId: string = '';

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
