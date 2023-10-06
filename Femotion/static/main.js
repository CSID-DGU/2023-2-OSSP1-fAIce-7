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
        // Hide initial form and show video elements

        initialForm.style.display = "none";
        emotionStreamForm.style.display = "block";
        saveButton.style.display = "none";
        restartButton.style.display = "none";
        initialButton.style.display = "none";

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
                        if (ws.readyState === WebSocket.OPEN) {
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
                        const emotionLabel = emotion['emotion_label'] + ': ' + emotion['emotion_probability'] + '%';
                        const color = `rgb(${emotion['color'][0]}, ${emotion['color'][1]}, ${emotion['color'][2]})`;
                    
                        // Draw the rectangle
                        ctx.strokeStyle = color;
                        ctx.lineWidth = 2;
                        ctx.strokeRect(coordinates['x'], coordinates['y'], coordinates['w'], coordinates['h']);
                    
                        // Draw the emotion label
                        ctx.fillStyle = color;
                        ctx.font = '16px Arial';
                        ctx.fillText(emotionLabel, coordinates['x'], coordinates['y'] - 10);
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
