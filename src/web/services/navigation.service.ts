import { Location } from '@angular/common';
import { Injectable } from '@angular/core';
import { ActivatedRoute, NavigationExtras, Params, Router } from '@angular/router';
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
              private masqueradeModeService: MasqueradeModeService,
              private activatedRoute: ActivatedRoute,
              private router: Router,
              private location: Location,
  ) {}

  encodeParams(params: Record<string, string>): string {
    if (Object.values(params).length === 0) {
      return '';
    }
    return `?${Object.keys(params).map(((key: string): string => `${key}=${encodeURIComponent(params[key])}`))
      .join('&')}`;
  }

  /**
   * Appends queryParams at the end of the current URL.
   */
  changeBrowserUrl(queryParams: Params): void {
    const newUrl = this.router.createUrlTree(
        [],
        { relativeTo: this.activatedRoute, queryParams },
    ).toString();

    this.location.go(newUrl);
  }

  navigateByURL(urlWithoutParams: string, params: Record<string, string> = {},
                extras: NavigationExtras = {}): Promise<boolean> {
    const masqueradeUser: string = this.masqueradeModeService.getMasqueradeUser();
    if (masqueradeUser !== '') {
      params['user'] = masqueradeUser;
    }
    return this.router.navigateByUrl(`${urlWithoutParams}${this.encodeParams(params)}`, extras);
  }

  /**
   * Navigates to the selected URL with URL param encoding
   */
  navigateByURLWithParamEncoding(urlWithoutParams: string, params: Record<string, string>): Promise<boolean> {
    return this.navigateByURL(urlWithoutParams, params);
  }

  /**
   * Navigates to the selected URL and shows an error message afterwards.
   */
  navigateWithErrorMessage(url: string, message: string): void {
    this.navigateByURL(url).then(() => {
      this.statusMessageService.showErrorToast(message);
    });
  }

  /**
   * Navigates to the selected URL and shows a success message afterwards.
   */
  navigateWithSuccessMessage(url: string, message: string, params: Record<string, string> = {}): void {
    this.navigateByURLWithParamEncoding(url, params).then(() => {
      this.statusMessageService.showSuccessToast(message);
    });
  }
}
