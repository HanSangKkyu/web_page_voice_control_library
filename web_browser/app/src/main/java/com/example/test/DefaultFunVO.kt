package com.example.test


class DefaultFunVO {
    var funList: ArrayList<Funtion> = ArrayList()

    constructor() {
        // 공통 기능
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "스크롤 내려",
                    "#scrollTo(document.documentElement.scrollTop, document.documentElement.scrollTop+200);"
                ),
                "스크롤 내려"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "스크롤 올려",
                    "#scrollTo(document.documentElement.scrollTop, document.documentElement.scrollTop-200);"
                ),
                "스크롤 올려"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "크게",
                    "#if(document.body.style.zoom==\"\"){\n" +
                        "        document.body.style.zoom = 110+\"%\";\n" +
                        "    }else{\n" +
                        "        var zoom = document.body.style.zoom.toString().substring(0, document.body.style.zoom.toString().indexOf(\"%\"));\n" +
                        "        console.log(zoom);\n" +
                        "        document.body.style.zoom = (parseInt(zoom)+10)+\"%\";\n" +
                        "    }"
                ),
                "화면 확대"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "작게",
                    "#if(document.body.style.zoom==\"\"){\n" +
                            "        document.body.style.zoom = 90+\"%\";\n" +
                            "    }else{\n" +
                            "        var zoom = document.body.style.zoom.toString().substring(0, document.body.style.zoom.toString().indexOf(\"%\"));\n" +
                            "        console.log(zoom);\n" +
                            "        document.body.style.zoom = (parseInt(zoom)-10)+\"%\";\n" +
                            "    }"
                ),
                "화면 축소"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "이전 페이지",
                    "#history.back();"
                ),
                "이전 페이지"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "다음 페이지",
                    "#history.forward();"
                ),
                "다음 페이지"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "새로 고침",
                    "#location.reload();"
                ),
                "새로 고침"
            )
        )
        // 공통기능 -> 안드로이드 함수
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "새 탭",
                    "@makeNewTab()"
                ),
                "새 탭 만들기"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "다음 탭",
                    "@showNextTab()"
                ),
                "다음 탭으로 이동"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "이전 탭",
                    "@showPreviousTab()"
                ),
                "이전 탭으로 이동"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "탭 닫기",
                    "@closeTab()"
                ),
                "현재 탭 닫기"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "북마크 추가",
                    "@addBookmark()"
                ),
                "북마크 추가"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "볼륨 업",
                    "@volUp()"
                ),
                "소리 크기 키우기"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "볼륨 다운",
                    "@volDown()"
                ),
                "소리 크기 줄이기"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "리스트",
                    "@startlistingElement(*)"
                ),
                "태그 포커스 모드 시작"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "다음",
                    "@moveElementList(1)"
                ),
                "포커스를 다음으로 이동"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "이전",
                    "@moveElementList(-1)"
                ),
                "포커스를 이전으로 이동"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "리스트 종료",
                    "@endListingElement()"
                ),
                "리스트 종료"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "재생",
                    "@play()"
                ),
                "현재 포커스 비디오 재생"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "정지",
                    "@pause()"
                ),
                "현재 포커스 비디오 정지"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "네이버 보여 줘",
                    "@goToSite(m.naver.com)"
                ),
                "포털 사이트 이동"
            )
        )



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