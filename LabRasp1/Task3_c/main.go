package main

import (
	"fmt"
	"math/rand"
)

var ingredients = map[string]int{
	"tobacco":0,
	"paper":1,
	"matches":2,
}

var semaphore = make(chan bool, 1)
var getWhatNeeded = make(chan bool, 1)
var endOfWork = make(chan bool, 1)
var dealerNotHave = -1

func smoker(have string){
	for {
		semaphore <- true
		if dealerNotHave == ingredients[have] {
			fmt.Println("Smoker with " + have + " get ingredients")
			dealerNotHave = -1
			getWhatNeeded <- true
		}
		<- semaphore
	}
}

func dealer(numberOfDeals int){
	for i := 0;i < numberOfDeals; i++ {
		semaphore <- true
		dealerNotHave = rand.Intn(3)
		<- semaphore
		<- getWhatNeeded
	}
	endOfWork <- true
}

func main() {

	go smoker("tobacco")
	go smoker("paper")
	go smoker("matches")
	go dealer(6)

	<- endOfWork
}
