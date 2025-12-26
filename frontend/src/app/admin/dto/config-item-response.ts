export type ConfigDataType = 'STRING' | 'INTEGER' | 'DECIMAL' | 'BOOLEAN';

export interface ConfigItemResponse {
  id: number;
  configKey: string;
  configValue: string;
  dataType: ConfigDataType;
  description?: string;
  configGroup?: string;
}

