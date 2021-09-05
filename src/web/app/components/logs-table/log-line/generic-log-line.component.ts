import { Component, Input, OnInit } from '@angular/core';
import { GeneralLogEntry } from '../../../../types/api-output';

/**
 * Generic log line, i.e. log lines that do not need specific displaying logic.
 */
@Component({
  selector: 'tm-generic-log-line',
  templateUrl: './generic-log-line.component.html',
  styleUrls: ['./generic-log-line.component.scss'],
})
export class GenericLogLineComponent implements OnInit {

  logValue!: GeneralLogEntry;
  summary: string = '';

  @Input()
  get log(): GeneralLogEntry {
    return this.logValue;
  }

  set log(log: GeneralLogEntry) {
    this.logValue = log;
    if (log.message) {
      this.summary = log.message;
    } else if (log.details) {
      this.summary = log.details.message || '';
    }
  }

  constructor() { }

  ngOnInit(): void {
  }

}
