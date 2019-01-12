// Yongun Seong
#include <stdio.h>

typedef struct {
	int n;
	int arr[8];
} disk;

disk disks[3];
int total;

void display(void) {
	putchar('\n');
	for (int i = total-1; i >= 0; i--) {
		for (int j = 0; j < 3; j++) {
			int size = disks[j].n > i ? disks[j].arr[i] : 0;

			int spaces = size ? total-size: total-1;
			for (int k = 0; k < spaces; k++) putchar(' ');
			if (size) for (int k = 0; k < (size-1)*2+1; k++)  putchar(size ? '*' : '|');
			else putchar('|');
			for (int k = 0; k < spaces; k++) putchar(' ');
			putchar(' ');
		}
		putchar('\n');
	}

	for (int i = 0; i < 6*total-1; i++) putchar('-');
	putchar('\n');
}

void move(disk *src, disk *dst) {
	dst->arr[dst->n++] = src->arr[--src->n];
	display();
}

void solve(disk *src, disk *dst, disk *tmp, int cnt) {
	if (!cnt) return;
	solve(src, tmp, dst, cnt-1);
	move(src, dst);
	solve(tmp, dst, src, cnt-1);
}

int main(void) {
	scanf("%d", &total);

	disks[0].n = total;
	for (int i = 1; i < 3; i++) disks[i].n = 0;

	for (int i = 0; i < total; i++) {
		disks[0].arr[i] = total-i;
	}

	display();
	solve(&disks[0], &disks[2], &disks[1], total);
}
