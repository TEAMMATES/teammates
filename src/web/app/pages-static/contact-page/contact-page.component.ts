import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs/operators';
import { ConfigService } from '../../../services/config.service';

/**
 * Contact page.
 */
@Component({
  selector: 'tm-contact-page',
  templateUrl: './contact-page.component.html',
  styleUrls: ['./contact-page.component.scss'],
})
export class ContactPageComponent {
  private readonly configService = inject(ConfigService);

  readonly supportEmail = toSignal(this.configService.getConfig().pipe(map((config) => config.supportEmail)), {
    initialValue: '',
  });
}
