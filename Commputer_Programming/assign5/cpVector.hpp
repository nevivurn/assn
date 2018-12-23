// Yongun Seong

#ifndef CPVECTOR_H
#define CPVECTOR_H

#include <vector>
#include "cpScalar.hpp"

class cpVector {
	public:
		std::vector<cpScalar> arr;

		cpVector() {}

		cpVector(cpScalar *sarr, unsigned int size) {
			for (unsigned int i = 0; i < size; i++) {
				arr.push_back(sarr[i]);
			}
		}
};

std::ostream& operator<<(std::ostream &sout, const cpVector &val) {
	sout << "[";
	if (val.arr.size()) sout << val.arr[0];
	for (int i = 1; i < val.arr.size(); i++) {
		sout << ", " << val.arr[i];
	}
	return sout << "]";
}

// v-sc
cpVector operator+(const cpVector &v, const cpScalar &sc) {
	std::vector<cpScalar> cpy = v.arr;
	for (int i = 0; i < cpy.size(); i++) cpy[i] = cpy[i] + sc;
	return cpVector(&cpy[0], cpy.size());
}

cpVector operator-(const cpVector &v, const cpScalar &sc) {
	std::vector<cpScalar> cpy = v.arr;
	for (int i = 0; i < cpy.size(); i++) cpy[i] = cpy[i] - sc;
	return cpVector(&cpy[0], cpy.size());
}

cpVector operator*(const cpVector &v, const cpScalar &sc) {
	std::vector<cpScalar> cpy = v.arr;
	for (int i = 0; i < cpy.size(); i++) cpy[i] = cpy[i] * sc;
	return cpVector(&cpy[0], cpy.size());
}

cpVector operator/(const cpVector &v, const cpScalar &sc) {
	std::vector<cpScalar> cpy = v.arr;
	for (int i = 0; i < cpy.size(); i++) cpy[i] = cpy[i] / sc;
	return cpVector(&cpy[0], cpy.size());
}

// sc-v
cpVector operator+(const cpScalar &sc, const cpVector &v) {
	return v+sc;
}

cpVector operator-(const cpScalar &sc, const cpVector &v) {
	std::vector<cpScalar> cpy = v.arr;
	for (int i = 0; i < cpy.size(); i++) cpy[i] = sc - cpy[i];
	return cpVector(&cpy[0], cpy.size());
}

cpVector operator*(const cpScalar &sc, const cpVector &v) {
	return v*sc;
}

cpScalar operator/(const cpScalar &sc, const cpVector &v) {
	cpScalar b(0);
	for (int i = 0; i < v.arr.size(); i++) b = v.arr[i].dnum > 0 ? b+v.arr[i] : b-v.arr[i];
	return sc / b;
}

// v-v
cpVector operator+(const cpVector &a, const cpVector &b) {
	std::vector<cpScalar> cpy = a.arr;
	for (int i = 0; i < cpy.size(); i++) cpy[i] = cpy[i] + b.arr[i];
	return cpVector(&cpy[0], cpy.size());
}

cpVector operator-(const cpVector &a, const cpVector &b) {
	std::vector<cpScalar> cpy = a.arr;
	for (int i = 0; i < cpy.size(); i++) cpy[i] = cpy[i] - b.arr[i];
	return cpVector(&cpy[0], cpy.size());
}

cpScalar operator*(const cpVector &a, const cpVector &b) {
	int v = 0;
	for (int i = 0; i < a.arr.size(); i++) v += (a.arr[i] * b.arr[i]).dnum;
	return cpScalar(v);
}

cpVector operator/(const cpVector &a, const cpVector &b) {
	cpScalar v(0);
	for (int i = 0; i < b.arr.size(); i++) v = b.arr[i].dnum > 0 ? v+b.arr[i] : v-b.arr[i];
	return a / v;
}

#endif
