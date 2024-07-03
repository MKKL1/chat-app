import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GifSearchComponent } from './gif-search.component';

describe('GifSearchComponent', () => {
  let component: GifSearchComponent;
  let fixture: ComponentFixture<GifSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GifSearchComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GifSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
