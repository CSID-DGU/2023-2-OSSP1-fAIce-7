import cv2
import numpy as np
from keras.models import model_from_json
from keras.utils import img_to_array
import time

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

# Function to calculate emotion
def calculate_emotion(predictions):
    return emotions[np.argmax(predictions)]

start_time = time.time()

# Define colors for each emotion (in BGR format)
emotion_colors_bgr = {
    'neutral': (230, 216, 173),
    'happiness': (0, 165, 255),
    'surprise': (0, 252, 124),
    'sadness': (180, 130, 70),
    'anger': (0, 0, 255),
    'disgust': (19, 69, 139),
    'fear': (128, 0, 128)
}

while True:
    ret, img = cap.read()
    if not ret:
        break

    gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces_detected = face_haar_cascade.detectMultiScale(gray_img, 1.1, 6, minSize=(150, 150))

    for i, (x, y, w, h) in enumerate(faces_detected):
        roi_gray = gray_img[y:y + h, x:x + w]
        roi_gray = cv2.resize(roi_gray, (48, 48))
        img_pixels = img_to_array(roi_gray)
        img_pixels = np.expand_dims(img_pixels, axis=0)

        # Normalize pixel values to between 0 and 1
        img_pixels /= 255.0

        predictions = model.predict(img_pixels)

        person_label = f'Person {i + 1}'

        # Calculate the emotion values for each frame
        emotion_values = [predictions[0][j] * 100 for j in range(len(emotions))]

        # Sort emotions by value in descending order
        sorted_emotions = [x for _, x in sorted(zip(emotion_values, emotions), reverse=True)]
        sorted_values = sorted(emotion_values, reverse=True)

        # Draw emotion values next to the face rectangle (sorted by value)
        for j, emotion in enumerate(sorted_emotions):
            text = f'{emotion}: {sorted_values[j]:.2f}%'  # Display as percentage
            text_color = emotion_colors_bgr[emotion]
            cv2.putText(img, text, (x, y - 30 - j * 25), cv2.FONT_HERSHEY_SIMPLEX, 0.7, text_color, 2)

        # Draw a rectangle around the detected face
        cv2.rectangle(img, (x, y), (x + w, y + h), (0, 255, 0), thickness=2)

        # Display the person label below the face rectangle
        cv2.putText(img, person_label, (x, y + h + 25), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 255, 255), 2)

    cv2.imshow('Facial Emotion Recognition', img)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
