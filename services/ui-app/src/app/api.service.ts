import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import {
  BookingRequest,
  BookingResponse,
  ChargePaymentRequest,
  ChargePaymentResponse,
  CreateEventRequest,
  EventAnalytics,
  EventItem
} from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) {}

  listEvents(): Observable<EventItem[]> {
    return this.http.get<EventItem[]>(`${this.baseUrl}/events`);
  }

  createEvent(payload: CreateEventRequest): Observable<EventItem> {
    return this.http.post<EventItem>(`${this.baseUrl}/admin/events`, payload);
  }

  eventAnalytics(eventId: string): Observable<EventAnalytics> {
    return this.http.get<EventAnalytics>(`${this.baseUrl}/admin/events/${encodeURIComponent(eventId)}/analytics`);
  }

  createBooking(payload: BookingRequest): Observable<BookingResponse> {
    return this.http.post<BookingResponse>(`${this.baseUrl}/bookings`, payload);
  }

  chargePayment(payload: ChargePaymentRequest): Observable<ChargePaymentResponse> {
    return this.http.post<ChargePaymentResponse>(`${this.baseUrl}/payments/charge`, payload);
  }
}
