import { Component, OnInit } from '@angular/core';
import { TimezoneService } from '../../../services/timezone.service';
import { TimeZones } from '../../../types/api-output';

/**
 * Timezone listing page for admin use.
 */
@Component({
  selector: 'tm-admin-timezone-page',
  templateUrl: './admin-timezone-page.component.html',
  styleUrls: ['./admin-timezone-page.component.scss'],
})
export class AdminTimezonePageComponent implements OnInit {

  javaTzVersion: string = '';
  javaTimezones: Record<string, number> = {};
  momentTzVersion: string = '';
  momentTimezones: Record<string, number> = {};

  constructor(private timezoneService: TimezoneService) {}

  ngOnInit(): void {
    this.momentTzVersion = this.timezoneService.getTzVersion();
    this.momentTimezones = this.timezoneService.getTzOffsets();
    this.timezoneService.getTimeZone().subscribe((res: TimeZones) => {
      this.javaTzVersion = res.version;
      this.javaTimezones = res.offsets;
    });
  }

}
