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
  msg: string = '';

  constructor() {
    this.msg  = '';

    this.getMsg();
  }

  getMsg(): void {
    setTimeout(() => { this.msg = 'Please wait, it is loading'; }, 10000);
  }
}
