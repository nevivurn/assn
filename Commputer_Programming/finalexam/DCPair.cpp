// Yongun Seong
// Computer Programming final exam: problem 2
// DCPair.cpp

#include "DCPair.h"
#include <iostream>

DCPair& operator+(const DCPair& a, const DCPair& b) {
	int dollars = a.dollar + b.dollar;
	int cents = a.cent + b.cent;
	dollars += cents/100;
	cents %= 100;

	DCPair *out = new DCPair(dollars, cents);
	return *out;
}

DCPair& operator-(const DCPair& a, const DCPair& b) {
	int dollars = a.dollar - b.dollar;
	int cents = a.cent - b.cent;
	dollars += cents/100;
	cents %= 100;

	if (cents < 0 && dollars > 0) {
		dollars--;
		cents += 100;
	} else if (cents > 0 && dollars < 0) {
		dollars++;
		cents -= 100;
	}

	DCPair *out = new DCPair(dollars, cents);
	return *out;
}

bool operator<(const DCPair& a, const DCPair& b) {
	return (a.dollar < b.dollar) || (a.dollar == b.dollar && a.cent < b.cent);
}

std::ostream& operator<<(std::ostream& out, const DCPair& dc) {
	if (dc.dollar == 0 && dc.cent < 0) out << "-";
	out << dc.dollar << ".";
	if ((dc.cent < 0 ? -dc.cent : dc.cent) < 10) out << "0";
	out << (dc.cent < 0 ? -dc.cent : dc.cent);
	return out;
}
