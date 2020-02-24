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

var numberOfThreads = 4
var waiter sync.WaitGroup
var mutex sync.Mutex
var strings []string
var barrier_stop = make(chan bool, 1)
var barrier_start = make(chan bool, 1)
var isRunning = true

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
	var sum []int
	for id := 0; id < numberOfThreads; id++ {
		sum = append(sum, 0)
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

func barrier(checkFunc func()) {
	for {
		for i := 0;i < numberOfThreads; i++ {
			<-barrier_start
		}
		checkFunc()
		for i := 0;i < numberOfThreads; i++ {
			barrier_stop <- true
		}
	}
}

func worker(id, size int) {
	defer waiter.Done()
	randomFill(id, size)

	for ;isRunning;{
		if rand.Intn(2) == 1 {
			var change = rand.Intn(size)
			mutex.Lock()
			var x = strings[id][change]
			var prev = strings[id]
			strings[id] = prev[:change] + string(lettersChange[x])
			if change+1 < size {
				strings[id] += prev[(change+1):]
			}
			mutex.Unlock()
			barrier_start <- true
			<- barrier_stop
		}
	}
}

func main() {
	numberOfThreads = 4
	strings = make([]string, numberOfThreads)

	waiter.Add(numberOfThreads)
	defer waiter.Wait()

	go barrier(check)
	var size = 10
	for i := 0;i < numberOfThreads; i++ {
		go worker(i, size)
	}

}
