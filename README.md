# 2023-2-OSSP1
2023년 2학기, 공개SW프로젝트, 01분반, fAIce팀, 7조

## 팀원 소개
|이름|전공|Email|
|:-:|:-:|:-:|
|[박찬혁](https://github.com/PetterChanHyuk)|컴퓨터공학전공|pchishahaboy@naver.com|
|[박은우](https://github.com/ewoo14)|컴퓨터공학전공|ewoo2821@gmail.com|
|[박상은](https://github.com/sangeun0612)|컴퓨터공학전공|sss1123634@naver.com|
|[이주환](https://github.com/jhan0121)|수학과|jhan0121@gmail.com|

- - - - - - - - - - - - - - - - - - - - - - - - - - - -
## FrameWork
<div>
    <img src="https://img.shields.io/badge/Vue.js-4FC08D?style=flat&logo=Vue.js&logoColor=white"/>
  	<img src="https://img.shields.io/badge/Java-007396?style=flat&logo=Java&logoColor=white" />
    <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=Spring Boot&logoColor=white" />
</div>  

## Server
<div>
    <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=flat&logo=amazonec2&logoColor=white"/>
</div>

## Stack
<div>
    <img src="https://img.shields.io/badge/CSS3-1572B6?style=flat&logo=CSS3&logoColor=white"/>
    <img src="https://img.shields.io/badge/SCSS-1867C0?style=flat&logo=CSS3&logoColor=white"/>
    <img src="https://img.shields.io/badge/JS-7DF1E?style=flat&logo=jss&logoColor=white"/>
    <img src="https://img.shields.io/badge/JPA-6DB33F?style=flat&logo=Spring Boot&logoColor=white" />
</div> 

## OpenSource
<div>
    <img src="https://img.shields.io/badge/STMP-FC7E0F?style=flat&logo=SMTP&logoColor=white"/>
    <img src="https://img.shields.io/badge/SSE-F43E37?style=flat&logo=SSE&logoColor=white"/>
    <img src="https://img.shields.io/badge/WebSocket-010101?style=flat&logo=socketdotio&logoColor=white"/>
</div>


## 기반 프로젝트 주소
2023년, 1학기, 공개SW프로젝트, 02분반, 우리만4명이조, 4조,
https://github.com/CSID-DGU/2023-1-OSSP2-4ofUs-4


## 1. 프로젝트 주제
<div>
<h4> Co-끼리(대학생 캠퍼스 네트워크 형성을 위한 익명 매칭 서비스)
</div>

## 2. 개요
<p>
        복학 후 친구가 없거나 친구를 사귀기 힘들어 하는 학생을 위해 관심분야를 작성하면 그것을 분석하여 일회성이 아닌 오랫동안 연락할 수 있게 최적의 매칭을 시켜주는 서비스이다.

</p>

## 3. 프로젝트 구조도

![스크린샷 2023-11-18 190400](https://github.com/CSID-DGU/2023-2-OSSP1-fAIce-7/assets/137492766/e6cc2f5b-356a-426f-b28a-b56581657407)

## 4. 주요기능
1) 유사도 알고리즘 + 매칭 알고리즘
   1. 유사도 알고리즘: 변형 자카드 + 카테고리 트리 분류 방식
      ![image](https://github.com/CSID-DGU/2023-2-OSSP1-fAIce-7/assets/137492766/d25cdbf6-6749-493f-ad6a-00f54b19be48)
      <P>
      사용자마다 가변적인 항목 개수로 인해 관심분야가 너무 적어 합집합이 작아지는 경우에는 예기치 않게 과대평가가 되기도 하고 관심분야의 수가 너무 많으면 반대로 과소평가되는 문제를 해결하고자 자카드의 분모를 합집합이 아닌 사용자의 입력 수의 평균과 분산을 분석해서 범위를 정하고 그 범위 안에서만 움직이게 설정한다. 
      </P>
![image](https://github.com/CSID-DGU/2023-2-OSSP1-fAIce-7/assets/137492766/ff3d818b-de39-4e20-a7f3-694af17213d0)
      <P>
      위 사진처럼 통계청에 나온 항목들을 3계층으로 항목을 분류하였다.
      </P>
      <p>
      1계층: 카테고리 분류(ex. 스포츠, 문화예술...)
      <p>
      2계층: 관심분야 분류(ex. 축구, 야구, 농구...)
      <p>
      3계층: 행동분류(ex. 축구하기, 축구관람, 연극...)
      </p>
      <p>
      계층을 다 분류된 관심분야들은 각각 값을 가지게 된다. (ex. 참여-축구: 000, 관람-축구: 001, 참여-미술: 301...)
      </p>
      사용자끼리 각 항목별로 계층에서 나온 값을 앞부터 비교하다가 값이 달라지면 멈추고 지금까지 계산한 값을 합산하고 비교한 횟수로 나눠서 평균을 내준다.(입력한 관심분야 개수에 영향을 받지 않기위해)
<p>      
   2. 매칭 알고리즘: 게일-섀플리 알고리즘
</p>

### 요약
   자카드로 동일성을 판단하여 1차 분류를 하고 카테고리 트리 분류에서 유사성을 판단해서 자카드에서 동점이 나왔던 항목들을 구별한다. 그리고 이를 우선순위를 매긴후 게일-섀플리 알고리즘으로 안정매칭을 한다.

## 5. 스크린샷
![image](https://github.com/CSID-DGU/2023-2-OSSP1-fAIce-7/assets/137492766/feea95e8-d0b9-41f3-8bfe-b60c50332e35)
    <P>
      관심분야 설정 페이지
      </p>
      ![image](https://github.com/CSID-DGU/2023-2-OSSP1-fAIce-7/assets/137492766/b3686f00-daa9-4432-9382-013ca3099baa)
    <P>
      매칭 신청 페이지
      </P>
      ![image](https://github.com/CSID-DGU/2023-2-OSSP1-fAIce-7/assets/137492766/0c6e4d52-4f91-40c5-84c8-aa5948cfbce1)
    <P>
      매칭 완료 페이지
      </P>
      
## 6. 평가
![image](https://github.com/CSID-DGU/2023-2-OSSP1-fAIce-7/assets/137492766/fdd26bd2-641f-4228-8945-72be5b146f0d)
    <P>
      자카드, 카테고리, (자카드+카테고리) 유사도 점수 그래프
      </P>
