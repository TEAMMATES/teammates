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

  encodeParams(params: {[key: string]: string}): string {
    return Object.keys(params).map(((key: string): string => `${key}=${encodeURIComponent(params[key])}`))
      .join('&');
  }

  /**
   * Navigates to the selected URL with URL param encoding
   */
  navigateByURLWithParamEncoding(router: Router,
    urlWithoutParams: string, params: {[key: string]: string}): Promise<Boolean> {
    return router.navigateByUrl(`${urlWithoutParams}?${this.encodeParams(params)}`);
  }

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

  /**
   * Navigates to the selected URL and shows successful message with URL param encoding
   */
  navigateWithSuccessMessageWithParamEncoding(router: Router, urlWithoutParams: string,
    params: {[key: string]: string}, message: string): void {
    return this.navigateWithSuccessMessage(router, `${urlWithoutParams}?${this.encodeParams(params)}`, message);
  }

  /**
   * Opens a new browser window.
   */
  openNewWindow(urlStr: string, params: {[key: string]: string}): void {
    const url: URL = new URL(`${urlStr}?${this.encodeParams(params)}`);
    if (this.masqueradeModeService.isInMasqueradingMode()) {
      url.searchParams.set('user', this.masqueradeModeService.getMasqueradeUser());
    }
    window.open(url.toString());
  }
}
