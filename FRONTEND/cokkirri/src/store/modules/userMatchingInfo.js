import axios from '../../api/index.js'

export default {
    namespaced: true,

    state: {
        publicMatchingRecord: null,
        classMatchingRecord: null,
    },
    mutations: {
        publicSave(state, record){
            state.publicMatchingRecord = record
            console.log(state.publicMatchingRecord)
            console.log("공강 매칭 불러오기 완료")
        },
        classSave(state, record){
            state.classMatchingRecord = record
            console.log(state.classMatchingRecord)
            console.log("수업 매칭 불러오기 완료")
        },
    },
    actions: {
        async callRecord({commit, rootState}) {
            try{
                console.log(rootState.id)
                await axios.get('/userMypage/publicMatchedList',{
                    params:{
                        userId: rootState.id
                }}).then((result)=>{
                    console.log(result.data)
                    commit('publicSave',result.data.data)
                }).catch(function(error){
                    console.log(error)
                })
            } catch(error){
                console.log(error)
            }
            try{
                console.log(rootState.id)
                await axios.get('/userMypage/classMatchedList',{
                    params:{
                        userId: rootState.id
                }}).then((result)=>{
                    console.log(result.data)
                    commit('publicSave',result.data.data)
                }).catch(function(error){
                    console.log(error)
                })
            } catch(error){
                console.log(error)
            }
        }
    }
}