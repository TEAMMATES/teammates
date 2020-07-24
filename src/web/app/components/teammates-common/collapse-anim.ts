import { animate, AnimationTriggerMetadata, style, transition, trigger } from '@angular/animations';

/**
 * Directive for collapsing of *ngIf columns.
 */
export const collapseAnim: AnimationTriggerMetadata = trigger('collapseAnim', [
  transition(':leave', [
    style({ height: '*', overflow: 'hidden' }),
    animate('300ms ease-in-out', style({ height: 0, opacity: 0 })),
  ]),
  transition(':enter', [
    style({ height: '0', overflow: 'hidden' }),
    animate('300ms ease-in-out', style({ height: '*', opacity: 1 })),
  ]),
]);
