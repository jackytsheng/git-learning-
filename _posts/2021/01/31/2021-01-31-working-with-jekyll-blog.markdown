---
title:  "Implement Advance Features with Jekyll Minimal-mistake theme"
tags: [Jekyll]
categories: [Programming]
toc: true
toc_label: "Table of content"
toc_icon: "list-ul"  # corresponding Font Awesome icon name (without fa prefix)
---


My Jekyll blog is using Minimal-mistake theme, but currently comment, Tags, Collection aren't implemented yet. So in this blog, I will documented how to implement them. [Set up the theme and Jekyll](https://mmistakes.github.io/minimal-mistakes/docs/quick-start-guide/)

## Implement Jekyll Tags

According to [Minimal-mistakes Layout](https://mmistakes.github.io/minimal-mistakes/docs/layouts/#archive-layout), there are a couple steps we need to do in order to implement Tags.

1. Added `tags` metadata inside post. 
   
In this blog my metadata is as followed. Insert this on top of this blog markdown page.

```markdown
---
title:  "Implement Tags,Collections & comment with Jekyll Minimal-mistake theme"
tags: [Jekyll]
---
```
1. Create `/_pages` under root directory if not yet created before and Create `tag-archive.md`
2. Insert the following meta data for `tag-archive.md`

```markdown
---
title: "Posts by Tag"
permalink: /tags/
layout: tags
author_profile: true
---
```

3. Tags and tags page should show as follow:
 
 ![tag](/assets/images/2021-01-31/tag.png)
 ![tag archive page](/assets/images/2021-01-31/tag-archive.png)
 

4. Inside `_config.yml` add the following:
```yml
tag_archive:
  type: liquid
  path: /tags/
```
### Add modified time

 To record *update time* include the following meta tag

```markdown
---
...
last_modified_at: 2021-01-31T17:48:00+10:00
...
---
```
Format as  `yyyy-mm-ddThh:mm:ss(time zone)` I am based in melbourne, so mine is UTC `+10:00`

### Add Category and Collection

Under `_pages` include both `/category-archive.md` and `/collection-archive.html`

1. `/category-archive.md` 
```md
---
title: "Posts by Category"
layout: categories
permalink: /categories/
author_profile: true
---
```

2. `/collection-archive.html`
```html
---
layout: archive
title: "Posts by Collection"
permalink: /collection-archive/
author_profile: true
---

{% capture written_label %}'None'{% endcapture %}

{% for collection in site.collections %}
  {% unless collection.output == false or collection.label == "posts" %}
    {% capture label %}{{ collection.label }}{% endcapture %}
    {% if label != written_label %}
      <h2 id="{{ label | slugify }}" class="archive__subtitle">{{ label }}</h2>
      {% capture written_label %}{{ label }}{% endcapture %}
    {% endif %}
  {% endunless %}
  {% for post in collection.docs %}
    {% unless collection.output == false or collection.label == "posts" %}
      {% include archive-single.html %}
    {% endunless %}
  {% endfor %}
{% endfor %}
```

3. Add Navigation tab for both collections and category archive
   
Under `/_data/navigation.yml` include the following

```yml
# main links
main:
  - title: "Posts"
    url: /year-archive/
  - title: "Collections"
    url: /collection-archive/
  - title: "Categories"
    url: /categories/
```
Both Post and url can be customised, url needs to match the `permalink` specified above.


4. Detail implementation with Collection check the [Minimal-mistake Docs](https://mmistakes.github.io/minimal-mistakes/docs/collections/)

5. Inside `_config.yml` add the following for category to work

```yml
category_archive:
  type: liquid
  path: /categories/
```
### Add Search To the Site

![search](/assets/images/2021-01-31/search.png)
[Incorporate search for the website](https://mmistakes.github.io/minimal-mistakes/docs/configuration/)

### Add Comment Functionality
To add comment we need a provider, I choosed [**Disqus**](https://disqus.com/profile/login/). 
1. Set up an account with disqus
2. Upon setting up account select basic plan
3. under `_config.yml` change setting as the following:

```yml
comments:
  provider: 'disqus' 
  disqus:
    shortname: #your short name
```

### Add table of content

inside post add the following

```md
---
toc: true
toc_label: "Unique Title"
toc_icon: "heart"  # corresponding Font Awesome icon name (without fa prefix)
---
```