import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from './api.service';
import { EventAnalytics, EventItem } from './models';
import { keycloak } from './auth/keycloak';

type GatewayCode = 'RAZORPAY' | 'STRIPE' | 'PAYPAL';
type PaymentMethodType = 'UPI' | 'CREDIT_CARD' | 'DEBIT_CARD';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  loading = false;
  message = '';
  error = '';

  events: EventItem[] = [];
  selectedEvent: EventItem | null = null;
  selectedAnalytics: EventAnalytics | null = null;

  bookingId = '';
  paymentAmount = 0;
  paymentStatus = '';
  showPaymentPanel = false;
  selectedGateway: GatewayCode = 'RAZORPAY';
  selectedPaymentMethod: PaymentMethodType = 'UPI';
  readonly paymentGateways: { code: GatewayCode; name: string; subtitle: string }[] = [
    { code: 'RAZORPAY', name: 'Razorpay', subtitle: 'UPI, cards, net banking, wallets' },
    { code: 'STRIPE', name: 'Stripe', subtitle: 'Cards, wallets and global methods' },
    { code: 'PAYPAL', name: 'PayPal', subtitle: 'PayPal, cards, Venmo (region based)' }
  ];
  readonly paymentMethods: { code: PaymentMethodType; label: string }[] = [
    { code: 'UPI', label: 'UPI' },
    { code: 'CREDIT_CARD', label: 'Credit Card' },
    { code: 'DEBIT_CARD', label: 'Debit Card' }
  ];
  readonly gatewayCapabilities: Record<GatewayCode, PaymentMethodType[]> = {
    RAZORPAY: ['UPI', 'CREDIT_CARD', 'DEBIT_CARD'],
    STRIPE: ['CREDIT_CARD', 'DEBIT_CARD'],
    PAYPAL: ['CREDIT_CARD', 'DEBIT_CARD']
  };

  readonly eventForm = this.fb.group({
    name: ['', [Validators.required]],
    city: ['', [Validators.required]],
    price: [50, [Validators.required, Validators.min(1)]]
  });

  readonly bookingForm = this.fb.group({
    eventId: ['', [Validators.required]],
    eventName: ['', [Validators.required]],
    userId: ['', [Validators.required]]
  });

  readonly paymentForm = this.fb.group({
    amount: [0, [Validators.required, Validators.min(1)]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly api: ApiService
  ) {}

  ngOnInit(): void {
    this.bookingForm.patchValue({ userId: this.username });
    this.loadEvents();
  }

  get username(): string {
    const parsed = keycloak.tokenParsed as { preferred_username?: string } | undefined;
    return parsed?.preferred_username ?? 'authenticated-user';
  }

  get isAdmin(): boolean {
    const parsed = keycloak.tokenParsed as { realm_access?: { roles?: string[] }; preferred_username?: string } | undefined;
    const roles = parsed?.realm_access?.roles ?? [];
    return roles.includes('admin') || parsed?.preferred_username === 'eventflowx-admin';
  }

  loadEvents(): void {
    this.loading = true;
    this.resetNotices();
    this.api.listEvents().subscribe({
      next: (res) => {
        this.events = res;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Failed to load events';
        this.loading = false;
      }
    });
  }

  createEvent(): void {
    if (this.eventForm.invalid) {
      return;
    }

    this.loading = true;
    this.resetNotices();

    const payload = this.eventForm.getRawValue() as { name: string; city: string; price: number };
    this.api.createEvent(payload).subscribe({
      next: () => {
        this.message = 'Event created successfully';
        this.eventForm.reset({ name: '', city: '', price: 50 });
        this.loadEvents();
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Failed to create event';
        this.loading = false;
      }
    });
  }

  viewAnalytics(event: EventItem): void {
    this.loading = true;
    this.resetNotices();

    this.api.eventAnalytics(event.id).subscribe({
      next: (res) => {
        this.selectedAnalytics = res;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Failed to load analytics';
        this.loading = false;
      }
    });
  }

  startBooking(event: EventItem): void {
    this.selectedEvent = event;
    this.bookingForm.patchValue({
      eventId: event.id,
      eventName: event.name,
      userId: this.username
    });
    this.paymentAmount = event.price;
    this.paymentForm.patchValue({ amount: event.price });
  }

  submitBooking(): void {
    if (this.bookingForm.invalid) {
      return;
    }

    this.loading = true;
    this.resetNotices();

    const payload = this.bookingForm.getRawValue() as { eventId: string; eventName: string; userId: string };
    this.api.createBooking(payload).subscribe({
      next: (res) => {
        this.bookingId = res.bookingId;
        this.showPaymentPanel = true;
        this.message = `Booking created (${res.bookingId}). Complete payment below.`;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Booking failed';
        this.loading = false;
      }
    });
  }

  submitPayment(gatewayCode?: GatewayCode): void {
    if (this.paymentForm.invalid || !this.bookingId) {
      return;
    }

    this.loading = true;
    this.resetNotices();

    const payment = this.paymentForm.getRawValue() as { amount: number };
    const provider = gatewayCode ?? this.selectedGateway;
    if (!this.isMethodSupported(provider, this.selectedPaymentMethod)) {
      this.error = `${this.selectedPaymentMethod} is not supported on ${provider}`;
      this.loading = false;
      return;
    }

    this.api.chargePayment({
      bookingId: this.bookingId,
      userId: this.username,
      paymentGateway: provider,
      paymentMethodType: this.selectedPaymentMethod,
      amount: Number(payment.amount)
    }).subscribe({
      next: (res) => {
        this.selectedGateway = provider;
        this.paymentStatus = `${res.status} via ${res.paymentGateway} (${res.paymentMethodType}) - charged ${res.chargedAmount}`;
        this.message = `Payment successful for booking ${res.bookingId}`;
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Payment failed';
        this.loading = false;
      }
    });
  }

  logout(): void {
    keycloak.logout({ redirectUri: 'http://localhost:6767' });
  }

  selectGateway(gateway: GatewayCode): void {
    this.selectedGateway = gateway;
    if (!this.isMethodSupported(gateway, this.selectedPaymentMethod)) {
      this.selectedPaymentMethod = this.gatewayCapabilities[gateway][0];
    }
  }

  selectPaymentMethod(method: PaymentMethodType): void {
    this.selectedPaymentMethod = method;
  }

  isMethodSupported(gateway: GatewayCode, method: PaymentMethodType): boolean {
    return this.gatewayCapabilities[gateway].includes(method);
  }

  private resetNotices(): void {
    this.message = '';
    this.error = '';
  }
}
