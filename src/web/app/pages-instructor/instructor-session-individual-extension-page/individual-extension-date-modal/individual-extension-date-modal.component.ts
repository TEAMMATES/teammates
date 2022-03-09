import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';
import { DateFormat } from 'src/web/app/components/datepicker/datepicker.component';
import { TimeFormat } from 'src/web/app/components/timepicker/timepicker.component';
import { IndividualExtensionConfirmModalComponent } from '../individual-extension-confirm-modal/individual-extension-confirm-modal.component';
/**
 * Modal to confirm permanent deletion of a feedback session.
 */
@Component({
  selector: 'individual-extension-date-modal',
  templateUrl: './individual-extension-date-modal.component.html',
  styleUrls: ['./individual-extension-date-modal.component.scss'],
})

export class IndividualExtensionDateModalComponent implements OnInit {
  @Input()
  numberOfStudents: number = 0;

  @Input()
  feedbackSessionDateTime = {
    date: { year: 0, month: 0, day: 0 },
    time: { hour: 23, minute: 59 },
    timeZone: "",
  }

  @Output()
  onConfirmExtension: EventEmitter<void> = new EventEmitter();
  
  constructor(public activeModal: NgbActiveModal,
              private ngbModal: NgbModal,
              )
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
  
  datePickerTime: DateFormat = { year: 0, month: 0, day: 0 };

  ngOnInit(): void {
  }

  onConfirm(): void {
    this.onConfirmExtension.emit(); // TODO: Emit the final date
  }

  addHoursAndFormat(hours: number ): String {
    let moment: moment.Moment = this.getMomentInstance(this.feedbackSessionDateTime.date, this.feedbackSessionDateTime.time);
    moment = this.addHoursToMomentInstance(moment, hours);
    return moment.format("d MMM YYYY h:mm:ss");
  }

  
  /**
   * Gets a moment instance from a date.
   */
  getMomentInstance(date: DateFormat, time: TimeFormat): moment.Moment {
    const inst: moment.Moment = moment();
    inst.set('year', date.year);
    inst.set('month', date.month - 1); // moment month is from 0-11
    inst.set('date', date.day);
    inst.set('hour', time.hour);
    inst.set('minute', time.minute);
    return inst;
  }

  addHoursToMomentInstance(moment: moment.Moment, hours: number): moment.Moment {
    return moment.add(hours, 'hours');
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