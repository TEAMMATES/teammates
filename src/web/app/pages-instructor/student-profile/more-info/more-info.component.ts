import { Component, Input, OnInit } from '@angular/core';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';

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

  constructor(private simpleModalService: SimpleModalService) { }

  ngOnInit(): void {
  }

  /**
   * Open the more info modal.
   */
  openMoreInfoModal(): void {
    this.simpleModalService
        .openInformationModal(`<b>${this.studentName}\'s Profile - More Info</b>`, SimpleModalType.NEUTRAL,
            this.moreInfoText, { confirmMessage: 'Close' });
  }

}
