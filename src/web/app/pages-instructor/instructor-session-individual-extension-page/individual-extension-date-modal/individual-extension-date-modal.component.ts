import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { DateFormat } from 'src/web/app/components/datepicker/datepicker.component';
import { SimpleModalType } from 'src/web/app/components/simple-modal/simple-modal-type';
import { TimeFormat } from 'src/web/app/components/timepicker/timepicker.component';
import { DateTimeHelper } from 'src/web/services/datetime-helper';
import { SimpleModalService } from 'src/web/services/simple-modal.service';
import { TimezoneService } from 'src/web/services/timezone.service';

/**
 * Modal to confirm permanent deletion of a feedback session.
 */
@Component({
  selector: 'tm-individual-extension-date-modal',
  templateUrl: './individual-extension-date-modal.component.html',
  styleUrls: ['./individual-extension-date-modal.component.scss'],
})

export class IndividualExtensionDateModalComponent
{
  @Input()
  numberOfStudents: number = 0;

  @Input()
  feedbackSessionEndingTime: number = 0;

  @Input()
  feedbackSessionTimeZone: string = "";

  @Output() onConfirmCallBack: EventEmitter<number> = new EventEmitter();
  
  constructor(public activeModal: NgbActiveModal,
              private timeZoneService: TimezoneService,
              private simpleModalService: SimpleModalService)
  {}
  
  radioOption: number = 1;
  extendByDeadlineKey: String = "";
  deadlineOptions: Map<String, Number> = new Map([
    ["12 hours", 0.5],
    ["1 day", 24],
    ["3 days", 72],
    ["1 week", 168],
    ["Customize", 0],
  ])
  
  DATETIME_FORMAT: string = "d MMM YYYY h:mm:ss";
  datePicker: DateFormat = { year: 0, month: 0, day: 0 };
  timePicker: TimeFormat = { hour: 23, minute: 59 };
  extendByDatePicker = { minutes: 0, hours: 0, days: 0 };

  onConfirm(): void {
    if (this.getExtensionTimestamp() < Date.now()) {
      const extensionTimeString = this.timeZoneService.formatToString(this.getExtensionTimestamp(), this.feedbackSessionTimeZone, this.DATETIME_FORMAT)
      const currentTimeString = this.timeZoneService.formatToString(Date.now(), this.feedbackSessionTimeZone, this.DATETIME_FORMAT)
      this.simpleModalService.openConfirmationModal(
        "Are you sure you want to extend to before the current time?",
        SimpleModalType.DANGER,
        `The current time now is ${ currentTimeString } and you are extending to ${ extensionTimeString } in ${ this.feedbackSessionTimeZone}. Do you wish to proceed?`,
      ).result.then(() => this.onConfirmCallBack.emit(this.getExtensionTimestamp()));
      
    } else {
      this.onConfirmCallBack.emit(this.getExtensionTimestamp());
    }
  }

  onChangeDateTime(data: DateFormat | TimeFormat, field: string) {
    if (field == "date") {
      this.datePicker = data as DateFormat;
    } else {
      this.timePicker = data as TimeFormat;
    }
  }

  getDateFormat(timestamp: number) : DateFormat {
    let momentInstance: moment.Moment = moment(timestamp)
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
    if (this.radioOption == 1) {
      if (this.extendByDeadlineKey != "Customize" && this.deadlineOptions.has(this.extendByDeadlineKey)) {
        return this.addTime(0, this.deadlineOptions.get(this.extendByDeadlineKey)!.valueOf(), 0);
      }
      if (this.isCustomize()) {
        return this.addTime(this.extendByDatePicker.minutes, this.extendByDatePicker.hours, this.extendByDatePicker.days)
      }
    } 
    if (this.radioOption == 2) {
      let timestamp = this.timeZoneService.resolveLocalDateTime(this.datePicker, this.timePicker, this.feedbackSessionTimeZone, true);
      return timestamp;
    }
    return this.feedbackSessionEndingTime;
  }

  isValidForm() : boolean { 
    return this.getExtensionTimestamp() > this.feedbackSessionEndingTime;
  }
  
  isCustomize(): boolean {
    return this.radioOption == 1 && this.extendByDeadlineKey == "Customize";
  }

  addTime(minutes: number, hours: number, days: number): number {
    return DateTimeHelper.addTime(this.feedbackSessionEndingTime, minutes, hours, days);
  }

  sortMapByOriginalOrder = (): number => {
    return 0;
  }

}