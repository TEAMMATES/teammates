import { Component, forwardRef, Input } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

// noinspection AngularMissingOrInvalidDeclarationInModule
@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'ngx-recaptcha2',
  template: '<input (click)="checkBox()" (change)="handleChange()">',
  providers: [
    { provide: NG_VALUE_ACCESSOR, useExisting: forwardRef(() => MockReCaptcha2Component), multi: true },
  ],
})
export class MockReCaptcha2Component implements ControlValueAccessor {
  @Input() siteKey: string = '';
  @Input() hl: string = '';
  @Input() useGlobalDomain: boolean = false;

  response: string = '';

  onChange: any = () => {};
  onTouch: any = () => {};

  resetCaptcha(): void {
    this.writeValue('');
    this.onChange(undefined);
  }

  getResponse(): string {
    return this.response;
  }

  checkBox(): void {
    this.writeValue('checked');
    this.onChange('checked');
  }

  handleChange(): void {
    this.onChange(this.response);
  }

  writeValue(value: string): void {
    this.response = value;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouch = fn;
  }
}
