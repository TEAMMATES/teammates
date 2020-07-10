import { Component, Input, OnInit } from '@angular/core';
import { ConfirmationModalService } from '../../../../services/confirmation-modal.service';
import { ConfirmationModalType } from '../../../components/confirmation-modal/confirmation-modal-type';

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

  constructor(private confirmationModalService: ConfirmationModalService) { }

  ngOnInit(): void {
  }

  /**
   * Open the more info modal.
   */
  openMoreInfoModal(): void {
    this.confirmationModalService
        .open(`${this.studentName}\'s Profile - More Info`, ConfirmationModalType.NEUTRAL,
            this.moreInfoText, { isNotificationOnly: true, confirmMessage: 'Close' });
  }

}
