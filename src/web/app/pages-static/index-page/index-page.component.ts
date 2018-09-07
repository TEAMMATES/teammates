import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NavigationService } from '../../navigation.service';

/**
 * Index page.
 */
@Component({
  selector: 'tm-index-page',
  templateUrl: './index-page.component.html',
  styleUrls: ['./index-page.component.scss'],
})
export class IndexPageComponent implements OnInit {

  testimonial: any;
  submissionsNumber: string = '10,000,000+';
  private testimonials: any[] = [];
  private testimonialIndex: number = -1;

  constructor(private router: Router, private navigationService: NavigationService,
      private httpClient: HttpClient) {}

  /**
   * Navigates user to another page.
   */
  navigateTo(url: string, event: any): void {
    this.navigationService.navigateTo(this.router, url, event);
  }

  ngOnInit(): void {
    this.httpClient.get('./assets/data/index.json').subscribe((res: any) => {
      const formatNumber: (n: number) => string = (n: number): string => {
        let number: string = String(n);
        const expression: any = /(\d+)(\d{3})/;
        while (expression.test(number)) {
          number = number.replace(expression, '$1,$2');
        }
        return number;
      };

      const timeElapsed: number = new Date().getTime() - new Date(res.submissionsBaseDate).getTime();
      this.submissionsNumber = formatNumber(
          res.submissionsBase + Math.floor(timeElapsed / 60 / 60 / 1000) * res.submissionsRate);

      this.testimonials = res.testimonials;

      const cycleTestimonial: () => void = (): void => {
        this.testimonialIndex = (this.testimonialIndex + 1) % this.testimonials.length;
        this.testimonial = this.testimonials[this.testimonialIndex];
      };

      cycleTestimonial();
      setInterval(cycleTestimonial, 5000);
    });
  }

}
