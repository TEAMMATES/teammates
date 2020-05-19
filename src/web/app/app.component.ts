import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Event, NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { MasqueradeModeService } from '../services/masquerade-mode.service';

/**
 * Root application page.
 */
@Component({
  selector: 'tm-root',
  template: '<router-outlet></router-outlet>',
})
export class AppComponent implements OnInit {
  constructor(private router: Router, private route: ActivatedRoute,
              private masqueradeModeService: MasqueradeModeService) {}

  ngOnInit(): void {
    // sync the URL param `user` with the masqueraded userID in the service
    this.router.events
        .pipe(filter((event: Event) => event instanceof NavigationEnd))
        .subscribe((event: Event) => {
          if (!this.masqueradeModeService.isInMasqueradingMode()) {
            return;
          }

          const currMasqueradeUser: string = this.masqueradeModeService.getMasqueradeUser();
          if (currMasqueradeUser !==
              new URL(environment.frontendUrl + (event as NavigationEnd).urlAfterRedirects).searchParams.get('user')) {
            this.router.navigate([], {
              relativeTo: this.route,
              replaceUrl: true,
              queryParams: { user: currMasqueradeUser },
              queryParamsHandling: 'merge',
            });
          }

        });
  }
}
