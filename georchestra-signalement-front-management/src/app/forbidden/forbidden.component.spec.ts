import {ComponentFixture, TestBed} from '@angular/core/testing';
import {AppModule} from '../app.module';

import {ForbiddenComponent} from './forbidden.component';

describe('ForbiddenComponent', () => {
  let component: ForbiddenComponent;
  let fixture: ComponentFixture<ForbiddenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ForbiddenComponent],
      imports: [AppModule],
    })
        .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ForbiddenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
