<template>
    <div class="background-setting">
        <div class="container">
            <div>
                <div class="frame-body">
                    <div>
                        <router-link to="/my" class="my-link">&lt;</router-link>
                        <div style="clear:both;"></div>
  
                        <div class="matching-img-box">
                            <div class="matching-img"></div>
                        </div>
                        
                        <div class="matching-txt"> 매칭 신청 </div>

                        <button class="a-btn" > 수정 </button>
                        <div style="clear:both;"></div>

                        <div class="line-for-division"></div>

                        <div class="frame-sub-body">
                            <div class = "interest-settings">
                                <div class = "sub-title"> 나의 관심 분야</div>
                                <div v-if="filteredInterests.length === 0" class="no-interests-message"> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 입력된 취미가 존재하지 않습니다. <br> (정확한 매칭을 위해 취미를 설정해주시기 바랍니다.) </div>
                                <div class="interest-list">
                                        <ul>
                                        <!-- 관심 분야 목록만 출력 -->
                                            <li v-for="(interest, index) in filteredInterests" :key="index">
                                                <span> {{ index+1 }}. &nbsp; </span>
                                                <span class="print-interest"> {{ interest }} </span>
                                            </li>
                                        </ul>
                                </div>
                            </div>
                        </div>
                        <div class="matching-submit-btn" @click="submitMatching">매칭 신청</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from '../../api/index.js'
export default {
    mounted(){
            if (this.$store.state.isLogin) {
                this.$store.dispatch('fetchUserInterests');
            }
        },
    data(){
        return{
            // 인원수
            headCount: '2',
            // 매칭 신청 ["이메일"]
            email: this.$store.state.id,
            // 희망 날짜 "2023-05-20"
            availableDay: '',
            //
            startTime: '',
            endTime: '',
            // 매칭 타입: 공강 - "free", 수업: "class", 관심분야: "hobby"
            matchingType: 'hobby',
            // 매칭 학수번호
            courseNumber: []
        }
    },
    computed: {
        filteredInterests() {
            // 이메일 형식을 제외한 관심분야 목록만 필터링
            return this.$store.state.userInterests.filter(interest => {
                return typeof interest === 'string' && !interest.includes('@'); // 이메일 형식 제외
            });
        }
    },
    methods: {
        submitMatching(){
            if(this.$store.state.restrctionDate === null){
                    if(this.matchingType==='hobby'){
                        this.resisterMatchingHobby()
                    }else {
                        alert("매칭 오류")
                    }
            }
            else{
                console.log("규제 기간 확인됨: "+this.$store.state.restrctionDate)
                const dateTemp = new Date(this.$store.state.restrctionDate.thString())
                alert("현재 사용자는 과거 노쇼를 한 기록으로 인해 " + dateTemp.getFullYear() + "년 " + dateTemp.getMonth() + "월 " + dateTemp.getDay() + "일 " + dateTemp.getHours() + "시 " + dateTemp.getMinutes() + "분까지 매칭이 금지된 상태입니다.")
            }
        },
        
        async resisterMatchingHobby(){
            try{
                await axios.post('/matching/hobby',{
                    headCount: this.headCount,
                    email: this.email,
                    interests: this.filteredInterests,
                    matchingType:"hobby"
                }).then(()=>{
                    this.$store.dispatch('callMatchingRecord')
                    this.$router.replace('/my/matching');
                    alert("관심분야 매칭 신청 완료")
                }).catch(function(error){
                    console.log(error)
                })
            } catch(error){
                console.log(error)
            }
        }
    },
}
</script>

<style lang="scss" scoped>
    // 배경화면 설정
.background-setting {
    height: 100vh;
    width: 100vw;
    margin: 0;
    background-image: url("../../assets/mypage/background.png");
    background-size: cover;
    background-repeat: no-repeat;
    background-position: center center;
    display: grid;
    grid-template-rows: auto;
    justify-items: center;
    align-items: center;
}
    
.container{
    display: flex;
    flex-direction: column; //행 방향 정렬
    align-items: center;  //가로 방향 정렬
}
.frame-body{
    width: 996px;
    height: 630px;
    background-color: #FFFFFF;
    border: 7px solid #ECBC76;
    border-radius: 20px;

    .my-link{
        width: 51px;
        height: 46px;

        margin-top: 0px;
        margin-left: 17px;
        float:left;

        cursor: pointer;
        text-decoration: none;

        font-style: normal;
        font-weight: 400;
        font-size: 40px;
        line-height: 75px;
        color: #B87514;
        display: flex;
        align-items: center;
    }
    .my-link:hover{
        color: darken($color: #B87514, $amount: 20%);
    }
    .matching-img-box{
        width: 69px;
        height: 55px;
            
        margin-top: 0px;
        margin-left: 68px;
        float: left;

        display: flex;
        justify-content: left;
        align-items: center;
    }
    .matching-img{
        width: 50px;
        height: 40px;

        float: left;

        background-image: url("../../assets/mypage/puzzle.png");
        background-repeat: no-repeat;
    }
    .matching-txt{
        width: 200px;
        height: 55px;

        float: left;

        display: flex;
        justify-content: left;
        align-items: center;
            
        margin-left:-10px;
        font-style: normal;
        font-weight: 400;
        font-size: 30px;
        line-height: 45px;
    }
    .a-btn{
        width: 163px;
        height: 55px;
        background-color: #B87514;

        cursor: pointer;

        margin-top: 0px;
        margin-left: 444px;
        border-radius: 20px;
        float: left;

        color: #FFFFFF;
        display: flex;
        justify-content: center;
        align-items: center;

        font-style: normal;
        font-weight: 400;
        font-size: 23px;
        line-height: 28px;
    }
    .a-btn:hover {
            background-color: darken($color: #B87514, $amount: 10%);
    }
    .line-for-division{
        width: 891px;
        height: 1px;
        margin-top: 25px;
        margin-left: 53px;
        margin-bottom: 0px;

        background-color: #B87514;
        border: 0.8px solid #B87514;
    }
    .frame-sub-body{
        width: 870px;
        height: 350px;
        
        margin-left: 100px;
        margin-top: 30px; 

        background: #FFFFFF;
        border-radius: 20px;
        overflow-y: scroll;
        .interest-settings {
            width: 800px;
            height: auto;
            background-color: #FFFFFF;
            border: 7px solid #ECBC76;
            border-radius: 20px;
            padding: 20px;
            box-sizing: border-box;
            .sub-title{
                font-size: 2.0vw;
                color: #333;
                margin-top: 5px;
                margin-bottom: 20px;
            }
            .no-interests-message{
                display: flex;
                justify-content: center;
                //align-items: center;
                font-style: normal;
                font-weight: 500;
                font-size: 25px;
                line-height: 38px;
                color: #B87514;
            }    
            .interest-list {
                width: 70%;
                display: flex;
                align-items: center;
                justify-content: left;
                margin-left: 20px;
                ul {
                    list-style-type: none;
                    padding: 0;
                }
                li {
                    position: relative; /* 가상 요소의 위치 기준점 */
                    font-size: 1.8vw;
                    margin-bottom: 50px; /* 목록 간 간격 */
                    color: #333;
                }
                .print-interest{
                    padding: 10px; /* 내부 여백 */
                    box-sizing: border-box; /* 박스 크기 계산 방식 */
                    background-color: white; /* 박스 내부 색상 */
                    border: 1px solid #333; /* 테두리 색상 지정 */
                    border-radius: 10px; /* 둥근 꼭지점 */
                }
            }
        }
    }
    .matching-submit-btn {
        width: 400px;
        height: 60px;
        margin-top: 30px;
        margin-left: 293px;
        background: #B87514;
        border-radius: 50px;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        text-align: center;
        font-style: normal;
        font-weight: 700;
        font-size: 32px;
        color: #FFFFFF;
        line-height: 24px;
        letter-spacing: 0.5px;
    }
    .matching-submit-btn:hover {
                background-color: darken($color: #B87514, $amount: 10%);
    }
            //스크롤바 스타일
        .frame-sub-body::-webkit-scrollbar {  //스크롤바의 너비
            width: 8px;
        }
        .frame-sub-body::-webkit-scrollbar-track {  //트랙(바탕 부분)의 색
            width: 30px;
            background: #FFDBAA;
        }
        .frame-sub-body::-webkit-scrollbar-thumb {  //스크롤바의 이동 부분
            height: 10%;
            background-color: #B87514;  
            border-radius: 10px;
        }
}
</style>