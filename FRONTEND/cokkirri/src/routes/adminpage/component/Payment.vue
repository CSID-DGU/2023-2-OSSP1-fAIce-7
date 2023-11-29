<template>
    <div>
        <div class="frame-main">
            <div class="title-h1">결제 기록 관리</div>
            <div class="line-for-division"></div>
            <div class="frame-sub-head">
                <div class="btn-loadPaymentList" @click="loadPaymentList()">전체 조회</div>

                <div class="btn-searchPaymentById" @click="searchPaymentById()">아이디 검색</div>
                <input type="text" placeholder="Search Id" class="input-searchPaymentById" v-model="searchingId" @change="searchPaymentById()">
                <div style="clear:both;"></div>
            </div>
            <div class="frame-sub-body">
                <div class="line-for-division"></div>
                <div class="attribute-id">결제 번호</div>
                <div class="attribute-detail">결제 계정</div>
                <div class="attribute-detail">결제 날짜</div>
                <div class="attribute-detail">하트 개수</div>
                <div style="clear:both;"></div>
                <div class="line-for-division"></div>

                <div v-for="(payment,index) in paymentList" :key="index">
                    <div class="content-row-id">{{payment.id}}</div>
                    <div class="content-row-detail">{{payment.userId}}</div>
                    <div class="content-row-detail">{{payment.payDate}}</div>
                    <div class="content-row-detail">{{payment.amount/200}}</div>
                    <div style="clear:both;"></div>
                </div>
            </div>
            <div class="line-for-division"></div>
        </div>
    </div>
</template>

<script>
    import axios from '../../../api/index.js'
    export default {
        data(){
            return{
                paymentList: [],
                searchingId: '',
            }
        },
        methods:{
            // 모든 결제내역 불러오기
            async loadPaymentList(){
                try{
                    await axios.get('/admin/payment')
                    .then((result)=>{
                        console.log("결제내역 출력")
                        this.paymentList = result.data
                        console.log(result)
                    })
                    .catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            },
            // 아이디로 결제내역 검색하기
            async searchPaymentById(){
                try{
                    await axios.get('/admin/user/payment',{
                        params:{
                            userId: this.searchingId
                        }
                    })
                    .then((result)=>{
                        //console.log(this.searchingId+"의 결제내역 조회 요청")
                        this.paymentList = result.data
                        //console.log(result)
                    })
                    .catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            }
        },
        mounted(){
            this.loadPaymentList()
        }
    }
</script>

<style lang="scss" scoped>
        .frame-main{
            width: 600px;
            height: 600px;

            //border: 1px solid #ECBC76; // frame 표시용 시용
            .title-h1{
                width: 600px;
                height: 50px;

                padding: 0px;
                margin-top: 0px;
                margin-left: 5px;

                display: flex;
                align-items: center;
                justify-content: left;
                text-align: left;

                
                font-style: normal;
                font-weight: 400;
                font-size: 30px;
                line-height: 24px;
                color: #B87514;
            }
            .frame-sub-head{
                width: 600px;
                height: 110px;
                
                // border: 1px solid #ECBC76; // frame 표시용 시용

                .btn-loadPaymentList{
                    width: 590px;
                    height: 45px;

                    margin-top: 10px;
                    margin-left: 5px;

                    cursor: pointer;

                    background: #B87514;
                    border-radius: 10px;    

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 700;
                    font-size: 16px;
                    line-height: 24px;

                    color: #FFFEF9;
                }
                .btn-searchPaymentById{
                    width: 95px;
                    height: 45px;

                    padding: 0px;
                    margin-top: 10px;
                    margin-left: 5px;
                    float: left;

                    cursor: pointer;

                    background: #ECBC76;
                    border-radius: 10px;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 700;
                    font-size: 16px;
                    line-height: 24px;
                    color: #FFFEF9;
                }
                .input-searchPaymentById{
                    width: 183px;
                    height: 43px;

                    padding: 0px;
                    margin-top: 10px;
                    margin-left: 10px;
                    margin-right: 0px;
                    float: left;

                    border: 1px solid #C4C4C4;
                    border-radius: 8px;

                    display: flex;
                    align-items: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 400;
                    font-size: 15px;
                    line-height: 130%;
                    color: #000000;
                }
            }
            .frame-sub-body{
                width: 600px;
                height: 428px;

                overflow-y: auto;

                .attribute-id{
                    width: 135px;
                    height: 54px;

                    padding: 0px;
                    margin-top: 0px;
                    margin-left: 0px;
                    float: left;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 700;
                    font-size: 16px;
                    line-height: 24px;
                    color: #ECBC76;
                }
                .attribute-detail{
                    width: 135px;
                    height: 54px;

                    padding: 0px;
                    margin-top: 0px;
                    margin-left: 20px;
                    float: left;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 700;
                    font-size: 16px;
                    line-height: 24px;
                    color: #ECBC76;
                }
                .content-row-id{
                    width: 135px;
                    height: 44px;

                    padding: 0px;
                    margin-top: 5px;
                    margin-left: 0px;
                    float: left;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 400;
                    font-size: 16px;
                    line-height: 24px;
                    color: #B87514;
                }
                .content-row-detail{
                    width: 135px;
                    height: 44px;

                    padding: 0px;
                    margin-top: 5px;
                    margin-left: 20px;
                    float: left;

                    display: flex;
                    align-items: center;
                    justify-content: center;
                    text-align: center;

                    
                    font-style: normal;
                    font-weight: 400;
                    font-size: 16px;
                    line-height: 24px;
                    color: #B87514;
                }
            }
            .line-for-division{
                width: 580px;
                height: 0px;
                margin-top: 0px;
                margin-left: 9px;
                margin-bottom: 0px;

                border: 1px solid #ECBC76;
            }
        }
</style>