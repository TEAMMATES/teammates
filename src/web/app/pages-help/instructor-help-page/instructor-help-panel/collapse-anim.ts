import { animate, AnimationTriggerMetadata, state, style, transition, trigger } from '@angular/animations';

/**
 * Directive for collapsing of div with overflow:hidden setting.
 */
export const collapseAnim: AnimationTriggerMetadata = trigger('collapseAnim', [
  state('collapsed', style({ height: '0px', minHeight: '0' })),
  state('expanded', style({ height: '*' })),
  transition('expanded <=> collapsed', animate('300ms ease-in-out')),
]);
