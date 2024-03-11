#include <string>
#include <vector>
#include <fstream>
#include <map>
#include <iostream>

#include "cryptography.h"
#include "file_io.h"

/**
 *
 * @param data
 * @param p - length of return string
 * @return return number in string with constant length (0 value)
 */
std::string int_to_string(const int data, const int p) {

    std::string temp;
    std::string data_string = std::to_string(data);

    for (int i=0; i < (p - data_string.size()); i++)
        temp += "0";

    return temp + data_string;
}

std::string get_time_line(const int line) {

    time_t ttime = time(0);
    tm *gm_time = gmtime(&ttime);

    switch (line) {
        case 11:
            return int_to_string(gm_time -> tm_hour+2, 2);
        case 22:
            return int_to_string(gm_time -> tm_min, 2);
        case 33:
            return int_to_string(gm_time -> tm_sec, 2);
        default:
            return "";
    }
}

/**
 *
 * @param vec vector contains all lines
 * @param file
 * @param d true = delete timestamps
 */
void get_all_lines(std::vector<std::string> &vec, std::fstream &file, const bool d = true) {

    int line = 1;
    for (std::string temp; getline(file, temp); line++) {
        if((line == 11 || line == 22 || line == 33) && d)
            temp.erase(0,2);

        if(temp.length() > 0)
            vec.push_back(temp);
    }
}


void set_timestamp(const std::string& path, const bool d = true) {
    std::vector<std::string> save_lines;

    std::fstream file = std::fstream(path, std::ios::in);

    get_all_lines(save_lines, file, d);

    file.close();

    file = std::fstream(path, std::ios::out);

    for (int i=0; i < 33 || i < save_lines.size(); i++) {

        file << get_time_line(i+1);

        if (i < save_lines.size())
            file << save_lines[i] << "\n";
        else
            file << "\n";
    }
    file.close();
}

void passwordIO::save_all_passwords(std::map<std::string, std::vector<std::string>> &data, const std::string &path, crypt::Keys &key) {

    std::fstream file = std::fstream(path, std::ios::out);

    // \1 - null (optional) data
    for(auto & i : data) {
        std::string name = i.first;
        std::string pass = i.second[0];
        std::string category = i.second[1];
        std::string website = i.second[2].empty() ? "\1" : i.second[2];
        std::string login = i.second[3].empty() ? "\1" : i.second[3];

        encode(name, key);
        encode(pass, key);
        encode(category, key);
        encode(website, key);
        encode(login, key);

        file << name << "\n"; // name
        file << pass << "\n"; // pass
        file << category << "\n"; // category
        file << website  << "\n"; // website
        file << login  << "\n"; // login
    }

    file.close();

    set_timestamp(path, false);

}

void passwordIO::get_all_passwords(std::map<std::string, std::vector<std::string>> &data, const std::string &path, crypt::Keys &key) {
    std::vector<std::string> lines;
    std::fstream file = std::fstream(path, std::ios::in);

    get_all_lines(lines, file);

    if (lines.size() % 5 != 0) {
        std::cout << "incorrect data";
        return;
    }


    for (int i=0; i < lines.size(); i+=5) {
        std::vector <std::string> temp_vec;

        temp_vec.push_back(lines[i+1]);
        temp_vec.push_back(lines[i+2]);

        if (lines[i+3].length() == 1 && lines[i+3][0] == 1)
            temp_vec.push_back("");
        else
            temp_vec.push_back(lines[i+3]);

        if (lines[i+4].length() == 1 && lines[i+4][0] == 1)
            temp_vec.push_back("");
        else
            temp_vec.push_back(lines[i+4]);

        for (auto &data : temp_vec) {
            decode(data, key);
        }

        decode(lines[i], key);

        data[lines[i]] = temp_vec;
    }

    set_timestamp(path);

}