# build.gradle 추가 설정
```
sourceSets {
    main {
        java {
            srcDirs += "build/generated/sources/annotationProcessor/java/main"
        }
    }
}
```

## build.gradle 파일을 STS4의 코드 에디터창에서 오픈하고 오른쪽 마우스 버튼 클릭 후, gradle -> refresh gradle project 선택 

# 터미널에서 다음 커맨드 라인 실행
```
./gradlew clean build
```

# 빌드
## Window -> Show view -> Gradle Tasks 선택
### noticeboard 확장 후, clean 더블 클릭 후 build 더블 클릭 실행
 
