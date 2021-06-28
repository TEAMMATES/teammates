import { Component, OnInit } from '@angular/core';

/**
 * Model for log.
 */
export interface Log {
  timestamp: string;
  data: JSON;
}

/**
 * A table to display logs.
 */
@Component({
  selector: 'tm-logs-table',
  templateUrl: './logs-table.component.html',
  styleUrls: ['./logs-table.component.scss'],
})
export class LogsTableComponent implements OnInit {

  logs: Log[] = [];

  constructor() { }

  ngOnInit(): void {
  }

}
