import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { ModalTypes } from './modal-types';

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
  @Input() type: ModalTypes = ModalTypes.NEUTRAL;

  // enum
  ModalTypes: typeof  ModalTypes = ModalTypes;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
