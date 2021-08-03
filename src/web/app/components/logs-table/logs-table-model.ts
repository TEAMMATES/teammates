import { GeneralLogEntry } from '../../../types/api-output';

/**
 * The model for a row of the logs table.
 */
export interface LogsTableRowModel {
  logEntry: GeneralLogEntry;
  timestampForDisplay: string;
  traceIdForDisplay: string;
  isDetailsExpanded: boolean;
}
