import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {IndexSelecterComponent} from './index-selecter.component';

describe('IndexSelecterComponent', () => {
  let component: IndexSelecterComponent;
  let fixture: ComponentFixture<IndexSelecterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [IndexSelecterComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IndexSelecterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
