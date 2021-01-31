---
title:  "Implement Tags,Collections,Modified Time & Comment with Jekyll Minimal-mistake theme"
tags: [Jekyll]
---

My Jekyll blog is using Minimal-mistake theme, but currently comment, Tags, Collection aren't implemented yet. So in this blog, I will documented how to implement them.

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
 
 ![tag](assets/images/2021-01-31/tag.png)
 ![tag archive page](assets/images/2021-01-31/tag-archive.png)
 


 ### Added modified time

 To record *update time* include the following meta tag

```markdown
---
...
last_modified_at: 2021-01-31T17:48:00+10:00
...
---
```
Format as  `yyyy-mm-ddThh:mm:ss(time zone)` I am based in melbourne, so mine is UTC `+10:00`