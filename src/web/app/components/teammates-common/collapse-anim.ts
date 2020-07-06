import { animate, AnimationTriggerMetadata, state, style, transition, trigger } from '@angular/animations';

/**
 * Directive for collapsing of *ngIf columns.
 */
export const collapseAnim: AnimationTriggerMetadata = trigger('collapseAnim', [
  state('*', style({ overflow: 'hidden' })),
  state('void', style({ overflow: 'hidden' })),
  transition(':leave', [
    style({ height: '*' }),
    animate('300ms ease-in-out', style({ height: 0, opacity: 0 })),
  ]),
  transition(':enter', [
    style({ height: '0' }),
    animate('300ms ease-in-out', style({ height: '*', opacity: 1 })),
  ]),
]);
