import { Component, OnInit } from '@angular/core';
import { default as index } from '../../../data/index.json';

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

  constructor() {}

  ngOnInit(): void {
    const formatNumber: (n: number) => string = (n: number): string => {
      let number: string = String(n);
      const expression: any = /(\d+)(\d{3})/;
      while (expression.test(number)) {
        number = number.replace(expression, '$1,$2');
      }
      return number;
    };

    const timeElapsed: number = new Date().getTime() - new Date(index.submissionsBaseDate).getTime();
    this.submissionsNumber = formatNumber(
        index.submissionsBase + Math.floor(timeElapsed / 60 / 60 / 1000) * index.submissionsRate);

    this.testimonials = index.testimonials;

    const cycleTestimonial: () => void = (): void => {
      this.testimonialIndex = (this.testimonialIndex + 1) % this.testimonials.length;
      this.testimonial = this.testimonials[this.testimonialIndex];
    };

    cycleTestimonial();
    setInterval(cycleTestimonial, 5000);
  }

}
