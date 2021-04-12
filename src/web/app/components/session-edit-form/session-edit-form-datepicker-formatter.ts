import { Injectable } from '@angular/core';
import { NgbDateParserFormatter, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import moment from 'moment-timezone';

const DATE_FORMAT: string = 'ddd, DD MMM, YYYY';

/**
 * Date formatter for date picker in session edit form
 */
@Injectable()
export class SessionEditFormDatePickerFormatter extends NgbDateParserFormatter {

  format(date: NgbDateStruct): string {
    if (date == null) {
      return '';
    }

    const inst: any = moment();
    inst.set('year', date.year);
    inst.set('month', date.month - 1); // moment month is from 0-11
    inst.set('date', date.day);

    return inst.format(DATE_FORMAT);
  }

  parse(value: string): NgbDateStruct {
    const inst: any = moment(value, DATE_FORMAT);
    return {
      year: inst.year(),
      month: inst.month() + 1, // moment return 0-11 for month
      day: inst.date(),
    };
  }

}
