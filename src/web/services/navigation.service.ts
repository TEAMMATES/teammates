import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { MasqueradeModeService } from './masquerade-mode.service';
import { StatusMessageService } from './status-message.service';

/**
 * Handles navigation to different URLs and setting status messages immediately afterwards.
 * And for opening new windows.
 * This service will user MasqueradeModeService to check whether it is in masquerade mode.
 *
 * Note that this is only effective for internal URLs as it uses Angular routing.
 */
@Injectable({
  providedIn: 'root',
})
export class NavigationService {

  constructor(private statusMessageService: StatusMessageService,
              private masqueradeModeService: MasqueradeModeService) {}

  /**
   * Navigates to the selected URL and shows an error message afterwards.
   */
  navigateWithErrorMessage(router: Router, url: string, message: string): void {
    this.navigateToUrl(router, url).then(() => {
      this.statusMessageService.showSuccessMessage(message);
    });
  }

  /**
   * Navigates to the selected URL and shows a success message afterwards.
   */
  navigateWithSuccessMessage(router: Router, url: string, message: string): void {
    this.navigateToUrl(router, url).then(() => {
      this.statusMessageService.showSuccessMessage(message);
    });
  }

  /**
   * Navigates to the selected URL preserving the previous params and shows a success message afterwards.
   */
  navigateWithSuccessMessagePreservingParams(router: Router, url: string, message: string): void {
    router.navigate([url], { queryParamsHandling: 'preserve' }).then(() => {
      this.statusMessageService.showSuccessMessage(message);
    });
  }

  /**
   * Open a new window with url and params provided, and optional fragment.
   */
  openWindow(baseUrl: string, params: { [key: string]: string }, fragment?: string): void {
    let url: string = baseUrl;
    const equalSign: string = '=';
    const andSigh: string = '&';
    const hashSign: string = '#';
    const masqueradeUser: string = this.masqueradeModeService.getMasqueradeUser();
    if (masqueradeUser !== '') {
      const user: string = 'user';
      params[user] = masqueradeUser;
    }

    if (Object.keys(params).length >= 1) {
      url = url.concat('?');
      for (const key of Object.keys(params)) {
        if (params[key]) {
          url = url.concat(key + equalSign + params[key] + andSigh);
        }
      }
      url = url.slice(0, -1);
    }
    if (fragment) {
      url = url.concat(hashSign + fragment);
    }

    window.open(url);
  }

  /**
   * Navigates to the selected URL providing optional params.
   * Also check if it is in masquerade mode.
   */
  navigateToUrl(router: Router, url: string, params?: { [key: string]: string }): Promise<boolean> {
    const masqueradeUser: string = this.masqueradeModeService.getMasqueradeUser();
    if (masqueradeUser !== '') {
      if (params) {
        const userKey: string = 'user';
        params[userKey] = masqueradeUser;
        return router.navigate([url], { queryParams: params });
      }
      return router.navigate([url], { queryParams: { user: masqueradeUser } });
    }
    if (params) {
      return router.navigate([url], { queryParams: params });
    }
    return router.navigateByUrl(url);
  }
}
