---
title:  "Sudoku solver with AWS lambda"
tags: [Algorithm,Python,AWS,AWS Lambda]
categories: [Cloud Tech,Programming]
---

[Sudoku Solver](https://gaget.jiajinzheng.com/sudokusolver)


[Github Repository](https://github.com/jackytsheng/gaget)

Thanks to the Professor Thorsten Altenkirch (University of Nottingham) on this recursive Sudoku solver.
Reference: [Sudo Solver](https://www.youtube.com/watch?v=G_UYXzGuqvM)

This project ultising the Sudoku solving algorithm and cloud solutions.

## Python sudoku solveing algorithm

### Define Grid

Sample input:

![sudoku-example](https://blogs.unimelb.edu.au/sciencecommunication/files/2016/10/sudoku-p14bi6.png)

Expected result:
![sudoku-result](/assets/images/2020-08-13/sudoku-result.png)

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
Result expected:
![sudoku-result](/assets/images/2020-08-13/sudoku-result.png)


This algorithm return ALL possible solutions, some time it could be more than one. And due to the recursive nature, we don't want it to run for solving grid that has too much freedom (i.e too less of numbers provided), because that would have taken too many memory and runtime. We could set it at the front end, which we could implement it in the front end.

### AWS Lambda

Log in AWS
navigate to lambda 

Enter function name : solveSudoku
Choose runtime of Python: Python 3.7
Permission: Create a new role with basic Lambda permissions



Function Code:

```py
import json
import sys
import copy

# sudoku solver

# Test it with the grid provided
# grid = [
# [9,0,6,0,7,0,0,0,0],
# [0,0,1,0,4,0,0,0,0],
# [0,0,0,2,0,0,8,1,0],	
# [0,8,5,0,0,0,0,0,7],
# [0,3,4,0,0,0,0,0,6],
# [0,0,0,0,0,4,0,0,0],
# [0,0,0,9,1,0,6,0,0],
# [0,0,7,6,0,3,0,0,5],
# [0,0,0,0,0,0,0,3,0],
# ]

def lambda_handler(event, context):
    body = event['body']
    json_grid = json.loads(body)['grid']
    grid = json.loads(json_grid)
    result = []
 
    def isPossible(y,x,n):
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
    
 
    def solve():
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
    solve()
    return {
        'statusCode': 200,
        "headers": {
            "Content-Type": "application/json",
            'Access-Control-Allow-Methods': 'OPTION',
            'Access-Control-Allow-Methods': 'POST',
            "Access-Control-Allow-Origin" : "*", 
            "Access-Control-Allow-Credentials" : True
        },
        
        'body': json.dumps(result)
    }
```


``` json
...
"headers": {
            "Content-Type": "application/json",
            "Access-Control-Allow-Methods": "OPTION",
            "Access-Control-Allow-Methods": "POST",
            "Access-Control-Allow-Origin" : "*", 
            "Access-Control-Allow-Credentials" : true
						}
...

```
These headers will address CORS issues. Every time I run into CORS issue, it takes me hours to figure out.

Next, make sure to configure it to allow more runtime, because it takes time to solve a puzzle recursively. 

![sudoku-solver-setting](/assets/images/2020-08-13/sudoku-solver-lambda-setting.png)


### AWS API Gateway

Navigate to API Gateway

1. Create a new `REST API`
2. Under Action, Create a `resource`, I named mine **sudokusolver**

![api-resource](/assets/images/2020-08-13/api-resource.png)
Notice that enable proxy allows lambda changes update in real time.

1. Checked API Gateway CORS, because we will also want to include `API Key`


2. Once resource is created, create a `POST method`.
 ![api-method](/assets/images/2020-08-13/api-method.png)

5. Under action we should enable CORS. Make sure Default 4XX and 5XX are enabled. 

When working with API Key, often 4XX result will be obtained.
When the algorithm break (may be due to Runtime error), 5XX result will be obtained. 
If CORS is not enabled these will result in CORS issues.

 ![api-cors](/assets/images/2020-08-13/api-cors.png)

6. Deploy the api, give it a `stage` if not already exist one.`
7. Create an API Key add it to the api stage deployed above.

Notice that every time changes are made to the API gateway, re-deploy of the API is needed.

8. Write down the API key and put it in the `X-API-KEY` header tag



### Frontend for reference

I built my frontend using React Framework and Material-UI

For reference, my UI looks like this:

 ![sudoku-demo](/assets/images/2020-08-13/sudoku-demo.png)

Few points to keep in mind:
1. Make sure to restrict on input type, only allowing 0-9.
2. Implement a number threshold (for example: at least 20 numbers must provided to restrict degree of freedom)
3. When making the API call, remember to add the `X-API-KEY` header tag
4. Send the JSON data with this the key `grid:"[[...],[...],...]"`, make sure it an array(9), and each of sub array is int array(9).
5. receive JSON is just an array(x) that contains x number of grids (i.e solutions), could be an empty array.