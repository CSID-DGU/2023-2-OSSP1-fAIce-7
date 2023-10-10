document.addEventListener("DOMContentLoaded", function () {
    let localVideoElement = document.getElementById("localVideo");
    let remoteVideoElement = document.getElementById("remoteVideo");
    let startButton = document.getElementById("startButton");
    let restartButton = document.getElementById("restartButton");
    let saveButton = document.getElementById("saveButton");
    let initialForm = document.getElementById("initialForm");
    let emotionStreamForm = document.getElementById("emotionStreamForm");
    let initialButton = document.getElementById("initialButton");
    let ws;

    startButton.addEventListener("click", function (event) {
        event.preventDefault();
        // 시작버튼 클릭 시 초기 폼 화면을 숨기고 비디오 표시

        initialForm.style.display = "none";
        emotionStreamForm.style.display = "block";
        saveButton.style.display = "none";
        restartButton.style.display = "none";
        initialButton.style.display = "none";

        // POST 요청 전송
        const formData = new FormData(initialForm);

        fetch('/', {
            method: 'POST',
            body: formData,
        })
            .then(response => response.json())
            .then(data => {
                selected_emotion = data.selected_emotion;
                min_people = data.min_people;
                emotion_threshold = data.emotion_threshold;
            })
            .catch((error) => {
                console.error('Error:', error);
            });

        // 실시간 웹캠 시작
        navigator.mediaDevices.getUserMedia({ video: true })
            .then(stream => {
                localVideoElement.srcObject = stream;
                localVideoElement.play();

                localVideoElement.addEventListener('timeupdate', function() {
                    console.log('Webcam frame updated');
                }, false);

                console.log("Wait to open WebSocket");

                // 웹소켓 연결 활성화
                ws = new WebSocket("ws://localhost:8000/ws/emotion/");

                ws.onopen = function (event) {
                    console.log("WebSocket is open now.");

                    // 웹소켓을 통해 서버로 비디오 데이터 전송
                    setInterval(() => {
                        if (ws.readyState === WebSocket.OPEN) {
                            let canvas = document.createElement("canvas");
                            canvas.width = localVideoElement.videoWidth;
                            canvas.height = localVideoElement.videoHeight;
                            canvas.getContext("2d").drawImage(localVideoElement, 0, 0);
                            let frameData = canvas.toDataURL("image/jpeg");
                            ws.send(JSON.stringify({ type: "start_stream", selected_emotion: selected_emotion, min_people: min_people, emotion_threshold: emotion_threshold, frameData: frameData }));
                            console.log("Sent frame data (last) 100 chars):", frameData.slice(-100));
                        }
                    }, 100);  // 100ms 마다 프레임 전송
                };

                ws.onclose = function (event) {
                    console.log("WebSocket is closed now.");
                };

                ws.onerror = function (err) {
                    console.error("WebSocket error observed:", err);
                };

                ws.onmessage = function (event) {
                    let data = JSON.parse(event.data);
                    console.log("Detected Emotions: ", data.detected_emotions);
                    console.log("Emotion Probability: ", data.emotion_probability);
                    console.log("Valid People Count: ", data.valid_people_count);

                    // 새로운 이미지 객체에 받은 프레임을 삽입
                    const image = new Image();
                    image.src = "data:image/jpeg;base64," + data.frame;

                    image.onload = function() {
                        
                        // 캔버스 위에 받은 비디오 이미지 그리기
                        const ctx = remoteVideoElement.getContext('2d');
                        ctx.drawImage(image, 0, 0, remoteVideoElement.width, remoteVideoElement.height);

                        // 감정 데이터를 이미지 위에 그리기
                        drawEmotions(data, remoteVideoElement);
                    };

                    // 조건을 만족한 경우 스크린샷 변수를 true로 변경
                    if (data.should_save_screenshot) {
                        // 서버 비디오 화면 일시중지 및 관련 버튼 활성화
                        remoteVideoElement.pause();
                        saveButton.style.display = "inline";
                        restartButton.style.display = "inline";
                        initialButton.style.display = "inline";
                    } else {
                        remoteVideoElement.src = "data:image/jpeg;base64," + data.frame;
                    }
                };

                function drawEmotions(data, canvasElement) {
                    // HTML에 canvas로 불러오고 있는건지 체크
                    if (!(canvasElement instanceof HTMLCanvasElement)) {
                        console.error("remoteVideo is not recognized as a canvas element");
                        return;
                    }
                    const ctx = canvasElement.getContext('2d');
    
                    // 얼굴 사각형과 데이터 수치 표시
                    data.detected_emotions.forEach((emotion) => {
                        const coordinates = emotion['coordinates'];
                        const emotionLabel = emotion['emotion_label'];
                        const selectedEmotionLabel = selected_emotion.charAt(0).toUpperCase() + selected_emotion.slice(1) + ': ' + emotion['emotion_probability'] + '%';
                        const color = `rgb(${emotion['color'][2]}, ${emotion['color'][1]}, ${emotion['color'][0]})`;
                    
                        // 얼굴 사각형 그리기
                        ctx.strokeStyle = color;
                        ctx.lineWidth = 2;
                        ctx.strokeRect(coordinates['x'], coordinates['y'], coordinates['w'], coordinates['h']);
                    
                        // 얼굴 사각형 위에 감정과 감정 수치 표시
                        ctx.fillStyle = color;
                        ctx.font = '16px Arial';
                        ctx.fillText(emotionLabel, coordinates['x'], coordinates['y'] + coordinates['h'] + 20);
                        
                        // 얼굴 사각형 아래에 사람 객체 표시
                        ctx.fillText(selectedEmotionLabel, coordinates['x'], coordinates['y'] - 10);
                    });
                }
            })
            .catch(err => {
                console.error("Error accessing the camera: ", err);
            });
    });

    restartButton.addEventListener("click", function () {
        ws.send(JSON.stringify({ type: "restart_stream" }));
        remoteVideoElement.play();
    });

    saveButton.addEventListener("click", function () {
        ws.send(JSON.stringify({ type: "save_button" }));
        ws.onmessage = function (event) {
            let data = JSON.parse(event.data);
            if (data.message_type === "screenshot") {
                let a = document.createElement("a");
                a.href = "data:image/jpeg;base64," + data.image;
                a.download = "screenshot.jpg";
                a.click();
            }
        };
    });

    initialButton.addEventListener("click", function () {
    // 이 부분에 "처음" 버튼이 눌렸을 때의 로직을 추가합니다.
    // 예를 들어, 특정 element의 style을 변경하거나, WebSocket 연결을 종료하는 등의 동작을 수행할 수 있습니다.
    });
});
