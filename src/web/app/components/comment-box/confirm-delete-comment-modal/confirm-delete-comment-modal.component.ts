import { Component, OnInit } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * Confirm delete modal.
 */
@Component({
  selector: 'tm-confirm-delete-comment-modal',
  templateUrl: './confirm-delete-comment-modal.component.html',
  styleUrls: ['./confirm-delete-comment-modal.component.scss'],
})
export class ConfirmDeleteCommentModalComponent implements OnInit {

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
  }

}
