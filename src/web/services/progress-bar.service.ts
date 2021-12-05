import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

/**
 * Service to handle display of the download progress bar
 */
@Injectable({
  providedIn: 'root',
})
export class ProgressBarService {

  progressPercentage: Subject<number> = new Subject<number>();

  /**
   * Update the progress percentage on the progress bar.
   */
  updateProgress(progressPercentage: number): void {
    this.progressPercentage.next(progressPercentage);
  }

}
