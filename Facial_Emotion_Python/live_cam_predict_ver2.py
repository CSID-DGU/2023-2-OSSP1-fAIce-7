import cv2
import numpy as np
from keras.models import model_from_json
from keras.utils import img_to_array
import time
import matplotlib.pyplot as plt

# Load model from JSON file
json_file = open('top_models/fer.json', 'r')
loaded_model_json = json_file.read()
json_file.close()
model = model_from_json(loaded_model_json)

# Load weights and add them to the model
model.load_weights('top_models/fer.h5')

face_haar_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')

cap = cv2.VideoCapture(0)

# Define the list of emotions
emotions = ['neutral', 'happiness', 'surprise', 'sadness', 'anger', 'disgust', 'fear']

# Create a figure for the real-time emotion graph
plt.figure(figsize=(10, 5))
plt.ylim(0, 1)
plt.xlabel('Time (seconds)')
plt.ylabel('Emotion Value')
lines = []

# Initialize an empty list to store emotion history for each emotion
emotion_history = [[] for _ in emotions]
time_points = []

# Define colors for each emotion (in BGR format)
emotion_colors_bgr = [(173, 216, 230), (255, 165, 0), (124, 252, 0), (70, 130, 180), (255, 0, 0), (128, 0, 128), (139, 69, 19)]

start_time = time.time()

while True:
    ret, img = cap.read()
    if not ret:
        break

    gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces_detected = face_haar_cascade.detectMultiScale(gray_img, 1.1, 6, minSize=(150, 150))

    for (x, y, w, h) in faces_detected:
        roi_gray = gray_img[y:y + h, x:x + w]  # Corrected the ROI region
        roi_gray = cv2.resize(roi_gray, (48, 48))
        img_pixels = img_to_array(roi_gray)
        img_pixels = np.expand_dims(img_pixels, axis=0)
        img_pixels /= 255.0

        predictions = model.predict(img_pixels)

        # Calculate the average emotion values
        avg_emotion_values = [sum(emotion_history[i]) / len(emotion_history[i]) if emotion_history[i] else 0 for i in range(len(emotion_history))]

        # Update the emotion history for each emotion
        for i in range(len(emotions)):
            emotion_history[i].append(predictions[0][i] * 100)  # Convert to percentage

        time_points.append(time.time() - start_time)

        # Update the emotion graph for each emotion
        for i, emotion in enumerate(emotions):
            if len(lines) <= i:
                line, = plt.plot([], [], label=emotion, color=np.array(emotion_colors_bgr[i]) / 255)
                lines.append(line)
            lines[i].set_data(time_points, emotion_history[i])
            plt.xlim(min(time_points), max(time_points))
            plt.ylim(0, 100)  # Set the y-axis limit to 0-100 (percentage)
            plt.legend(loc='upper left')

        plt.pause(0.01)  # Pause to update the graph

        # Draw a rectangle around the detected face
        cv2.rectangle(img, (x, y), (x + w, y + h), (0, 255, 0), thickness=2)

        # Calculate the size of the white rectangle
        rect_width = 280
        rect_height = len(emotions) * 35 + 40

        # Draw a white rectangle at the top left corner
        cv2.putText(img, 'Emotions:', (int(x), int(y - 10)), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (255, 255, 255), 1)
        cv2.rectangle(img, (0, 0), (rect_width, rect_height), (255, 255, 255), thickness=cv2.FILLED)

        # Display emotion values on the video without "emotions:" and with percentage
        cv2.putText(img, 'Emotions(%):', (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (0, 0, 0), 2)
        for i, emotion in enumerate(emotions):
            text = f'{emotion}: {predictions[0][i] * 100:.2f}'  # Display as percentage
            text_color_rgb = (emotion_colors_bgr[i][2], emotion_colors_bgr[i][1], emotion_colors_bgr[i][0])
            cv2.putText(img, text, (10, 65 + i * 35), cv2.FONT_HERSHEY_SIMPLEX, 1.0, text_color_rgb, 2)

        cv2.imshow('Facial Emotion Recognition', img)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()