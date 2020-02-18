package main

import (
	"fmt"
	"math/rand"
	"strconv"
	"sync"
	"time"
)

var graph[][] int
var readWriteLock sync.RWMutex
var done = make(chan bool, 4)

func getCityPair() (int, int){
	var size = len(graph)
	var first = rand.Intn(size)
	var second = rand.Intn(size)
	for first == second {
		second = rand.Intn(size)
	}
	return first, second
}

func setNewWeight(i, j, weight int) {
	graph[i][j] = weight
	graph[j][i] = weight
}

func changePrice(numberOfIterations int) {
	for i := 0;i < numberOfIterations; i++ {
		readWriteLock.Lock()
		var currentSize = len(graph)
		if currentSize > 1 {
			var first, second = getCityPair()
			if graph[first][second] != 0 {
				setNewWeight(first, second, rand.Intn(100) + 1)
			}
		}
		readWriteLock.Unlock()
		time.Sleep(100*time.Millisecond)
	}
	done <- true
}

func addDelWay(numberOfIterations int){
	for i := 0;i < numberOfIterations; i++ {
		readWriteLock.Lock()
		var currentSize = len(graph)
		if currentSize > 1 {
			var first, second = getCityPair()
			if rand.Intn(2) == 1 {
				setNewWeight(first, second, rand.Intn(100) + 1)
			} else {
				setNewWeight(first, second, 0)
			}
		}
		readWriteLock.Unlock()
		time.Sleep(100*time.Millisecond)
	}
	done <- true
}

func addDelCity(numberOfIterations int) {
	for i := 0; i < numberOfIterations; i++ {
		readWriteLock.Lock()
		var currentSize = len(graph)
		if rand.Intn(3) != 1 {
			currentSize++
			graph = append(graph, make([]int, currentSize))
			for j := 0; j < currentSize-1; j++ {
				graph[j] = append(graph[j], 0)
			}
		} else {
			currentSize--
			graph = graph[:currentSize]
			for j:= 0;j < currentSize; j++ {
				graph[j] = graph[j][:currentSize]
			}
		}

		readWriteLock.Unlock()
		time.Sleep(100*time.Millisecond)
	}
	done <- true
}

func getDistance(numberOfIterations int) {
	for i := 0;i < numberOfIterations; i++ {
		readWriteLock.RLock()
		var currentSize = len(graph)
		if currentSize > 1 {
			var start, end = getCityPair()
			var used = make([]int, currentSize)

			var queue []int
			queue = append(queue, start)
			for len(queue) > 0 {
				var curV = queue[0]
				queue = queue[1:]
				for j := 0;j < currentSize; j++ {
					if graph[curV][j] > 0 && used[j] == 0 {
						used[j] = used[curV] + graph[curV][j]
						queue = append(queue, j)
					}
				}
			}
			if used[end] != 0 {
				fmt.Println("Path from city " + strconv.Itoa(start) + " to city " + strconv.Itoa(end) + " is " +
				strconv.Itoa(used[end]))
			} else {
				fmt.Println("Path from city " + strconv.Itoa(start) + " to city " + strconv.Itoa(end) + " is not exists")
			}

		} else {
			fmt.Println("Graph has less than 2 vertexes")
		}
		readWriteLock.RUnlock()
		time.Sleep(100*time.Millisecond)
	}
	done <- true
}

func main() {
	go addDelCity(15)
	go addDelWay(30)
	go changePrice(10)
	go getDistance(30)

	for i:= 0;i < 4; i++ {
		<- done
	}
}
