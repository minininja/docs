import {Injectable} from '@angular/core';
import {Client} from 'elasticsearch';

@Injectable()
export class EsService {

  private client: Client;

  constructor() {
  }

  queryalldocs = {
    'query': {
      'match_all': {}
    }
  };

  getAllDocuments(_index, _type): any {
    return this.client.search({
      index: _index,
      type: _type,
      body: this.queryalldocs,
      filterPath: ['hits.hits._source']
    });
  }
}
