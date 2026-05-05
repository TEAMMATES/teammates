import { Component, EventEmitter, Input, Output } from '@angular/core';

/**
 * Retry button for when loading fails.
 */
@Component({
  selector: 'tm-loading-retry',
  templateUrl: './loading-retry.component.html',
  imports: [],
})
export class LoadingRetryComponent {
  @Input() message?: string;

  @Input() shouldShowRetry = false;

  @Input() retryButtonDisabled = false;

  @Output() retryEvent: EventEmitter<any> = new EventEmitter<any>();
}
