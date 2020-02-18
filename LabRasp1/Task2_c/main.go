package main

import (
	"fmt"
	"strconv"
)

var monk = []int{1, 2, 5, 8, 12, 0, 5, 4, 5, 10, 2, 4}

func getWinner(answer chan <- int, l, r int) {
	if r == l{
		answer <- l
	} else if r > l {
		currentWinners := make(chan int, 2)

		mid := (r+l)/2
		go getWinner(currentWinners, l, mid)
		go getWinner(currentWinners, mid+1, r)

		winner1 := <-currentWinners
		winner2 := <-currentWinners

		if monk[winner1] > monk[winner2] {
			answer <- winner1
		} else {
			answer <- winner2
		}
	}
}

func main() {
	answerChan := make(chan int)
	go getWinner(answerChan, 0, len(monk)-1)
	answer := <- answerChan

	fmt.Println("Winner is " + strconv.Itoa(answer+1) + "\nIt's value is " + strconv.Itoa(monk[answer]))
}
