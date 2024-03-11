#include <string>
#include "cryptography.h"


int NWD(int a, int b) {
    return b == 0 ? a : NWD(b, a%b);
}

/**
 * Extended Euclidean algorithm
 * @param a value
 * @param b size definition of ring
 * @return inverse number in Z (ring)
 */
int ex_euklides(int a, int b=256, int xa=1, int ya=0, int xb=0, int yb=1) {

    if (a == 0) {
        while (xb < 0)
            xb += 256;
        return xb;
    }
    else if (b == 0) {
        while (xa < 0)
            xa += 256;
        return xa;
    }
    else if (b > a)
        return ex_euklides(a, b%a, xa, ya, xb-= (b/a)*xa, yb-= (b/a)*ya);
    else
        return ex_euklides(a%b, b, xa-=(a/b)*xb, ya-=(a/b)*yb, xb, yb);
}


void crypt::encode(std::string &data, Keys &keys) {

    uint16_t j = 0; // key length (period)
    for (char &i : data) {
        i = (keys.second*i + keys.first.length()) % 256; // szyfr afiniczny
        i ^= keys.first[j++]; // xor
        if (j >= keys.first.length())
            j = 0;
    }
}

void crypt::decode(std::string &data, Keys &keys) {
    uint16_t j = 0; // key length (period)
    for (uint16_t i = 0; i < data.length(); i++) {
        data[i] ^= keys.first[j++]; // xor
        data[i] = ex_euklides(keys.second) * (data[i] - keys.first.length()) % 256;
        if (j >= keys.first.length())
            j = 0;
    }
}

uint32_t get_number_from_string(std::string &data) {
    uint32_t key = 0;
    uint32_t multi = 1;
    for (int i=data.length()-1; i >= 0; i--) {
        if (data[i] >= '0' && data[i] <= '9') {
            key += multi * (data[i]-'0');
            multi *= 10;
        }
    }
    return key;
}

void crypt::gen_key_pair(crypt::Keys &keys) {
    uint32_t key = get_number_from_string(keys.first);
    if (key == 0)
        key = 100;
    while (NWD(key++, 256) != 1);
    keys.second = key-1;
}