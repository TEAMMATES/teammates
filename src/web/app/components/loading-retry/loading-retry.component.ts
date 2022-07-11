import { Component, EventEmitter, Input, Output } from '@angular/core';

/**
 * Retry button for when loading fails.
 */
@Component({
  selector: 'tm-loading-retry',
  templateUrl: './loading-retry.component.html',
  styleUrls: ['./loading-retry.component.scss'],
})
export class LoadingRetryComponent {

  @Input() message: string = '';

  @Input() shouldShowRetry: boolean = false;

  @Input() retryButtonDisabled: boolean = false;

  @Output() retryEvent: EventEmitter<any> = new EventEmitter<any>();

}
