package main

import (
	"fmt"
	"math/rand"
	"time"
)

var SIZE = 1000
var processorsNum = 1

var done = make(chan bool, 1)

var mat1 = make([]float32, SIZE*SIZE)
var mat2 = make([]float32, SIZE*SIZE)
var mat_ans = make([]float32, SIZE*SIZE)

func init_mat(mat []float32) {
	for i := 0; i < SIZE; i++ {
		for j := 0;j < SIZE; j++ {
			mat[i*SIZE + j] = (rand.Float32() - 0.5)*2
		}
	}
}

func tape_circuit(pid int) {
	var cur_size = int(SIZE/processorsNum)
	var begin = cur_size*pid
	var end = cur_size*(pid+1)
	for i := begin;i < end; i++ {
		for j := 0; j < SIZE; j ++ {
			mat_ans[i*SIZE + j] = 0.0
			for k := 0; k < SIZE; k++ {
				mat_ans[i*SIZE + j] += mat1[i*SIZE + k]*mat2[k*SIZE + j]
			}
		}
	}
	done <- true
}

func main() {
	init_mat(mat1)
	init_mat(mat2)
	init_mat(mat_ans)

	start := time.Now()

	for i := 0;i < processorsNum; i++ {
		go tape_circuit(i)
	}
	for i := 0;i < processorsNum; i++ {
		<- done
	}
	end := time.Now()
	fmt.Println(float32(end.Sub(start))/1000000000.0)
}