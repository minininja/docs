import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {HttpClientModule} from "@angular/common/http";
import {IndexComponent} from './index/index.component';
import {DataService} from "./services/data.service";
import {DocSearchComponent} from './doc-search/doc-search.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {TagInputModule} from 'ngx-chips';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {KeyPipePipe} from './key-pipe.pipe';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

@NgModule({
  declarations: [
    AppComponent,
    IndexComponent,
    DocSearchComponent,
    KeyPipePipe
  ],
  imports: [
    TagInputModule,
    NgbModule.forRoot(),
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule
  ],
  providers: [DataService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
