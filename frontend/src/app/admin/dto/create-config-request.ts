import { ConfigDataType } from './config-item-response';

export interface CreateConfigRequest {
  configKey: string;
  configValue: string;
  dataType: ConfigDataType;
  description?: string;
  configGroup?: string;
}

