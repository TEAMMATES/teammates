import { Directive, HostListener } from '@angular/core';

/**
 * Directive for loading spinner component
 */
@Directive({
  selector: '[tmDisableWheel]',
})
export class WheelDisablerDirective {

  @HostListener('wheel', ['$event'])
  onWheel(e: Event): void {
    e.preventDefault();
  }

}
