import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { DateFormat } from '../../../components/datepicker/datepicker.component';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { FormatDateDetailPipe } from '../../../components/teammates-common/format-date-detail.pipe';
import { TimeFormat } from '../../../components/timepicker/timepicker.component';

enum RadioOptions {
  EXTEND_TO = 1,
  EXTEND_BY = 2,
}

enum DateTime {
  DATE,
  TIME,
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
  feedbackSessionEndingTimestamp: number = 0;

  @Input()
  feedbackSessionTimeZone: string = '';

  @Output() onConfirmCallBack: EventEmitter<number> = new EventEmitter();

  constructor(
    public activeModal: NgbActiveModal,
    private timeZoneService: TimezoneService,
    private simpleModalService: SimpleModalService,
  ) {}

  RadioOptions: typeof RadioOptions = RadioOptions;
  radioOption: RadioOptions = RadioOptions.EXTEND_BY;
  DateTime: typeof DateTime = DateTime;

  extendByDeadlineKey: String = '';
  extendByDeadlineOptions: Map<String, Number> = new Map([
    ['12 hours', 12],
    ['1 day', 24],
    ['3 days', 72],
    ['1 week', 168],
    ['Customize', 0],
  ]);
  extendByDatePicker = { hours: 0, days: 0 };

  ONE_MINUTE_IN_MILLISECONDS = 60 * 1000;
  ONE_HOUR_IN_MILLISECONDS = 60 * this.ONE_MINUTE_IN_MILLISECONDS;
  ONE_DAY_IN_MILLISECONDS = 24 * this.ONE_HOUR_IN_MILLISECONDS;
  MAX_EPOCH_TIME_IN_DAYS = 100000000;
  MAX_EPOCH_TIME_IN_MILLISECONDS = this.MAX_EPOCH_TIME_IN_DAYS * this.ONE_DAY_IN_MILLISECONDS;
  extendToDatePicker: DateFormat = { year: 0, month: 0, day: 0 };
  extendToTimePicker: TimeFormat = { hour: 23, minute: 59 };
  dateDetailPipe = new FormatDateDetailPipe(this.timeZoneService);

  onConfirm(): void {
    if (this.getExtensionTimestamp() >= Date.now()) {
      this.onConfirmCallBack.emit(this.getExtensionTimestamp());
      return;
    }

    const extensionTimeString = this.dateDetailPipe.transform(
      this.getExtensionTimestamp(),
      this.feedbackSessionTimeZone,
    );
    const currentTimeString = this.dateDetailPipe.transform(
      Date.now(),
      this.feedbackSessionTimeZone,
    );
    this.simpleModalService
      .openConfirmationModal(
        'Are you sure you wish to set the new deadline to before the current time?',
        SimpleModalType.WARNING,
        '<b>Any users affected will have their sessions closed immediately.</b>'
          + ` The current time now is ${currentTimeString} and you are extending to`
          + ` ${extensionTimeString}. Do you wish to proceed?`,
      )
      .result.then(() => this.onConfirmCallBack.emit(this.getExtensionTimestamp()), () => {});
  }

  onChangeDateTime(data: DateFormat | TimeFormat, field: DateTime): void {
    if (field === DateTime.DATE) {
      this.extendToDatePicker = data as DateFormat;
    } else if (field === DateTime.TIME) {
      this.extendToTimePicker = data as TimeFormat;
    }
  }

  getDateFormat(timestamp: number): DateFormat {
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
      if (this.isCustomize()) {
        return this.addTime(this.feedbackSessionEndingTimestamp, this.extendByDatePicker.hours,
          this.extendByDatePicker.days);
      }
      if (this.extendByDeadlineOptions.has(this.extendByDeadlineKey)) {
        return this.addTime(this.feedbackSessionEndingTimestamp,
          this.extendByDeadlineOptions.get(this.extendByDeadlineKey)!.valueOf(), 0,
        );
      }
    }
    if (this.isRadioExtendTo()) {
      return this.timeZoneService.resolveLocalDateTime(
        this.extendToDatePicker,
        this.extendToTimePicker, this.feedbackSessionTimeZone,
        true);
    }
    return this.feedbackSessionEndingTimestamp;
  }

  addTimeAndFormat(hours: number, days: number): string {
    const time = this.addTime(this.feedbackSessionEndingTimestamp, hours, days);
    return this.dateDetailPipe.transform(time, this.feedbackSessionTimeZone);
  }

  private addTime(timestamp: number, hours: number, days: number): number {
    return timestamp + hours * this.ONE_HOUR_IN_MILLISECONDS + days * this.ONE_DAY_IN_MILLISECONDS;
  }

  isValidForm(): boolean {
    return this.isDateSelectedLaterThanCurrentEndingTimestamp() && this.isCustomizeValid();
  }

  isDateSelectedLaterThanCurrentEndingTimestamp(): boolean {
    return this.getExtensionTimestamp() > this.feedbackSessionEndingTimestamp;
  }

  isCustomizeValid(): boolean {
    return this.isCustomizeDateTimeIntegers() && this.isCustomizeBeforeMaxDate();
  }

  isCustomizeDateTimeIntegers(): boolean {
    if (!this.isCustomize()) {
      return true;
    }

    return Number.isInteger(this.extendByDatePicker.days) && Number.isInteger(this.extendByDatePicker.hours);
  }

  isCustomizeBeforeMaxDate(): boolean {
    if (!this.isCustomize()) {
      return true;
    }

    const timeSelected = this.addTime(this.feedbackSessionEndingTimestamp, this.extendByDatePicker.hours,
      this.extendByDatePicker.days);
    return timeSelected < this.MAX_EPOCH_TIME_IN_MILLISECONDS;
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
