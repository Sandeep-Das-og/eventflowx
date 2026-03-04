import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AppComponent } from './app.component';
import { ApiService } from './api.service';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        {
          provide: ApiService,
          useValue: {
            listEvents: () => of([]),
            createEvent: () => of({}),
            eventAnalytics: () => of({}),
            createBooking: () => of({ bookingId: 'b1', userId: 'u1', eventId: 'e1', paymentRequired: true }),
            chargePayment: () => of({})
          }
        }
      ]
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
