import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { DateFormat } from 'src/web/app/components/datepicker/datepicker.component';
import { IndividualExtensionConfirmModalComponent } from '../individual-extension-confirm-modal/individual-extension-confirm-modal.component';

/**
 * Modal to confirm permanent deletion of a feedback session.
 */
@Component({
  selector: 'individual-extension-date-modal',
  templateUrl: './individual-extension-date-modal.component.html',
  styleUrls: ['./individual-extension-date-modal.component.scss'],
})

export class IndividualExtensionDateModalComponent implements OnInit
{
  @Input()
  numberOfStudents: number = 0;

  @Input()
  feedbackSessionEndingTime: number = 0;

  @Output()
  onConfirmExtension: EventEmitter<void> = new EventEmitter();
  
  constructor(public activeModal: NgbActiveModal,
              private ngbModal: NgbModal)
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
  datePickerTime: DateFormat = { year: 0, month: 0, day: 0 };

  ngOnInit(): void
  {
  }

  onConfirm(): void
  {
    this.onConfirmExtension.emit(); // TODO: Emit the final date
  }

  addHoursAndFormat(hours: number): number {
    return this.feedbackSessionEndingTime * (hours * 60);
  }

  getDateFormat(timestamp: number) : DateFormat {
    let momentInstance: moment.Moment = moment(timestamp)
    if (momentInstance.hour() === 0 && momentInstance.minute() === 0) {
      momentInstance = momentInstance.subtract(1, 'minute');
    }
    console.log(momentInstance.year());
    return {
      year: momentInstance.year(),
      month: momentInstance.month() + 1, // moment return 0-11 for month
      day: momentInstance.date(),
    };  
  }

  setA() {
    console.log(this.radioOption)    
  }
  
  onExtend() { 
    this.activeModal.close()
    const modalRef: NgbModalRef = this.ngbModal.open(IndividualExtensionConfirmModalComponent);
    console.log("Open second modal" + modalRef)
  }
  
  onDelete()
  {
  }

  isValidForm() : boolean { 
    if (this.radioOption == 1 && !this.deadlineOptions.has(this.extendByDeadlineKey)) {
      return false;
    }
    if (this.radioOption == 2 && this.datePickerTime.year == 0) { // TODO: Date should just be after..
      return false;
    }
    return true;
  }

}