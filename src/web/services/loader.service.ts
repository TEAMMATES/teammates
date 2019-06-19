import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoaderService {

  private numOfLoadingRequests: number = 0;

  public isShown = new Subject<boolean>();

  constructor() { }

  startLoad() {
    this.numOfLoadingRequests++;
    this.updateLoadingState()
  }

  finishLoad() {
    this.numOfLoadingRequests--;
    this.updateLoadingState();
  }

  updateLoadingState() {
    if (this.numOfLoadingRequests > 0) {
      this.isShown.next(true);
    }

    if (this.numOfLoadingRequests == 0) {
      this.isShown.next(false);
    }
  }

}
