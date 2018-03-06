import {Component, Injectable, OnInit, Pipe} from '@angular/core';
import {DataService} from "../services/data.service";

@Pipe({
  name: 'prettyprint'
})
export class PrettyPrintPipe {
  transform(val) {
    return JSON.stringify(val, null, 2)
      .replace(/ /g, '&nbsp;')
      .replace(/\n/g, '<br/>');
  }
}

@Component({
  selector: 'app-doc-search',
  templateUrl: './doc-search.component.html',
  styleUrls: ['./doc-search.component.css']
})
@Injectable()
export class DocSearchComponent implements OnInit {

  ds: DataService;
  results: any;

  constructor(ds: DataService) {
    this.ds = ds;
  }

  doSearch(search: string) {
    console.log("Searching for " + search);
    this.ds.get("/mfc/_search?q=" + search).subscribe(
      data => {
        this.results = data;
      }
    )
  }

  stringify(obj: any) {
    return JSON.stringify(obj, null, "    ").replace(/ /g, '&nbsp;').replace(/\n/g, '<br/>');
  }

  show(hit: any) {
    if (hit.show === null || !hit.show) {
      console.log("showing row");
      hit.show = true;
    }
    else {
      console.log("hiding row");
      hit.show = false;
    }
  }

  ngOnInit() {
  }

}
