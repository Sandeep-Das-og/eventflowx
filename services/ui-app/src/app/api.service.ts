import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { BookingRequest, BookingResponse, EventItem, WalletResponse } from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly baseUrl = environment.apiBaseUrl;

  constructor(private readonly http: HttpClient) {}

  createBooking(payload: BookingRequest): Observable<BookingResponse> {
    return this.http.post<BookingResponse>(`${this.baseUrl}/bookings`, payload);
  }

  getWallet(userId: string): Observable<WalletResponse> {
    return this.http.get<WalletResponse>(`${this.baseUrl}/wallets/${encodeURIComponent(userId)}`);
  }

  creditWallet(userId: string, amount: number): Observable<WalletResponse> {
    return this.http.post<WalletResponse>(
      `${this.baseUrl}/wallets/${encodeURIComponent(userId)}/credit?amount=${amount}`,
      null
    );
  }

  listEvents(): Observable<EventItem[]> {
    return this.http.get<EventItem[]>(`${this.baseUrl}/events`);
  }
}
