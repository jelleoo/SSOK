# AGENTS.md

## 프로젝트와 지침 적용

이 저장소는 현대자동차그룹 차량용 앱 공모전을 위한 휴게소 메이트 Android 앱이다.
작업 전 이 파일, README.md, 요청과 관련된 소스와 Gradle 설정을 읽는다.
사용자의 현재 요청과 명시적으로 승인한 범위를 우선하고, 하위 폴더에 AGENTS.md가 있으면 해당 범위의 지침도 확인한다.
새 기능은 기존 구조에 맞춰 필요한 파일만 수정한다. Kotlin과 Jetpack Compose를 사용한다.

## 처음 작업할 때

1. `git rev-parse --show-toplevel`, `git status --short --branch`, `git remote -v`로 작업 위치와 변경 상태를 확인한다.
2. 원격 저장소는 현재 `https://github.com/jelleoo/reststop-mate-android.git`이다. 조직 이전 등으로 주소가 바뀌었다면 실제 origin을 기준으로 한다.
3. 다른 사람의 미커밋 변경은 보존한다. 자동으로 reset, checkout 복구, stash, force push를 실행하지 않는다.
4. 사용자 요청에서 담당 화면과 완료 기준을 파악한다. 명확한 작업은 그대로 진행하고, 겹치는 변경이나 필요한 정보가 있을 때만 확인한다.
5. 코드 수정은 기능 브랜치에서 진행한다. main이라면 변경을 보존한 채 작업명에 맞는 새 브랜치를 만든다. 기존 작업 브랜치에서는 요청과의 관련성을 확인한다.
6. 코드를 고치기 전에 작업 범위를 한국어로 짧게 설명한다. Git 초보자도 이해하도록 변경과 확인 결과를 설명한다.

## 개발 환경

- 단일 Android app 모듈. 패키지 및 applicationId: `com.yourssu.reststopmate`.
- Jetpack Compose, Material 3, ViewModel, Preferences DataStore.
- Gradle wrapper 8.7, Android Gradle Plugin 8.6.1, Kotlin 2.0.21.
- 검증 기준 JDK 21. compileSdk 35, targetSdk 34, minSdk 28.
- Android Studio의 Gradle JDK와 터미널 JAVA_HOME을 확인한다. JDK 25 이상에서 발생한 빌드 오류를 소스 오류로 판단하지 않는다.
- 버전의 최종 기준은 현재 Gradle 파일이다. 요청과 무관한 SDK/플러그인/라이브러리 업그레이드를 하지 않는다.
- Android Studio에서 저장소 루트를 Open하고 Gradle Sync한다. local.properties는 각자의 Android SDK 경로로 생성하며 커밋하지 않는다.

## 화면 담당별 파일

아래 경로는 `app/src/main/java/com/yourssu/reststopmate/` 기준이다.

| 담당 | 주 수정 파일 | 책임 |
| --- | --- | --- |
| 로그인 | ui/login/LoginScreen.kt | 로그인 UI, 게스트 버튼, 소셜 로그인 준비 중 표시 |
| 게스트 상태 | ui/SessionViewModel.kt, data/SessionRepository.kt | 초기 로딩, 저장과 복원, 중복 클릭과 오류 처리 |
| Home | ui/home/HomeScreen.kt | Home 콘텐츠와 검색 바로가기 |
| Search | ui/search/SearchScreen.kt | 검색 입력, 필터, 검색 결과와 빈 상태 |
| My | ui/my/MyScreen.kt | 게스트 정보, 방문 휴게소, 월별 먹방 목록 |
| 탭 연결 | ui/navigation/MainTabsScreen.kt | 선택한 탭, 검색어, 각 목록의 스크롤 상태, 뒤로가기 |
| 공통 메뉴 | ui/navigation/TabMenu.kt | 좌측 메뉴와 좁은 화면의 하단 메뉴 |
| 공통 카드 | ui/components/RestAreaRow.kt | 휴게소 카드와 터치 가로 목록 |
| 공통 제목 | ui/components/Headings.kt | 페이지/영역 제목 |
| 공통 색상 | ui/theme/Colors.kt | 색상과 Material 색상 구성 |
| 목데이터 | data/mock/MockData.kt | 방문 기록과 메뉴 목데이터 |
| 앱 진입 | MainActivity.kt | 세션 상태에 따른 로그인/메인 분기 |

이미지는 `app/src/main/res/drawable/`, 로그인 로고는 `app/src/main/res/raw/`에 있다.
공통 파일을 변경할 때는 이를 사용하는 화면을 함께 살핀다. 함수 인자를 바꾸면 모든 호출부를 갱신하고 변경 이유를 결과에 적는다.
화면 담당 작업 중 다른 화면의 외관, 공통 탐색 구조, 패키지 구조를 불필요하게 바꾸지 않는다.

## 유지해야 하는 동작

- 최초 실행은 세션 확인 로딩 후 로그인 화면이다.
- 게스트 버튼은 DataStore 저장 성공 후 Home으로 이동한다. 버튼 연속 입력을 막고 저장 오류를 표시한다.
- 앱 강제 종료와 재실행 후에도 게스트 상태를 유지한다. 상태를 읽는 중 로그인 화면을 잠깐 노출하지 않는다.
- 로그인 화면을 Home의 뒤로가기 기록에 남기지 않는다.
- 로그인 직후 기본 탭은 Home이다. Home/Search/My로 전환할 수 있다.
- Search/My에서 뒤로가면 Home, Home에서 뒤로가면 시스템의 기본 종료 동작을 따른다.
- 검색어와 목록별 스크롤 위치를 탭 전환 시 보존한다. 같은 상태를 여러 화면에 중복 생성하지 않는다.
- 휴게소/메뉴 목록은 LazyRow의 터치 드래그와 플링을 사용한다. 사용자 요청 없이 스크롤바나 휠 전용 동작으로 바꾸지 않는다.
- My의 방문 휴게소 목록과 메뉴 목록은 각각 독립적으로 스크롤된다.
- 폭이 좁으면 하단 메뉴, 넓으면 좌측 메뉴를 사용한다. Figma 픽셀 좌표를 고정 배치하는 대신 Compose 레이아웃을 사용한다.

## 승인된 화면 콘텐츠

- 김천휴게소: `3일 전 방문`
- 매봉휴게소: `2주 전 방문`
- 문산휴게소: `방문 기록 없음`
- My의 첫 목록 제목은 `자주 방문한 휴게소`이며 휴게소 이름과 방문 문구만 표시한다.
- 다음 영역 제목은 `이번 달 휴게소 먹방 횟수`, 횟수는 목데이터 `8회`이다.
- 먹방 영역의 가로 카드에는 메뉴 사진, 휴게소 이름, 메뉴 이름을 표시한다.
- 메뉴 목데이터는 김천휴게소의 매운철판돈까스와 매봉휴게소의 해물라면이다. 반복 카드는 서로 다른 목데이터 ID를 유지한다.
- 사용자가 삭제한 `미리보기 예시`, `선택됨 · 상세 화면은 이후 연결`, My의 `게스트 이용 기록은 이 기기에 저장됩니다.` 문구를 다시 넣지 않는다.
- Figma 댓글/협업자 아바타/선택 테두리는 앱 UI로 구현하지 않는다.

## 목데이터와 실제 연동 구분

현재 Home은 추천 기능을 연결하기 위한 임시 콘텐츠이다. Search는 /api/rest-stops API로 검색하며, 선택한 휴게소의 최근 검색 기록을 별도 DataStore에 저장한다. 소셜 로그인과 실제 방문/식사 기록 수집은 미구현이다.
My의 방문·식사 기록과 8회는 고정 목데이터이다. Search의 실제 최근 검색 기록과 구분하며, My의 기록이 실제 측정되었다고 보고하지 않는다.
Pleos SDK의 사용 가능 상태는 프로젝트 권한과 에뮬레이터 버전에 따라 확인한다. FusedLocation, TextToSpeech, Gleo 등의 연동을 확인 없이 동작한다고 가정하지 않는다.
현재 로그인/탭/목데이터 화면 작업에 Pleos SDK나 새 백엔드를 불필요하게 추가하지 않는다.
향후 위치 기능에서 Fleet API를 로컬 위치 SDK와 동일하게 취급하지 않는다. 도로 정체와 휴게소 내부 혼잡도도 서로 다른 데이터이다.

## 코드 작성 기준

- 기존 컴포넌트와 색상을 재사용한다. UI, 상태 처리, 저장소, 목데이터의 책임을 유지한다.
- 화면 이동은 전달받은 콜백으로 처리하고, 각 화면에서 새 탭 상태나 게스트 저장소를 만들지 않는다.
- 영구 저장은 SessionRepository에, 세션 상태 처리는 SessionViewModel에 둔다.
- 한 목록에 독립된 LazyListState를 사용하고 안정적인 item key를 유지한다.
- 기존 로고/음식 이미지를 사용한다. 임시 Figma URL을 앱 실행 시 네트워크 이미지 주소로 사용하지 않는다.
- 함수/파일을 분리한 뒤에는 package, import, 접근 제한자와 호출부를 확인한다.

## 빌드와 동작 확인

코드 변경 후 저장소 루트에서 필요한 APK 빌드를 한 번 실행한다.

Windows PowerShell:
```powershell
.\gradlew.bat :app:assembleDebug
```

macOS/Linux:
```sh
./gradlew :app:assembleDebug
```

문서만 변경한 작업에는 Android 빌드를 요구하지 않는다.
테스트가 존재하면 변경과 관련된 테스트를 실행한다. 의미 없는 테스트나 구현을 그대로 복제하는 테스트를 추가하지 않는다.
UI/탐색/저장 동작을 변경했다면 가능할 때 해당 흐름을 에뮬레이터에서 확인한다.

기본 확인 목록:
1. 첫 로그인에서 게스트 시작 후 Home 진입.
2. Home/Search/My 전환과 검색어/스크롤 상태 유지.
3. Search 필터 및 결과 없음 상태.
4. My의 두 가로 목록을 터치로 각각 이동하고 반대 방향으로 복귀.
5. 앱 강제 종료 후 재실행 시 로딩 후 게스트 Home 복원.

에뮬레이터가 없으면 빌드 결과와 실행 미확인 범위를 구분해서 보고한다.
Pleos는 운전자 사용자 ID가 시스템 사용자와 다를 수 있다. `adb shell am get-current-user`로 확인한다.
저장 상태 초기화는 사용자에게 필요한 작업임이 명시적으로 승인된 경우에만 수행한다.
`verification/check-tabs.ps1`은 앱 데이터를 초기화하는 검사이므로 내용을 읽고 사용한다.
에뮬레이터의 느린 로딩을 곧바로 기능 실패로 판단하지 말고 제한된 시간 안에서 준비 상태를 재확인한다.
이전 작업자의 검증 기록을 이번 변경의 검증 결과로 재사용하지 않는다.

## Git과 PR

- 작업 브랜치 예: feat/home-recommendation, feat/search-filter, feat/my-history, fix/login-layout.
- 새 브랜치는 작업 시작 시 최신 main을 기준으로 만든다. 미커밋 변경이 있으면 이를 보존하고 안전한 방식으로 진행한다.
- 본인 작업 파일만 커밋한다. 변경된 파일을 확인하지 않고 일괄 커밋하지 않는다.
- 커밋 예: `feat: add rest area search filter`, `fix: preserve guest session`.
- main에 직접 push하거나 PR을 병합하는 것은 명시적으로 요청받았을 때만 한다.
- PR의 base는 main, compare는 작업 브랜치이다. 같은 작업의 수정은 같은 PR에 추가 커밋한다.
- PR 설명에는 변경 내용, 확인한 빌드/동작, UI 변경 시 화면 자료, 남은 제약을 적는다.
- 인증 토큰, API 키, 비밀번호, local.properties, 개인 SDK 경로, .gradle, build 결과와 개인 설정은 커밋하지 않는다.
- .gitignore를 유지한다. 공유가 필요한 설정은 실제 비밀값 대신 예시와 설정 방법을 문서화한다.

## 작업 완료 보고

한국어로 짧게 작성한다.
- 바뀐 기능과 파일
- 실제 실행한 빌드/테스트/에뮬레이터 확인 결과
- 확인하지 못한 범위 또는 남은 연동
- 커밋/PR/업로드를 수행했다면 해당 링크
