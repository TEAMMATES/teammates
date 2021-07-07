/**
 * The model for a row of the logs table.
 */
export interface LogsTableRowModel {
  timestamp: string;
  severity: string;
  traceId: string,
  httpStatus?: number;
  responseTime ?: number;
  summary: string;
  details: JSON;
  isDetailsExpanded: boolean;
}
