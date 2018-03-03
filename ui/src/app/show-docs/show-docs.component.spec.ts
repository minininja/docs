import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ShowDocsComponent} from './show-docs.component';

describe('ShowDocsComponent', () => {
  let component: ShowDocsComponent;
  let fixture: ComponentFixture<ShowDocsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ShowDocsComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowDocsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
