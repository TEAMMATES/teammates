import { Component, Input, OnInit } from '@angular/core';

/**
 * Displays a contribution value relative to baseline of 100.
 */
@Component({
  selector: 'tm-contribution',
  templateUrl: './contribution.component.html',
  styleUrls: ['./contribution.component.scss'],
})
export class ContributionComponent implements OnInit {

  @Input() value: number = 100;
  @Input() diffOnly: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

}
