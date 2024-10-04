import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GifSearchComponent } from './gif-search.component';
import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {provideHttpClient} from "@angular/common/http";
import {NoopAnimationsModule} from "@angular/platform-browser/animations";

describe('GifSearchComponent', () => {
  let component: GifSearchComponent;
  let fixture: ComponentFixture<GifSearchComponent>;

  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GifSearchComponent, NoopAnimationsModule],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GifSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    httpTesting = TestBed.inject(HttpTestingController);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
