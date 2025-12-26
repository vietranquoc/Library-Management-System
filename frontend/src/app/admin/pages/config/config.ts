import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminService } from '../../services/admin.service';
import { UpdateConfigRequest } from '../../dto/update-config-request';
import { ConfigResponse } from '../../dto/config-response';
import { ConfigListResponse } from '../../dto/config-list-response';
import { ConfigItemResponse, ConfigDataType } from '../../dto/config-item-response';
import { CreateConfigRequest } from '../../dto/create-config-request';
import { UpdateConfigValueRequest } from '../../dto/update-config-value-request';
import { AdminSidebar } from '../../../shared/components/admin-sidebar/admin-sidebar';

@Component({
  selector: 'app-admin-config',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, AdminSidebar],
  templateUrl: './config.html',
  styleUrl: './config.scss',
})
export class AdminConfig implements OnInit {
  // Tab management
  activeTab: 'basic' | 'all' = 'basic';

  // Basic config
  loading = false;
  loadingData = false;
  errorMessage = '';
  successMessage = '';
  config: ConfigResponse | null = null;

  form = new FormGroup({
    loanPeriodDays: new FormControl<number>(7, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(1)],
    }),
    finePerDay: new FormControl<number>(10000, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(0)],
    }),
    maxBooksPerMember: new FormControl<number>(5, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(1)],
    }),
  });

  // All configs management
  allConfigs: ConfigItemResponse[] = [];
  loadingAllConfigs = false;
  showAddForm = false;
  editingConfig: ConfigItemResponse | null = null;

  addConfigForm = new FormGroup({
    configKey: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required, Validators.pattern(/^[a-z0-9.]+$/)],
    }),
    configValue: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    dataType: new FormControl<ConfigDataType>('STRING', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    description: new FormControl<string>(''),
    configGroup: new FormControl<string>(''),
  });

  editConfigForm = new FormGroup({
    configValue: new FormControl<string>('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  dataTypes: ConfigDataType[] = ['STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN'];

  constructor(private readonly adminService: AdminService) {}

  ngOnInit(): void {
    this.loadConfig();
  }

  // ========== Basic Config Methods ==========

  loadConfig(): void {
    this.loadingData = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.adminService.getConfig().subscribe({
      next: (res) => {
        this.loadingData = false;
        this.config = res.data || null;
        if (this.config) {
          this.form.patchValue({
            loanPeriodDays: this.config.loanPeriodDays,
            finePerDay: this.config.finePerDay,
            maxBooksPerMember: this.config.maxBooksPerMember,
          });
        }
      },
      error: (err) => {
        this.loadingData = false;
        this.errorMessage = this.getErrorMessage(err, 'Không thể tải cấu hình hệ thống.');
      },
    });
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;
    const payload: UpdateConfigRequest = {
      loanPeriodDays: this.form.value.loanPeriodDays!,
      finePerDay: this.form.value.finePerDay!,
      maxBooksPerMember: this.form.value.maxBooksPerMember!,
    };

    this.adminService.updateConfig(payload).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMessage = res.message || 'Cập nhật cấu hình thành công';
        this.config = res.data || null;
        setTimeout(() => {
          this.loadConfig();
          if (this.activeTab === 'all') {
            this.loadAllConfigs();
          }
        }, 1000);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = this.getErrorMessage(err, 'Cập nhật cấu hình thất bại. Vui lòng thử lại.');
      },
    });
  }

  // ========== All Configs Methods ==========

  loadAllConfigs(): void {
    this.loadingAllConfigs = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.adminService.getAllConfigs().subscribe({
      next: (res) => {
        this.loadingAllConfigs = false;
        this.allConfigs = res.data?.allConfigs || [];
      },
      error: (err) => {
        this.loadingAllConfigs = false;
        this.errorMessage = this.getErrorMessage(err, 'Không thể tải danh sách cấu hình.');
      },
    });
  }

  switchTab(tab: 'basic' | 'all'): void {
    this.activeTab = tab;
    if (tab === 'all' && this.allConfigs.length === 0) {
      this.loadAllConfigs();
    }
  }

  showAddConfigForm(): void {
    this.showAddForm = true;
    this.editingConfig = null;
    this.addConfigForm.reset({
      configKey: '',
      configValue: '',
      dataType: 'STRING',
      description: '',
      configGroup: '',
    });
  }

  cancelAddForm(): void {
    this.showAddForm = false;
    this.addConfigForm.reset();
  }

  onSubmitAddConfig(): void {
    if (this.addConfigForm.invalid) {
      this.addConfigForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    const payload: CreateConfigRequest = {
      configKey: this.addConfigForm.value.configKey!,
      configValue: this.addConfigForm.value.configValue!,
      dataType: this.addConfigForm.value.dataType!,
      description: this.addConfigForm.value.description || undefined,
      configGroup: this.addConfigForm.value.configGroup || undefined,
    };

    this.adminService.createConfig(payload).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMessage = res.message || 'Tạo cấu hình thành công';
        this.showAddForm = false;
        this.addConfigForm.reset();
        this.loadAllConfigs();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = this.getErrorMessage(err, 'Tạo cấu hình thất bại. Vui lòng thử lại.');
      },
    });
  }

  editConfig(config: ConfigItemResponse): void {
    this.editingConfig = config;
    this.showAddForm = false;
    this.editConfigForm.patchValue({
      configValue: config.configValue,
    });
  }

  cancelEdit(): void {
    this.editingConfig = null;
    this.editConfigForm.reset();
  }

  onSubmitEditConfig(): void {
    if (!this.editingConfig || this.editConfigForm.invalid) {
      return;
    }

    this.loading = true;
    const payload: UpdateConfigValueRequest = {
      configValue: this.editConfigForm.value.configValue!,
    };

    this.adminService.updateConfigValue(this.editingConfig.configKey, payload).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMessage = res.message || 'Cập nhật cấu hình thành công';
        this.editingConfig = null;
        this.editConfigForm.reset();
        this.loadAllConfigs();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = this.getErrorMessage(err, 'Cập nhật cấu hình thất bại. Vui lòng thử lại.');
      },
    });
  }

  deleteConfig(config: ConfigItemResponse): void {
    if (!confirm(`Bạn có chắc chắn muốn xóa cấu hình "${config.configKey}"?`)) {
      return;
    }

    this.loading = true;
    this.adminService.deleteConfig(config.configKey).subscribe({
      next: (res) => {
        this.loading = false;
        this.successMessage = res.message || 'Xóa cấu hình thành công';
        this.loadAllConfigs();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = this.getErrorMessage(err, 'Xóa cấu hình thất bại. Vui lòng thử lại.');
      },
    });
  }

  getConfigsByGroup(): { [key: string]: ConfigItemResponse[] } {
    const grouped: { [key: string]: ConfigItemResponse[] } = {};
    this.allConfigs.forEach((config) => {
      const group = config.configGroup || 'other';
      if (!grouped[group]) {
        grouped[group] = [];
      }
      grouped[group].push(config);
    });
    return grouped;
  }

  // ========== Helper Methods ==========

  private getErrorMessage(err: any, fallback: string): string {
    return (
      err?.error?.message ||
      err?.error?.errors?.[0]?.defaultMessage ||
      err?.message ||
      fallback
    );
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }).format(amount);
  }

  formatConfigValue(config: ConfigItemResponse): string {
    if (config.dataType === 'DECIMAL') {
      const num = parseFloat(config.configValue);
      if (!isNaN(num)) {
        return num.toLocaleString('vi-VN');
      }
    }
    return config.configValue;
  }
}
