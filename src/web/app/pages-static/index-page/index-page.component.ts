import { Component, OnInit } from '@angular/core';
import { default as indexData } from '../../../data/index.json';
import { RouterLink } from '@angular/router';

/**
 * Testimonial from index.json
 */
interface Testimonial {
  content: string;
  author: string;
}

/**
 * Index page.
 */
@Component({
  selector: 'tm-index-page',
  templateUrl: './index-page.component.html',
  styleUrls: ['./index-page.component.scss'],
  imports: [RouterLink],
})
export class IndexPageComponent implements OnInit {
  testimonial: Testimonial | null = null;
  submissionsNumber = '10,000,000+';
  private testimonials: Testimonial[] = [];
  private testimonialIndex = -1;

  ngOnInit(): void {
    const formatNumber = (n: number): string => {
      let number = String(n);
      const expression = /(\d+)(\d{3})/;
      while (expression.test(number)) {
        number = number.replace(expression, '$1,$2');
      }
      return number;
    };

    const timeElapsed: number = new Date().getTime() - new Date(indexData.submissionsBaseDate).getTime();
    this.submissionsNumber = formatNumber(
      indexData.submissionsBase + Math.floor(timeElapsed / 60 / 60 / 1000) * indexData.submissionsRate,
    );

    this.testimonials = indexData.testimonials;

    const cycleTestimonial = (): void => {
      this.testimonialIndex = (this.testimonialIndex + 1) % this.testimonials.length;
      this.testimonial = this.testimonials[this.testimonialIndex];
    };

    cycleTestimonial();
    setInterval(cycleTestimonial, 5000);
  }
}
