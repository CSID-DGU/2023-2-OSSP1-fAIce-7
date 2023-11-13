<template>
    <div class="background-setting">
        <div class="frame-header">
            <div class="font-h1-box">
                <div class="font-h1-img"></div>
                <div class="font-h1">Manager</div>
            </div>
            <div style="clear:both;"></div>
            <div 
                :class="{'selected-btn':state.btnState==='user', 'unselected-btn':state.btnState!=='user'}"
                @click="btnStateApply('user')">유저</div>
            <div 
                :class="{'selected-btn':state.btnState==='payment', 'unselected-btn':state.btnState!=='payment'}"
                @click="btnStateApply('payment')">결제</div>
            <div 
                :class="{'selected-btn':state.btnState==='matching', 'unselected-btn':state.btnState!=='matching'}"
                @click="btnStateApply('matching')">매칭</div>
            <div 
                :class="{'selected-btn':state.btnState==='noshow', 'unselected-btn':state.btnState!=='noshow'}"
                @click="btnStateApply('noshow')">노쇼</div>
            <div 
                :class="{'selected-btn':state.btnState==='reported', 'unselected-btn':state.btnState!=='reported'}"
                @click="btnStateApply('reported')">신고</div>
        </div>
        <!---관리자 페이지 기본(유저)--->
        <div class="frame-body">
            <div v-if="state.btnState==='user'">
                <User/>
            </div>
            <!---관리자 페이지 결제--->
            <div v-else-if="state.btnState==='payment'">  
                <Payment/>
            </div>

            <!---관리자 페이지 매칭--->
            <div v-else-if="state.btnState==='matching'">
                <Matching/>
            </div>
            <div v-else-if="state.btnState==='noshow'">
                <Noshow/>
            </div>
            <div v-else-if="state.btnState==='reported'">
                <Reported/>
            </div>
            <div v-else class="title"> 
                메뉴를 선택해 주세요!
            </div>
        </div>
    </div>
</template>

<script>
    import User from './component/User.vue'
    import Payment from './component/Payment.vue'
    import Matching from './component/Matching.vue'
    import Noshow from './component/Noshow.vue'
    import Reported from './component/ReportedNoshow.vue'
    export default {
        components:{
            User,
            Payment,
            Matching,
            Noshow,
            Reported,
        },
        data() {
            return {
                state: {
                    btnState: 'user'
                }
            }
        },
        methods: {
            btnStateApply(btnState){
                if(this.state.btnState === btnState){
                    this.state.btnState = ''
                }
                else{
                    this.state.btnState = btnState
                }
            }
        }
    }
</script>

<style lang="scss" scoped>
    .background-setting{
        width: 100vw;
        height: 100vh;
        margin:0;

        display: flex;
        flex-direction: row;
    }
    .frame-header{
        width: 249px;
        height: 100vh;

        display: block;
        
        background: rgba(236, 188, 118, 0.6);
        .font-h1-box{
            margin-top: 40px;
            margin-left: 47px;
            margin-right: 40px;
            padding-bottom:90px;
        }
        .font-h1-img{
            width: 50px;
            height: 50px;
            
            float: left;

            background-image: url("../../assets/admin/adminuser.png");
            background-size: cover;
            background-repeat: no-repeat;
        }
        .font-h1{
            width: 108px;
            height: 50px;

            float: left;

            display: flex;
            align-items: center;
            text-align: center;

            font-style: normal;
            font-weight: 600;
            font-size: 20px;
            line-height: 24px;
        }
        .selected-btn{
            width: 137px;
            height: 50px;

            margin-top: 39px;
            margin-left: 54px;
            margin-right: 58px;

            cursor: pointer;

            background: #B87514;
            border-radius: 10px;

            display: flex;
            align-items: center;
            justify-content: center;

            font-style: normal;
            font-weight: 700;
            font-size: 16px;
            line-height: 24px;      
        }
        .unselected-btn{
            width: 137px;
            height: 50px;

            margin-top: 39px;
            margin-left: 54px;
            margin-right: 58px;

            cursor: pointer;

            background: #ECBC76;
            border-radius: 10px;

            display: flex;
            align-items: center;
            justify-content: center;

            font-style: normal;
            font-weight: 700;
            font-size: 16px;
            line-height: 24px;      
        }
    }
    .frame-body{
        width: 70vw; // computed 사용해서 px계산 후 적용할 예정
        height: 100vh;

        overflow:hidden;

        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        .title{
            width: 500px;
            height: 2000px;

            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;

            font-style: normal;
            font-weight: 700;
            font-size: 50px;
            line-height: 24px;
            color: #ECBC76;
        }
    }

</style>