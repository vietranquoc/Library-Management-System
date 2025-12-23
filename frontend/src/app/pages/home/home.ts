import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  constructor(private readonly router: Router) {}

  onLogout(): void {
    localStorage.removeItem('access_token');
    this.router.navigateByUrl('/auth/login');
  }
}


