import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountRequest, AccountRequests } from '../../../types/api-output';
import { DateFormat } from '../../components/datepicker/datepicker.component';
import { collapseAnim } from '../../components/teammates-common/collapse-anim';
import { ErrorMessageOutput } from '../../error-message-output';

interface FormQueryModel {
  fromDate: DateFormat;
  toDate: DateFormat;
}

/**
 * Admin requests page.
 */
@Component({
  selector: 'tm-admin-requests-page',
  templateUrl: './admin-requests-page.component.html',
  styleUrls: ['./admin-requests-page.component.scss'],
  animations: [collapseAnim],
})
export class AdminRequestsPageComponent implements OnInit {

  accountRequestsPendingProcessing: AccountRequest[] = [];
  hasAccountRequestsPendingProcessingLoadingFailed: boolean = false;
  isLoadingAccountRequestsPendingProcessing: boolean = false;

  accountRequestsWithinPeriod: AccountRequest[] = [];
  hasAccountRequestsWithinPeriodLoadingFailed: boolean = false;
  isLoadingAccountRequestsWithinPeriod: boolean = false;
  hasQueried: boolean = false;

  formModel: FormQueryModel = {
    fromDate: { year: 0, month: 0, day: 0 },
    toDate: { year: 0, month: 0, day: 0 },
  };
  dateToday: DateFormat = { year: 0, month: 0, day: 0 };
  earliestSearchDate: DateFormat = { year: 2016, month: 1, day: 1 };
  timezone: string = '';

  constructor(private accountService: AccountService,
              private statusMessageService: StatusMessageService,
              private timezoneService: TimezoneService) {
  }

  ngOnInit(): void {
    this.timezone = this.timezoneService.guessTimezone() || 'UTC';

    const now = new Date();
    this.dateToday.year = now.getFullYear();
    this.dateToday.month = now.getMonth() + 1;
    this.dateToday.day = now.getDate();

    this.formModel.fromDate = { ...this.dateToday };
    this.formModel.toDate = { ...this.dateToday };

    this.loadAccountRequestsPendingProcessing();
  }

  /**
   * Loads all account requests pending processing.
   */
  loadAccountRequestsPendingProcessing(): void {
    this.accountRequestsPendingProcessing = [];
    this.hasAccountRequestsPendingProcessingLoadingFailed = false;
    this.isLoadingAccountRequestsPendingProcessing = true;

    this.accountService.getAccountRequestsPendingProcessing()
      .pipe(finalize(() => {
        this.isLoadingAccountRequestsPendingProcessing = false;
      }))
      .subscribe((resp: AccountRequests) => {
        this.accountRequestsPendingProcessing = resp.accountRequests;
        // TODO: sort account requests
      }, (resp: ErrorMessageOutput) => {
        this.accountRequestsPendingProcessing = [];
        this.hasAccountRequestsPendingProcessingLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      });
  }

  /**
   * Loads all account requests submitted within the period specified in formModel.
   */
  loadAccountRequestsWithinPeriod(): void {
    if (this.formModel.fromDate === null) {
      this.statusMessageService.showErrorToast('Please enter a start date');
      return;
    }
    if (this.formModel.toDate === null) {
      this.statusMessageService.showErrorToast('Please enter a end date');
      return;
    }

    this.hasQueried = true;
    this.accountRequestsWithinPeriod = [];
    this.hasAccountRequestsWithinPeriodLoadingFailed = false;
    this.isLoadingAccountRequestsWithinPeriod = true;

    const timestampFrom = this.timezoneService.resolveLocalDateTime(
      this.formModel.fromDate, { hour: 0, minute: 0 }, this.timezone);
    const timestampTo = this.timezoneService.resolveLocalDateTime(
      this.formModel.toDate, { hour: 23, minute: 59 }, this.timezone);
    this.accountService.getAccountRequestsWithinPeriod(timestampFrom, timestampTo)
      .pipe(finalize(() => {
        this.isLoadingAccountRequestsWithinPeriod = false;
      }))
      .subscribe((resp: AccountRequests) => {
        this.accountRequestsWithinPeriod = resp.accountRequests;
        // TODO: sort account requests
      }, (resp: ErrorMessageOutput) => {
        this.accountRequestsWithinPeriod = [];
        this.hasAccountRequestsWithinPeriodLoadingFailed = true;
        this.statusMessageService.showErrorToast(resp.error.message);
      });
  }

}
