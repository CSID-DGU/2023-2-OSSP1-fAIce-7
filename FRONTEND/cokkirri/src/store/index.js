import { createStore } from 'vuex';
import axios from "../api/index.js";
// vuex의 mutations 및 action 에서 주소관리를 하기 위해서 가져옴

import router from '../routes/index.js';
// 모듈 불러오기


// vuex 를 사용하여 로그인 상태와 로그인 id 를 저장
export default createStore({
    // 컴포넌트에서 해당 state의 값을 불러오고자 한다면, this.$store.state.~ 형식으로 불러올 수 있다.
    // 예시 : this.$store.state.id
    state: {
        id: null,
        isLogin: false,
        admin: false,

        major: null,
        name: null,
        number: null,
        sex: null,
        studentNum: null,
        course: [],
        password: null,
        heart: null,
        restrctionDate: null,

        usageHistory: [],

        notification: null,

        publicMatchingRecord: null,
        classMatchingRecord: null,

        // 관심분야 설정 여부
        isSetInterests: false, // 관심분야 설정 여부 변경
        categories: [],  // 기존 카테고리 상태
        userInterests: [], // 사용자의 관심분야 저장

        // 매칭 대기 정보 불러오기
        classMatchingWait: null,
        publicMatchingWait: null,

        // 선택된 매칭 번호
        matchingIdForChatroom: null,
        matchingTypeForChatroom: null,

        // 매칭 대기 상태
        isExistClassMatchingWait: false,
        isExistPublicMatchingWait: false,
    },
    mutations: {

        // 사용자 관심분야 설정
        setUserInterests(state, interests) {
            state.userInterests = interests;
        },

        // 매칭 대기 저장
        SaveClassWait(state,record){
            if(Object.keys(record).length > 0){
                state.classMatchingWait = record
                state.isExistClassMatchingWait = true
            }
            else{
                console.log("수업 대기 없다고 판단")
                state.classMatchingWait = null
                state.isExistClassMatchingWait = false
            }
        },
        SavePublicWait(state,record){
            if(Object.keys(record).length > 0){
                state.publicMatchingWait = record
                state.isExistPublicMatchingWait = true
            }
            else{
                console.log("공강 대기 없다고 판단")
                state.publicMatchingWait = record = null
                state.isExistPublicMatchingWait = false
            }
        },

        // 사용자 매칭 정보 불러오기
        publicSave(state, record){
            state.publicMatchingRecord = record
        },
        classSave(state, record){
            state.classMatchingRecord = record
        },

        //하트 사용내역 불러오기
        setUsageHistory(state, history) {
            state.usageHistory = history;
        },
        // 로그인 적용 후 ~ 페이지로 이동. 추후 메인 페이지로 이동 변경 예정.
        loginSuccess(state, payload) {
            state.isLogin = true
            state.id = payload
        },
        userInfoApply(state, payload){
            state.major = payload.major
            state.name = payload.name
            state.number = payload.number
            state.sex = payload.sex
            state.studentNum = payload.studentNum
            state.course = payload.course
            state.password = payload.password
            state.heart = payload.heart
            state.restrctionDate = payload.restrctionDate
            state.admin = payload.admin
            state.isSetInterests = payload.setInterests // 관심분야 설정 여부 확인
        },
        dateReform(state){
            if(state.restrctionDate===null){
                console.log("규제 기간 없다고 판단->매칭 신고")
                return
            }
            else{
                console.log(state.restrctionDate)
                const dateTemp = new Date(state.restrctionDate)
                alert("현재 사용자는 과거 노쇼를 한 기록으로 인해 " + dateTemp.getFullYear() + "년 " + dateTemp.getMonth() + "월 " + dateTemp.getDay() + "일 " + dateTemp.getHours() + "시 " + dateTemp.getMinutes() + "분까지 매칭이 금지된 상태입니다.")
            }
        },
        logout(state) { 
            state.isLogin = false
            state.admin = false
            
            state.id = null
    
            state.major = null
            state.name = null
            state.number = null
            state.sex = null
            state.studentNum = null
            state.course = []
            state.password = null
            state.heart = null
            state.restrctionDate = null
    
            state.usageHistory = []
    
            state.notification = null
    
            state.publicMatchingRecord = null
            state.classMatchingRecord = null
    
            // 매칭 대기 정보 불러오기
            state.classMatchingWait = null
            state.publicMatchingWait = null
    
            // 선택된 매칭 번호
            state.matchingIdForChatroom = null
            state.matchingTypeForChatroom = null
    
            // 매칭 대기 상태
            state.isExistClassMatchingWait = false
            state.isExistPublicMatchingWait = false

            router.replace('/')
        },
        SET_NOTIFICATION(state, notification){
            console.log("들어온 신호"+notification)
            state.notification = notification
            console.log("저장한 신호"+state.notification)
        },

        updateUsageHistory(state, newHistory) {
            state.usageHistory.push(newHistory);
        }
    },
    actions: {
        fetchUserInterests({ commit }) {
            // 서버에서 사용자의 관심분야를 가져오는 코드
            axios.get('/api/user/interests').then(response => {
                commit('setUserInterests', response.data.interests);
            });
        },
        updateUserInterests({ commit }, interests) {
            // 서버에 사용자의 새로운 관심분야를 업데이트하는 코드
            axios.post('/api/user/interests', { interests }).then(() => {
                commit('setUserInterests', interests);
            });
        },
        // 매칭 대기 존재하는지 반환
        async checkMatchingSubmitState({dispatch,state}){
            await dispatch('callMatchingWaitRecord')
            return (state.isExistClassMatchingWait | state.isExistPublicMatchingWait)
        },
        // 매칭 정보 불러오기
        async callMatchingRecord({commit, dispatch, state}) {
            try{
                await axios.get('/userMypage/publicMatchedList',{
                    params:{
                        userId: state.id
                }}).then((result)=>{
                    commit('publicSave',result.data)
                    console.log("공강 매칭 적용 완료")
                    console.log(result.data)
                }).catch(function(error){
                    console.log(error)
                })
            } catch(error){
                console.log(error)
            }
            try{
                await axios.get('/userMypage/classMatchedList',{
                    params:{
                        userId: state.id
                }}).then((result)=>{
                    commit('classSave',result.data)
                    console.log("수업 매칭 적용 완료")
                    console.log(result.data)
                }).catch(function(error){
                    console.log(error)
                })
            } catch(error){
                console.log(error)
            }
            dispatch('callMatchingWaitRecord')
        },

        // 각 컴포넌트에서 this.$store.dispatch('메소드 이름', { 데이터 변수: 입력값 }) 형식으로 사용 가능
        // 로그인 요청 및 store 정보 업데이트
        async loginRequest({commit, dispatch}, {inputId, inputPassword}){
            // 로그인 api 요청 부분. 반환값에 토큰 없음.
            try {
                const response = await axios.post('/login', null, {
                    params: {
                        id: inputId,
                        password: inputPassword,
                    }
                });
                if(response.status === 200 && response.data === true) {
                    commit('loginSuccess', inputId);
                    await dispatch('userInfoUpdate'); // 사용자 정보 업데이트
                    await dispatch('callMatchingRecord');
                    dispatch('ConnectSse');
        
                    if(this.state.admin){
                        router.replace('/admin');
                    } else if(!this.state.isSetInterests) { // 관심분야 설정 여부 확인
                        alert("관심분야를 설정하지 않으셨습니다. 설정 페이지로 넘어갑니다.");
                        router.replace('/InterestSettingsPage');
                    } else {
                        router.replace('/Starting');
                    }
                } else {
                    alert(response.data.password + '아이디 및 비밀번호에 대응되는 회원 정보가 없습니다.');
                }
            } catch(error) {
                console.log('로그인 오류:', error);
            }
        },
        // vuex의 state.id 기반으로 현재 유저의 정보를 업데이트한다.
        async userInfoUpdate({state,commit}){
            try{
                await axios.get('/admin/user/id',{
                        params: {
                            userId: state.id
                        }
                    })
                    .then((result) =>{
                        if(result.status === 200){
                            commit('userInfoApply', result.data)
                            console.log(result.data)
                            console.log("유저 정보 업데이트 완료")
                        }
                        else{
                            console.log("로그인 실패")
                        }
                    })
                    .catch(function(error){
                        console.log(error)
                    })
            } catch(error){
                console.log(error);
            }
        },
        //마이페이지에서 유저 정보 수정
        async userInfoEdit({dispatch}, {inputId, inputMajor, inputNumber, inputCourse, inputPassword}){
            try{
                await axios.put('/userMypage/'+inputId, null, {
                    params: {
                        password: inputPassword,
                        major: inputMajor,
                        number: inputNumber,
                        course: inputCourse.map(encodeURIComponent).join(',')
                    }
                })
                .then(()=>{
                    dispatch('userInfoUpdate')
                    alert("수정 완료")
                })
                .catch(function(error){
                    console.log(error)
                })
            } catch(error){
                console.log(error)
            }
        },
        // sse 연결
        ConnectSse({ state }) {
            let eventSource = new EventSource('http://localhost:8080/subscribe/' + state.id);
            
            eventSource.addEventListener("sse",(event)=>{
                try{
                    let eventData = event.data
                    let rawProperties = eventData.slice(eventData.indexOf('(')+1, eventData.lastIndexOf(')')).split(', ');
                    let parsedData = {};
                    rawProperties.forEach(prop => {
                        let [key, value] = prop.split('=');
                        if (value === 'null') {
                            value = null;
                        } else if (value === 'true') {
                            value = true;
                        } else if (value === 'false') {
                            value = false;
                        }
                        parsedData[key] = value;
                    });

                    if(parsedData.notDummy){
                        let alertMsg = parsedData.receiver + "님의 "
                        if(parsedData.notificationType==='free'){
                            alertMsg = alertMsg + "공강 매칭 인원이 모두 모였습니다. 매칭 내역에서 확인 가능합니다."
                        }else if(parsedData.notificationType==='class'){
                            alertMsg = alertMsg + "수업 매칭 인원이 모두 모였습니다. 매칭 내역에서 확인 가능합니다."
                        }else{
                            alertMsg = alertMsg + "매칭 타입 null에 대한 알림이 왔습니다."
                        }
                        alert(alertMsg)
                        console.log("isRead")
                        console.log(parsedData.isRead)
                    }
                    console.log(event.data)
                    console.log(parsedData)
                }catch(error){
                    console.log(error)
                    console.log(event.data)
                    console.log("정해진 형식과 다른 데이터를 수신받았습니다.")
                }
            });
            
            eventSource.onerror = error => {
                console.error('SSE connection error', error);
                if (eventSource.readyState === EventSource.CLOSED) {
                    eventSource = new EventSource('http://localhost:8080/subscribe/' + state.id);
                }
                else{
                    console.log("sse 연결된 상태입니다. 하지만 sse 응답 에러가 발생했습니다.")
                }
            }; 
            
        },
        /*
        async testSse(){
            const id = this.state.id;
            const eventSource = new EventSource('http://3.37.37.164:8080/subscribe/' + id);

            eventSource.addEventListener("sse", function (event) {
                console.log(event.data);

                const data = JSON.parse(event.data);

                (async () => {
                    // 브라우저 알림
                    const showNotification = () => {
                        
                        const notification = new Notification('코드 봐줘', {
                            body: data.content
                        });
                        
                        setTimeout(() => {
                            notification.close();
                        }, 10 * 1000);
                        
                        notification.addEventListener('click', () => {
                            window.open(data.url, '_blank');
                        });
                    }

                    // 브라우저 알림 허용 권한
                    let granted = false;

                    if (Notification.permission === 'granted') {
                        granted = true;
                    } else if (Notification.permission !== 'denied') {
                        let permission = await Notification.requestPermission();
                        granted = permission === 'granted';
                    }

                    // 알림 보여주기
                    if (granted) {
                        showNotification();
                    }
                })();
            })
        },
        */
        async fetchUsageHistory({ commit }) {
            try {
              const response = await axios.get('/admin/user/payment'); // 적절한 API 엔드포인트로 변경
              const history = response.data; // 가져온 데이터를 적절히 가공하여 history 변수에 저장
                commit('setUsageHistory', history);
            } catch (error) {
                console.log(error);
            }
        },
        async callMatchingWaitRecord({state, commit}){
            try{
                await axios.get('/matching/get/class/matchingWait',{
                    params:{
                        email: state.id
                    }
                })
                .then((result)=>{
                    commit('SaveClassWait',result.data)
                })
                .catch(function(error){
                    console.log(error)
                })
            }catch(error){
                console.log(error)
            }
            try{
                await axios.get('/matching/get/free/matchingWait',{
                    params:{
                        email: state.id
                    }
                })
                .then((result)=>{
                    commit('SavePublicWait',result.data)
                })
                .catch(function(error){
                    console.log(error)
                })
            }catch(error){
                console.log(error)
            }

        }

    },
})
