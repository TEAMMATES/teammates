import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { AuthInfo } from '../../../types/api-output';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { InstructorRequestFormComponent } from './instructor-request-form/instructor-request-form.component';
import { InstructorRequestFormModel } from './instructor-request-form/instructor-request-form-model';

/**
 * Account verification request page.
 */
@Component({
  selector: 'tm-request-page',
  templateUrl: './request-page.component.html',
  styleUrls: ['./request-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [InstructorRequestFormComponent, LoadingSpinnerDirective],
})
export class RequestPageComponent implements OnInit {
  private readonly authService = inject(AuthService);

  readonly isLoading = signal(true);
  readonly authInfo = signal<AuthInfo | null>(null);
  readonly submittedFormData = signal<InstructorRequestFormModel | null>(null);

  ngOnInit(): void {
    this.authService.getAuthUser().subscribe({
      next: (auth: AuthInfo) => {
        this.authInfo.set(auth);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      },
    });
  }

  onRequestSubmitted(data: InstructorRequestFormModel): void {
    this.submittedFormData.set(data);
  }
}
