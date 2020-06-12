# 로또명당지도

## Intro
본 어플리케이션 [나눔로또](https://dhlottery.co.kr/common.do?method=main)에서 제공하는 로또 판매점의 누적 당첨수를 기반으로 순위를 나눠어 해당 결과를 제공합니다. 이제는 더 이상 로또명당을 찾기 위해 헤매지마세요🥳

## Overview
| 로그인 | 아리수맵 |
| :---: | :---: | 
| <img src="./image/Overview1.gif" width="200"> |    <img src="./image/Overview2.gif" width="200">    

## Architecture
<p align="center">
     <img src="./resource/Architecture.png" width="500"/>
</p>

<p align="center">
    [Project Architecture]
</p>

## Function
- 현 위치기반 로또판매점 검색
- 지역 검색을 통한 로또판매점 검색
- 로또 판매점 정보 (순위, 전화번호, 당첨내역 등)

## Not Yet
- 데이터 갱신에 따른 FCM Push Message
- 이번 주 당첨점 정보

## Library
- GoogleMap (Map)
- GoogleGeocoding (Location search)  
- Gson (Json convert)
- Retrofit2 (Network)
- Rxjava2 (Async)
- Koin (DI)
- Stetho (Debug)
- Logger (Logging)
- JUnit (Testing)
- Selenium (Crawling)
- Pandas (Data Mutate)
- AWS Amplify (Mobile Back-end)
- AWS S3 (Storage)
- AWS IAM (Security)
- AWS Cognito (Security)

## About
- ### [구글 플레이스토어 다운로드](https://play.google.com/store/apps/details?id=com.ono.lotto_map)
- ### [개인정보처리방침](https://myungjunchae.github.io/android/%EC%95%B1-%EA%B0%9C%EC%9D%B8%EC%A0%95%EB%B3%B4%EC%B2%98%EB%A6%AC%EB%B0%A9%EC%B9%A8/)
