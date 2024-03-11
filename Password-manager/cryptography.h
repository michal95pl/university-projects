#include <utility>

#ifndef crypt_lib
    #define crypt_lib

    namespace crypt {


        struct Keys {
            std::string first; // haslo
            uint32_t second = 0; // liczba wzglednie pierwsza

            explicit Keys(std::string pass) :first(std::move(pass)){};
        };

        /**
         *  Affine cipher and xor encoder
         * @param data string txt
         * @param keys keys structure with 2 keys
         */
        void encode(std::string &data, Keys &keys);

        /**
         * Affine cipher and xor decoder
         * @param data string txt
         * @param keys keys structure with 2 keys
         */
        void decode(std::string &data, Keys &keys);

        /**
         * create key pair. Second key is greatest common divisor with 256 (ring / max value of char) equals 1
         * @param keys
         */
        void gen_key_pair(Keys &keys);
    }


#endif