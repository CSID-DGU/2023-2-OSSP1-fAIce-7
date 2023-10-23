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
    let stopTransmission = false;  //전송 중지 플래그

    startButton.addEventListener("click", function (event) {
        event.preventDefault();
        // Hide initial form and show video elements

        initialForm.style.display = "none";
        emotionStreamForm.style.display = "block";
        
	// POST 요청을 보냅니다.
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

        // Start local video stream
        navigator.mediaDevices.getUserMedia({ video: true })
            .then(stream => {
                localVideoElement.srcObject = stream;
                localVideoElement.play();

                localVideoElement.addEventListener('timeupdate', function() {
                    console.log('Webcam frame updated');
                }, false);

                console.log("Wait to open WebSocket");

                // Establish WebSocket connection
                ws = new WebSocket("ws://localhost:8000/ws/emotion/");

                ws.onopen = function (event) {
                    console.log("WebSocket is open now.");

                    // Send frames to the server via WebSocket
                    setInterval(() => {
                        if (ws.readyState === WebSocket.OPEN && !stopTransmission) {  //전송 중지 플래그 추가
                            let canvas = document.createElement("canvas");
                            canvas.width = localVideoElement.videoWidth;
                            canvas.height = localVideoElement.videoHeight;
                            canvas.getContext("2d").drawImage(localVideoElement, 0, 0);
                            let frameData = canvas.toDataURL("image/jpeg");            
                            ws.send(JSON.stringify({ type: "start_stream", selected_emotion: selected_emotion, min_people: min_people, emotion_threshold: emotion_threshold, frameData: frameData }));
                            console.log("Sent frame data (last) 100 chars):", frameData.slice(-100));
                        }
                    }, 100);  // Sending frame every 100ms
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

                    //현재 감정 확률이 사용자가 설정한 감정 수치보다 크면 캔버스 갱신 중지
                    if (data.emotion_probability >= emotion_threshold) {
                        stopTransmission = true;  //전송 중지 플래그 활성화
                    }

                    // Create a new image and set its source
                    const image = new Image();
                    image.src = "data:image/jpeg;base64," + data.frame;

                    // Ensure the image is loaded before drawing on it
                    image.onload = function() {
                        
                        // Draw the image onto the canvas
                        const ctx = remoteVideoElement.getContext('2d');
                        ctx.drawImage(image, 0, 0, remoteVideoElement.width, remoteVideoElement.height);

                        // Draw emotions on top of the image
                        drawEmotions(data, remoteVideoElement);
                    };

                    if (data.should_save_screenshot) {
                        remoteVideoElement.pause();
                        saveButton.style.display = "block";
                        restartButton.style.display = "block";
                        initialButton.style.display = "block";
                        // Rest of the logic for pausing/stopping local stream
                    } else {
                        remoteVideoElement.src = "data:image/jpeg;base64," + data.frame;
                    }
                };

                function drawEmotions(data, canvasElement) {
                    // Get the canvas context
                    if (!(canvasElement instanceof HTMLCanvasElement)) {
                        console.error("remoteVideo is not recognized as a canvas element");
                        return;
                    }
                    const ctx = canvasElement.getContext('2d');
    
                    // Draw rectangles and text
                    data.detected_emotions.forEach((emotion) => {
                        const coordinates = emotion['coordinates'];
                        const emotionLabel = emotion['emotion_label'];
                        const selectedEmotionLabel = selected_emotion.charAt(0).toUpperCase() + selected_emotion.slice(1) + ': ' + emotion['emotion_probability'] + '%';
                        const color = `rgb(${emotion['color'][2]}, ${emotion['color'][1]}, ${emotion['color'][0]})`;
                    
                        // Draw the rectangle
                        ctx.strokeStyle = color;
                        ctx.lineWidth = 2;
                        ctx.strokeRect(coordinates['x'], coordinates['y'], coordinates['w'], coordinates['h']);
                    
                        // Draw the emotion label below the rectangle
                        ctx.fillStyle = color;
                        ctx.font = '16px Arial';
                        ctx.fillText(emotionLabel, coordinates['x'], coordinates['y'] + coordinates['h'] + 20);
                        
                        // Draw the selected emotion probability above the rectangle
                        ctx.fillText(selectedEmotionLabel, coordinates['x'], coordinates['y'] - 10);
                    });
                }
            })
            .catch(err => {
                console.error("Error accessing the camera: ", err);
            });
    });

    //캔버스 재시작
    restartButton.addEventListener("click", function () {
        stopTransmission = false;  //전송 중지 플래그 비활성화
    });


    //정지한 캔버스의 이미지를 저장
    saveButton.addEventListener("click", function () {
        let link = document.createElement('a');
        link.href = remoteVideoElement.toDataURL('image/png');
   	    link.download = 'captured_emotion.png';
   	    link.click();
    });

    initialButton.addEventListener("click", function () {
     	initialForm.style.display = "block";
        emotionStreamForm.style.display = "none";
        
	if(localVideoElement.srcObject){
		const tracks = localVideoElement.srcObject.getTracks();
		tracks.forEach(track => track.stop());
		localVideoElement.srcObject = null;
	}
	
	if(ws && ws.readyState === WebSocket.OPEN){
		ws.close();
	}
    });
});