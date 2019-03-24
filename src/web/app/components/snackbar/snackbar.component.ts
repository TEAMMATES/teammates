import { Component, Input, OnInit } from '@angular/core';
import { MDCSnackbar } from '@material/snackbar';
import { Snackbar } from './snackbar';

/**
 * Component for snackbar status messages.
 */
@Component({
  selector: 'tm-snackbar',
  templateUrl: './snackbar.component.html',
  styleUrls: ['./snackbar.component.scss'],
})
export class SnackbarComponent implements OnInit {

  @Input() messages: Snackbar[] = [];

  message: string = '';

  constructor() { }

  ngOnInit(): void {

  }

  /**
   * Shows the message via snackbar.
   */
  showMessage(message: string): void {
    this.message = message;
    const snackbar: MDCSnackbar = new MDCSnackbar((document.querySelector('.mdc-snackbar')) as Element);
    snackbar.open();
  }

}
