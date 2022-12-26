import { Component, OnInit } from '@angular/core';
import { ProgressBarService } from '../../../services/progress-bar.service';

/**
 * Progress bar used to show download progress.
 */
@Component({
  selector: 'tm-progress-bar',
  templateUrl: './progress-bar.component.html',
})
export class ProgressBarComponent implements OnInit {

  progressPercentage: number = 10;

  constructor(private progressBarService: ProgressBarService) { }

  ngOnInit(): void {
    this.getProgress();
  }

  getProgress(): void {
    this.progressBarService.progressPercentage.subscribe((progressPercentage: number) => {
      this.progressPercentage = progressPercentage;
    });
  }
}
