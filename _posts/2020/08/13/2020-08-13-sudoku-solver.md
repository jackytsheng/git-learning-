---
title:  "Sudoku solver with AWS lambda "
# tags: [Algorithm:recursion,Python]
---

Thanks to the Professor Thorsten Altenkirch (University of Nottingham) on this recursive Sudoku solver.
Reference: [Sudo Solver](https://www.youtube.com/watch?v=G_UYXzGuqvM)




## Python algorithm

### Define Grid

Sample input:

![sudoku-example](https://blogs.unimelb.edu.au/sciencecommunication/files/2016/10/sudoku-p14bi6.png)

Expected result:
![sudoku-result](assets/images/2020-08-13/soduku-result.png)

``` python
## Testing grid function
grid = [
 [0,0,0,0,0,0,0,0,0],
 [0,3,0,0,0,0,1,6,0],
 [0,6,7,0,3,5,0,0,4],
 [6,0,8,1,2,0,9,0,0],
 [0,9,0,0,8,0,0,3,0],
 [0,0,2,0,7,9,8,0,6],
 [8,0,0,6,9,0,3,5,0],
 [0,2,6,0,0,0,0,9,0],
 [0,0,0,0,0,0,0,0,0],
]

// x horizontal axis
...
//  [0,3,0,0,0,0,1,6,0],
...
// y vertical axis

...
//  [[0...],[0...],[0...]],
...

```


### Algorithm

define a function to check if certain number can belong to a slot
```py
def isPossible(y,x,n):
	global grid
	for i in range(0,9):
		if grid[y][i] == n :
			return False
	for i in range(0,9):
		if grid[i][x] == n :
			return False
	x0 = (x//3)*3
	y0 = (y//3)*3
	for i in range(0,3):
		for j in range(0,3):
			if grid[y0+i][x0+j] ==n:
				return False
	return True
```

define solve function that use recursion method

```py
result = []
def solve():
	global grid
	global result
	for y in range(0,9):
		for x in range(0,9):
			if grid[y][x] == 0:
				for n in range(1,10):
					if isPossible(y, x, n):
						grid[y][x] = n
						solve()
						grid[y][x] = 0
				return 

	result.append(copy.deepcopy(grid))
	return 

# run the solve function 
solve()

# Could print the result using numpy library
# for grid in result:
# 	print(numpy.matrix(grid))	
# 	print("\n")	
```


### AWS Lambda

Log in AWS
navigate to lambda 

Enter function name : solveSudoku
Choose runtime of Python: Python 3.7
Permission: Create a new role with basic Lambda permissions

