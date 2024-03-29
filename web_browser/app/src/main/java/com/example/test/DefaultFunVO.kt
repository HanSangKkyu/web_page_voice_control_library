package com.example.test


class DefaultFunVO {
    companion object{
        var funList: ArrayList<Funtion> = ArrayList()
    }

    constructor() {
        // 공통 기능
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "스크롤 내려",
                    "@scrollDown()"
                ),
                "스크롤 내려"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "스크롤 올려",
                    "@scrollUp()"
                ),
                "스크롤 올려"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "맨 위로",
                    "@scrollUpMax()"
                ),
                "맨 위로 스크롤을 이동시킨다"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "왼쪽으로",
                    "@scrollLeft()"
                ),
                "왼쪽으로 스크롤을 이동시킨다"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "옆으로,오른쪽으로",
                    "@scrollRight()"
                ),
                "오른쪽으 스크롤을 이동시킨다"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "크게",
                    "@zoomIn()"
                ),
                "화면 확대"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "작게",
                    "@zoomOut()"
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
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "*찾아",
                    "@findElementsInView(*)"
                ),
                "엘리먼트 찾기"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "다음요",
                    "@selectFindedElements(1)"
                ),
                "다음 엘리먼트 선택"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "이전요",
                    "@selectFindedElements(-1)"
                ),
                "이전 엘리먼트 선"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "마크 지우기",
                    "@clearFindedItemMarks()"
                ),
                "마크 지우"
            )
        )
        funList.add(
            Funtion(
                "공통 명령어",
                Command(
                    "클릭",
                    "@clickHere()"
                ),
                "클릭"
            )
        )








        // 검색해줘 명령어 (구글, 네이버, 다음, 네이트, 빙, 야후)
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

        // 유튜브 특화 명령어
        funList.add(
            Funtion(
                "m.youtube.com",
                Command("*검색해 줘", "#location.href='https://m.youtube.com/results?search_query=*';"),
                "음성 검색"
            )
        )
        funList.add(
            Funtion(
                "m.youtube.com",
                Command("구독으로", "#location.href='https://m.youtube.com/feed/subscriptions';"),
                "구독한 동영상 보기"
            )
        )
        funList.add(
            Funtion(
                "m.youtube.com",
                Command("기록 보기", "#location.href='https://m.youtube.com/feed/history';"),
                "기록 화면으로 이동"
            )
        )
        funList.add(
            Funtion(
                "m.youtube.com",
                Command("내 채널 보기", "#location.href='https://m.youtube.com/channel/UCEy_dpSm5NzlS6J6sLaFKZw/videos';"),
                "내 채널 보기"
            )
        )
        funList.add(
            Funtion(
                "m.youtube.com",
                Command("인기 동영상 보기", "#location.href='https://m.youtube.com/feed/trending?bp=6gQJRkVleHBsb3Jl';"),
                "인기 동영상 보기"
            )
        )
        funList.add(
            Funtion(
                "m.youtube.com",
                Command("음악 보기", "#location.href='https://m.youtube.com/channel/UC-9-kyTW8ZkZNDHQJ6FgpwQ';"),
                "음악 화면으로 이동"
            )
        )
        funList.add(
            Funtion(
                "m.youtube.com",
                Command("게임으로", "#location.href='https://m.youtube.com/gaming';"),
                "게임 화면으로 이동"
            )
        )
        funList.add(
            Funtion(
                "m.youtube.com",
                Command("스포츠 보기", "#location.href='https://m.youtube.com/channel/UCEgdi0XIXXZ-qJOFPf4JSKw';"),
                "스포츠 화면으로 이동"
            )
        )
        funList.add(
            Funtion(
                "m.youtube.com",
                Command("학습보기", "#location.href='https://m.youtube.com/channel/UCtFRv9O2AHqOZjjynzrv-xg';"),
                "학습 화면으로 이동"
            )
        )
        funList.add(
            Funtion(
                "m.youtube.com",
                Command("나중에 볼 동영상으로", "#location.href='https://m.youtube.com/playlist?list=WL';"),
                "나중에 볼 동영상 화면으로"
            )
        )

        // 만개의 레시피 특화 명령어
        funList.add(
            Funtion(
                "m.10000recipe.com",
                Command("*검색해 줘", "#location.href='https://www.10000recipe.com/recipe/list.html?q=*';"),
                "음성 검색"
            )
        )

        funList.add(
            Funtion(
                "m.10000recipe.com",
                Command("분석 탭으로", "#location.href='https://m.10000recipe.com/recipe/index.html?tab=2';"),
                "분석 탭으로 이동"
            )
        )


        funList.add(
            Funtion(
                "m.10000recipe.com",
                Command("랭킹 탭으로", "#location.href='https://m.10000recipe.com/ranking/home_new.html';"),
                "랭킹 탭으로 이동"
            )
        )

        funList.add(
            Funtion(
                "m.10000recipe.com",
                Command("매거진 탭으로", "#location.href='https://m.10000recipe.com/recipe/index.html?tab=6';"),
                "매거진 탭으로 이동"
            )
        )
        funList.add(
            Funtion(
                "m.10000recipe.com",
                Command("클래스 탭으로", "#location.href='https://m.10000recipe.com/class/home.html';"),
                "클래스 탭으로 이동"
            )
        )

        // 건대 e캠퍼스
        funList.add(
            Funtion(
                "ecampus.konkuk.ac.kr",
                Command("로그인 해", "#document.getElementById('usr_id').value = '아이디';document.getElementById('usr_pwd').value = '비밀번호';loginForm();"),
                "로그인"
            )
        )
        funList.add(
            Funtion(
                "ecampus.konkuk.ac.kr",
                Command("*라고 글 써 줘", "#document.getElementById('wrtTitle').value = '*';document.getElementById('TXT').value = '*';ins('http://ecampus.konkuk.ac.kr/ilos/m/community/share_insert.acl');"),
                "소모임 글쓰기"
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