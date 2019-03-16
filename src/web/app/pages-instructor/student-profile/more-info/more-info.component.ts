import { Component, Input, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

/**
 * More info box and dialog
 */
@Component({
  selector: 'tm-more-info',
  templateUrl: './more-info.component.html',
  styleUrls: ['./more-info.component.scss'],
})
export class MoreInfoComponent implements OnInit {

  @Input() studentName: string = '';
  @Input() moreInfoText: string = '';

  constructor(private ngbModal: NgbModal) { }

  ngOnInit(): void {
  }

  /**
   * Open the more info modal.
   */
  openModal(content: any): void {
    this.ngbModal.open(content);
  }

}
