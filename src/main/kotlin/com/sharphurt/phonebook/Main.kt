package com.sharphurt.phonebook

import com.sharphurt.phonebook.viewController.ViewController

fun main() {
    val io = ViewController({ message: String -> print(message)}, { (readLine() ?: "").trimStart() }, "> ")
    io.run()
//    model.add(Person(1, "jack", "6777", "ppp@gmail.com"))
//    model.add(Person(2, "pasha", "6924", "wojgps@gmail.com"))
//    model.add(Person(3, "misha", "6173890", "pgbufhejid@yandex.com"))
//    model.add(Person(4, "sukhrab", "666", "shs@mail.com"))
}
