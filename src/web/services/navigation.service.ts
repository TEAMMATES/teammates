import { Injectable } from '@angular/core';
import { NavigationExtras, Router } from '@angular/router';
import { MasqueradeModeService } from './masquerade-mode.service';
import { StatusMessageService } from './status-message.service';

/**
 * Handles navigation to different URLs and setting status messages immediately afterwards.
 *
 * Note that this is only effective for internal URLs as it uses Angular routing.
 */
@Injectable({
  providedIn: 'root',
})
export class NavigationService {

  constructor(private statusMessageService: StatusMessageService,
              private masqueradeModeService: MasqueradeModeService) {}

  encodeParams(params: Record<string, string>): string {
    if (Object.values(params).length === 0) {
      return '';
    }
    return `?${Object.keys(params).map(((key: string): string => `${key}=${encodeURIComponent(params[key])}`))
      .join('&')}`;
  }

  navigateByURL(router: Router, urlWithoutParams: string, params: Record<string, string> = {},
                                        extras: NavigationExtras = {}): Promise<boolean> {
    const masqueradeUser: string = this.masqueradeModeService.getMasqueradeUser();
    if (masqueradeUser !== '') {
      params.user = masqueradeUser;
    }
    return router.navigateByUrl(`${urlWithoutParams}${this.encodeParams(params)}`, extras);
  }

  /**
   * Navigates to the selected URL with URL param encoding
   */
  navigateByURLWithParamEncoding(router: Router,
    urlWithoutParams: string, params: Record<string, string>): Promise<Boolean> {
    return this.navigateByURL(router, urlWithoutParams, params);
  }

  /**
   * Navigates to the selected URL and shows an error message afterwards.
   */
  navigateWithErrorMessage(router: Router, url: string, message: string): void {
    this.navigateByURL(router, url).then(() => {
      this.statusMessageService.showErrorToast(message);
    });
  }

  /**
   * Navigates to the selected URL and shows a success message afterwards.
   */
  navigateWithSuccessMessage(router: Router, url: string, message: string, params: Record<string, string> = {}): void {
    this.navigateByURLWithParamEncoding(router, url, params).then(() => {
      this.statusMessageService.showSuccessToast(message);
    });
  }

}
