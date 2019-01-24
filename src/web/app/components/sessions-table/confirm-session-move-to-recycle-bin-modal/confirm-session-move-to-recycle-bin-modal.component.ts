import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Confirm session move to recycle bin modal.
 */
@Component({
  selector: 'tm-confirm-session-move-to-recycle-bin-modal',
  templateUrl: './confirm-session-move-to-recycle-bin-modal.component.html',
  styleUrls: ['./confirm-session-move-to-recycle-bin-modal.component.scss'],
})
export class ConfirmSessionMoveToRecycleBinModalComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {
  }

}
