import { Component, Input, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Reset Google ID confirmation modal.
 */
@Component({
  selector: 'tm-reset-google-id-confirm-modal',
  templateUrl: './reset-google-id-confirm-modal.component.html',
  styleUrls: ['./reset-google-id-confirm-modal.component.scss'],
})
export class ResetGoogleIdConfirmModalComponent implements OnInit {

  @Input()
  name: string = '';

  @Input()
  course: string = '';

  constructor(public activeModal: NgbActiveModal) {}

  ngOnInit(): void {}

}
