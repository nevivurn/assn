// Yongun Seong
// Computer Programming final exam: problem 2
// DCPair.h

#ifndef DCPAIR_H_INCLUDED
#define DCPAIR_H_INCLUDED

#include <iostream>

class DCPair{
private:
    int dollar;
    int cent;
    //You may add more functions or variables.
public:
    friend DCPair& operator+(const DCPair& pairA, const DCPair& pairB);
    friend DCPair& operator-(const DCPair& pairA, const DCPair& pairB);
    friend bool operator<(const DCPair& pairA, const DCPair& pairB);
    friend std::ostream& operator<<(std::ostream& os, const DCPair& dc);
    //void print();
    //You can add more functions.
    DCPair(int d, int c) : dollar(d), cent(c) {};
};

#endif // DCPAIR_H_INCLUDED
