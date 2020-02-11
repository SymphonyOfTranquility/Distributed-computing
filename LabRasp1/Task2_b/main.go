package main

import (
	"fmt"
	"os"
	"strconv"
)

var goods[]string
var size int
var steal = make(chan string)
var load = make(chan string)
var done = make(chan bool)

func stealingGoods(){
	for i := 0; i < size; i++{
		steal <- goods[i]
	}
}

func loadingGoods(){
	for i := 0; i < size; i++{
		good := <- steal
		load <- good
	}
}

func countGoods(){
	for i := 0; i < size; i++{
		good := <- load
		fmt.Println(good)
	}
	done <- true
}

func main() {
	fmt.Println("Input number of goods")
	fmt.Fscan(os.Stdin, &size)

	for i := 0; i < size; i++{
		goods = append(goods, "good id #" + strconv.Itoa(i))
	}

	go stealingGoods()
	go loadingGoods()
	go countGoods()
	<- done
}
