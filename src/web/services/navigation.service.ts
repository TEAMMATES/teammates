import { Location } from '@angular/common';
import { Injectable, inject } from '@angular/core';
import { ActivatedRoute, NavigationEnd, NavigationExtras, Params, Router } from '@angular/router';
import { filter, take } from 'rxjs/operators';
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
  private statusMessageService = inject(StatusMessageService);
  private masqueradeModeService = inject(MasqueradeModeService);
  private activatedRoute = inject(ActivatedRoute);
  private router = inject(Router);
  private location = inject(Location);

  encodeParams(params: Record<string, string>): string {
    if (Object.values(params).length === 0) {
      return '';
    }
    return `?${Object.keys(params)
      .map((key: string): string => `${key}=${encodeURIComponent(params[key])}`)
      .join('&')}`;
  }

  /**
   * Appends queryParams at the end of the current URL.
   */
  changeBrowserUrl(queryParams: Params): void {
    const newUrl = this.router.createUrlTree([], { relativeTo: this.activatedRoute, queryParams }).toString();

    this.location.go(newUrl);
  }

  navigateByURL(
    urlWithoutParams: string,
    params: Record<string, string> = {},
    extras: NavigationExtras = {},
  ): Promise<boolean> {
    const masqueradeAccountId: string = this.masqueradeModeService.getMasqueradeAccountId();
    if (masqueradeAccountId !== '') {
      params['masqueradeaccountid'] = masqueradeAccountId;
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

  /**
   * Navigates to the previous page in the browser history.
   */
  navigateBack(): void {
    this.location.back();
  }

  /**
   * Navigates to the previous page in the browser history and shows a success message afterwards.
   *
   * The message is shown only after the navigation completes so that it is not cleared by the
   * navigation itself.
   */
  navigateBackWithSuccessMessage(message: string): void {
    this.router.events
      .pipe(
        filter((event): event is NavigationEnd => event instanceof NavigationEnd),
        take(1),
      )
      .subscribe(() => {
        this.statusMessageService.showSuccessToast(message);
      });
    this.location.back();
  }
}
