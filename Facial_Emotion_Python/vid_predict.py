import argparse
import cv2
import numpy as np
from keras.models import model_from_json
from keras.utils import load_img, img_to_array
import matplotlib.pyplot as plt
import mplcursors

import sys
sys.setrecursionlimit(3000)

# Parse the video file path argument
ap = argparse.ArgumentParser()
ap.add_argument('video', help='path to input video file')
args = vars(ap.parse_args())

# Loading JSON model
json_file = open('top_models/fer.json', 'r')
loaded_model_json = json_file.read()
json_file.close()
model = model_from_json(loaded_model_json)

# Loading weights
model.load_weights('top_models/fer.h5')

face_haar_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')

cap = cv2.VideoCapture(args['video'])

# Initialize emotion probabilities
emotions = ['neutral', 'happiness', 'surprise', 'sadness', 'anger', 'disgust', 'fear']
emotion_probabilities = {emotion: [] for emotion in emotions}

# Initialize lists to store timestamps and captured face images
timestamps = []
captured_face_images = []

while True:
    ret, img = cap.read()
    if not ret:
        break

    gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    faces_detected = face_haar_cascade.detectMultiScale(gray_img, 1.2, 6)

    # Check if a face is detected in the current frame
    face_detected = False

    for (x, y, w, h) in faces_detected:
        cv2.rectangle(img, (x, y), (x + w, y + h), (0, 255, 0), thickness=2)
        face_detected = True  # Face detected in this frame

        roi_gray = gray_img[y:y + w, x:x + h]
        roi_gray = cv2.resize(roi_gray, (48, 48))
        img_pixels = img_to_array(roi_gray)
        img_pixels = np.expand_dims(img_pixels, axis=0)
        img_pixels /= 255.0

        predictions = model.predict(img_pixels)

        # Store the emotion probabilities for this face
        frame_emotion_probabilities = {emotion: prob for emotion, prob in zip(emotions, predictions[0])}

        # Normalize the probabilities to get a percentage
        total_probability = sum(frame_emotion_probabilities.values())
        for emotion in emotions:
            frame_emotion_probabilities[emotion] /= total_probability

        # Collect emotion probabilities for plotting
        for emotion in emotions:
            emotion_probabilities[emotion].append(frame_emotion_probabilities[emotion])

        # Store the captured face image
        captured_face_images.append(img.copy())

    if face_detected:
        # If a face is detected, add the timestamp to the list
        timestamps.append(cap.get(cv2.CAP_PROP_POS_MSEC))

    cv2.imshow('Facial Emotion Recognition', img)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()

# Plot the emotion probabilities over time
fig, ax = plt.subplots(figsize=(10, 6))
lines = {}  # Store lines for later interaction

for emotion in emotions:
    line, = ax.plot(timestamps, emotion_probabilities[emotion], label=emotion)
    lines[emotion] = line  # Store the line for interaction

ax.set_xlabel('Time (milliseconds)')
ax.set_ylabel('Probability')
ax.legend()
ax.set_title('Emotion Probabilities Over Time')

# Create an empty text annotation on the graph
text_annotation = ax.text(0.5, 0.95, "", transform=ax.transAxes, va='top', ha='center', fontsize=10, bbox=dict(boxstyle='round,pad=0.5', facecolor='white', alpha=0.8))

# Define cursor for mouse hover
cursor = mplcursors.cursor(hover=True)

# Display emotion probabilities and face image on mouse hover
@cursor.connect("add")
def on_hover(sel):
    if sel.index is not None:
        index = int(sel.index)
        if 0 <= index < len(timestamps):
            probabilities_at_index = {emotion: emotion_probabilities[emotion][index] for emotion in emotions}
            text = "\n".join([f"{emotion}: {probability:.2f}" for emotion, probability in probabilities_at_index.items()])
            text_annotation.set_text(text)

            # Display the captured face image
            if captured_face_images:
                cv2.imshow('Captured Face Image', captured_face_images[index])
                cv2.waitKey(0)
                cv2.destroyWindow('Captured Face Image')

# Define the function to update the annotations when the user hovers over a point
def update_annotations(event):
    if event.inaxes is None:
        return
    for emotion in emotions:
        line = lines[emotion]
        xdata, ydata = line.get_data()
        index = np.searchsorted(xdata, event.xdata)
        if 0 <= index < len(xdata) and np.isclose(xdata[index], event.xdata, atol=1e-2):
            probabilities_at_index = {emotion: emotion_probabilities[emotion][index]}
            text = "\n".join([f"{emotion}: {probability:.2f}" for emotion, probability in probabilities_at_index.items()])
            line.figure.texts[0].set_text(text)
            line.figure.canvas.draw()

# Connect the mouse motion event to update_annotations
fig.canvas.mpl_connect('motion_notify_event', update_annotations)

plt.show()
