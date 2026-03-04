import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from './api.service';
import { EventAnalytics, EventItem } from './models';
import { keycloak } from './auth/keycloak';

type GatewayCode = 'RAZORPAY' | 'STRIPE' | 'PAYPAL';
type PaymentMethodType = 'UPI' | 'CREDIT_CARD' | 'DEBIT_CARD';
type ModuleKey = 'overview' | 'events' | 'bookings' | 'admin-create' | 'admin-analytics' | 'payments';

interface NavItem {
  key: ModuleKey;
  label: string;
  icon: string;
  role: 'all' | 'admin' | 'user';
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  message = '';
  error = '';
  loadingEvents = false;
  loadingAnalytics = false;
  submittingEvent = false;
  submittingBooking = false;
  submittingPayment = false;

  currentModule: ModuleKey = 'overview';
  events: EventItem[] = [];
  selectedEvent: EventItem | null = null;
  selectedAnalytics: EventAnalytics | null = null;
  bookingId = '';
  paymentStatus = '';
  showPaymentPage = false;

  selectedGateway: GatewayCode = 'RAZORPAY';
  selectedPaymentMethod: PaymentMethodType = 'UPI';
  readonly navItems: NavItem[] = [
    { key: 'overview', label: 'Overview', icon: 'OV', role: 'all' },
    { key: 'events', label: 'Events', icon: 'EV', role: 'all' },
    { key: 'bookings', label: 'Bookings', icon: 'BK', role: 'user' },
    { key: 'admin-create', label: 'Create Event', icon: 'CE', role: 'admin' },
    { key: 'admin-analytics', label: 'Event Analytics', icon: 'AN', role: 'admin' }
  ];

  readonly moduleNames: Record<ModuleKey, string> = {
    overview: 'Overview',
    events: 'Events',
    bookings: 'Bookings',
    'admin-create': 'Create Event',
    'admin-analytics': 'Event Analytics',
    payments: 'Payment'
  };

  readonly paymentGateways: { code: GatewayCode; name: string; subtitle: string }[] = [
    { code: 'RAZORPAY', name: 'Razorpay', subtitle: 'UPI, cards, net banking, wallets' },
    { code: 'STRIPE', name: 'Stripe', subtitle: 'Cards and global wallets' },
    { code: 'PAYPAL', name: 'PayPal', subtitle: 'Cards and PayPal balance' }
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
    name: ['', [Validators.required, Validators.minLength(3)]],
    city: ['', [Validators.required, Validators.minLength(2)]],
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
    const parsed = keycloak.tokenParsed as { realm_access?: { roles?: string[] } } | undefined;
    const roles = parsed?.realm_access?.roles ?? [];
    return roles.includes('admin');
  }

  get visibleNavItems(): NavItem[] {
    return this.navItems.filter((item) => item.role === 'all' || (item.role === 'admin' ? this.isAdmin : !this.isAdmin));
  }

  get breadcrumb(): string {
    return `Console / ${this.isAdmin ? 'Admin' : 'User'} / ${this.moduleNames[this.currentModule]}`;
  }

  selectModule(key: ModuleKey): void {
    if (key === 'payments' && !this.showPaymentPage) {
      return;
    }
    this.currentModule = key;
    this.clearNotices();
    if ((key === 'events' || key === 'admin-analytics' || key === 'overview') && this.events.length === 0) {
      this.loadEvents();
    }
  }

  loadEvents(): void {
    this.loadingEvents = true;
    this.clearNotices();
    this.api.listEvents().subscribe({
      next: (res) => {
        this.events = res;
        this.loadingEvents = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Failed to load events.';
        this.loadingEvents = false;
      }
    });
  }

  createEvent(): void {
    if (this.eventForm.invalid) {
      this.error = 'Please fix validation errors in the event form.';
      return;
    }

    this.submittingEvent = true;
    this.clearNotices();
    const payload = this.eventForm.getRawValue() as { name: string; city: string; price: number };

    this.api.createEvent(payload).subscribe({
      next: () => {
        this.message = 'Event created successfully.';
        this.eventForm.reset({ name: '', city: '', price: 50 });
        this.submittingEvent = false;
        this.loadEvents();
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Failed to create event.';
        this.submittingEvent = false;
      }
    });
  }

  viewAnalytics(event: EventItem): void {
    this.loadingAnalytics = true;
    this.clearNotices();
    this.selectedAnalytics = null;
    this.api.eventAnalytics(event.id).subscribe({
      next: (res) => {
        this.selectedAnalytics = res;
        this.loadingAnalytics = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Failed to load event analytics.';
        this.loadingAnalytics = false;
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
    this.paymentForm.patchValue({ amount: event.price });
    this.currentModule = 'bookings';
    this.clearNotices();
  }

  submitBooking(): void {
    if (this.bookingForm.invalid) {
      this.error = 'Booking form is incomplete.';
      return;
    }

    this.submittingBooking = true;
    this.clearNotices();
    const payload = this.bookingForm.getRawValue() as { eventId: string; eventName: string; userId: string };
    this.api.createBooking(payload).subscribe({
      next: (res) => {
        this.bookingId = res.bookingId;
        this.showPaymentPage = true;
        this.message = `Booking created (${res.bookingId}). Continue on the Payment page.`;
        this.submittingBooking = false;
        this.currentModule = 'payments';
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Booking failed.';
        this.submittingBooking = false;
      }
    });
  }

  submitPayment(): void {
    if (this.paymentForm.invalid || !this.bookingId) {
      this.error = 'Payment is not ready. Create a booking first.';
      return;
    }

    this.submittingPayment = true;
    this.clearNotices();
    const payment = this.paymentForm.getRawValue() as { amount: number };
    if (!this.isMethodSupported(this.selectedGateway, this.selectedPaymentMethod)) {
      this.error = `${this.selectedPaymentMethod} is not supported on ${this.selectedGateway}.`;
      this.submittingPayment = false;
      return;
    }

    this.api.chargePayment({
      bookingId: this.bookingId,
      userId: this.username,
      paymentGateway: this.selectedGateway,
      paymentMethodType: this.selectedPaymentMethod,
      amount: Number(payment.amount)
    }).subscribe({
      next: (res) => {
        this.paymentStatus = `${res.status} via ${res.paymentGateway} (${res.paymentMethodType})`;
        this.message = `Payment completed for booking ${res.bookingId}.`;
        this.submittingPayment = false;
      },
      error: (err) => {
        this.error = err?.error?.message ?? 'Payment failed.';
        this.submittingPayment = false;
      }
    });
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

  retryCurrentModule(): void {
    if (this.currentModule === 'events' || this.currentModule === 'overview') {
      this.loadEvents();
    } else if (this.currentModule === 'admin-analytics') {
      if (this.selectedEvent) {
        this.viewAnalytics(this.selectedEvent);
      }
    }
  }

  logout(): void {
    keycloak.logout({ redirectUri: 'http://localhost:6767' });
  }

  private clearNotices(): void {
    this.message = '';
    this.error = '';
  }
}
