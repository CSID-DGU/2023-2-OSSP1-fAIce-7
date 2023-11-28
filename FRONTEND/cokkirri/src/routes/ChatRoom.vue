<template>
  <div>
    <header>
      <nav class="menuBar">
        <span>
          <a href="javascript:history.back();">
            <img :src="imagePath_arrow" alt="Arrow" class="Back_img" />
          </a>
        </span>
        <span>
          <a href="javascript:history.back();" class="Back_txt">뒤로가기</a>
        </span>
        <div class="co-txt">
          <span>
            <h1>Co-kkirri</h1>
          </span>
        </div>
      </nav>
    </header>

    <main>
      <div class="WholeBox">
        <div class="chat-room">
          <div class="messages">
            <div v-for="message in formattedMessages" :key="message.id" class="message">
              <div :class="{'sent-by-me': message.sender === sender, 'received-from': message.sender !== sender}">
                {{ message.isSentByMe ? message.text : message.content }}
              </div>
            </div>
          </div>
          <div class="input-section">
            <textarea v-model="newMessage" @keyup.enter="sendMessage" placeholder="메시지를 입력하세요" class="messageInput"></textarea>
            <a @click="sendMessage" class="sendMessage"></a>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import axios from '../api/index.js';
import { useStore } from 'vuex';

const formattedTime = (message) => {
  const date = new Date(message.timestamp);
  const hours = date.getHours();
  const minutes = date.getMinutes();
  return `${hours < 10 ? '0' : ''}${hours}:${minutes < 10 ? '0' : ''}${minutes}`;
};

export default {
  data() {
    return {
      imagePath_arrow: require('@/assets/pay/arrow-left.png'),
    };
  },
  
  setup() {
    const store = useStore();

    const messages = ref([]);
    const newMessage = ref('');
    const matchingId = ref(store.state.matchingIdForChatroom);
    const matchingType = ref(store.state.matchingTypeForChatroom);
    const sender = ref(store.state.id);

    const formattedMessages = computed(() =>
      messages.value.map((message) => ({
        ...message,
        formattedTime: formattedTime(message)
      }))
    );

    let stompClient = null;
    
    const connectToWebSocket = () => {
      const socket = new SockJS('http://localhost:8081/ws');
      stompClient = Stomp.over(socket);
      stompClient.connect({}, () => {
        stompClient.subscribe(`/room/${matchingId.value}/${matchingType.value}`, (message) => {
          try {
            const receivedMessage = JSON.parse(message.body);
            console.log('Received message:', receivedMessage);
            receivedMessage.isSentByMe = receivedMessage.sender === sender.value;

            // Filter out the sender's own messages
            if (!receivedMessage.isSentByMe) {
              messages.value.push(receivedMessage);
            }
          } catch (error) {
            console.error('Failed to parse message body:', error);
          }
        });
      });

      socket.onerror = (error) => {
        console.log(`WebSocket Error: ${error}`);
      };
    };



    const sendMessage = () => {
      if (!newMessage.value || !stompClient || newMessage.value.trim() === '') {
        return;
      }

      const chatMessage = {
        matchingId: parseInt(matchingId.value),
        matchingType: matchingType.value,
        sender: sender.value,
        content: newMessage.value.trim(),
      };

      stompClient.send(`/send/${matchingId.value}/${matchingType.value}`, {}, JSON.stringify(chatMessage));

      const sentMessage = {
        id: Date.now(),
        text: newMessage.value,
        isSentByMe: true,
        timestamp: new Date().toISOString(),
        sender: sender.value // sender를 추가
      };

      messages.value.push(sentMessage);
      newMessage.value = '';
    };

    const fetchChatHistory = async () => {
      try {
        const response = await axios.get(`/room/${matchingId.value}/${matchingType.value}`);
        messages.value = response.data;
      } catch (error) {
        console.error('Failed to fetch chat history:', error);
      }
    };

    onMounted(async () => {
      connectToWebSocket();
      try {
        await fetchChatHistory();
      } catch (error) {
        console.error('Failed to load chat history:', error);
      }
    });

    return {
      messages,
      newMessage,
      matchingId,
      matchingType,
      sender,
      sendMessage,
      formattedMessages,
    };
  },
};
</script>


<style scoped>

header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  
  height: 30px;
  padding: 1rem;
  color: #B87514;
  background: #fffef9;
  font-weight: bold;
  display: flex;
  justify-content: space-between;
  align-items: center;
  text-align : center;
}

.chat-room {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: 20px;
  box-sizing: border-box;
  padding-top: 75px;
}


.messages {
  display: flex;
  flex-direction: column;
  max-height: 700px;
  overflow-y: auto;
  align-items: flex-start;
}

.message {
  width: 100%;
  display: flex;
  justify-content: flex-start; /* default alignment */
}

.sent-by-me {
  text-align: right;
  background-color: #B87514;
  padding: 10px;
  color: #fffef9;
  border-radius: 15px;
  max-width: 500px;
  max-height: 30px;
  margin-left: auto; /* push message to the right */
  margin-bottom : 10px;
}

.received-from {
  display: inline-block;
  background-color : #ECBC76;
  text-align: left;
  padding: 10px;
  color : black;
  border-radius: 15px;
  max-width: 500px;
  max-height : 30px;
  margin-bottom : 10px;
}


.input-section {
  margin-top: 20px;
  display: flex;
}

.input-section input {
  flex: 30;
}

.input-section button {
  margin-left: 10px;
}
.messageInput {
  background-color: #FFFEF9;
  width: 100%; /* 화면의 너비에 맞게 조절됨 */
  height: auto; /* 내용에 맞게 자동으로 조절됨 */
  font-size: 15px;
  border: 0;
  border: 1.5px solid #B87514;
  border-radius: 40px;
  outline: none;
  padding: 8px 10px;
  display: flex;
  color: #B87514;
  resize: none;
}

.messageInput::placeholder{
  color : #B87514;
  text-align: left;
}

.sendMessage{
  text-decoration:none;
  position: absolute;
  left: 80.49%;
  right: 17.43%;
  top: 89.56%;
  bottom: 7.11%;
}

.Back_img{
position: absolute;
position : fixed;
left: 3.68%;
right: 93.89%;
top: 10px;
bottom: 90.11%;
}

.Back_txt{color : #B87514;
position: absolute;
position : fixed;
width: 98px;
height: 38px;
left: 100px;
top: 10px;

text-decoration : none;
font-style: normal;
font-weight: 600;
font-size: 20px;
line-height: 38px;
}

.co-txt {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left : 680px;
}

.co-txt h1 {
  text-align: center;
}

</style>