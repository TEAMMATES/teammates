import { TemplateRef } from '@angular/core';

/**
 * Represents a status message toast.
 */
export interface Toast {
  messages: string[] | TemplateRef<any>;
  delay?: number;
  classes: string;
  autohide: boolean;
}
