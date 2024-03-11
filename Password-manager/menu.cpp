#include <filesystem>
#include <iostream>

#include <set>
#include <map>
#include <vector>
#include <string>

#include "fmt/color.h"

#include "cryptography.h"
#include "file_io.h"
#include "menu.h"

void menuUtility::load_files(std::vector<std::filesystem::directory_entry> &data, const std::string &path) {
    const auto directory = std::filesystem::directory_iterator(path);

    for (const auto &files: directory)
        data.push_back(files);
}


void menuUtility::choose_files(const std::vector<std::filesystem::directory_entry> &data, std::string &choosed_path) {

    int choose;
    int cnt;

    do {
        cnt=0;
        std::cout << "choose files: " << std::endl;

        for (const auto &files : data)
            fmt::print("  {} - {}\n", cnt++, files.path().filename().string());

        fmt::print("  {} - custom path\n", cnt);
        std::cin >> choose;

        if (choose < cnt)
            choosed_path = data[choose].path().string();
        else if (choose == cnt) {
            do {
                fmt::print("set path: ");
                std::cin >> choosed_path;
            } while(!std::filesystem::exists(choosed_path));
        }

    } while (choose > cnt);

}


const std::string special_alphabet = "!@#$%^&*,.?/_";

std::string generate_password(const int num_char, const bool uppercase, const bool special_char) {

    std::string password = "";

    for (int i=0; i < num_char; i++) {

        int random_option = (int)(rand() % 4);

        switch (random_option) {
            case 0: password += (char)(rand() % 25 + 97); break;
            case 1: password += (char)(rand() % 9 + 48); break;
            case 2: {
                password += (uppercase ? (char)(rand() % 25 + 65) : (char)(rand() % 25 + 97));
                break;
            }
            case 3: {
                password += (special_char ? special_alphabet[rand() % special_alphabet.length()] : (char)(rand() % 25 + 97));
                break;
            }
        }
    }
    return password;
}


int get_range_char_num(const std::string &pass, const char char_from, const char char_to) {

    int cnt = 0;

    for (int i=0; i < pass.length(); i++) {
        if (pass[i] >= char_from && pass[i] <= char_to)
            cnt++;
    }

    return cnt;
}

int get_special_char_num(const std::string &pass) {

    int cnt = 0;

    for (char pas : pass) {
        for (char j : special_alphabet) {
             if (pas == j) {
                 cnt += 1;
                 break;
             }
        }
    }

    return cnt;
}

/**
 *
 * @return password security scale (0 - 10)
 */
int check_password(const std::string pass) {

    int scale = 0;

    int number = get_range_char_num(pass, '0', '9');

    if (number >= 8)
        scale += 4;
    else if (number >= 4)
        scale += 2;
    else if (number > 0)
        scale += 1;

    number = get_special_char_num(pass);

    if (number >= 4)
        scale += 3;
    else if (number >= 2)
        scale += 2;
    else if (number > 0)
        scale += 1;

    number = get_range_char_num(pass, 'A', 'Z');

    if (number >= 6)
        scale += 3;
    else if (number >= 3)
        scale += 2;
    else if (number > 0)
        scale += 1;

    return scale;
}


int show_menu() {
    int option;
    do {
        fmt::print("1. search password\n2. Sort password\n3. add password\n"
                   "4. edit password\n5. delete password\n6. add category\n7. delete category"
                   "\n8. exit\n");
        std::cin >> option;
    } while (option > 8 || option < 0);
    return option;
}

void show_password(std::pair<std::string, std::vector<std::string>> data) {
    std::cout << "name: " << data.first << " ,pass: " << data.second[0] << " ,category: " << data.second[1];
    if (!(data.second[2].length() == 1 && data.second[2][0] == 1))
        std::cout << " ,website: " << data.second[2];
    if (!(data.second[3].length() == 1 && data.second[3][0] == 1))
        std::cout << " ,login " << data.second[3];
    std::cout << std::endl;
}


void get_categories_from_passwords(const std::map<std::string, std::vector<std::string>> &passwords, std::set<std::string> &categories) {
    for (std::pair i : passwords) {
        categories.insert(i.second[1]);
    }
}


void add_password(std::map<std::string, std::vector<std::string>> &passwords, std::set<std::string> &categories) {

    get_categories_from_passwords(passwords, categories);

    if (categories.size() == 0) {
       std::cout << "create category, before create password!" << std::endl;
       return;
    }


    int option = 0;
    do {
        fmt::print("0. add own password\n1. generate password");
        std::cin >> option;
    } while (option < 0 || option > 1);

    std::string password = "";

    switch (option) {
        case 0: {

            char c;
            do {
                fmt::print("write new password: ");
                std::cin >> password;
                fmt::print("\nsecurity level of password: {}%", check_password(password) * 10);
                fmt::print("\nOk (y/n): ");
                std::cin >> c;
            } while(c != 'y');

            break;
        }

        case 1: {

            char c;
            int cnt_pass;
            char uppper_case;
            char special_char;

            do {
                fmt::print("set password length: ");
                std::cin >> cnt_pass;
            }while (cnt_pass <= 0);

            fmt::print("\nuse upper case (y/n): "); std::cin >> uppper_case;
            fmt::print("\nuse special characters (y/n): "); std::cin >> special_char;

            do {
                password = generate_password(cnt_pass, uppper_case == 'y', special_char == 'y');
                fmt::print("\npassword: {}", password);
                fmt::print("\nsecurity level of password: {}%", check_password(password) * 10);
                fmt::print("\nOk (Y/N): ");
                std::cin >> c;
            } while(c != 'y');

            break;
        }
    }

    std::string name;
    std::string website;
    std::string login;

    fmt::print("\nset name: "); std::cin >> name;
    while (passwords.contains(name)) {
        fmt::print("\nname exists. Set new name: ");
        std::cin >> name;
    }


    fmt::print("\nchoose category: \n");

    int j = 0;
    for (std::string cat : categories) {
        std::cout << j++ << ". " << cat << std::endl;
    }

    std::cout << ": ";
    int indx;
    do {
        std::cin >> indx;
    } while (indx < 0 && indx >= categories.size());

    fmt::print("\nwebsite (optional): "); std::cin.ignore(); std::getline(std::cin, website);
    fmt::print("\nlogin (optional): "); std::getline(std::cin, login);



    passwords[name].push_back(password);

    passwords[name].push_back(*std::next(categories.begin(), indx));
    passwords[name].push_back(website);
    passwords[name].push_back(login);
}

void add_category(std::set<std::string> &categories) {
    std::string cat;
    fmt::print("Add category: ");

    do {
        std::cin >> cat;
    } while(cat.empty());

    categories.insert(cat);
    std::cout << std::endl;
}

void delete_category(std::map<std::string, std::vector<std::string>> &passwords, std::set<std::string> &categories, const std::string &path, crypt::Keys &key) {
    get_categories_from_passwords(passwords, categories);

    if (categories.empty()) {
        std::cout << "no categories to delete!" << std::endl;
        return;
    }

    int j = 0;
    std::cout << "Delete passwords from category: " << std::endl;
    for (std::string i : categories) {
        std::cout << j++ << ". " << i << std::endl;
    }

    int index;
    do {
        std::cin >> index;
    } while (index > 0 && index < categories.size());

    std::string cat_del = *std::next(categories.begin(), index);

    std::erase_if(passwords, [&cat_del](const std::pair<std::string, std::vector<std::string>> &x) -> bool {
        return x.second[1] == cat_del;
    });

    categories.erase(cat_del);
    passwordIO::save_all_passwords(passwords, path, key);
}

void search_password(const std::map<std::string, std::vector<std::string>> &passwords) {

    std::cout << "search by (press enter to ): " << std::endl;
    std::string name;
    std::cin.ignore();
    std::cout << "name: "; std::getline(std::cin, name);

    std::string category;
    std::cout << "category: "; std::getline(std::cin, category);

    std::string website;
    std::cout << "website: "; std::getline(std::cin, website);

    std::string login;
    std::cout << "login: "; std::getline(std::cin, login);


    for (const std::pair data : passwords) {
        if ( (data.first == name || name.empty()) && (data.second[1] == category || category.empty())
        && (data.second[2] == website || website.empty()) && (data.second[3] == login || login.empty()))
            show_password(data);
        else if (name.empty() && category.empty() && website.empty() && login.empty())
            show_password(data);
    }
}

void delete_passwords(std::map<std::string, std::vector<std::string>> &passwords, const std::string &path, crypt::Keys &key) {

    if (passwords.empty()) {
        std::cout << "no passwords to delete!" << std::endl;
        return;
    }

    int j = 0;
    for (const std::pair i : passwords) {
        std::cout << std::to_string(j++) + ". ";
        show_password(i);
    }

    fmt::print("\nwrite numbers to delete: ");
    std::vector<int> indexes;

    std::string val;
    std::cin.ignore();
    std::getline(std::cin, val);
    std::istringstream stream(val);

    int indx;
    while (stream >> indx)
        if (indx >= 0 && indx < j)
            indexes.push_back(indx);

    fmt::print("\ndelete? ");
    for (int i : indexes) {
        std::cout << i << ", ";
    }
    fmt::print(" y/n: ");
    char c;
    std::cin >> c;

    if (c == 'y') {
        j=0;
        for (std::pair i : passwords) {
            if (std::find(indexes.begin(), indexes.end(), j++) != indexes.end()) {
                passwords.erase(i.first);
            }
        }
        passwordIO::save_all_passwords(passwords, path, key);
    }

}

void edit_password(std::map<std::string, std::vector<std::string>> &passwords, const std::string &path, crypt::Keys &key, std::set<std::string> &categories) {

    if (passwords.empty()) {
        std::cout << "no passwords to edit!" << std::endl;
        return;
    }

    int j = 0;
    for (std::pair data : passwords) {
        std::cout << j++ << ". ";
        show_password(data);
    }

    int index;
    std::cout << "select password: ";
    do {
        std::cin >> index;
    } while(index < 0 && index >= passwords.size());



    std::string name, category, website, login;

    fmt::print("set new value or press enter: \n");
    fmt::print("name: "); std::cin.ignore(); std::getline(std::cin, name);

    get_categories_from_passwords(passwords, categories);
    if (!categories.empty()) {
        std::cout << "category (write any key to choose category): ";
        std::getline(std::cin, category);
    }

    fmt::print("\nwebsite: "); std::getline(std::cin, website);
    fmt::print("\nlogin: ");  std::getline(std::cin, login);

    if (!website.empty())
        std::next(passwords.begin(), index)->second[2] = website;
    if (!login.empty())
        std::next(passwords.begin(), index)->second[3] = login;

    if (category.length() > 0) {
        int j=0;
        for (std::string i : categories) {
            fmt::print("{}. {}", j, i);
        }

        int index;
        do {
            std::cin >> index;
        } while (index < 0 || index >= categories.size());

        std::next(passwords.begin(), index)->second[3] = *std::next(categories.begin(), index);
    }


    if (!name.empty()) {
        auto nodeHandler = passwords.extract(std::next(passwords.begin(), index)->first);
        nodeHandler.key() = name;
        passwords.insert(std::move(nodeHandler));
    }

    passwordIO::save_all_passwords(passwords, path, key);

}

std::vector<std::vector<std::string>> map_to_vec(const std::map<std::string, std::vector<std::string>> &passwords) {
    std::vector<std::vector<std::string>> vec;
    for (std::pair i : passwords) {
        std::vector<std::string> temp;
        temp.push_back(i.first);
        for (std::string j : i.second)
            temp.push_back(j);
        vec.push_back(temp);
    }
    return vec;
}

void sort_passwords(std::map<std::string, std::vector<std::string>> &passwords) {

    std::vector<std::vector<std::string>> vec = map_to_vec(passwords);

    bool to_sort[] = {false, false, false, false, false};
    std::string temp;


    fmt::print("choose val: \n");
    fmt::print("\n by name (y/n): ");     std::cin >> temp; if (temp == "y") {to_sort[0] = true;}
    fmt::print("\n by password (y/n): "); std::cin >> temp; if (temp == "y") {to_sort[1] = true;}
    fmt::print("\n by category (y/n): "); std::cin >> temp; if (temp == "y") {to_sort[2] = true;}
    fmt::print("\n by website (y/n): ");  std::cin >> temp; if (temp == "y") {to_sort[3] = true;}
    fmt::print("\n by login (y/n): ");    std::cin >> temp; if (temp == "y") {to_sort[4] = true;}


    std::ranges::sort(vec.begin(), vec.end(), [to_sort](auto x, auto y) -> bool {

        // name - 0, pass - 1, cat - 2, web - 3, login - 4;
        for (int i=0; i < 5; i++) {
            if (to_sort[i]) {
                if (x[i] < y[i])
                    return true;
                else if (x[i] > y[i])
                    return false;
            }
        }
        return false;
    });

    for (auto &i : vec) {
        std::cout << "name: " << i[0];
        std::cout << " password: " << i[1];
        std::cout << " category: " << i[2];

        if (!(i[3].length() == 1 && i[3][0] == 1))
            std::cout << " website: " << i[3];

        if (!(i[4].length() == 1 && i[4][0] == 1))
            std::cout << " login: " << i[4];

        std::cout << std::endl;
    }

}

bool menuUtility::show_gui(std::map<std::string, std::vector<std::string>> &passwords, std::set<std::string> &categories, const std::string &choosed_file, crypt::Keys &key) {
    switch (show_menu()) {
        case 1: {
            search_password(passwords);
            break;
        }
        case 2: {
            sort_passwords(passwords);
            break;
        }
        case 3: {
            add_password(passwords, categories);
            passwordIO::save_all_passwords(passwords, choosed_file, key);
            break;
        }
        case 4: {
            edit_password(passwords, choosed_file, key, categories);
            break;
        }
        case 5: {
            delete_passwords(passwords, choosed_file, key);
            break;
        }
        case 6: {
            add_category(categories);
            break;
        }
        case 7: {
            delete_category(passwords, categories, choosed_file, key);
            break;
        }
        case 8: return false;
    }
    return true;
}