import { EventEmitter } from "@angular/core";
import { first } from 'rxjs/operators';

export default function testEventEmission<T>(eventEmitter: EventEmitter<T>, callback: (value: T) => void, takeOnlyFirst: boolean = true): void {
    if (takeOnlyFirst) {
      eventEmitter.pipe(first()).subscribe((value: T) => {
        callback(value);
      });
    } else {
      eventEmitter.subscribe((value: T) => {
        callback(value);
      });
    }
  }