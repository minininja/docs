// import { BadInput } from './../common/bad-input';
// import { NotFoundError } from './../common/not-found-error';
// import { AppError } from './../common/app-error';
import {Http} from '@angular/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/observable/throw';

@Injectable()
export class DataService {
  private url: string;

  constructor(private http: Http) {
    this.url = "http://localhost:8080/index";
  }

  get(path) {
    return this.http.get(this.url + path)
      .map(response => response.json())
      .catch(this.handleError);
  }

  put(path, resource) {
    return this.http.post(this.url + path, JSON.stringify(resource))
      .map(response => response.json())
      .catch(this.handleError);
  }

  post(path, resource) {
    return this.http.patch(this.url + path, JSON.stringify({isRead: true}))
      .map(response => response.json())
      .catch(this.handleError);
  }

  delete(path) {
    return this.http.delete(this.url + path)
      .map(response => response.json())
      .catch(this.handleError);
  }

  private handleError(error: Response) {
    console.log("response: " + error.status);
    return Observable.throw(error);
    // if (error.status === 400) {
    //   return Observable.throw(new BadInput(error.json()));
    // } else if (error.status === 404) {
    //   return Observable.throw(new NotFoundError());
    // }
    // return Observable.throw(new AppError(error));
  }
}
