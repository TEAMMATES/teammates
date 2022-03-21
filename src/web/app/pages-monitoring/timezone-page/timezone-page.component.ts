import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { TimezoneService } from '../../../services/timezone.service';
import { TimeZones } from '../../../types/api-output';

/**
 * Timezone listing page for admin use.
 */
@Component({
  selector: 'tm-timezone-page',
  templateUrl: './timezone-page.component.html',
  styleUrls: ['./timezone-page.component.scss'],
})
export class TimezonePageComponent implements OnInit {

  javaTzVersion: string = '';
  javaTimezones: Record<string, number> = {};
  momentTzVersion: string = '';
  momentTimezones: Record<string, number> = {};

  isTimezonesLoading: boolean = false;

  constructor(private timezoneService: TimezoneService) {}

  ngOnInit(): void {
    this.momentTzVersion = this.timezoneService.getTzVersion();
    this.momentTimezones = this.timezoneService.getTzOffsets();
    this.timezoneService.getTimeZone()
        .pipe(finalize(() => {
          this.isTimezonesLoading = false;
        }))
        .subscribe((res: TimeZones) => {
          this.javaTzVersion = res.version;
          this.javaTimezones = res.offsets;
        });
  }
}
