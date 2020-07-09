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

  // enum
  ConfirmationModalType: typeof  ConfirmationModalType = ConfirmationModalType;

  @Input() header: string = '';
  @Input() content: any = '';
  @Input() type: ConfirmationModalType = ConfirmationModalType.NEUTRAL;
  @Input() isNotificationOnly?: boolean =  false; // true will cause modal to only have 1 button
  @Input() confirmMessage?: string = 'Yes'; // custom text message for confirm button
  @Input() cancelMessage?: string = 'No, cancel the operation'; // custom text message for cancel button

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
