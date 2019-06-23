package main

import (
	"bufio"
	"log"
	"os"
	"os/exec"
	"runtime"
	"strings"
	"sync"
)

func work(s string) {
	sp := strings.SplitN(s, ":", 3)
	if len(sp) != 3 {
		log.Printf("invalid job spec: %q", s)
		return
	}

	inf, err := os.Open(sp[1])
	if err != nil {
		log.Printf("inf %q: %v", sp[1], err)
		return
	}
	defer inf.Close()

	outf, err := os.Create(sp[2])
	if err != nil {
		log.Printf("outf %q: %v", sp[2], err)
		return
	}
	defer outf.Close()

	cmds := strings.Fields(sp[0])
	cmd := exec.Command(cmds[0], cmds[1:]...)
	cmd.Stdin = inf
	cmd.Stdout = outf
	cmd.Stderr = outf

	log.Printf("%s < %s > %s\n", sp[0], sp[1], sp[2])

	if err := cmd.Run(); err != nil {
		log.Printf("cmd %q: %v", s, err)
	}
}

func main() {
	c := make(chan string)

	var wg sync.WaitGroup
	wg.Add(runtime.NumCPU())
	for i := 0; i < runtime.NumCPU(); i++ {
		go func() {
			for s := range c {
				work(s)
			}
			wg.Done()
		}()
	}

	sc := bufio.NewScanner(os.Stdin)
	for sc.Scan() {
		c <- sc.Text()
	}

	close(c)
	wg.Wait()
}
