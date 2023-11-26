<template>
    <!-- 매칭 신청 페이지 -->
    <div class="background-setting">
        <div class="container" >
            <div class="frame-first-step-body">
                <div class="frame-sub">
                    <div class="font-h1">매칭</div>
                    <div class="font-h2">#이런 친구는 어때요?</div>
                    <div style="clear:both;"></div>
                    <div class="matching-mode-btn-1" :class="{shadow: matchingType === 'class'}" @click="clickedBtnMatchingTypeClass">수업</div>
                    <div class="matching-mode-btn-2" :class="{shadow: matchingType === 'free'}" @click="clickedBtnMatchingTypeFree">공강</div>
                    <div style="clear:both;"></div>
                    <div class="font-h3"># 몇 명이서 모일까요?</div>
                    <div class="matching-numset-btn-1" :class="{shadow: headCount === '2'}" @click="clickedBtnHeadCountTwo">2명</div>
                    <div class="matching-numset-btn-2" :class="{shadow: headCount === '4'}" @click="clickedBtnHeadCountFour">4명</div>
                    <div style="clear:both;"></div>
                </div>
                <DetailInput 
                    :selectedMatchingType="matchingType" 
                    @update:date="availableDay = $event"
                    @update:starttime="startTime = $event"
                    @update:endtime="endTime = $event"
                    @update:course="courseNumber = $event"
                    :style="{'margin-top': '23px', float: left}"/>
                <div style="clear:both;"></div>
                <div class="font-h4">
                    * ‘동의합니다' 버튼을 누르면, 자동으로 매칭에 참여되며 매칭 수수료로 하트 3개가 소진됩니다.
                    <br>* 매칭 완료 시 자동으로 채팅방이 생성되며, 생성된 채팅방은 24시간동안 유지됩니다.</div>
                <div class="matching-submit-btn" @click="checkHeartCount()">매칭 신청</div>
            </div>
        </div>
    </div>
</template>

<script>
import axios from '../../api/index.js'
import DetailInput from './component/DetailInput.vue'
export default {
    components:{
        DetailInput
    },
    data(){
        return{
            // 인원수
            headCount: '',
            // 매칭 신청 ["이메일"]
            email: this.$store.state.id,
            // 희망 날짜 "2023-05-20"
            availableDay: '',
            //
            startTime: '',
            endTime: '',
            // 매칭 타입: 공강 - "free", 수업: "class"
            matchingType: '',
            // 매칭 학수번호
            courseNumber: []
        }
    },
    methods: {
        checkHeartCount(){
            this.$store.dispatch('userInfoUpdate')
            console.log("하트개수: "+this.$store.state.heart+"개 확인 완료")
            if(this.$store.state.heart >= 10){
                this.submitMatching()
                this.$store.dispatch('userInfoUpdate')
                console.log("사용자 정보 자동 업데이트")
            }else{
                alert("하트 개수가 "+this.$store.state.heart+"개 입니다."+"10개 이상 소지하고 있어야 매칭 신청이 가능합니다")
            }
        },
        clickedBtnHeadCountTwo(){
            this.headCount = '2'
        },
        clickedBtnHeadCountFour(){
            this.headCount = '4'
        },
        clickedBtnMatchingTypeFree(){
            this.matchingType = 'free'
        },
        clickedBtnMatchingTypeClass(){
            this.matchingType = 'class'
        },
        submitMatching(){
            if(this.$store.state.restrctionDate === null){
                if(this.headCount!==''){
                    if(this.matchingType==='free'){
                        if(this.availableDay!=='' & this.startTime!=='' & this.endTime!==''){
                            this.resisterMatchingFree()
                        }
                        else{
                            alert("날짜 및 시간이 설정되지 않았습니다.")
                        }
                    }
                    else if(this.matchingType==='class'){
                        if(this.courseNumber.length!==0){
                            this.resisterMatchingClass()
                        }
                        else{
                            alert("학수번호가 선택되지 않았습니다.")
                        }
                    }
                    else{
                        alert("매칭 타입이 설정되지 않았습니다.")
                    }
                }
                else{
                    alert("인원이 설정되지 않았습니다.")
                }
            }
            else{
                console.log("규제 기간 확인됨: "+this.$store.state.restrctionDate)
                const dateTemp = new Date(this.$store.state.restrctionDate.thString())
                alert("현재 사용자는 과거 노쇼를 한 기록으로 인해 " + dateTemp.getFullYear() + "년 " + dateTemp.getMonth() + "월 " + dateTemp.getDay() + "일 " + dateTemp.getHours() + "시 " + dateTemp.getMinutes() + "분까지 매칭이 금지된 상태입니다.")
            }


        },
        async resisterMatchingFree(){
            try{
                await axios.post('/matching/free',{
                        headCount: this.headCount,
                        email: this.email,
                        availableDay: this.availableDay,
                        startTime:this.startTime,
                        endTime:this.endTime,
                        matchingType:"free"
                }).then(()=>{
                    this.$store.dispatch('callMatchingRecord')
                    this.$router.replace('/my/matching');
                    alert("공강 매칭 신청 완료")
                }).catch(function(error){
                    console.log(error)
                })
            } catch(error){
                console.log(error)
            }
        },
        async resisterMatchingClass(){
            try{
                await axios.post('/matching/class',{
                    headCount: this.headCount,
                    email: this.email,
                    courseNumber: this.courseNumber,
                    matchingType:"class"
                }).then(()=>{
                    this.$store.dispatch('callMatchingRecord')
                    this.$router.replace('/my/matching');
                    alert("수업 매칭 신청 완료")
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
        background-image: url("../../assets/mypage/background.png"); // 배경 이미지 적용
        background-size: cover; // 이미지가 배경 전체를 커버하도록 설정
        background-repeat: no-repeat; // 이미지가 반복되지 않도록 설정
        background-position: center center; // 이미지가 배경 중앙에 위치하도록 설정
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
        background: #FFFFFF;
        border-radius: 20px;
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