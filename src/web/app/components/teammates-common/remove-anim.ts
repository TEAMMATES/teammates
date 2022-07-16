import { animate, AnimationTriggerMetadata, style, transition, trigger } from '@angular/animations';

/**
 * Directive for removing *ngFor items.
 */
export const removeAnim: AnimationTriggerMetadata = trigger('removeAnim', [
  transition(':leave', [
    style({ height: '*', opacity: 1, overflow: 'hidden' }),
    animate('300ms ease-in-out', style({ height: 0, opacity: 0 })),
  ]),
]);
