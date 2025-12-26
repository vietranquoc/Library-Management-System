import { ConfigItemResponse } from './config-item-response';

export interface ConfigListResponse {
  loanPeriodDays: number;
  finePerDay: number;
  maxBooksPerMember: number;
  allConfigs: ConfigItemResponse[];
}

