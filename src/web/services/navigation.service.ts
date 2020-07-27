import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
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

  /**
   * Navigates to the selected URL with URL param encoding
   */
  navigateByURLWithParamEncoding(router: Router,
    urlWithoutParams: string, params: Record<string, string>): Promise<Boolean> {
    return router.navigateByUrl(`${urlWithoutParams}${this.encodeParams(params)}`);
  }

  /**
   * Navigates to the selected URL and shows an error message afterwards.
   */
  navigateWithErrorMessage(router: Router, url: string, message: string): void {
    router.navigateByUrl(url).then(() => {
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

  /**
   * Navigates to the selected URL preserving the previous params and shows a success message afterwards.
   */
  navigateWithSuccessMessagePreservingParams(router: Router, url: string, message: string): void {
    router.navigate([url], { queryParamsHandling: 'preserve' }).then(() => {
      this.statusMessageService.showSuccessToast(message);
    });
  }

  /**
   * Opens a new browser window.
   */
  openNewWindow(urlStr: string, params: Record<string, string> = {}): void {
    let url: URL;
    if (urlStr.startsWith('/')) {
      url = new URL(`${window.location.origin}${urlStr}${this.encodeParams(params)}`);
    } else {
      url = new URL(`${urlStr}${this.encodeParams(params)}`);
    }
    if (this.masqueradeModeService.isInMasqueradingMode()) {
      url.searchParams.set('user', this.masqueradeModeService.getMasqueradeUser());
    }
    window.open(url.toString().replace(/\+/g, '%20'));
  }
}
