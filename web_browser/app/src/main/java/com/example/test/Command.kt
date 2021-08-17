package com.example.test

class Command{
    lateinit var line : String // 명령어
    lateinit var function : String // 함수

    constructor(line : String, function : String) {
        this.line = line
        this.function = function
    }


}
