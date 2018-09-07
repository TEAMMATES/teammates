import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

/**
 * Usermap page.
 */
@Component({
  selector: 'tm-usermap-page',
  templateUrl: './usermap-page.component.html',
  styleUrls: ['./usermap-page.component.scss'],
})
export class UsermapPageComponent implements OnInit {

  lastUpdated: string = '';
  nInstitutions: number = 0;
  nCountries: number = 0;

  constructor(private httpClient: HttpClient) {}

  ngOnInit(): void {
    this.httpClient.get('./assets/data/userMapData.json').subscribe((res: any) => {
      this.lastUpdated = res.lastUpdated;
      this.nInstitutions = 0;
      const dataTable: (string | number)[][] = [['Country', 'Institutions']];
      for (const country of Object.keys(res.institutes)) {
        const nInstitutionsInCountry: number = res.institutes[country].length;
        this.nInstitutions += nInstitutionsInCountry;
        dataTable.push([country, nInstitutionsInCountry]);
      }
      this.nCountries = Object.keys(res.institutes).length;

      const { google }: any = window as any;

      google.charts.load('current', {
        packages: ['geochart'],
      });

      google.charts.setOnLoadCallback(() => {
        const data: any = google.visualization.arrayToDataTable(dataTable);
        const chart: any = new google.visualization.GeoChart(document.getElementById('world-map'));
        chart.draw(data, {});
      });

    });
  }

}
