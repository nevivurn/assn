// Yongun Seong

#include <stdio.h>
#include <limits.h>
#include <string.h>

int op_score(const char a) {
	switch (a) {
	case 0:
		return 0;
	case '+':
	case '-':
		return 1;
	case '*':
	case '/':
		return 2;
	case '%':
		return 3;
	case '^':
		return 4;
	default:
		return 0;
	}
}

int op_cmp(const char a, const char b) {
	return op_score(a) - op_score(b);
}

int eval(char *ex) {
	int stack[8];
	size_t head = 0;
	char* tok = strtok(ex, " ");

	while (tok != NULL) {
		int a, b, v;
		switch (tok[0]) {
		case '+':
			b = stack[--head];
			a = stack[--head];
			if (b > 0 && a > INT_MAX-b) return -1;
			if (b < 0 && a < INT_MIN-b) return -1;
			stack[head++] = a+b;
			break;
		case '-':
			b = stack[--head];
			a = stack[--head];
			if (b < 0 && a > INT_MAX+b) return -1;
			if (b > 0 && a < INT_MIN+b) return -1;
			stack[head++] = a-b;
			break;
		case '*':
			b = stack[--head];
			a = stack[--head];
			if (a == -1 && b == INT_MIN) return -1;
			if (b == -1 && a == INT_MIN) return -1;
			if (b != 0 && a > INT_MAX/b) return -1;
			if (b != 0 && a < INT_MIN/b) return -1;
			stack[head++] = a*b;
			break;
		case '/':
			b = stack[--head];
			a = stack[--head];
			if (b == 0) return -1;
			if (a%b != 0) return -1;
			if (a == -1 && b == INT_MIN) return -1;
			if (b == -1 && a == INT_MIN) return -1;
			stack[head++] = a/b;
			break;
		case '%':
			b = stack[--head];
			a = stack[--head];
			stack[head++] = a%b;
			break;
		case '^':
			b = stack[--head];
			a = stack[--head];

			if (b < 0 && (a == -1 || a == 1)) {
				if (b%2 == 0) stack[head++] = 1;
				else stack[head++] = a;
			} else if (b < 0) return -1;

			v = 1;
			for (int i = 0; i < b; i++) {
				if (a == -1 && v == INT_MIN) return -1;
				if (v == -1 && a == INT_MIN) return -1;
				if (v != 0 && a > INT_MAX/v) return -1;
				if (v != 0 && a < INT_MIN/v) return -1;
				v *= a;
			}
			stack[head++] = v;
			break;
		default:
			sscanf(tok, "%d", &stack[head++]);
		}

		tok = strtok(NULL, " ");
	}

	return stack[--head];
}

int main(void) {
	char input[1024];
	scanf("%s", input);

	size_t length = strlen(input);

	// Postfix representation
	char postfix[1024];
	size_t write = 0;

	// Unknowns
	char *unknown[10];
	bool first[10];
	size_t unknowns = 0;

	// Operator stack, used while converting to postfix
	char op_stack[10];
	op_stack[0] = 0;
	size_t op_head = 1;

	// Convert into postfix
	size_t ind;
	for (ind = 0; ind < length; ind++) {
		if (input[ind] >= '0' && input[ind] <= '9') {
			// Number
			postfix[write++] = input[ind];
		} else if (input[ind] == '[') {
			// Blank
			ind++; // Skip closing
			first[unknowns] = input[ind+1] == '[' ||
				(input[ind+1] >= '0' && input[ind+1] <= '9');
			unknown[unknowns++] = &postfix[write++];
		} else if (input[ind] == '=') {
			// =
			break;
		} else {
			// Operators
			// Flush higher operators
			while (op_cmp(input[ind], op_stack[op_head-1]) < 0) {
				postfix[write++] = ' ';
				postfix[write++] = op_stack[--op_head];
			}
			// Left associative
			if (!op_cmp(input[ind], op_stack[op_head-1])) {
				postfix[write++] = ' ';
				postfix[write++] = op_stack[--op_head];
			}
			// Add current to stack
			postfix[write++] = ' ';
			op_stack[op_head++] = input[ind];
		}
	}
	// Flush remaining operators
	while (op_head > 1) {
		postfix[write++] = ' ';
		postfix[write++] = op_stack[--op_head];
	}
	postfix[write++] = '\0';

	// Read final answer
	int answer;
	sscanf(&input[++ind], "%d", &answer);

	// Initialize unknowns
	for (size_t i = 0; i < unknowns; i++) {
		*unknown[i] = first[i] ? '1' : '0';
	}

	char copy[1024];
	strcpy(copy, postfix);
	while (eval(copy) != answer) {
		//strcpy(copy, postfix);
		//printf("%s = %d\n", postfix, eval(copy));

		bool max = true;
		for (size_t i = 0; i < unknowns; i++) {
			if (*unknown[i] != '9') {
				max = false;
				break;
			}
		}
		if (max) {
			puts("No solution");
			return 0;
		}

		for (size_t i = 0; i < unknowns; i++) {
			if (*unknown[unknowns-i-1] == '9') {
				*unknown[unknowns-i-1] = first[unknowns-i-1] ? '1' : '0';
			} else {
				(*unknown[unknowns-i-1])++;
				break;
			}
		}
		strcpy(copy, postfix);
	}

	char *last = unknown[0]-1;
	for (size_t i = 0; i < unknowns; i++) {
		if (last+1 != unknown[i]) printf(", ");
		putchar(*unknown[i]);
		last = unknown[i];
	}
	putchar('\n');

	return 0;
}
