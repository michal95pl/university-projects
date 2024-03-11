#ifndef menu_lib
    #define menu_lib


    namespace menuUtility {

        /**
         * scan all files in folder
         * @param data store all files information
         * @param path
         */
        void load_files(std::vector<std::filesystem::directory_entry> &data, const std::string &path);

        /**
         *  choose file to read and check if exists
         * @param data vector contains all files
         * @param choosed_path store selected file path
         */
        void choose_files(const std::vector<std::filesystem::directory_entry> &data, std::string &choosed_path);

        /**
         *
         * @param passwords
         * @param categories
         * @param choosed_file
         * @param key
         * @return
         */
        bool show_gui(std::map<std::string, std::vector<std::string>> &passwords, std::set<std::string> &categories, const std::string &choosed_file, crypt::Keys &key);

    }


#endif