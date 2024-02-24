import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { TimezoneService } from '../../../../services/timezone.service';
import {
  DateFormat,
  TimeFormat,
  getDefaultDateFormat,
  getLatestTimeFormat,
  Hours,
  Milliseconds,
} from '../../../../types/datetime-const';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { FormatDateDetailPipe } from '../../../components/teammates-common/format-date-detail.pipe';

export enum RadioOptions {
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

  @Output()
  confirmCallbackEvent: EventEmitter<number> = new EventEmitter();

  constructor(
    public activeModal: NgbActiveModal,
    private timeZoneService: TimezoneService,
    private simpleModalService: SimpleModalService,
  ) {}

  RadioOptions: typeof RadioOptions = RadioOptions;
  radioOption: RadioOptions = RadioOptions.EXTEND_BY;
  DateTime: typeof DateTime = DateTime;

  extendByDeadlineKey: string = '';
  extendByDeadlineOptions: Map<string, number> = new Map([
    ['12 hours', Hours.TWELVE],
    ['1 day', Hours.IN_ONE_DAY],
    ['3 days', Hours.IN_THREE_DAYS],
    ['1 week', Hours.IN_ONE_WEEK],
    ['Customize', Hours.ZERO],
  ]);
  extendByDatePicker = { hours: 0, days: 0 };

  MAX_EPOCH_TIME_IN_DAYS = 100000000;
  MAX_EPOCH_TIME_IN_MILLISECONDS = this.MAX_EPOCH_TIME_IN_DAYS * Milliseconds.IN_ONE_DAY;
  extendToDatePicker: DateFormat = getDefaultDateFormat();
  extendToTimePicker: TimeFormat = getLatestTimeFormat();
  dateDetailPipe = new FormatDateDetailPipe(this.timeZoneService);

  sortMapByOriginalOrder = (): number => 0;

  onConfirm(): void {
    if (this.getExtensionTimestamp() >= Date.now()) {
      this.confirmCallbackEvent.emit(this.getExtensionTimestamp());
      return;
    }

    const extensionTimeString = this.adjustToFeedbackSessionTimeZone(this.getExtensionTimestamp());
    const currentTimeString = this.adjustToFeedbackSessionTimeZone(Date.now());

    this.simpleModalService
      .openConfirmationModal(
        'Are you sure you wish to set the new deadline to before the current time?',
        SimpleModalType.WARNING,
        '<b>Any users affected will have their sessions closed immediately.</b>'
          + ` The current time now is ${currentTimeString} and you are extending to`
          + ` ${extensionTimeString}. Do you wish to proceed?`,
      )
      .result.then(() => this.confirmCallbackEvent.emit(this.getExtensionTimestamp()), () => {});
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
      momentInstance = momentInstance.subtract(1, 'minute'); // formats midnight to 23:59
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
        return this.addTime(this.feedbackSessionEndingTimestamp,
          this.extendByDatePicker.hours, this.extendByDatePicker.days);
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

  extendAndFormatEndTimeBy(hours: number, days: number): string {
    const time = this.addTime(this.feedbackSessionEndingTimestamp, hours, days);
    return this.adjustToFeedbackSessionTimeZone(time);
  }

  private addTime(timestamp: number, hours: number, days: number): number {
    return timestamp + hours * Milliseconds.IN_ONE_HOUR + days * Milliseconds.IN_ONE_DAY;
  }

  private adjustToFeedbackSessionTimeZone(time: number): string {
    return this.dateDetailPipe.transform(time, this.feedbackSessionTimeZone);
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

    const timeSelected = this.addTime(this.feedbackSessionEndingTimestamp,
      this.extendByDatePicker.hours, this.extendByDatePicker.days);
    return timeSelected < this.MAX_EPOCH_TIME_IN_MILLISECONDS;
  }

  isRadioExtendBy(): boolean {
    return this.radioOption === RadioOptions.EXTEND_BY;
  }

  isCustomize(): boolean {
    return this.isRadioExtendBy() && this.extendByDeadlineKey === 'Customize';
  }

  isRadioExtendTo(): boolean {
    return this.radioOption === RadioOptions.EXTEND_TO;
  }
}
