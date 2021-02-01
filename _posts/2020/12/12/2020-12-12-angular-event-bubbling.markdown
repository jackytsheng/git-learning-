---
title:  "Angular Event Bubbling (Propagation) & Click outside Directive"
last_modified_at: 2021-01-31T17:48:00+10:00
tags: [Angular] 
categories: [Programming]
---

I was working on my Internship project. There was a requirement about **clicking outside to disable popup**. Due to intensive use of libraries, there were a huge deck of events being executed at once (due to a giant DOM). So this time I am about to investigate how event propagate and when will directive be executed


### Set up three `div` in DOM

```ts
@Component({
  selector: 'app-root',
  template: `
    <div class="Rect1 Rect">
      <div class="Rect2 Rect">
        <div class="Rect3 Rect"></div>
      </div>
    </div>
  `,
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
}

```

Scss:
```scss
.Rect{
  display: flex;
  justify-content: center;
  align-items: center;
  &1{
    width: 300px;
    height:300px;
    background-color: red;
  }
  &2{
    width: 200px;
    height:200px;
    background-color: blue;
  }
  &3{
    width: 100px;
    height:100px;
    background-color: green;
  }
}

```

![three-rectangles](/assets/images/2020-12-12/three-rectangles.png)

### Give it a (click) event handler


```ts
@Component({
  selector: 'app-root',
  template: `
    <div class="Rect1 Rect" (click)="onClick($event, 'Rect1')">
      <div class="Rect2 Rect" (click)="onClick($event, 'Rect2')">
        <div class="Rect3 Rect" (click)="onClick($event, 'Rect3')">
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  onClick(event: MouseEvent, RectNo: string) {
    console.log(RectNo, 'is being clicked');
  }
}

```

Then Click the Green(inner) Rect:
![click-three-rectangles](/assets/images/2020-12-12/click-three-rectangles.png)


### Event Bubbling


To stop that Event Penetrate all three box, angular has built in method call `stopPropagation()` in **MouseEvent**

```ts
  ...
  onClick(event: MouseEvent, RectNo: string) {
    event.stopPropagation();
    console.log(RectNo, 'is being clicked');
  }
  ...
```
If Now `Rect3` div is clicked. Event will stop **Penetrating through all rectangles**

### Now add a directive to listen for clicking outside of the element


Refers to [Building a ClickOutside directive](https://willtaylor.blog/click-outside-directive/) by Will Taylor
```ts
@Directive({
  selector: '[clickOutside]',
})
export class ClickOutsideDirective {
  @Output() clickOutside = new EventEmitter<void>();

  constructor(private elementRef: ElementRef) {}

  @HostListener('document:click', ['$event.target'])
  public onClick(target) {
    const clickedInside = this.elementRef.nativeElement.contains(target);
    if (!clickedInside) {
      this.clickOutside.emit();
    }
  }
}

```

now adds it to the Component

```ts
@Component({
  selector: 'app-root',
  template: `
    <div
      class="Rect1 Rect"
      (click)="onClick($event, 'Rect1')"
      (clickOutside)="onClickOutside('Rect1')"
    >
      <div
        class="Rect2 Rect"
        (click)="onClick($event, 'Rect2')"
        (clickOutside)="onClickOutside('Rect2')"
      >
        <div
          class="Rect3 Rect"
          (click)="onClick($event, 'Rect3')"
          (clickOutside)="onClickOutside('Rect3')"
        ></div>
      </div>
    </div>
  `,
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  onClick(event: MouseEvent, RectNo: string) {
    console.log(RectNo, 'is being clicked');
  }
  onClickOutside(RectNo:string) {
    console.log(RectNo, 'is Clicking outside');
  }

}

```

If clicked on the red rectangle (Outer) expected results are logging
- Inside of `Rect1`
- Outside of `Rect3`
- Outside of `Rect2`

This is exactly as shown below, looks like the click outside event got execute in the same order as "From the most inner DOM Out"
![click-outside](/assets/images/2020-12-12/click-outside.png)

### Adding stopPropagation() when clickOutside Directive is in used

Now what happens if a stopPropagation() method is added to `Rect3` the most outer Red Rectangle and click the most inner one?

expected result will be :

- `Rect3`, `Rect2`, `Rect1` Click method will be called
- None of the Click outside event will be emitted, as it's listening to the most Outside Layer of the DOM (White background)

Now add another method onClickRectOne to the most outer Rect and test:

```ts

  onClickRectOne(event: MouseEvent, RectNo: string) {
    event.stopPropagation();
    console.log(RectNo, 'is being clicked');
  }

```

Result Are as expected.
![blocking-event](/assets/images/2020-12-12/blocking-event.png)

### Conclusion

Avoid using `event.stopPropation()` unnecessarily as it will intervene with the event bubbling and eventually block the event to be listened by the directive if there's any sitting at the document level.