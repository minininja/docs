import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {HttpClientModule} from "@angular/common/http";
import {IndexComponent} from './index/index.component';
import {DataService} from "./services/data.service";
import {DocSearchComponent} from './doc-search/doc-search.component';

@NgModule({
  declarations: [
    AppComponent,
    IndexComponent,
    DocSearchComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule
  ],
  providers: [DataService,],
  bootstrap: [AppComponent]
})
export class AppModule {
}
