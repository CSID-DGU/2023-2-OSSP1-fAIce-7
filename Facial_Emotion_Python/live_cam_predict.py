import cv2
import numpy as np
from keras.models import model_from_json
from keras.utils import img_to_array
import time
import matplotlib.pyplot as plt
import mplcursors  # Import mplcursors for hover functionality

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

# Initialize variables to store emotion probabilities
emotion_probabilities = {emotion: [] for emotion in emotions}
frame_images = []

start_time = time.time()
end_time = start_time + 10.0  # Capture for 10 seconds

# Create timestamps for frames
frame_rate = 60  # Assuming 60 FPS
timestamps = []
captured_face_images = []

while time.time() < end_time:
    ret, img = cap.read()
    if not ret:
        break

    gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    faces_detected = face_haar_cascade.detectMultiScale(gray_img, 1.1, 6, minSize=(150, 150))

    for (x, y, w, h) in faces_detected:
        roi_gray = gray_img[y:y + w, x:x + h]
        roi_gray = cv2.resize(roi_gray, (48, 48))
        img_pixels = img_to_array(roi_gray)
        img_pixels = np.expand_dims(img_pixels, axis=0)
        img_pixels /= 255.0

        predictions = model.predict(img_pixels)
        max_index = int(np.argmax(predictions))

        predicted_emotion = emotions[max_index]
        
        # Draw a rectangle around the detected face
        cv2.rectangle(img, (x, y), (x + w, y + h), (0, 255, 0), thickness=2)
        # cv2.putText(img, predicted_emotion, (int(x), int(y)), cv2.FONT_HERSHEY_SIMPLEX, 0.75, (255, 255, 255), 2)
        
        resized_img = cv2.resize(img, (1000, 700))
        cv2.imshow('Facial Emotion Recognition', resized_img)

        for emotion in emotions:
            emotion_probabilities[emotion].append(predictions[0][emotions.index(emotion)])

        frame_images.append(img)  # Store the frame images
        timestamps.append(time.time() - start_time)  # Store timestamps
        captured_face_images.append(roi_gray)  # Store the captured face image
    
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()

# Plot the emotion probabilities over time as line plots
fig, ax = plt.subplots(figsize=(10, 6))

for emotion in emotions:
    plt.plot(timestamps, emotion_probabilities[emotion], label=emotion)

plt.xlabel('Time (seconds)')
plt.ylabel('Probability')
plt.legend()
plt.title('Emotion Probabilities Over 10 Seconds')
plt.grid()

# Create a cursor to display frame images and overall emotion probabilities on hover
cursor = mplcursors.cursor(hover=True)

def hover(event):
    if event.index is not None:
        index = int(event.index)
        if 0 <= index < len(timestamps):
            # Get the overall emotion probabilities for the selected frame
            overall_probabilities = emotion_probabilities[emotions[0:len(emotions)]][index]
            overall_probabilities_text = "\n".join([f"{emotions[i]}: {overall_probabilities[i]:.2f}" for i in range(len(emotions))])

            # Display the overall emotion probabilities and the corresponding frame
            cursor.set_annotation_text(f"Emotion Probabilities:\n{overall_probabilities_text}")
            
            # Display the captured face image
            if captured_face_images:
                frame = cv2.cvtColor(frame_images[index], cv2.COLOR_BGR2RGB)
                ax_hover.imshow(frame)
                fig.canvas.draw_idle()

ax_hover = plt.axes([0.1, 0.01, 0.8, 0.1])
ax_hover.axis('off')
ax_hover.set_facecolor('lightgray')

plt.show()