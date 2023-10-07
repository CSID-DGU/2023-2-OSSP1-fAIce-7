import argparse
import cv2
import numpy as np
from keras.models import model_from_json
from keras.utils import load_img, img_to_array
import matplotlib.pyplot as plt
import mplcursors
import time
import tkinter as tk
from tkinter import filedialog  # tkinter 파일 대화 상자 모듈 추가

import sys
sys.setrecursionlimit(3000)

# Create a Tkinter root window to display the file dialog
root = tk.Tk()
root.withdraw()  # Hide the root window

# Ask the user to select a video file
video_file_path = filedialog.askopenfilename(title="Select a video file", filetypes=[("Video files", "*.mp4 *.avi *.mov")])

if not video_file_path:
    print("No video file selected. Exiting.")
    exit()

# Loading JSON model
json_file = open('top_models/fer.json', 'r')
loaded_model_json = json_file.read()
json_file.close()
model = model_from_json(loaded_model_json)

# Loading weights
model.load_weights('top_models/fer.h5')

face_haar_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')

cap = cv2.VideoCapture(video_file_path)

# Define colors for each emotion (in BGR format)
emotion_colors_bgr = [(173, 216, 230), (255, 165, 0), (124, 252, 0), (70, 130, 180), (255, 0, 0), (128, 0, 128), (139, 69, 19)]

# Initialize emotion probabilities
emotions = ['neutral', 'happiness', 'surprise', 'sadness', 'anger', 'disgust', 'fear']
emotion_probabilities = {emotion: [] for emotion in emotions}

# Initialize lists to store timestamps and captured face images
timestamps = []
captured_face_images = []

# Create a figure for the real-time emotion graph
plt.figure(figsize=(10, 5))
plt.ylim(0, 1)
plt.xlabel('Time (seconds)')
plt.ylabel('Emotion Value')
lines = []

# Initialize an empty list to store emotion history for each emotion
emotion_history = [[] for _ in emotions]
time_points = []

start_time = time.time()

# Create the initial empty plot for the emotion graph
for i, emotion in enumerate(emotions):
    line, = plt.plot([], [], label=emotion, color=np.array(emotion_colors_bgr[i]) / 255)
    lines.append(line)

plt.xlim(0, 1)  # Set initial x-axis limits
plt.ylim(0, 100)  # Set initial y-axis limits
plt.legend(loc='upper left')
plt.pause(0.01)  # Pause to initialize the graph

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

        # Calculate the average emotion values
        avg_emotion_values = [sum(emotion_history[i]) / len(emotion_history[i]) if emotion_history[i] else 0 for i in range(len(emotion_history))]

        # Update the emotion history for each emotion
        for i in range(len(emotions)):
            emotion_history[i].append(predictions[0][i] * 100)  # Convert to percentage

        time_points.append(time.time() - start_time)

        # Update the emotion graph for each emotion
        for i, emotion in enumerate(emotions):
            lines[i].set_data(time_points, emotion_history[i])
            plt.xlim(min(time_points), max(time_points))
            plt.ylim(0, 100)  # Set the y-axis limit to 0-100 (percentage)

        plt.pause(0.01)  # Pause to update the graph

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

    # Draw a white rectangle at the top left corner
    cv2.putText(img, 'Emotions:', (int(x), int(y - 10)), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (255, 255, 255), 1)
    cv2.rectangle(img, (0, 0), (300, len(emotions) * 35 + 40), (255, 255, 255), thickness=cv2.FILLED)

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

# Tkinter 창을 숨김
root.iconify()
root.update()

# Create a figure for the final emotion graph
fig, ax = plt.subplots(figsize=(10, 5))
plt.ylim(0, 1)
plt.xlabel('Time (seconds)')
plt.ylabel('Emotion Value')
lines = {}

# Create an empty text annotation on the graph
text_annotation = ax.text(0.5, 0.95, "", transform=ax.transAxes, va='top', ha='center', fontsize=10, bbox=dict(boxstyle='round,pad=0.5', facecolor='white', alpha=0.8))

for i, emotion in enumerate(emotions):
    line, = ax.plot(timestamps, emotion_probabilities[emotion], label=emotion, color=np.array(emotion_colors_bgr[i]) / 255)
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
            text_annotation.set_text(text)

            # Display the captured face image
            if captured_face_images and 0 <= index < len(captured_face_images):
                face_image = captured_face_images[index]

                # Add a white rectangle background for the emotion values
                cv2.rectangle(face_image, (0, 0), (300, len(emotions) * 35 + 40), (255, 255, 255), thickness=cv2.FILLED)

                for i, emotion in enumerate(emotions):
                    emotion_text = f'{emotion}: {probabilities_at_index[emotion] * 100:.2f}'  # Emotion value as percentage
                    text_color_rgb = (emotion_colors_bgr[i][2], emotion_colors_bgr[i][1], emotion_colors_bgr[i][0])

                    # Display the emotion values on the white background
                    cv2.putText(face_image, emotion_text, (10, 65 + i * 35), cv2.FONT_HERSHEY_SIMPLEX, 1.0, text_color_rgb, 2)

                # Add a text label above the emotion values for the top emotion (Neutral in this case)
                top_emotion_label = "Emotions(%):"
                cv2.putText(face_image, top_emotion_label, (10, 25), cv2.FONT_HERSHEY_SIMPLEX, 1.0, (0, 0, 0), 2)

                cv2.imshow('Captured Face Image', face_image)
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

# Remove unnecessary plot after video
plt.close()

# Show only one plot
plt.show()
