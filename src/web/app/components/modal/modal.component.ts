import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalType } from './modal-type';

/**
 * A component to display contents of confirmation modals.
 */
@Component({
  selector: 'tm-modal',
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss'],
})
export class ModalComponent implements OnInit {
  @Input() header: string = '';
  @Input() content: any = '';
  @Input() type: ModalType = ModalType.NEUTRAL;

  // enum
  ModalType: typeof  ModalType = ModalType;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
