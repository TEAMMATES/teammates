import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { StatusMessageService } from './status-message.service';
import {MasqueradeModeService} from "./masquerade-mode.service";

/**
 * Handles navigation to different URLs and setting status messages immediately afterwards.
 * And for opening new windows.
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
    router.navigateByUrl(url).then(() => {
      this.statusMessageService.showErrorMessage(message);
    });
  }

  /**
   * Navigates to the selected URL and shows a success message afterwards.
   */
  navigateWithSuccessMessage(router: Router, url: string, message: string): void {
    router.navigateByUrl(url).then(() => {
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

  openWindow(baseUrl: string, params: { [key: string]: string }, fragment?: string): void {
    this.masqueradeModeService.fetchMasqueradeUser();
    const masqueradeUser: string = this.masqueradeModeService.getMasqueradeUser();
    if (masqueradeUser != '') {
      params['user'] = masqueradeUser;
    }

    if (Object.keys(params).length >= 1) {
      baseUrl = baseUrl.concat('?');
      for (const key of Object.keys(params)) {
        if (params[key]) {
          baseUrl = baseUrl.concat(key + '=' + params[key] + '&');
        }
      }
      baseUrl = baseUrl.slice(0, -1);
    }
    if (fragment) {
      baseUrl = baseUrl.concat('#' + fragment);
    }

    window.open(baseUrl);
  }
}
