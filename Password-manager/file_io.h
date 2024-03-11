#ifndef file_io_lib
    #define file_io_lib

        namespace passwordIO {
            /**
             * quarry passwords from file and set timestamps
             * @param data return map contains all passwords from file
             * @param path path of passwords file
             * @param key keys to decode passwords
             */
            void get_all_passwords(std::map<std::string, std::vector<std::string>> &data, const std::string &path, crypt::Keys &key);

            /**
             * save all passwords in map and create timestamps. Write 0x1 value in optional (empty) fields
             * @param data map key - name, values - vector [password, category, website, login].
             * @param path
             */
            void save_all_passwords(std::map<std::string, std::vector<std::string>> &data, const std::string &path, crypt::Keys &key);
        }

#endif