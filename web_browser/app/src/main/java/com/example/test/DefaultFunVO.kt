package com.example.test


class DefaultFunVO {
    var funList: ArrayList<Funtion> = ArrayList()

    constructor() {
        // 검색해줘 명령어 (구글, 네이버, 다음, 네이트, 빙, 야후, 유튜브,)
        funList.add(
            Funtion(
                "m.naver.com",
                Command(
                    "*검색해 줘",
                    "#location.href='https://m.search.naver.com/search.naver?sm=mtp_sly.hst&where=m&query=*';"
                ),
                "음성 검색"
            )
        )
        funList.add(
            Funtion(
                "m.search.naver.com",
                Command(
                    "*검색해 줘",
                    "#location.href='https://m.search.naver.com/search.naver?sm=mtp_sly.hst&where=m&query=*';"
                ),
                "음성 검색"
            )
        )
        funList.add(
            Funtion(
                "www.google.com",
                Command("*검색해 줘", "#location.href='https://www.google.com/search?q=*';"),
                "음성 검색"
            )
        )
        funList.add(
            Funtion(
                "m.daum.net",
                Command(
                    "*검색해 줘",
                    "#location.href='https://search.daum.net/search?w=tot&DA=YZR&t__nil_searchbox=btn&sug=&sugo=&sq=&o=&q=*';"
                ),
                "음성 검색"
            )
        )
        funList.add(
            Funtion(
                "m.search.daum.net",
                Command(
                    "*검색해 줘",
                    "#location.href='https://search.daum.net/search?w=tot&DA=YZR&t__nil_searchbox=btn&sug=&sugo=&sq=&o=&q=*';"
                ),
                "음성 검색"
            )
        )
        funList.add(
            Funtion(
                "m.nate.com",
                Command("*검색해 줘", "#location.href='https://m.search.daum.net/nate?q=*';"),
                "음성 검색"
            )
        )
        funList.add(Funtion("m.search.daum.net/nate", Command("*검색해 줘", "#location.href='?q=*';"), "음성 검색"))
        funList.add(
            Funtion(
                "www.yahoo.com",
                Command("*검색해 줘", "#location.href='https://search.yahoo.com/search?p=*';"),
                "음성 검색"
            )
        )
        funList.add(
            Funtion(
                "search.yahoo.com",
                Command("*검색해 줘", "#location.href='https://search.yahoo.com/search?p=*';"),
                "음성 검색"
            )
        )
        funList.add(
            Funtion(
                "www.bing.com",
                Command("*검색해 줘", "#location.href='https://www.bing.com/search?q=*';"),
                "음성 검색"
            )
        )
        funList.add(
            Funtion(
                "m.youtube.com",
                Command("*검색해 줘", "#location.href='https://m.youtube.com/results?search_query=*';"),
                "음성 검색"
            )
        )


    }
}

class Funtion {
    lateinit var url: String
    lateinit var command: Command
    lateinit var description: String

    constructor(url: String, command: Command, description: String) {
        this.url = url
        this.command = command
        this.description = description
    }
}