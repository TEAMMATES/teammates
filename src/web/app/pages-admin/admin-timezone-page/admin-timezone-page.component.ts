import { Component, OnInit } from '@angular/core';
import { HttpRequestService } from '../../../services/http-request.service';
import { TimezoneService } from '../../../services/timezone.service';

interface TimezoneData {
  version: string;
  offsets: { [key: string]: number };
}

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
  javaTimezones: { [key: string]: number } = {};
  momentTzVersion: string = '';
  momentTimezones: { [key: string]: number } = {};

  constructor(private timezoneService: TimezoneService, private httpRequestService: HttpRequestService) {}

  ngOnInit(): void {
    this.momentTzVersion = this.timezoneService.getTzVersion();
    this.momentTimezones = this.timezoneService.getTzOffsets();
    this.httpRequestService.get('/timezone').subscribe((res: TimezoneData) => {
      this.javaTzVersion = res.version;
      this.javaTimezones = res.offsets;
    });
  }

}
