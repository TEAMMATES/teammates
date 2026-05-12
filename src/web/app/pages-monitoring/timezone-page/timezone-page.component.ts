import { KeyValuePipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { TimezoneService } from '../../../services/timezone.service';
import { TimeZones } from '../../../types/api-output';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';

/**
 * Timezone listing page for admin use.
 */
@Component({
  selector: 'tm-timezone-page',
  templateUrl: './timezone-page.component.html',
  imports: [LoadingSpinnerDirective, KeyValuePipe],
})
export class TimezonePageComponent implements OnInit {
  private timezoneService = inject(TimezoneService);

  javaTzVersion = '';
  javaTimezones: Record<string, number> = {};
  momentTzVersion = '';
  momentTimezones: Record<string, number> = {};

  isTimezonesLoading = false;

  ngOnInit(): void {
    this.momentTzVersion = this.timezoneService.getTzVersion();
    this.momentTimezones = this.timezoneService.getTzOffsets();
    this.timezoneService
      .getTimeZone()
      .pipe(
        finalize(() => {
          this.isTimezonesLoading = false;
        }),
      )
      .subscribe((res: TimeZones) => {
        this.javaTzVersion = res.version;
        this.javaTimezones = res.offsets;
      });
  }
}
