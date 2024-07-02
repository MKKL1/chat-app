import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextChannelInfoComponent } from './text-channel-info.component';

describe('TextChannelInfoComponent', () => {
  let component: TextChannelInfoComponent;
  let fixture: ComponentFixture<TextChannelInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TextChannelInfoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TextChannelInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
