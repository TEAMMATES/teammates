import { ComponentRef, Directive, Input, TemplateRef, ViewContainerRef, inject } from '@angular/core';
import { LoadingSpinnerComponent } from './loading-spinner.component';

/**
 * Directive for loading spinner component
 */
@Directive({ selector: '[tmIsLoading]' })
export class LoadingSpinnerDirective {
  private templateRef = inject<TemplateRef<any>>(TemplateRef);
  private viewContainer = inject(ViewContainerRef);

  loadingComponent: ComponentRef<LoadingSpinnerComponent>;

  constructor() {
    this.loadingComponent = this.viewContainer.createComponent(LoadingSpinnerComponent);
  }

  @Input() set tmIsLoading(loading: boolean) {
    this.viewContainer.clear();

    if (loading) {
      this.loadingComponent = this.viewContainer.createComponent(LoadingSpinnerComponent);
    } else {
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
  }
}
