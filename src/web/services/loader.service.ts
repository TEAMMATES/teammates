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
    console.log("LOADING");
    this.updateLoadingState()
  }

  finishLoad() {
    console.log("FINISH LOADING");
    this.numOfLoadingRequests--;
    this.updateLoadingState();
  }

  updateLoadingState() {
    console.log(this.numOfLoadingRequests);

    if (this.numOfLoadingRequests > 0) {
      this.isShown.next(true);
    }

    if (this.numOfLoadingRequests == 0) {
      console.log("REALLY FINISHED LOADING");
      this.isShown.next(false);
    }
  }

}
