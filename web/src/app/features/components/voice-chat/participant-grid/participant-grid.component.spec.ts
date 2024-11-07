import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParticipantGridComponent } from './participant-grid.component';

describe('ParticipantGridComponent', () => {
  let component: ParticipantGridComponent;
  let fixture: ComponentFixture<ParticipantGridComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParticipantGridComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParticipantGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
