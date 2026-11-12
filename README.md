# 휴게소 메이트 — 게스트 로그인 MVP

Android Studio에서 이 폴더를 Open한 뒤 Gradle Sync하고 app을 실행하세요.
JDK 17 또는 21, Android SDK 35가 필요합니다. minSdk는 28입니다. 현재 빌드는 JDK 21 기준입니다. Android Studio의 Gradle JDK가 25라면 Settings > Build, Execution, Deployment > Build Tools > Gradle에서 JDK 21로 변경하세요.

## 동작

- 최초 실행: 저장 상태를 읽는 동안 로딩 → 로그인.
- 게스트로 시작하기: DataStore 저장 → Home으로 전환.
- 앱 강제 종료/재실행 및 화면 회전 후에도 게스트 유지.
- Home은 로그인 화면 위에 쌓이지 않으므로 뒤로가기로 로그인에 돌아가지 않음.
- 저장/조회 오류는 사용자에게 표시하고 재시도 제공.
- 카카오/네이버 버튼은 준비 중인 비활성 버튼. 실제 인증 미구현.
- 로그인 후 Home 진입. 공통 Home/Search/My 탭 제공.
- Home은 팀 추천 화면을 붙일 연결용 화면과 검색 바로가기를 제공.
- Search: 김천·매봉·문산 휴게소 목데이터와 이름 필터링.
- My: 자주 방문한 휴게소와 이번 달 메뉴 목데이터, 먹방 횟수 8회.
- 휴게소/메뉴 목록은 스크롤바 없는 LazyRow로 터치 드래그 및 플링 지원.
- My의 두 가로 목록은 독립적인 스크롤 위치를 유지하며 탭 전환 후에도 복원.
- 게스트 상태는 인증 토큰이 아니며 서버 인증 용도로 사용하지 않음.
- 앱 데이터 삭제/제거 시 상태 초기화. 자동 백업 비활성화.

## 구조

- `data/SessionRepository.kt`: Preferences DataStore 영구 저장.
- `ui/SessionViewModel.kt`: 초기 읽기, 중복 클릭 차단, 저장 오류 처리.
- `ui/login/LoginScreen.kt`: 로그인 화면과 게스트 시작 버튼.
- `ui/home/HomeScreen.kt`: Home 화면과 검색 바로가기.
- `ui/search/SearchScreen.kt`: 검색 입력과 검색 기록.
- `ui/my/MyScreen.kt`: 게스트 정보, 방문 휴게소, 월별 메뉴 기록.
- `ui/navigation/MainTabsScreen.kt`: 탭 상태와 화면 연결.
- `ui/navigation/TabMenu.kt`: 공통 가로/세로 탭 메뉴.
- `ui/components/`: 공통 휴게소 카드 목록과 제목.
- `ui/theme/Colors.kt`: 공통 색상.
- `data/mock/MockData.kt`: 방문 기록과 메뉴 목데이터.
- `MainActivity.kt`: 세션 상태에 따라 로그인/Home을 선택하는 루트.

Figma 토큰의 배경 #F7F8FA, 본문 #131417, 게스트 버튼 #44464E 및
카카오·네이버 로고 원본 SVG를 사용했습니다. 첫 실행 화면은 앞서 합의한
앱 소개·게스트 진입 흐름으로 구성하고, 초안의 탭 메뉴·Label은 제거했습니다.
폰트는 플랫폼 기본 sans-serif를 사용합니다(Asta Sans 폰트 파일 미제공).

## 확인할 시나리오

1. 앱 데이터 초기화 후 실행하면 로그인 표시.
2. 게스트 버튼을 연속 클릭해도 한 번만 시작.
3. Home에서 뒤로가기를 누르면 로그인 없이 종료.
4. 앱 강제 종료 후 다시 실행하면 로그인 없이 Home 표시.
5. 가로/세로 전환 및 시스템 글꼴 확대 시 버튼과 오류 문구 확인.

Pleos 위치·음성 SDK와 네트워크가 없어도 로그인 기능은 실행됩니다.

## 에뮬레이터 확인 결과

Pleos Connect v2.1.0에서 APK 설치 후 게스트 로그인 → Home,
Search/My/Home 탭 이동, Search 목록과 My의 두 목록의 터치 드래그,
메뉴 역방향 드래그, 앱 강제 종료 후 게스트 Home 복원을 확인했습니다.
화면과 UI 계층 기록은 `verification/tabs-*`에 있습니다.
현재 메뉴·방문 기록과 8회 표시는 고정 목데이터이며 실제 이력 수집은 미구현입니다.


## 팀 작업 규칙

각 화면 담당자는 ui/login, ui/home, ui/search, ui/my에서 작업합니다.
공통 navigation, components, theme와 목데이터 변경은 담당자 간 먼저 공유하세요.
main은 빌드 가능한 상태로 유지하고 feat/login, feat/home, feat/search, feat/my처럼
기능 브랜치에서 작업한 뒤 Pull Request로 합칩니다.
화면 내부에서 직접 탭 상태나 게스트 저장소를 만들지 말고 전달받은 상태와 콜백을 사용합니다.
소셜 로그인과 실제 휴게소 API는 아직 연결되지 않았습니다.
