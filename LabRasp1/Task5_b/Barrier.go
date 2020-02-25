package main

type Barrier struct {
	start chan bool
	stop chan bool
	numberOfThreads int
}

func (b Barrier) routine(check func()) {
	for {
		for i := 0;i < b.numberOfThreads; i++ {
			<- b.start
		}
		check()
		for i := 0;i < numberOfThreads; i++ {
			b.stop <- true
		}
	}
}

func (b Barrier) run(check func()){
	go b.routine(check)
}

func (b Barrier) await() {
	b.start <- true
	<- b.stop
}
