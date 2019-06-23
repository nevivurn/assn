%.o: %.c
	$(CC) -O2 -Wall -c -o $@ $<

all: cachesim
	
cachesim: cache.o cachesim.o
	$(CC) -Wall -o $@ $^

clean:
	rm -f *.o cachesim

