// Yongun Seong

#ifndef CPSCALAR_H
#define CPSCALAR_H

#include <iostream>

class cpScalar {
	public:
		double dnum;
		cpScalar() {
			dnum = 0;
		}

		cpScalar(int num) {
			dnum = num;
		}

		cpScalar(double num) {
			dnum = num;
		}
};

std::ostream& operator<<(std::ostream &sout, const cpScalar &val) {
	return sout << val.dnum;
}

cpScalar operator+(const cpScalar &a, const cpScalar &b) {
	return cpScalar(a.dnum + b.dnum);
}

cpScalar operator-(const cpScalar &a, const cpScalar &b) {
	return cpScalar(a.dnum - b.dnum);
}

cpScalar operator*(const cpScalar &a, const cpScalar &b) {
	return cpScalar(a.dnum * b.dnum);
}

cpScalar operator/(const cpScalar &a, const cpScalar &b) {
	return cpScalar(a.dnum / b.dnum);
}

#endif
