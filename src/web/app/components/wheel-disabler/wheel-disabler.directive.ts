import { Directive, HostListener } from '@angular/core';

/**
 * Directive for loading spinner component
 */
@Directive({ selector: '[tmDisableWheel]' })
export class WheelDisablerDirective {

  @HostListener('wheel', ['$event'])
  onWheel(e: WheelEvent): void {
    (e.target as HTMLElement).blur();
  }

}
