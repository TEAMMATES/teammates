import { TemplateRef } from '@angular/core';

/**
 * Represents a status message toast.
 */
export interface Toast {
  message: string | TemplateRef<void>;
  delay?: number;
  classes: string;
  autohide: boolean;
}
