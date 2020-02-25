package main

import (
	"fmt"
	"math/rand"
	"sync"
)

var numberOfThreads int
var waiter sync.WaitGroup
var arrays [][]int
var maxValue, minValue = 50, -50
var isRunning = true
var barrier Barrier

func randomFill(id, size int) {
	arrays[id] = make([]int, size)
	for i := 0;i < size; i++ {
		arrays[id][i] = rand.Intn(maxValue-minValue + 1) + minValue
	}
}

func check() {
	var sum = make([]int, numberOfThreads)
	for id := 0; id < numberOfThreads; id++ {
		for i := 0; i < len(arrays[id]); i++ {
			sum[id] += arrays[id][i]
		}
	}
	var ok = true
	for i := 1;i < len(sum); i++ {
		if sum[i] != sum[i-1] {
			ok = false
		}
	}
	if ok {
		isRunning = false
		fmt.Print("Complete. Sum is ")
		fmt.Println(sum[0])
		for i := 0;i < numberOfThreads; i++ {
			fmt.Println(arrays[i])
		}
	}
}

func worker(id, size int)  {
	defer waiter.Done()
	randomFill(id, size)

	for ;isRunning; {
		var pos = rand.Intn(size)
		if rand.Intn(2) == 1 {
			if rand.Intn(2) == 1{
				if arrays[id][pos] < maxValue {
					arrays[id][pos]++
				} else {
					arrays[id][pos]--
				}
			} else {
				if arrays[id][pos] > minValue {
					arrays[id][pos]--
				} else {
					arrays[id][pos]++
				}
			}
		}
		barrier.await()
	}
}

func main() {
	numberOfThreads = 3
	arrays = make([][]int, numberOfThreads)

	waiter.Add(numberOfThreads)
	defer waiter.Wait()

	var size = 5
	barrier = Barrier {make(chan bool, 1), make(chan bool, 1), numberOfThreads}
	barrier.run(check)
	for i := 0;i < numberOfThreads; i++ {
		go worker(i, size)
	}
}
