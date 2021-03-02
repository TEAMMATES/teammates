import {Component, OnInit} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { COURSE_ID_MAX_LENGTH } from '../../../types/field-validator';

/**
 * Copy current course modal.
 */
@Component({
  selector: 'tm-copy-course-modal',
  templateUrl: './copy-course-modal.component.html',
  styleUrls: ['./copy-course-modal.component.scss']
})
export class CopyCourseModalComponent implements OnInit {

  // const
  public COURSE_ID_MAX_LENGTH: number = COURSE_ID_MAX_LENGTH;

  public newCourseId : string = '';

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

  /**
   * Fires the copy event.
   */
  copy(): void {
    this.activeModal.close({
      newCourseId: this.newCourseId,
    });
  }

}
