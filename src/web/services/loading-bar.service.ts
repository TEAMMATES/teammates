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

  constructor() { }

  /**
   * Displays the loading bar.
   */
  startLoad(): void {
    this.isShown.next(true);
  }

  /**
   * Hides the loading progress bar.
   */
  finishLoad(): void {
    this.isShown.next(false);
  }

}
