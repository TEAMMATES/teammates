import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

/**
 * Handles navigation to different URLs.
 *
 * This service is necessary due to the implementation of Angular 6's router which by default ignores event's keys
 * (i.e. without any modification, CTRL + click will not open a link in new tab).
 */
@Injectable()
export class NavigationService {

  constructor() {}

  /**
   * Navigates to the selected URL while taking into account CTRL/CMD/SHIFT keys pressed.
   */
  public navigateTo(router: Router, url: string, event: any, windowParam?: any): void {
    if (event.metaKey || event.shiftKey || event.ctrlKey) {
      (windowParam || window).open(url);
    } else {
      router.navigateByUrl(url);
    }
  }

}
