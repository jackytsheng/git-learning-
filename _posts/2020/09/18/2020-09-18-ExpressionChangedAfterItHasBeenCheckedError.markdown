---
title:  "Angular ExpressionChangedAfterItHasBeenCheckedError"
tags: [Angular] 
categories: [Programming]
---

I Was working on a project and encountered this error.

`ExpressionChangedAfterItHasBeenCheckedError`

Other good resources in explaining this issue:

1. [Blog by Ali Kamalizade](https://medium.com/better-programming/expressionchangedafterithasbeencheckederror-in-angular-what-why-and-how-to-fix-it-c6bdc0b22787)
2. [Youtube Part 1 by Angular University](https://www.youtube.com/watch?v=l3jZDGOZBEs&t=286s)
3. [Youtube Part 2 by Angular University](https://www.youtube.com/watch?v=MFNvDBb6q9c&ab_channel=AngularUniversity)


## Description of the problem:

1. Create an Angular app
2. Modified the App component to be like the following:


```ts

import {AfterViewInit,Component,OnInit,} from "@angular/core";
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent implements OnInit, AfterViewInit{

  ngOnInit(){}
  ngAfterViewInit(){}
}
```

3. Modifiy to see the lifecyle of react render.

```ts
...
@Component({
  ...
  template: `
    <div>
    {{printHello()}}
    </div>
  `,
  ...
})
export class AppComponent implements OnInit, AfterViewInit{

  title:String = "hello world";

  ngOnInit(){
    this.title = "hello world in OnInit";
  }
  ngAfterViewInit(){}
  
  printHello(){
    console.log(this.title);
    return this.title;
  }
}
```

if `this.title` is changed inside ngOnInit which in this case set to `"hello world in OnInit"`, it replaces what was initially set `"hello world"`. And only `"hello world in OnInit"` is rendered.


![onInit](/assets/images/2020-09-18/onInit.png)


4. Now modify `this.title` in `ngAfterViewInit`

```ts
...

export class AppComponent implements OnInit, AfterViewInit{

  title:String = "hello world";

  ngOnInit(){
    this.title = "hello world in OnInit";
  }
  
  ngAfterViewInit(){
    this.title = "hello world in ngAfterViewInit";
  }
  
  printHello(){
    console.log(this.title);
    return this.title;
  }
}

```
![ngAfterViewInit](/assets/images/2020-09-18/ngAfterViewInit.png)

`ExpressionChangedAfterItHasBeenCheckedError` is thrown. And both `this.title` values are printed inside console.

Lots of application may require calling API inside `ngAfterViewInit()`, so this error is relatively common.

## Ideas:

According to document about [AfterViewInit](https://angular.io/api/core/AfterViewInit), its a callback function **after** view has fully initialised. Because the initially when view is about to show, Angular takes `this.value` to be `"hello world"` as it's the value got assigned to `this.value` after `ngOnInit`. Once `"hello world"` is shown on the view it then immediately triggers the `ngAfterViewInit` callback, but it then triggers the change to the view **again**. So it throw this error out.

## Solution:

1. to avoid change inside ngAfterViewInit, and initialise variable inside ngOnInit(){}

2. wrap a `setTimeout()` around it. As this would **'defer'** the task inside the `ngAfterViewInit()` callback by setting it **async**.


```ts

  ngAfterViewInit(){
  
    setTimeout(()=>this.title = "hello world in ngAfterViewInit",0);

  }

```
![after-setTimeout](/assets/images/2020-09-18/after-setTimeout.png)

This will make the actual change to the `this.title` happens after the angular component lifecycle.

