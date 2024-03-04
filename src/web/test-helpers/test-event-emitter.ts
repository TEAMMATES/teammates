import { EventEmitter } from '@angular/core';
import { first } from 'rxjs/operators';

/**
 *
 * A utility function to test for event emission.
 *
 * @param eventEmitter The EventEmitter to be tested
 * @param callback Function to be called when the event is emitted
 * @param takeOnlyFirst Whether to only take the first value that the event emitter emits
 * @example
 * let isInEditMode: boolean | undefined;
 * testEventEmission(component.eventEmitter, (emittedValue) => { isInEditMode = emittedValue; });
 * component.eventEmitter.emit(true);
 * expect(isInEditMode).toBeTruthy();
 */
export default function testEventEmission<T>(eventEmitter: EventEmitter<T>,
      callback: (value: T) => void, takeOnlyFirst: boolean = true): void {
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
