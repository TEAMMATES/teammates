import {
    Directive, HostListener,
  } from '@angular/core';
  
/**
 * Directive for loading spinner component
 */
@Directive({
  selector: '[disableWheel]',
})
export class WheelDisablerDirective {

  @HostListener('wheel', ['$event'])
  onWheel(e: Event) {
    e.preventDefault();
  }

}
