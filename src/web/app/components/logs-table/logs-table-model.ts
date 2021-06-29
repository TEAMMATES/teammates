/**
 * The model for a row of the logs table.
 */
export interface LogsTableRowModel {
  timestamp: number;
  severity: string;
  summary: string;
  logDetail: JSON;
  isDetailsExpanded: boolean;
}