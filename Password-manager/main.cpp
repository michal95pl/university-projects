#include <iostream>
#include <vector>
#include <string>
#include <set>
#include <map>
#include <cstdlib>
#include <filesystem>
#include <ctime>

#include "cryptography.h"
#include "file_io.h"
#include "menu.h"


int main() {

    std::set<std::string> categories;

    srand (time(NULL));

    std::vector<std::filesystem::directory_entry> dir;
    std::string choosed_file;


    menuUtility::load_files(dir, ".\\passwords");
    menuUtility::choose_files(dir, choosed_file);

    std::string pass;
    std::cout << "Write file password: "; std:: cin >> pass;
    crypt::Keys key(pass);
    gen_key_pair(key);


    std::map<std::string, std::vector<std::string>> passwords;

    passwordIO::get_all_passwords(passwords, choosed_file, key);


    while(menuUtility::show_gui(passwords, categories, choosed_file, key));

}