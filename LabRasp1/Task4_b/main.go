package main

import (
	"fmt"
	"math/rand"
	"os"
	"strconv"
	"sync"
	"time"
)

var readWriteLock sync.RWMutex

var width, height int
var garden [][]string
var done = make(chan bool, 2)

func gardener(){
	for {
		readWriteLock.Lock()

		counter := 5
		for i := 0; i < height && counter > 0; i++ {
			for j := 0;j < width && counter > 0; j++ {
				if garden[i][j] == "bad" {
					garden[i][j] = "good"
					counter--
				}
			}
		}

		readWriteLock.Unlock()

		time.Sleep(time.Second)
	}
}

func nature(){
	for {
		readWriteLock.Lock()

		for i := 0; i < height; i++ {
			for j := 0;j < width; j++ {
				if rand.Intn(2) == 1 {
					garden[i][j] = "good"
				} else {
					garden[i][j] = "bad"
				}
			}
		}

		readWriteLock.Unlock()
		time.Sleep(time.Second)
	}
}

func monitor1(numberOfIterations int){
	fileOutput, err := os.Create("info.txt")
	if err != nil {
		panic(err)
	}
	for i := 0;i < numberOfIterations; i++ {
		readWriteLock.RLock()
		fileOutput.WriteString("Iteration #" + strconv.Itoa(i) + "\n")
		for i := 0;i < height; i++ {
			for j := 0;j < width; j++ {
				fileOutput.WriteString(garden[i][j] + " ")
			}
			fileOutput.WriteString("\n")
		}
		fileOutput.WriteString("\n")
		readWriteLock.RUnlock()
		time.Sleep(time.Second)
	}
	done <- true
}

func monitor2(numberOfIterations int){
	for i := 0;i < numberOfIterations; i++ {
		readWriteLock.RLock()
		fmt.Println("Iteration #" + strconv.Itoa(i))
		for i := 0;i < height; i++ {
			for j := 0;j < width; j++ {
				fmt.Print(garden[i][j] + " ")
			}
			fmt.Println()
		}
		fmt.Println()

		readWriteLock.RUnlock()
		time.Sleep(time.Second)
	}
	done <- true
}

func main() {
	width = 3; height = 3
	for i := 0;i < height; i++ {
		var slice[] string
		for j := 0;j < width; j++ {
			if rand.Intn(2) == 1 {
				slice = append(slice, "bad")
			} else {
				slice = append(slice, "good")
			}
		}
		garden = append(garden, slice)
	}

	go gardener()
	go nature()
	go monitor1(5)
	go monitor2(20)

	<- done
	<- done
}
