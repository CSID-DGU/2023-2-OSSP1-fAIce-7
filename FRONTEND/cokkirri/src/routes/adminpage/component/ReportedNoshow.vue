<template>
    <div class="frame-main">
        <div class="title-h1">신고 기록 관리</div>
        <div class="line-for-division"></div>
        <div class="frame-sub-head">
            <div class="btn-loadReportedNoshow" @click="loadReportedNoshow()">전체 조회</div>
            <div style="clear:both;"></div>

            <div class="btn-searchReportedNoshow" @click="searchReportedNoshow()">매칭ID로 신고 조회</div>
            <input type="text" placeholder="Search matchingId" class="input-matchingIdForNoshowRecord" v-model="matchingIdForNoshowRecord" @click="searchReportedNoshow()">
            <div style="clear:both;"></div>
        </div>
        <div class="frame-sub-body">
            <div class="line-for-division"></div>
            <div class="attribute-medium">Type</div>
            <div class="attribute-medium">매칭ID</div>
            <div class="attribute-long">신고자 이메일</div>
            <div class="attribute-content">신고 내용</div>
            <div style="clear:both;"></div>
            <div class="line-for-division"></div>
            <!---
                matchingType 신고된 매칭의 타입
                matchingId 신고된 매칭의 id
                email 신고자
                title 신고 제목
                comment 신고 내용
            --->
            <div v-for="(Reported,index) in classReportedNoshow" :key="index">
                <div class="content-row-medium">수업</div>
                <div class="content-row-medium">{{Reported.matchingId}}</div>
                <div class="content-row-long">{{Reported.email}}</div>
                <div class="content-row-title">
                    제목 : {{Reported.title}}
                </div>
                <div class="content-row-content">
                    내용 : {{Reported.comment}}
                </div>
                <div style="clear:both;"></div>
            </div>
            <!---
                matchingType 신고된 매칭의 타입
                matchingId 신고된 매칭의 id
                email 신고자
                title 신고 제목
                comment 신고 내용
            --->
            <div v-for="(Reported,index) in publicReportedNoshow" :key="index">
                <div class="content-row-medium">공강</div>
                <div class="content-row-medium">{{Reported.matchingId}}</div>
                <div class="content-row-long">{{Reported.email}}</div>
                <div class="content-row-title">
                    제목 : {{Reported.title}}
                </div>
                <div class="content-row-content">
                    내용 : {{Reported.comment}}
                </div>
            </div>
        </div>
        <div class="line-for-division"></div>
    </div>
</template>

<script>
    import axios from '../../../api/index.js'
    export default {
        data(){
            return{
                classReportedNoshow: [],
                publicReportedNoshow: [],

                matchingIdForNoshowRecord: '',
            }
        },
        methods:{
            // 타입별 노쇼 신고 조회(구체적인 테스트 필요)
            loadReportedNoshow(){
                this.loadReportedClassNoshow()
                this.loadReportedPublicNoshow()
            },
            async loadReportedClassNoshow(){
                try{
                    await axios.get('matching/get/class/accusation',{
                        params:{
                            matchingType: "class"
                        }
                    })
                    .then((result)=>{
                        console.log(result)
                        this.classReportedNoshow = result.data
                        //this.publicReportedNoshow = []
                    }).catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            },
            async loadReportedPublicNoshow(){
                try{
                    await axios.get('matching/get/public/accusation',{
                        params:{
                            matchingType: "free"
                        }
                    })
                    .then((result)=>{
                        console.log(result)
                        //this.classReportedNoshow = []
                        this.publicReportedNoshow = result.data
                    }).catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            },
            // 타입별 메칭 아이디 노쇼 신고 조회
            searchReportedNoshow(){
                this.searchReportedClassNoshow()
                this.searchReportedPublicNoshow()
            },
            async searchReportedClassNoshow(){
                try{
                    await axios.get('/matching/get/classmatch/accusation',{
                        params:{
                            matchingId: this.matchingIdForNoshowRecord,
                            matchingType: "class"
                        }
                    })
                    .then((result)=>{
                        // 특이하게 dara가 Array 형태로 반환되지 않음. 이거 확인 필요함. 오류 유의.
                        console.log(result)
                        this.classReportedNoshow = result.data
                        //this.publicReportedNoshow = []
                    }).catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            },
            async searchReportedPublicNoshow(){
                try{
                    await axios.get('/matching/get/publicmatch/accusation',{
                        params:{
                            matchingId: this.matchingIdForNoshowRecord,
                            matchingType: "free"
                        }
                    })
                    .then((result)=>{
                        // 특이하게 data가 Array 형태로 반환되지 않음. 이거 확인 필요함. 오류 유의.
                        console.log(result)
                        //this.classReportedNoshow = []
                        this.publicReportedNoshow = result.data
                    }).catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            },
        },
        mounted(){
            this.loadReportedNoshow()
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

            .btn-loadReportedNoshow{
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
            .btn-searchReportedNoshow{
                width: 292px;
                height: 45px;

                margin-top: 10px;
                margin-left: 5px;
                float: left;

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
            .input-matchingIdForNoshowRecord{
                width: 286px;
                height: 43px;

                margin-top: 10px;
                margin-left: 5px;
                margin-bottom: 5px;
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

            .attribute-long{
                width: 300px;
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
            .attribute-medium{
                width: 150px;
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
            .attribute-content{
                width: 600px;
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
            .content-row-long{
                width: 300px;
                height: 44px;

                padding: 0px;
                margin-top: 0px;
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
            .content-row-medium{
                width: 150px;
                height: 44px;

                padding: 0px;
                margin-top: 0px;
                margin-left: 0px;
                float: left;

                display: flex;
                align-items: center;
                justify-content: center;
                text-align: center;

                
                font-style: normal;
                font-weight: 400;
                font-size: 12px;
                line-height: 24px;
                color: #B87514;
            }
            .content-row-title{
                width: 600px;
                height: 44px;

                padding: 0px;
                margin-top: 0px;
                margin-left: 0px;
                float: left;

                display: flex;
                align-items: center;
                justify-content: center;
                text-align: center;

                
                font-style: normal;
                font-weight: 400;
                font-size: 14px;
                line-height: 24px;
                color: #B87514;
            }
            .content-row-content{
                width: 600px;
                height: 88px;

                padding: 0px;
                margin-top: 0px;
                margin-left: 0px;
                float: left;

                display: flex;
                align-items: top;
                justify-content: center;
                text-align: center;

                
                font-style: normal;
                font-weight: 400;
                font-size: 8px;
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