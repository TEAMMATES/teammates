import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { map } from 'rxjs/operators';
import { ConfigService } from '../../../services/config.service';

/**
 * Student help page.
 */
@Component({
  selector: 'tm-student-help-page',
  templateUrl: './student-help-page.component.html',
  styleUrls: ['./student-help-page.component.scss'],
  imports: [RouterLink],
})
export class StudentHelpPageComponent {
  private readonly configService = inject(ConfigService);

  readonly supportEmail = toSignal(this.configService.getConfig().pipe(map((config) => config.supportEmail)), {
    initialValue: '',
  });
}
