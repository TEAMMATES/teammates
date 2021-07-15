import { SourceLocation } from '../../../types/api-output';

/**
 * The model for a row of the logs table.
 */
export interface LogsTableRowModel {
  timestamp: string;
  severity: string;
  traceId: string;
  sourceLocation: SourceLocation;
  httpStatus?: number;
  responseTime ?: number;
  summary: string;
  details: any;
  userInfo?: any;
  isDetailsExpanded: boolean;
}
