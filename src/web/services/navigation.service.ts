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
   * Opens a new browser window.
   */
  openNewWindow(urlStr: string): void {
    const url: URL = new URL(urlStr);
    if (this.masqueradeModeService.isInMasqueradingMode()) {
      url.searchParams.set('user', this.masqueradeModeService.getMasqueradeUser());
    }
    window.open(url.toString());
  }
}
