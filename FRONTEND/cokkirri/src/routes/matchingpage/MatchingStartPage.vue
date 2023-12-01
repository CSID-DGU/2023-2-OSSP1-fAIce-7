<template>
    <!-- 매칭 신청 페이지 -->
    <div class="background-setting">
        <div class="container" >
            <div class="frame-first-step-body">
                <div class="frame-sub">
                    <div class="font-h1">매칭</div>
                </div>
                <div style="clear:both;"></div>
                <div class="font-h4">
                    <br>* 매칭 완료 시 자동으로 채팅방이 생성되며, 생성된 채팅방은 24시간동안 유지됩니다.</div>
                <div class="matching-submit-btn" @click="submitMatching()">매칭 신청</div>
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
                    courseNumber: this.courseNumber,
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
    .background-setting{
        height: 100vh;
        width: 100vw;
        margin:0;

        background-color: #FFFEF9;
        display: grid;
        grid-template-rows: auto;
        justify-items: center;
        align-items: center;
    }
    // container 클래스 위치 조정
    .container{
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .frame-first-step-body{
        width: 996px;
        height: 625px;

        border: 5px solid #ECBC76;
        border-radius: 20px;
        .shadow{
            box-shadow: 0 5px #B87514;
        }

        .font-h4{
            width: 986px;
            height: 112px;

            margin-top: 0px;

            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;

            
            font-style: normal;
            font-weight: 500;
            line-height: 38px;
            font-size: 25px;
            color: #B87514;
        }
        .matching-submit-btn{
            width: 400px;
            height: 60px;
            
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

        .frame-sub{
            width: 301px;
            height: 401px;

            margin-top: 23px;
            margin-left: 13px;

            padding-left: 0px;
            float: left;

            .font-h1{
                width: 130px;
                height: 73px;

                float: left;

                display: flex;
                align-items: center;
                justify-content: center;

                
                font-style: normal;
                font-weight: 700;
                line-height: 75px;
                font-size: 50px;
                color: #B87514;
            }
            .font-h2{
                width: 301px;
                height: 34px;

                margin-top: 14px;
                float: left;

                display: flex;
                align-items: center;
                justify-content: left;

                
                font-style: normal;
                font-weight: 500;     
                line-height: 38px;
                font-size: 25px;
                color: #B87514;
            }
            .font-h3{
                width: 301px;
                height: 34px;

                margin-top: 14px;

                display: flex;
                align-items: center;
                justify-content: left;

                
                font-style: normal;
                font-weight: 500;
                line-height: 38px;
                font-size: 25px;
                color: #B87514;
            }
            .matching-mode-btn-1{
                width: 100px;
                height: 100px;
                
                background: #ECBC76;
                border-radius: 20px;

                margin-top: 10px;
                float: left;

                cursor: pointer;

                display: flex;
                align-items: center;
                justify-content: center;

                
                font-style: normal;
                font-weight: 700;
                line-height: 45px;
                font-size: 30px;
                color: #000000;
            }
            .matching-mode-btn-2{
                width: 100px;
                height: 100px;
                
                margin-left: 54px;
                background: #ECBC76;
                border-radius: 20px;

                margin-top: 10px;
                float: left;

                cursor: pointer;

                display: flex;
                align-items: center;
                justify-content: center;

                
                font-style: normal;
                font-weight: 700;
                line-height: 45px;
                font-size: 30px;
                color: #000000;
            }
            .matching-numset-btn-1{
                width: 100px;
                height: 100px;
                
                background: #ECBC76;
                border-radius: 20px;

                margin-top: 10px;
                float: left;

                cursor: pointer;

                display: flex;
                align-items: center;
                justify-content: center;

                
                font-style: normal;
                font-weight: 700;
                line-height: 45px;
                font-size: 30px;
                color: #000000;
            }
            .matching-numset-btn-2{
                width: 100px;
                height: 100px;
                
                margin-left: 54px;
                background: #ECBC76;
                border-radius: 20px;

                margin-top: 10px;
                float: left;

                cursor: pointer;

                display: flex;
                align-items: center;
                justify-content: center;

                
                font-style: normal;
                font-weight: 700;
                line-height: 45px;
                font-size: 30px;
                color: #000000;
            }
        }
    }
</style>