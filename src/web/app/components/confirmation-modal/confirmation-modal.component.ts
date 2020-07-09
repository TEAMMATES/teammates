import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmationModalType } from './confirmation-modal-type';

/**
 * A component to display contents of confirmation modals.
 */
@Component({
  selector: 'tm-confirmation-modal',
  templateUrl: './confirmation-modal.component.html',
  styleUrls: ['./confirmation-modal.component.scss'],
})
export class ConfirmationModalComponent implements OnInit {
  @Input() header: string = '';
  @Input() content: any = '';
  @Input() type: ConfirmationModalType = ConfirmationModalType.NEUTRAL;
  @Input() isNotificationOnly?: boolean = false;

  // enum
  ConfirmationModalType: typeof  ConfirmationModalType = ConfirmationModalType;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
