import { EventEmitter, Output } from '@angular/core';
import { Observable } from 'rxjs';

/**
 * Base class for constraint components.
 */
export abstract class QuestionConstraintComponent {
  @Output()
  isValidEvent: EventEmitter<Observable<boolean>> = new EventEmitter();

  abstract isValid(): Observable<boolean>;
}
