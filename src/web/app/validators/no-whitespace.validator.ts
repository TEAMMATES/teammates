import { AbstractControl, ValidationErrors } from '@angular/forms';

/**
 * Validator that checks if the control value consists of only whitespace characters.
 */
export function noWhitespaceValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  return typeof value === 'string' && value.trim().length === 0 ? { whitespace: true } : null;
}
