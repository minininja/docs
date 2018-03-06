import {Component, Injectable, OnInit} from '@angular/core';
import {Search} from "../search";
import {DataService} from "../services/data.service";

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

  ngOnInit() {
  }

}
