/**
 * The model for a row of the logs table.
 */
export interface LogsTableRowModel {
  timestamp: string;
  severity: string;
  summary: string;
  details: JSON;
  isDetailsExpanded: boolean;
}