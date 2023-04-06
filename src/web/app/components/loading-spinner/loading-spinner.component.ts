import { Component } from '@angular/core';

/**
 * Loading spinner to show when waiting for request.
 */
@Component({
  selector: 'tm-loading-spinner',
  templateUrl: './loading-spinner.component.html',
  styleUrls: ['./loading-spinner.component.scss'],
})
export class LoadingSpinnerComponent {
  loadingMessage: string = 'Loading...';

  constructor() {
    // After 5 seconds of loading show message to assure user the page is not hanging.
    setTimeout(() => { this.loadingMessage = 'Still loading...'; }, 5000);
  }
}
