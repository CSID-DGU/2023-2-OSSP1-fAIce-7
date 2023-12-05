<template>
    <!-- MyPage  -->
    <div class="background-setting">
        <div class="container" >
            <div>
                <router-link to="/Starting" class="link-box">
                    <div class="link-img"></div>
                    <div class="link-text">
                        Co-kkirri
                    </div>
                </router-link>
                <div style="clear:both;"></div>
                <div class="frame-head">
                    <div class="user-img"></div>
                    <router-link to="/" v-if="!this.$store.state.isLogin" class="user-name">로그인</router-link>
                    <div v-else class="user-name">{{this.$store.state.name}}</div>
                    <router-link to="/my/profile" class="user-btn">내 정보</router-link>
                    <div style="clear:both;"></div>
                </div>
                <div class="frame-body">
                    <div>
                        <div class="heart-img"></div>
                            <div class="heart-txt">관심분야</div>
                            <router-link to="/InterestSettingsPage" class="heart-btn">재설정 &gt;</router-link>
                            <div style="clear:both;"></div>
                        <div class="line-for-division"></div>

                        <div class="timetable-img"></div>
                            <div class="timetable-txt">내 시간표</div>
                            <router-link to="/my/timetable" class="timetable-btn">등록 및 수정 &gt;</router-link>
                            <div style="clear:both;"></div>
                        <div class="line-for-division"></div>

                        <div class="matching-img"></div>
                            <div class="matching-txt">매칭 결과</div>
                            <router-link to="/my/matching" class="matching-btn">확인하기 &gt;</router-link>
                        <div style="clear:both;"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        data(){
            return{
                heart: ''
            }
        },
        mounted(){
            if (this.$store.state.isLogin) {
                this.$store.dispatch('fetchUserInterests');
            }
        },
    }
</script>

<style lang="scss" scoped>
    @import "../../scss/main";
    
    // 배경화면 설정
    .background-setting{
        height: 100vh;
        width: 100vw;
        margin:0;
        background-image: url("../../assets/mypage/background.png"); // 배경 이미지 적용
        background-size: cover; // 이미지가 배경 전체를 커버하도록 설정
        background-repeat: no-repeat; // 이미지가 반복되지 않도록 설정
        background-position: center center; // 이미지가 배경 중앙에 위치하도록 설정
        display: grid;
        align-items: center;
        justify-content: center;
    }
    // container 클래스 위치 조정
    .container{
        width: 996px;
        height: 650px;
        background-color: #FFFFFF; 
        display: grid;
        border: 7px solid #ECBC76; 
        border-radius: 20px;
        flex-direction: column; //행 방향 정렬
        align-items: center;
        justify-content: center;
    }
    .link-box{
        width: 125px;
        height: 21px;

        .link-img{
            width: 23px;
            height: 21px;
            
            margin-left: 20px;
            float: left;
            
            background-image: url("../../assets/mypage/home.png");
            background-size: cover;
            background-repeat: no-repeat;
        }
        
        .link-text{
            width: 100px;
            height: 21px;

            margin-left: 5px;
            float: left;

            font-weight: 600;
            font-size: 20px;
            line-height: 25px;
            color: #B87514;
        }
        
    }
    .link-box:hover .link-img{
            background-image: url("../../assets/mypage/dark-home.png");       
    }
    .link-box:hover .link-text{
            color: darken($color: #B87514, $amount: 30%);
    }
    .frame-head{
        width: 900px;
        height: 125px;

        margin-top: 28px;

        background-color: #FFFFFF;
        border-radius: 20px;
        border: 7px solid #ECBC76;
        display: flex;
        align-items: center;
        .user-img{
            width: 92px;
            height: 95px;
            margin-left: 30px;
            background-image: url("../../assets/mypage/user.png");
            background-size: cover;
            background-repeat: no-repeat;
            float: left;
        }
        .user-name{
            width: 200px;
            height: 57px;
            margin-left: 0px;

            font-size: 35px; 
            display: flex;
            justify-content: center;
            align-items: center;
            float: left;
        }
        .user-btn{
            width: 163px;
            height: 55px;
            
            margin-left: 370px;
            float: left;

            text-decoration: none;

            border-radius: 20px;
            background-color: #ECBC76;

            display: flex;
            justify-content: center;
            align-items: center;

            
            font-style: normal;
            font-weight: 400;
            font-size: 23px;
            line-height: 28px;
        }
        .user-btn:hover {
                background-color: darken($color: #B87514, $amount: 10%);
        }
    }
    .frame-body{
        width: 900px;
        height: 397px;
        margin-top: 20px;
        background-color: #FFFFFF;
        border: 7px solid #ECBC76;
        border-radius: 20px;
        display: flex;

        .heart-img{
            width: 43px;
            height: 40px;
            margin-top: 40px;
            margin-left: 50px;
            background-image: url("../../assets/mypage/heart.png");
            background-size: cover;
            
            float: left;
        }
        .heart-txt{
            width: 180px;
            height: 40px;
            margin-top: 40px;
            margin-left: 30px;
            float: left;

            font-size: 30px; 
            display: flex;
            align-items: center;
        }
        .heart-btn{
            width: 530px;
            height: 40px;
            margin-top: 40px;
            float:left;

            text-decoration: none;

            font-size: 30px;
            color: #B87514;
            display: flex;
            justify-content: right;
            align-items: center;
        }
        .heart-btn:hover{
            color: darken($color: #B87514, $amount: 20%);
        }
        .timetable-img{
            width: 40px;
            height: 40px;
            margin-top: 40px;
            margin-left: 49px;
            background-image: url("../../assets/mypage/timetable.png");
            background-size: cover;
            background-repeat: no-repeat;
            float: left;
        }
        .timetable-txt{
            width: 180px;
            height: 40px;
            margin-top: 40px;
            margin-left: 30px;
            float: left;

            font-size: 30px; 
            display: flex;
            align-items: center;
        }
        .timetable-btn{
            width: 530px;
            height: 40px;
            margin-top: 40px;
            float:left;

            text-decoration: none;

            font-size: 30px;
            color: #B87514;
            display: flex;
            justify-content: right;
            align-items: center;
        }
        .timetable-btn:hover{
            color: darken($color: #B87514, $amount: 20%);
        }
        .matching-img{
            width: 40px;
            height: 40px;
            margin-top: 40px;
            margin-left: 49px;
            background-image: url("../../assets/mypage/matching.png");
            background-size: cover;
            background-repeat: no-repeat;
            float: left;
        }
        .matching-txt{
            width: 180px;
            height: 40px;
            margin-top: 40px;
            margin-left: 30px;
            float: left;

            font-size: 30px; 
            display: flex;
            align-items: center;
        }
        .matching-btn{
            width: 530px;
            height: 40px;
            margin-top: 40px;
            float:left;

            text-decoration: none;

            font-size: 30px;
            color: #B87514;
            display: flex;
            justify-content: right;
            align-items: center;
        }
        .matching-btn:hover{
            color: darken($color: #B87514, $amount: 20%);
        }
        .line-for-division{
            width: 810px;
            height: 1px;
            margin-top: 39px;
            margin-left: 40px;

            border: 1px solid #B87514
        }
    }

</style>