package main

import (
	"fmt"
	"math/rand"
	"sort"
	"sync"
)

var lettersChange = map[uint8]uint8{
	'A':'C',
	'B':'D',
	'C':'A',
	'D':'B',
}

var numberOfThreads int
var waiter sync.WaitGroup
var strings []string
var isRunning = true
var barrier Barrier

func randomFill(id, size int) {
	for i := 0;i < size; i++ {
		var x = rand.Intn(4)
		if x == 0 {
			strings[id] += "A"
		} else if x == 1 {
			strings[id] += "B"
		} else if x == 2 {
			strings[id] += "C"
		} else {
			strings[id] += "D"
		}
	}
}

func check() {
	var sum = make([]int, numberOfThreads)
	for id := 0; id < numberOfThreads; id++ {
		for i := 0; i < len(strings[id]); i++ {
			if strings[id][i] == 'A' || strings[id][i] == 'B' {
				sum[id]++
			}
		}
	}
	sort.Ints(sum)
	for i := 1; i < len(sum) - 1; i++ {
		if sum[i] == sum[i-1] && sum[i] == sum[i+1] {
			isRunning = false
			fmt.Println("Done")
			break
		}
	}
	if !isRunning {
		fmt.Println(strings)
	}
}

func worker(id, size int) {
	defer waiter.Done()
	randomFill(id, size)

	for ;isRunning;{
		var change = rand.Intn(size)
		if rand.Intn(2) == 1 {
			var x = strings[id][change]
			var prev = strings[id]
			strings[id] = prev[:change] + string(lettersChange[x])
			if change+1 < size {
				strings[id] += prev[(change + 1):]
			}
		}
		barrier.await()
	}
}

func main() {
	numberOfThreads = 4
	strings = make([]string, numberOfThreads)

	waiter.Add(numberOfThreads)
	defer waiter.Wait()

	barrier = Barrier{make(chan bool, 1), make(chan bool, 1), numberOfThreads}
	barrier.run(check)
	var size = 10
	for i := 0;i < numberOfThreads; i++ {
		go worker(i, size)
	}
}
