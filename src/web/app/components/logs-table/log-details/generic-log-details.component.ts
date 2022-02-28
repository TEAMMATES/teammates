import { Component, Input } from '@angular/core';
import { GeneralLogEntry } from '../../../../types/api-output';

/**
 * Additional details for generic logs, i.e. logs that do not need specific displaying logic.
 */
@Component({
  selector: 'tm-generic-log-details',
  templateUrl: './generic-log-details.component.html',
  styleUrls: ['./generic-log-details.component.scss', './log-details.component.scss'],
})
export class GenericLogDetailsComponent {

  logValue!: GeneralLogEntry;

  @Input()
  get log(): GeneralLogEntry {
    return this.logValue;
  }

  set log(log: GeneralLogEntry) {
    this.logValue = log;
  }

}
