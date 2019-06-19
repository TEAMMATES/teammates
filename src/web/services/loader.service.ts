import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoaderService {

  constructor() { }

  incrementLoader() {
    console.log("incrementLoader")
  }

  decrementLoader() {
    console.log("decrement loader")
  }
}
