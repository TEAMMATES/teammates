import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

/**
 * Service to handle display of the loading progress bar
 */
@Injectable({
  providedIn: 'root',
})
export class LoadingBarService {

  isShown: Subject<boolean> = new Subject<boolean>();

  /**
   * Displays the loading bar.
   */
  showLoadingBar(): void {
    this.isShown.next(true);
  }

  /**
   * Hides the loading bar.
   */
  hideLoadingBar(): void {
    this.isShown.next(false);
  }

}
