import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContextMapDialogComponent } from './context-map-dialog.component';

describe('ContextMapDialogComponent', () => {
  let component: ContextMapDialogComponent;
  let fixture: ComponentFixture<ContextMapDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContextMapDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ContextMapDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
