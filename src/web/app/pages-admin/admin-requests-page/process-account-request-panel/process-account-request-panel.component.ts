import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TimezoneService } from '../../../../services/timezone.service';
import { AccountRequest, AccountRequestStatus } from '../../../../types/api-output';

export enum ProcessAccountRequestPanelStatus {
  SUBMITTED,
  EDITING,
  APPROVED,
  REJECTED,
  REGISTERED,
}

export interface EditedAccountRequestInfoModel {
  editedName: string;
  editedInstitute: string;
  editedEmail: string;
}

/**
 * Panel to display an account request being processed.
 */
@Component({
  selector: 'tm-process-account-request-panel',
  templateUrl: './process-account-request-panel.component.html',
  styleUrls: ['./process-account-request-panel.component.scss'],
})
export class ProcessAccountRequestPanelComponent implements OnInit {

  @Input()
  accountRequest!: AccountRequest;

  @Input()
  panelStatus: ProcessAccountRequestPanelStatus = ProcessAccountRequestPanelStatus.SUBMITTED;

  @Input()
  isSavingChanges: boolean = false;

  @Input()
  errorMessage: string = '';

  @Output()
  editAccountRequestEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  cancelEditAccountRequestEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  saveAccountRequestEvent: EventEmitter<EditedAccountRequestInfoModel> = new EventEmitter();

  @Output()
  approveAccountRequestEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  rejectAccountRequestEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  deleteAccountRequestEvent: EventEmitter<void> = new EventEmitter();

  @Output()
  resetAccountRequestEvent: EventEmitter<void> = new EventEmitter();

  // enums
  ProcessAccountRequestPanelStatus: typeof ProcessAccountRequestPanelStatus = ProcessAccountRequestPanelStatus;
  AccountRequestStatus: typeof AccountRequestStatus = AccountRequestStatus;

  editedName!: string;
  editedInstitute!: string;
  editedEmail!: string;

  timezone: string = '';

  constructor(private timezoneService: TimezoneService) {
  }

  ngOnInit(): void {
    this.timezone = this.timezoneService.guessTimezone() || 'UTC';
  }

  editAccountRequest(): void {
    this.editAccountRequestEvent.emit();
    this.editedName = this.accountRequest!.name;
    this.editedInstitute = this.accountRequest!.institute;
    this.editedEmail = this.accountRequest!.email;
  }

  cancelEditAccountRequest(): void {
    this.cancelEditAccountRequestEvent.emit();
  }

  saveAccountRequest(): void {
    const editedInfo: EditedAccountRequestInfoModel = {
      editedName: this.editedName,
      editedInstitute: this.editedInstitute,
      editedEmail: this.editedEmail,
    };
    this.saveAccountRequestEvent.emit(editedInfo);
  }

  approveAccountRequest(): void {
    this.approveAccountRequestEvent.emit();
  }

  rejectAccountRequest(): void {
    this.rejectAccountRequestEvent.emit();
  }

  deleteAccountRequest(): void {
    this.deleteAccountRequestEvent.emit();
  }

  resetAccountRequest(): void {
    this.resetAccountRequestEvent.emit();
  }

}
