import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Modal to contain status messages.
 */
@Component({
  selector: 'tm-status-message-modal',
  templateUrl: './status-message-modal.component.html',
  styleUrls: ['./status-message-modal.component.scss'],
})
export class StatusMessageModalComponent implements OnInit {

  @Input() title: string = '';
  @Input() subtitle: string = '';
  @Input() message: string = '';
  @Input() color: string = 'info';

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
