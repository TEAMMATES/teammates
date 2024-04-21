import { AbstractControl, ValidationErrors } from '@angular/forms';

export class WhitespaceValidator {
  static cannotContainWhitespace(control: AbstractControl): ValidationErrors | null {
    if (typeof control.value === 'string' && control.value.trim().length === 0) {
      return { cannotContainWhitespace: true };
    }
    return null;
  }
}
