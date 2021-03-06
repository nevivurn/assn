# Skeleton code taken from
# https://github.com/javelinsman/DM-Boolean-Function-Solver/blob/master/skeleton.py

from itertools import chain, combinations, product # iteration helper function, don't delete this line

from functools import reduce

# Don't delete this function. You might need it.
def all_combinations_of(l):
    """return all possible combinations of elements in a list l
    arguments:
    l: list -- any list which is NOT empty
    return:
    A list containing all possible combinations, sorted by cardinality in ascending order.
    Each combination is a list of elements in l.
    """
    return list(map(list, chain.from_iterable(combinations(l, r) for r in range(0, len(l)+1))))

def zero_and_one(atoms):
    return set(), set(atoms)

def list_all_elements(atoms):
    return [set(s) for s in all_combinations_of(atoms)]

def evaluate_minterm(x, y, one, minterm):
    x, y = set(x), set(y)
    xp, yp = one^x, one^y

    if minterm == "xy":
        return x&y
    if minterm == "xy'":
        return x&yp
    if minterm == "x'y":
        return xp&y
    if minterm == "x'y'":
        return xp&yp

def add_elements(zero, elements):
    return reduce(lambda x, y: x|y, elements, zero)

def find_boolean_function(atoms, func):
    zero, one = zero_and_one(atoms)
    elements = list_all_elements(atoms)
    minterms = ["xy", "x'y", "xy'", "x'y'"]
    for combination in all_combinations_of(minterms):
        for x, y in product(elements, elements):
            terms = [evaluate_minterm(x, y, one.copy(), minterm) for minterm in combination]
            if func(x, y) != add_elements(zero.copy(), terms):
                break
        else:
            return combination
    return None

if __name__ == "__main__":
    atoms = ['a', 'b']
    def func(X, Y):
        elements = [set(), {'a'}, {'b'}, {'a', 'b'}]
        z0, za, zb, z1 = elements
        d = [z0, za, zb, z1, za, z0, z1, zb, zb, z1, z0, za, z1, zb, za, z0]
        for (x, y), r in zip(product(elements, elements), d):
            if (x, y) == (X, Y):
                return r

    print(find_boolean_function(atoms, func)) # ["x'y", "xy'"]
