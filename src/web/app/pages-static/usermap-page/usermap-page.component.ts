import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'tm-usermap-page',
  templateUrl: './usermap-page.component.html',
  styleUrls: ['./usermap-page.component.scss'],
})
export class UsermapPageComponent implements OnInit {

  lastUpdated: string;
  nInstitutions: number;
  nCountries: number;

  constructor(private httpClient: HttpClient) {}

  ngOnInit() {
    this.httpClient.get('./assets/data/userMapData.json').subscribe(resObj => {
      const res = resObj as any;
      this.lastUpdated = res.lastUpdated;
      this.nInstitutions = 0;
      const dataTable = [['Country', 'Institutions']];
      for (const country of Object.keys(res.institutes)) {
        const nInstitutionsInCountry = res.institutes[country].length;
        this.nInstitutions += nInstitutionsInCountry;
        dataTable.push([country, nInstitutionsInCountry]);
      }
      this.nCountries = Object.keys(res.institutes).length;

      const { google } = window as any;

      google.charts.load('current', {
        packages: ['geochart'],
      });

      google.charts.setOnLoadCallback(() => {
        const data = google.visualization.arrayToDataTable(dataTable);
        const chart = new google.visualization.GeoChart(document.getElementById('world-map'));
        chart.draw(data, {});
      });

    });
  }

}
