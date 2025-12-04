import { Component } from '@angular/core';

/**
 * Features page.
 */
@Component({
  selector: 'tm-features-page',
  templateUrl: './features-page.component.html',
  styleUrls: ['./features-page.component.scss'],
})
export class FeaturesPageComponent {

  /**
   * Alexa: Jump to a section
   */
  scroll(anchor: string): void {
    const el: HTMLElement | null = document.getElementById(anchor);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }
}

