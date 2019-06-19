import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoaderService {

  private loadCapacity: number = 0;
  private loadProgress: number = 0;
  private value: number = 0;

  constructor() { }

  startLoad() {
    this.loadCapacity++;
  }

  finishLoad() {
    this.loadProgress++;
  }

  getValue() {
    this.value = this.loadProgress != 0? this.loadProgress/this.loadCapacity*100: 0;

    if (this.value >= 100) {
      this.loadCapacity = 0;
      this.loadProgress = 0;
    }

    return this.value;
  }

}
