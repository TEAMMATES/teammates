import { Component, OnInit } from '@angular/core';
import { MDCSnackbar } from '@material/snackbar';

/**
 * Component for snackbar status messages.
 */
@Component({
  selector: 'tm-snackbar',
  templateUrl: './snackbar.component.html',
  styleUrls: ['./snackbar.component.scss'],
})
export class SnackbarComponent implements OnInit {

  snackbar: Element;

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Opens the snackbar.
   */
  openSnackbar(): void {
    this.snackbar = new MDCSnackbar((document.querySelector('.mdc-snackbar')) as Element);
    this.snackbar.open();
  }

}
