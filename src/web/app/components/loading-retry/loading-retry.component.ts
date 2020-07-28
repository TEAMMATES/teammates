import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

/**
 * Retry button for when loading fails.
 */
@Component({
  selector: 'tm-loading-retry',
  templateUrl: './loading-retry.component.html',
  styleUrls: ['./loading-retry.component.scss'],
})
export class LoadingRetryComponent implements OnInit {

  @Input() message: string = '';

  @Input() shouldShowRetry: boolean = false;

  @Output() retryEvent: EventEmitter<any> = new EventEmitter<any>();

  constructor() { }

  ngOnInit(): void {
  }

}
