import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Regenerate student's course links modal.
 */
@Component({
  selector: 'tm-regenerate-links-confirm-modal',
  templateUrl: './regenerate-links-confirm-modal.component.html',
  styleUrls: ['./regenerate-links-confirm-modal.component.scss'],
})
export class RegenerateLinksConfirmModalComponent implements OnInit {

  @Input()
  studentName: string = '';

  @Input()
  regenerateLinksCourseId: string = '';

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {}

}
