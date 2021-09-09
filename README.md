# 음성제어 웹브라우저 회의록

### 2021.08.31 오리엔테이션

필요 물품은 없음 
계획서 
중간보고서 
최종보고서 
전시회 
시연 

### 2021.09.02 수업시간 조별 미팅

###### 1학기에 무엇을 했는가?

웹페이지 음성제어 라이브러리를 기획하고 프로토타입을 만들어보았습니다. 라이브러리를 사용하는 웹사이트에서만 음성제어를 사용할 수 있다는 단점이 존재하여 모든 웹페이지에서 음성제어를 사용할 수 있도록 웹브라우저를 만들기로 결정하였습니다. 마이크가 필요하기 때문에 모바일 웹브라우저를 만들기로 했습니다. 

###### 완성작을 위해서 필요한 일

개인화 기능(명령어 추가 및 공유 기능, 화자 인식) 

명령 인식률 증가를 위해 음성인식 인공지능 구현(교수님이 제일 관심있는 부분) 

UI/UX 개선 

###### 중요한 부분 3분 발표

1학기 동안 저희는 웹 페이지를 음성으로 조작할 수 있도록 만들어주는 자바스크립트 라이브러리를 만드는 것을 목표로 하였습니다. 

그러나 개발 과정에서 라이브러리가 범용적으로 적용될 수 없다는 한계가 있다고 생각하였고 비슷한 기느의 제품인 브라우저 확장 프로그램이나 라이브러리가 이미 존재함을 확인했습니다. 

그래서 저희는 개발 방향을 음성 제어를 할 수 있는 모바일 웹브라우저를 개발하는 것으로 바꾸었습니다. 

개발 방향을 바꾼 뒤 지도 교수님과의 회의를 진행하며 여름방학 동안 기본적인 기능을 구현한 프로토타입을 개발하였습니다. 

현재 완성작을 만들기 위해서 필요한 작업은 

첫번째로 개인화 기능의 완성입니다. 명령어를 추가하고 공유할 수 있는 기능, 그리고 화자 인식 등의 기능을 구현하는 것을 목표로 하고 있습니다. 

두번째로 명령 인식률 개선을 위한 보조 음성인식 인공지능의 구현입니다. 

마지막으로 개발 단계에서 만든 UI를 터치 없이 쓸 수 있는 것을 목표로 하는 사용자 중심 UI로 수정하여 음성 인식만으로 조작할 수 있는 모바일 웹 브라우저를 완성하는 것입니다. 

감사합니다. 

 

###### 발표 후 교수님 피드백:

보고서를 제출할 때 모바일 웹 브라우저를 음성으로 사용할 때 음성을 어떻게 사용하고, 활용하려고 하는지에 대해서 자세히 설명해 주길 바란다. 

10월말까지는 완성되어야 한다고 생각하고 개발 계획을 만들어야 한다. 

이슈1. 음성으로 시나리오나 유즈 케이스를 만들어서 보여달라 

이슈2. 어떤 웹킷을 사용해서 어떤 구조로 만들어 개발할 것인지 확실하게 담아서 제출하라 


### 2021.09.06 지도 교수님 미팅

어디까지 직접개발한 영역인지 기존에 있는 것을 가져다 쓴것인지를 명확히 구분하는 것이 필요하다.  

발표를 할 때 강점을 더 어필하자 

UI에 영어 써지말자 

두개로 나눈 명령어를  

사용자 피드백을 받아봐라 

1. 한글설명, 영어설명이 섞여있으면 난잡함이 생기니, UI의 일관성에 대해서 생각해볼 것. 

2. '선별 후 작업'에 대한 구현.(링크 클릭해 줘. 버튼 클릭해 줘) -> 자연스러운 명령어를 만드는 방법 

3. 사용자 명령과 기본제공 명령의 충돌이 발생할 경우에 대한 가이드를 만들어 둘 것. 

 

 

카테고리를 나눠서 주차보고를 하자 UI개선을 했다. 테스트를 했다. 유저 사용성을 개선해보았다. 

 

### 2021.09.09 수업 시간 회의

###### 1. 내용 변경 사항에 대해 디테일하게 언급하기 - 다시는 변경사항이 없도록

 주제변경: 음성제어 모바일 웹브라우저 

 터치하지 않고 음성 명령을 통해 웹브라우저를 사용하자 

 웹브라우저 기본 기능(스크롤, 북마크, 새로고침)을 음성 명령을 통해서 실행한다. 

 명령어를 등록해서 등록한 명령어 대로 웹브라우저가 동작한다. 

###### 2. 완성도를 높이는 데에 있어서 가장 어려운 부분 식별

 실제 사용자의 피드백을 들어보기 어려웠다. 사용성이 실제로 좋은지 

 모든 동작을 음성 명령으로 대체해야 하기 때문에 모든 상황에 대한 조사가 완전해야 한다. 

###### 3. 해당 부분 공략 전략

 친구, 가족들에게 실제로 사용을 요청하고 불편한 점을 조사하고 개선하겠습니다. 

 지속적인 웹브라우저 기능 조사와 사용자의 피드백 

대표 시연 시나리오(단숨에 설명이 가능해야 함) 

 음성 명령으로 ‘스크롤 내려’라고 했을때 스크롤이 내려가는 이벤트가 발생한다. 

 

###### 발표 후 교수님 피드백

화자 인식을 기술적으로 어렵지만 검토해보아라 

음성으로 웹브라우저를 제어해야만 하는 상황에 특화되게 기능을 강화하여라 

실제 사용자가 느낄 때 가장 불편한 점을 개선하여라 
