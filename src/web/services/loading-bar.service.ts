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

  private numOfLoadingRequests: number = 0;

  constructor() { }

  /**
   * Displays the loading bar.
   * This must always be called first before calling {@link finishLoad()}.
   */
  startLoad(): void {
    this.numOfLoadingRequests += 1;
    this.isShown.next(true);
  }

  /**
   * Hides the loading progress bar if there are no remaining requests.
   */
  finishLoad(): void {
    this.numOfLoadingRequests -= 1;
    if (this.numOfLoadingRequests === 0) {
      this.isShown.next(false);
    }
  }

}
