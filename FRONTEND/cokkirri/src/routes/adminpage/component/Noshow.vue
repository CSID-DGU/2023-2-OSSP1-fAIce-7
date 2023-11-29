<template>
    <div class="frame-main">
        <div class="title-h1">노쇼 기록 관리</div>
        <div class="line-for-division"></div>
        <div class="frame-sub-head">
            <div class="btn-loadNoshow" @click="loadNoshow()">전체 조회</div>
            <div style="clear:both;"></div>

            <div class="btn-resisterClassNoshow" @click="resisterNoshow('class')">수업 노쇼 등록</div>
            <div class="btn-resisterPublicNoshow" @click="resisterNoshow('free')">공강 노쇼 등록</div>
            <input type="text" placeholder="Input matchingId" class="input-matchingIdForNoshowResister" v-model="matchingIdForNoshowResister">
            <input type="text" placeholder="Input id" class="input-idForNoshowResister" v-model="idForNoshowResister">
            <div style="clear:both;"></div>
        </div>
        <div class="frame-sub-body">
            <div class="line-for-division"></div>
            <div class="attribute-long">노쇼 이메일</div>
            <div class="attribute-short">Type</div>
            <div class="attribute-short">매칭ID</div>
            <div class="attribute-medium">규제기한</div>
            <div style="clear:both;"></div>
            <div class="line-for-division"></div>
            <!---
                email
                matchingId
                matchingType
                restrictionTime
            --->
            <div v-for="(noshow,index) in classNoshow" :key="index">
                <div class="content-row-long">{{noshow.email}}</div>
                <div class="content-row-short">수업</div>
                <div class="content-row-short">{{noshow.matchingId}}</div>
                <div class="content-row-medium">{{noshow.restrictionTime}}</div>
                <div class="btn-delete" @click="cancelNoshow(noshow.email,'class')">취소</div>
                <div style="clear:both;"></div>
            </div>
            <!---
                email
                matchingId
                matchingType
                restrictionTime
            --->
            <div v-for="(noshow,index) in publicNoshow" :key="index">
                <div class="content-row-long">{{noshow.email}}</div>
                <div class="content-row-short">공강</div>
                <div class="content-row-short">{{noshow.matchingId}}</div>
                <div class="content-row-medium">{{noshow.restrictionTime}}</div>
                <div class="btn-delete" @click="cancelNoshow(noshow.email,'free')">취소</div>
                <div style="clear:both;"></div>
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
            classNoshow: [],
            publicNoshow: [],

            matchingIdForNoshowResister: '',

            idForNoshowResister: '',
        }
    },
    methods:{
        // 타입별 노쇼 매칭 조회(구체적인 테스트 필요)
        loadNoshow(){
            this.loadClassNoshow()
            this.loadPublicNoshow()
        },
        async loadClassNoshow(){
            try{
                await axios.get('/matching/get/noshow/class')
                .then((result)=>{
                    this.classNoshow = result.data
                    //this.publicNoshow = []
                }).catch(function(error){
                    console.log(error)
                })
            }catch(error){
                console.log(error)
            }
        },
        async loadPublicNoshow(){
            try{
                await axios.get('/matching/get/noshow/public')
                .then((result)=>{
                    //this.classNoshow = []
                    this.publicNoshow = result.data
                }).catch(function(error){
                    console.log(error)
                })
            }catch(error){
                console.log(error)
            }
        },
        // 타입별 노쇼 등록
        resisterNoshow(matchingId){
            if(this.matchingIdForNoshowResister !== '' && this.idForNoshowResister !==''){
                if(matchingId==="class"){
                    this.resisterClassNoshow()
                }
                else if(matchingId==="free"){
                    this.resisterPublicNoshow()
                }
            }
            else if(this.matchingIdForNoshowResister === ''){
                alert("등록할 매칭의 id가 입력되지 않았습니다.")
            }
            else if(this.idForNoshowResister ===''){
                alert("등록할 유저의 id가 입력되지 않았습니다.")
            }
            else{
                alert("잘못된 입력입니다.")
            }
        },
        async resisterClassNoshow(){
            alert(this.classMatchingIdForNoshowResister)

            try{
                await axios.post('/matching/post/noshow/class',{
                        matchingId: this.matchingIdForNoshowResister,
                        email: this.idForNoshowResister,
                        matchingType: "class"

                })
                .then(()=>{
                    alert("노쇼 등록 성공")
                    this.loadNoshow()
                    this.sendEmailAboutNoshow('class')
                    this.matchingIdForNoshowResister = ''
                    this.idForNoshowResister = ''
                }).catch(function(error){
                    console.log(error)
                })
            }catch(error){
                console.log(error)
            }
        },
        async resisterPublicNoshow(){
            try{
                await axios.post('/matching/post/noshow/public',{
                        matchingId: this.matchingIdForNoshowResister,
                        email: this.idForNoshowResister,
                        matchingType: "free"
                })
                .then(()=>{
                    alert("노쇼 등록 성공")
                    this.loadNoshow()
                    this.sendEmailAboutNoshow('free')
                    this.matchingIdForNoshowResister = ''
                    this.idForNoshowResister = ''
                }).catch(function(error){
                    console.log(error)
                })
            }catch(error){
                console.log(error)
            }
        },
        async sendEmailAboutNoshow(matchingType){
            try{
                await axios.get('/matching/send/noshow',{
                    params:{
                        userId: this.idForNoshowResister,
                        matchingType: matchingType
                    }
                }).then((result)=>{
                    alert("노쇼 유저에게 이메일을 보냈습니다.")
                    console.log(result)
                }).catch(function(error){
                    console.log(error)
                })
            }catch(error){
                console.log(error)
            }
        },
        async cancelNoshow(email,matchingType){
            if(matchingType==='class'){
                try{
                    await axios.get('/matching/change/class/noshow',{
                        params:{
                            email: email
                        }
                    }).then(()=>{
                        alert(email+" 의 노쇼 등록 취소가 완료되었습니다.")
                        this.loadNoshow()
                    }).catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            }
            else if(matchingType==='free'){
                try{
                    await axios.get('/matching/change/free/noshow',{
                        params:{
                            email: email
                        }
                    }).then(()=>{
                        alert(email+" 의 노쇼 등록 취소가 완료되었습니다.")
                        this.loadNoshow()
                    }).catch(function(error){
                        console.log(error)
                    })
                }catch(error){
                    console.log(error)
                }
            }
        }
    },
    mounted(){
        this.loadNoshow()
    },
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

            .btn-loadNoshow{
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
            .btn-resisterClassNoshow{
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
            .btn-resisterPublicNoshow{
                width: 292px;
                height: 45px;

                margin-top: 10px;
                margin-left: 6px;
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
            .input-matchingIdForNoshowResister{
                width: 292px;
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
            .input-idForNoshowResister{
                width: 292px;
                height: 43px;

                margin-top: 10px;
                margin-left: 6px;
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
                width: 230px;
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
            .attribute-short{
                width: 70px;
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
                width: 230px;
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
            .content-row-short{
                width: 70px;
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
            .btn-delete{
                width: 60px;
                height: 30px;

                padding: 0px;
                margin-top: 7px;
                margin-bottom: 7px;
                margin-left: 10px;
                float: left;

                cursor: pointer;

                background-color: #B87514;
                border-radius: 5px;

                display: flex;
                align-items: center;
                justify-content: center;
                text-align: center;

                
                font-style: normal;
                font-weight: 400;
                font-size: 16px;
                line-height: 24px;
                color: #FFFFFF;
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