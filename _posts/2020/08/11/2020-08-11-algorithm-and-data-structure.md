---
title:  "Top Algorithms and Data Structures"
# tags: [Python,DataStructure,Algorithm] 
---

## Linked list related:

### Time complexity
![time-complexity](https://i.stack.imgur.com/SpCcj.png)

### Inserting data at a particular position in the linked list
[HackerRank: Insert a node at a specific position in a linked list](https://www.hackerrank.com/challenges/insert-a-node-at-a-specific-position-in-a-linked-list/problem)

```py
def insertNodeAtPosition(head, data, position):
    index = 0
    cur = head
    while index < position - 1:
        index += 1
        cur = cur.next

    new_node = SinglyLinkedListNode(data)
    new_node.next = cur.next
    cur.next = new_node

    return head
```

### Inserting data in the linked list

[HackerRank: Insert a node at the head of a linked list](https://www.hackerrank.com/challenges/insert-a-node-at-the-head-of-a-linked-list/problem)

```py
# SinglyLinkedListNode:
#     int data
#     SinglyLinkedListNode next
#
#
def insertNodeAtHead(llist, data):
    new_node = SinglyLinkedListNode(data)
    new_node.next = llist
    return new_node
```

Thought: Instantiate a new node and point it's next pointer to the new head.

### Print data in the linked list

[HackerRank: Print the elements of a linked list](https://www.hackerrank.com/challenges/print-the-elements-of-a-linked-list/problem)


```py
def printLinkedList(head):
    cur = head
    while cur !=None:
        print(cur.data)
        if cur.next == None:
            return
        cur = cur.next
```


### Reverse a linked list

[HackerRank: Reverse a linked list](https://www.hackerrank.com/challenges/reverse-a-linked-list/problem?h_r=internal-search)

```py
def reverse(head):
    cur = head
    last_node = None
    while True:
        next_node = cur.next
        cur.next = last_node
        if next_node == None:
            return cur
        last_node = cur
        cur = next_node
```

Thought: temporarily stores the previous node once assign the next node to another temporarily variable, point current pointer's next node back to the previous node.

## Binary search tree related:

tree is just special graph
Tree is not linear.
Traversal -> breadth-first and depth-first search
### Breadth-first (level order):
visiting children before visiting grand children