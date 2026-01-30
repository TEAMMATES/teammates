import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgIf } from '@angular/common';

/**
 * Retry button for when loading fails.
 */
@Component({
  selector: 'tm-loading-retry',
  templateUrl: './loading-retry.component.html',
  styleUrls: ['./loading-retry.component.scss'],
  imports: [NgIf],
})
export class LoadingRetryComponent {

  @Input() message: string = '';

  @Input() shouldShowRetry: boolean = false;

  @Input() retryButtonDisabled: boolean = false;

  @Output() retryEvent: EventEmitter<any> = new EventEmitter<any>();

}
