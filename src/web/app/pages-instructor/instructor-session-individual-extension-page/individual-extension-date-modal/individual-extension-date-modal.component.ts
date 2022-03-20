import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { DateFormat } from '../../../components/datepicker/datepicker.component';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { FormatDateDetailPipe } from '../../../components/teammates-common/format-date-detail.pipe';
import { TimeFormat } from '../../../components/timepicker/timepicker.component';

export enum RadioOptions {
  EXTEND_TO = 1,
  EXTEND_BY = 2,
}

@Component({
  selector: 'tm-individual-extension-date-modal',
  templateUrl: './individual-extension-date-modal.component.html',
  styleUrls: ['./individual-extension-date-modal.component.scss'],
})

export class IndividualExtensionDateModalComponent {
  @Input()
  numStudents: number = 0;

  @Input()
  numInstructors: number = 0;

  @Input()
  feedbackSessionEndingTime: number = 0;

  @Input()
  feedbackSessionTimeZone: string = '';

  @Output() onConfirmCallBack: EventEmitter<number> = new EventEmitter();

  constructor(public activeModal: NgbActiveModal,
              private timeZoneService: TimezoneService,
              private simpleModalService: SimpleModalService) {}

  RadioOptions: typeof RadioOptions = RadioOptions;
  radioOption: RadioOptions = RadioOptions.EXTEND_BY;
  extendByDeadlineKey: String = '';
  extendByDeadlineOptions: Map<String, Number> = new Map([
    ['12 hours', 0.5],
    ['1 day', 24],
    ['3 days', 72],
    ['1 week', 168],
    ['Customize', 0],
  ]);

  DATETIME_FORMAT: string = 'd MMM YYYY h:mm:ss';
  ONE_MINUTE_IN_MILLISECONDS = 60 * 1000;
  ONE_HOUR_IN_MILLISECONDS = 60 * this.ONE_MINUTE_IN_MILLISECONDS;
  ONE_DAY_IN_MILLISECONDS = 24 * this.ONE_HOUR_IN_MILLISECONDS;
  datePicker: DateFormat = { year: 0, month: 0, day: 0 };
  timePicker: TimeFormat = { hour: 23, minute: 59 };
  extendByDatePicker = { hours: 0, days: 0 };

  onConfirm(): void {
    if (this.getExtensionTimestamp() < Date.now()) {
      const extensionTimeString = this.timeZoneService
        .formatToString(this.getExtensionTimestamp(), this.feedbackSessionTimeZone, this.DATETIME_FORMAT);
      const currentTimeString = this.timeZoneService
        .formatToString(Date.now(), this.feedbackSessionTimeZone, this.DATETIME_FORMAT);
      this.simpleModalService.openConfirmationModal(
        'Are you sure you wish to set the new deadline to before the current time?',
        SimpleModalType.WARNING,
        '<b>Any users affected will have their sessions closed immediately.</b>'
        + ` The current time now is ${currentTimeString} and you are extending to`
        + ` ${extensionTimeString} in ${this.feedbackSessionTimeZone}. Do you wish to proceed?`,
      ).result.then(() => this.onConfirmCallBack.emit(this.getExtensionTimestamp()));
    } else {
      this.onConfirmCallBack.emit(this.getExtensionTimestamp());
    }
  }

  onChangeDateTime(data: DateFormat | TimeFormat, field: string): void {
    if (field === 'date') {
      this.datePicker = data as DateFormat;
    } else if (field === 'time') {
      this.timePicker = data as TimeFormat;
    }
  }

  getDateFormat(timestamp: number) : DateFormat {
    let momentInstance: moment.Moment = moment(timestamp);
    if (momentInstance.hour() === 0 && momentInstance.minute() === 0) {
      momentInstance = momentInstance.subtract(1, 'minute');
    }
    return {
      year: momentInstance.year(),
      month: momentInstance.month() + 1, // moment return 0-11 for month
      day: momentInstance.date(),
    };
  }

  getExtensionTimestamp(): number {
    if (this.isRadioExtendBy()) {
      if (!this.isCustomize() && this.extendByDeadlineOptions.has(this.extendByDeadlineKey)) {
        return this.addTime(this.feedbackSessionEndingTime,
          this.extendByDeadlineOptions.get(this.extendByDeadlineKey)!.valueOf(), 0);
      }
      if (this.isCustomize()) {
        return this.addTime(this.feedbackSessionEndingTime, this.extendByDatePicker.hours,
          this.extendByDatePicker.days);
      }
    }
    if (this.isRadioExtendTo()) {
      const timestamp = this.timeZoneService
        .resolveLocalDateTime(this.datePicker, this.timePicker, this.feedbackSessionTimeZone, true);
      return timestamp;
    }
    return this.feedbackSessionEndingTime;
  }

  addTimeAndFormat(hours: number, days: number): string {
    const time = this.addTime(this.feedbackSessionEndingTime, hours, days);
    const dateDetailPipe = new FormatDateDetailPipe(this.timeZoneService);
    return dateDetailPipe.transform(time, this.feedbackSessionTimeZone);
  }

  addTime(timestamp: number, hours: number, days: number): number {
    return timestamp + (hours * this.ONE_HOUR_IN_MILLISECONDS) + (days * this.ONE_DAY_IN_MILLISECONDS);
  }

  isValidForm() : boolean {
    return this.getExtensionTimestamp() > this.feedbackSessionEndingTime;
  }

  isRadioExtendBy(): boolean {
    return this.radioOption === RadioOptions.EXTEND_BY;
  }

  isRadioExtendTo(): boolean {
    return this.radioOption === RadioOptions.EXTEND_TO;
  }

  isCustomize(): boolean {
    return this.isRadioExtendBy() && this.extendByDeadlineKey === 'Customize';
  }

  sortMapByOriginalOrder = (): number => {
    return 0;
  };

}
