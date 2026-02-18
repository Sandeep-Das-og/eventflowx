import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from './api.service';
import { EventItem } from './models';
import { keycloak } from './auth/keycloak';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  readonly bookingForm = this.fb.group({
    customerName: ['', [Validators.required]],
    eventName: ['', [Validators.required]]
  });

  readonly walletForm = this.fb.group({
    userId: ['', [Validators.required]]
  });

  readonly creditForm = this.fb.group({
    userId: ['', [Validators.required]],
    amount: [50, [Validators.required, Validators.min(1)]]
  });

  bookingId = '';
  walletBalance: number | null = null;
  message = '';
  error = '';
  loading = false;
  events: EventItem[] = [];

  constructor(
    private readonly fb: FormBuilder,
    private readonly api: ApiService
  ) {}

  submitBooking(): void {
    if (this.bookingForm.invalid) {
      return;
    }

    this.resetNotices();
    this.loading = true;

    this.api.createBooking(this.bookingForm.getRawValue() as { customerName: string; eventName: string }).subscribe({
      next: (res) => {
        this.bookingId = res.bookingId;
        this.message = `Booking created: ${res.bookingId}`;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Failed to create booking';
        this.loading = false;
      }
    });
  }

  fetchWallet(): void {
    if (this.walletForm.invalid) {
      return;
    }

    const userId = this.walletForm.getRawValue().userId ?? '';
    this.resetNotices();
    this.loading = true;

    this.api.getWallet(userId).subscribe({
      next: (res) => {
        this.walletBalance = res.balance;
        this.message = `Wallet for ${res.userId}: ${res.balance}`;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Wallet not found or unavailable';
        this.loading = false;
      }
    });
  }

  creditWallet(): void {
    if (this.creditForm.invalid) {
      return;
    }

    const values = this.creditForm.getRawValue();
    const userId = values.userId ?? '';
    const amount = Number(values.amount ?? 0);

    this.resetNotices();
    this.loading = true;

    this.api.creditWallet(userId, amount).subscribe({
      next: (res) => {
        this.walletBalance = res.balance;
        this.message = `Credited ${amount}. New balance for ${res.userId}: ${res.balance}`;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Failed to credit wallet';
        this.loading = false;
      }
    });
  }

  private resetNotices(): void {
    this.message = '';
    this.error = '';
  }

  loadEvents(): void {
    this.resetNotices();
    this.loading = true;
    this.api.listEvents().subscribe({
      next: (res) => {
        this.events = res;
        this.message = `Loaded ${res.length} events`;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Failed to load events';
        this.loading = false;
      }
    });
  }

  get username(): string {
    const parsed = keycloak.tokenParsed as { preferred_username?: string } | undefined;
    return parsed?.preferred_username ?? 'authenticated-user';
  }

  logout(): void {
    keycloak.logout({ redirectUri: 'http://localhost:6767' });
  }
}
