# SSOK! (쏙)

**고속도로 위, 맛있는 쉼표.**

SSOK!은 고속도로 휴게소의 대표 먹거리와 가격을 한눈에 살펴보고, 휴게소 검색부터 전체 메뉴 탐색까지 이어주는 차량용 앱입니다.


## 1. 주요 기능과 구현 범위

- **게스트 로그인**: 별도 회원가입 없이 Home으로 이동하며, 앱 재실행 후에도 게스트 상태를 유지합니다.
- **Home**: 서버에서 가까운 휴게소와 편의시설 정보를 불러옵니다. 휴게소 카드의 `+` 버튼으로 메뉴를 열고, 가격순·이름순으로 정렬할 수 있습니다.
- **Search**: 휴게소 이름을 검색하고 결과를 선택하면 대표 메뉴의 사진·이름·가격을 확인할 수 있습니다. `전체 메뉴 보기`에서 다른 메뉴와 가격을 세로 터치 스크롤로 탐색합니다.
- **최근 검색**: 검색 결과에서 선택한 휴게소를 최대 10개까지 저장하며, 앱 재실행 후에도 복원합니다.
- **My**: 방문 휴게소와 메뉴 기록을 가로 터치 스크롤로 확인합니다. 현재 표시되는 방문·식사 기록과 월별 `8회`는 목데이터입니다.
- **화면 대응**: 넓은 앱 영역은 좌측 탭, 좁은 앱 영역은 하단 탭으로 Home·Search·My를 전환합니다.

### 현재 MVP의 데이터 및 연동 범위

- Home 위치는 `FakeLocationProvider`의 고정 시연 좌표를 사용합니다. 실제 차량의 현재 위치를 수집하지 않습니다.
- Home의 혼잡도 표시는 시연용 데이터이며, 실시간 휴게소 혼잡도 측정 결과가 아닙니다.
- 휴게소·메뉴 조회는 실제 서버 API를 사용합니다. 현재 서버 제공 범위는 경부고속도로 부산 방향입니다.
- Pleos 차량 위치·음성 안내·내비게이션 경유지 연결, 관심 메뉴 보관함, 소셜 로그인은 개발 예정입니다.
- 게스트 상태와 최근 검색 기록은 기기에 저장됩니다. 앱 데이터 삭제 또는 앱 제거 시 초기화됩니다.

## 2. 실행 방법

### Android Studio에서 실행

1. 저장소를 클론합니다.

   ```sh
   git clone https://github.com/jelleoo/reststop-mate-android.git
   ```

2. Android Studio에서 `reststop-mate-android` 폴더를 **Open**합니다.
3. SDK Manager에서 **Android SDK Platform 35**를 설치합니다.
4. Gradle JDK를 **JDK 21**로 지정한 뒤 Gradle Sync를 실행합니다.
5. Pleos Connect 에뮬레이터를 실행하고, 실행 대상으로 선택한 뒤 `app`을 Run합니다.
6. 앱에서 **게스트로 시작하기**를 누르면 Home으로 이동합니다. 이후 Search·My 탭으로 이동할 수 있습니다.

현재 저장소가 비공개라면 GitHub 계정에 저장소 접근 권한이 있어야 클론할 수 있습니다.

### 터미널에서 APK 빌드

Windows PowerShell에서는 저장소 루트에서 실행합니다. `JAVA_HOME`은 각자의 JDK 21 설치 경로로 설정합니다.

```powershell
.\gradlew.bat :app:assembleDebug
```

macOS / Linux:

```sh
chmod +x gradlew
./gradlew :app:assembleDebug
```

빌드가 완료되면 아래 경로에 APK가 생성됩니다.

```text
app/build/outputs/apk/debug/app-debug.apk
```

### APK 직접 설치 및 실행

Android SDK의 `platform-tools`에 있는 `adb`를 사용할 수 있도록 PATH를 설정합니다. 에뮬레이터가 여러 개면 `adb -s <기기 ID>`로 설치 대상을 지정합니다.

```sh
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

설치 후 에뮬레이터 앱 목록에서 **휴게소 메이트**를 실행합니다. Pleos에서 여러 사용자 프로필이 있다면 현재 운전자 프로필의 앱 목록을 확인해 주세요.

## 3. 테스트 환경 및 필요한 설정

### 개발·빌드 환경

- 언어·UI: Kotlin 2.0.21, Jetpack Compose, Material 3
- 빌드 검증 JDK: **21**
- Android Gradle Plugin: 8.6.1 / Gradle Wrapper: 8.7
- compileSdk: **35** / targetSdk: **34** / minSdk: **28**
- applicationId: `com.yourssu.reststopmate`
- 앱 버전: `0.1.0` (versionCode 1)

### 필요한 설정

- `local.properties`의 `sdk.dir`은 각자의 Android SDK 설치 경로를 사용합니다. 이 파일은 Git에 커밋하지 않습니다.
- 서버 데이터와 메뉴 사진 조회에는 **인터넷 연결**이 필요합니다.
- API 기본 주소는 `https://3.34.5.244/`입니다. 현재 MVP 실행에 별도 API 키나 로그인 계정 입력은 필요하지 않습니다.
- 로그인·My 목데이터는 서버 조회와 독립적입니다. Home·Search의 서버 목록과 메뉴 조회는 API 서버의 응답 상태에 영향을 받습니다.
- 현재 위치는 고정 시연 좌표이므로 GPS 경로나 실제 차량 정보를 입력하지 않아도 실행할 수 있습니다.

### 확인한 테스트 범위

최신 Home·Search 통합 기능의 전체 시나리오는 일반 **Android 14 / API 34 에뮬레이터**에서 검증했습니다.

- 화면: **1600 × 1000 px**, **160dpi**, 소프트웨어 GPU
- 게스트 로그인 → Home 진입 및 Home·Search·My 탭 전환
- Home 메뉴 진입, 가격 정렬, 뒤로가기 및 가로 터치 드래그
- 한글 휴게소 검색 → 대표 메뉴 → 전체 메뉴 및 세로 터치 스크롤
- 앱 강제 종료·재실행 후 게스트 상태 및 최근 검색 기록 복원

## 4. Pleos Connect 에뮬레이터 버전 및 화면 규격

개발에 사용한 Pleos AVD 설정은 다음과 같습니다.

- 에뮬레이터: **Pleos Connect v2.1.0**
- 시스템 이미지: **Pleos Connect v2.1.0 APIs Intel x86_64 Atom System Image**
- Android: **14 / API Level 34**
- ABI: **x86_64**
- 화면: **2560 × 1440 px**, 가로형
- 화면 밀도: **160dpi**

위 해상도는 에뮬레이터 전체 디스플레이 기준입니다. 시스템 UI와 앱 창 배치에 따라 앱이 실제 사용하는 영역은 달라지며, 앱은 사용 가능한 너비에 맞춰 탭과 목록 배치를 변경합니다.

## 5. APK 파일 위치

- 파일: [`app/build/outputs/apk/debug/app-debug.apk`](app/build/outputs/apk/debug/app-debug.apk)
- 기획서에도 링크 첨부하였습니다.
- 용량: 약 **30.8 MB**

