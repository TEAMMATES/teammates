import {
  ComponentRef,
  Directive,
  Input,
  TemplateRef,
  ViewContainerRef,
} from '@angular/core';
import { LoadingSpinnerComponent } from './loading-spinner.component';

/**
 * Directive for loading spinner component
 */
@Directive({
  selector: '[tmIsLoading]',
})
export class LoadingSpinnerDirective {
  loadingComponent: ComponentRef<LoadingSpinnerComponent>;

  constructor(private templateRef: TemplateRef<any>,
              private viewContainer: ViewContainerRef) {
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
