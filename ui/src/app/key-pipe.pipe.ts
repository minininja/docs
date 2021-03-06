import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'keys'
})
export class KeyPipePipe implements PipeTransform {

  transform(value: any, args: any[] = null): any {
    return Object.keys(value)
  }

}
