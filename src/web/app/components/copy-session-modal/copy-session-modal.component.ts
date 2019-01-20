import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Course } from '../../course';

/**
 * Copy current session modal.
 */
@Component({
  selector: 'tm-copy-session-modal',
  templateUrl: './copy-session-modal.component.html',
  styleUrls: ['./copy-session-modal.component.scss'],
})
export class CopySessionModalComponent implements OnInit {

  @Input()
  courseCandidates: Course[] = [];

  @Input()
  sessionToCopyCourseId: string = '';

  newFeedbackSessionName: string = '';
  copyToCourseId: string = '';

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
  }

  /**
   * Fires the copy event.
   */
  copy(): void {
    this.activeModal.close({
      newFeedbackSessionName: this.newFeedbackSessionName,
      copyToCourseId: this.copyToCourseId,
    });
  }

}
