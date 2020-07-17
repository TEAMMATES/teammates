import { animate, AnimationTriggerMetadata, state, style, transition, trigger } from '@angular/animations';

/**
 * Directive for collapsing of div with overflow:hidden setting.
 */
export const collapseAnim: AnimationTriggerMetadata = trigger('collapseAnim', [
  state('collapsed', style({ height: '0px', display: 'none' })),
  state('expanded', style({ height: '*', display: 'block' })),
  transition('expanded <=> collapsed', [
    style({ display: 'block' }),
    animate('300ms ease-in-out'),
  ]),
]);
