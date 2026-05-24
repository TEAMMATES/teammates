import { ViewportScroller } from '@angular/common';
import { Injectable, inject } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class PageScrollService {
  private readonly viewportScroller = inject(ViewportScroller);

  constructor() {
    this.viewportScroller.setOffset([0, 70]);
  }

  scrollToAnchor(anchorId: string): void {
    this.viewportScroller.scrollToAnchor(anchorId, { behavior: 'smooth' });
  }
}
