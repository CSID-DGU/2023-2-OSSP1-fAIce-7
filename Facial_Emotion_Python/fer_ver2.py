import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from keras.optimizers import Adam
from keras.models import Sequential, model_from_json
from keras.layers import Conv2D, MaxPooling2D, Dense, Flatten, Dropout, BatchNormalization, Activation
from keras.preprocessing.image import ImageDataGenerator
from sklearn import model_selection
from math import ceil
import os

def preprocess_data():
    data = pd.read_csv('fer2013.csv')
    labels = pd.read_csv('fer2013new.csv')

    orig_class_names = ['neutral', 'happiness', 'surprise', 'sadness', 'anger', 'disgust', 'fear', 'contempt',
                        'unknown', 'NF']

    n_samples = len(data)
    w = 48
    h = 48

    y = np.array(labels[orig_class_names])
    X = np.zeros((n_samples, w, h, 3))
    for i in range(n_samples):
        pixels = np.fromstring(data['pixels'][i], dtype=int, sep=' ').reshape((h, w))
        img_colored = np.stack([pixels]*3, axis=-1)
        X[i] = img_colored

    return X, y

def clean_data_and_normalize(X, y):
    orig_class_names = ['neutral', 'happiness', 'surprise', 'sadness', 'anger', 'disgust', 'fear', 'contempt',
                        'unknown', 'NF']

    # Using mask to remove unknown or NF images
    y_mask = y.argmax(axis=-1)
    mask = y_mask < orig_class_names.index('unknown')
    X = X[mask]
    y = y[mask]

    # Convert to probabilities between 0 and 1
    y = y[:, :-2] * 0.1

    # Add contempt to neutral and remove it
    y[:, 0] += y[:, 7]
    y = y[:, :7]

    # Normalize image vectors
    X = X / 255.0

    return X, y

def split_data(X, y):
    test_size = ceil(len(X) * 0.1)
    x_train, x_test, y_train, y_test = model_selection.train_test_split(X, y, test_size=test_size, random_state=42)
    x_train, x_val, y_train, y_val = model_selection.train_test_split(x_train, y_train, test_size=test_size,
                                                                      random_state=42)
    return x_train, y_train, x_val, y_val, x_test, y_test

def data_augmentation(x_train):
    shift = 0.1
    datagen = ImageDataGenerator(
        rotation_range=90,
        horizontal_flip=True,
        height_shift_range=shift,
        width_shift_range=shift)
    datagen.fit(x_train)
    return datagen

def show_augmented_images(datagen, x_train, y_train):
    it = datagen.flow(x_train, y_train, batch_size=1)
    plt.figure(figsize=(10, 7))
    for i in range(25):
        plt.subplot(5, 5, i + 1)
        plt.xticks([])
        plt.yticks([])
        plt.grid(False)
        plt.imshow(it.next()[0][0], cmap='gray')
    plt.show()

def define_model(input_shape=(48, 48, 3), classes=7):
    num_features = 64

    model = Sequential()

    # 1st stage
    model.add(Conv2D(num_features, kernel_size=(3, 3), input_shape=input_shape))
    model.add(BatchNormalization())
    model.add(Activation(activation='relu'))
    model.add(Conv2D(num_features, kernel_size=(3, 3)))
    model.add(BatchNormalization())
    model.add(Activation(activation='relu'))
    model.add(Dropout(0.5))

    # 2nd stage
    model.add(Conv2D(num_features, (3, 3), activation='relu'))
    model.add(Conv2D(num_features, (3, 3), activation='relu'))
    model.add(MaxPooling2D(pool_size=(2, 2), strides=(2, 2)))

    # 3rd stage
    model.add(Conv2D(2 * num_features, kernel_size=(3, 3)))
    model.add(BatchNormalization())
    model.add(Activation(activation='relu'))
    model.add(Conv2D(2 * num_features, kernel_size=(3, 3)))
    model.add(BatchNormalization())
    model.add(Activation(activation='relu'))

    # 4th stage
    model.add(Conv2D(2 * num_features, (3, 3), activation='relu'))
    model.add(Conv2D(2 * num_features, (3, 3), activation='relu'))
    model.add(MaxPooling2D(pool_size=(2, 2), strides=(2, 2)))

    # 5th stage
    model.add(Conv2D(4 * num_features, kernel_size=(3, 3)))
    model.add(BatchNormalization())
    model.add(Activation(activation='relu'))
    model.add(Conv2D(4 * num_features, kernel_size=(3, 3)))
    model.add(BatchNormalization())
    model.add(Activation(activation='relu'))

    model.add(Flatten())

    # Fully connected neural networks
    model.add(Dense(1024, activation='relu'))
    model.add(Dropout(0.2))
    model.add(Dense(1024, activation='relu'))
    model.add(Dropout(0.2))

    model.add(Dense(classes, activation='softmax'))

    return model

def plot_acc_loss(history):
    plt.plot(history.history['accuracy'], label='accuracy')
    plt.plot(history.history['val_accuracy'], label='val_accuracy')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy')
    plt.ylim([0, 1])
    plt.legend(loc='upper left')
    plt.show()

    plt.plot(history.history['loss'], label='loss')
    plt.plot(history.history['val_loss'], label='val_loss')
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.legend(loc='upper right')
    plt.show()

def save_model_and_weights(model, test_acc):
    test_acc = int(test_acc * 10000)
    model_json = model.to_json()
    with open('Saved-Models\\model' + str(test_acc) + '.json', 'w') as json_file:
        json_file.write(model_json)
    model.save_weights('Saved-Models\\model' + str(test_acc) + '.h5')
    print('Model and weights are saved in separate files.')

def load_model_and_weights(model_path, weights_path):
    json_file = open(model_path, 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    model = model_from_json(loaded_model_json)
    model.load_weights(weights_path)
    model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])
    print('Model and weights are loaded and compiled.')

fer_classes = ['neutral', 'happiness', 'surprise', 'sadness', 'anger', 'disgust', 'fear']

def modified_split_data(X, y, random_seed):
    test_size = ceil(len(X) * 0.1)
    x_train, x_test, y_train, y_test = model_selection.train_test_split(X, y, test_size=test_size, random_state=random_seed)
    x_train, x_val, y_train, y_val = model_selection.train_test_split(x_train, y_train, test_size=test_size, random_state=random_seed)
    return x_train, y_train, x_val, y_val, x_test, y_test

def run_model_and_save_weights(X, y, random_seed, save_name, model=None):
    x_train, y_train, x_val, y_val, x_test, y_test = modified_split_data(X, y, random_seed)
    
    datagen = data_augmentation(x_train)  # Using the data_augmentation function
    epochs = 100
    batch_size = 64
    
    # 모델이 제공되지 않은 경우 새로운 모델을 정의
    if model is None:
        model = define_model(input_shape=x_train[0].shape, classes=len(fer_classes))
    
    model.compile(optimizer=Adam(lr=0.0001), loss='binary_crossentropy', metrics=['accuracy'])
    
    history = model.fit(datagen.flow(x_train, y_train, batch_size=batch_size), epochs=epochs,
                        steps_per_epoch=len(x_train) // batch_size,
                        validation_data=(x_val, y_val), verbose=2)
    
    test_loss, test_acc = model.evaluate(x_test, y_test, batch_size=batch_size)
    
    # 지정된 이름으로 모델의 가중치 저장
    model.save_weights(save_name)
    
    return history, test_acc, model

def run_iteratively_and_aggregate(X, y):
    # 첫번째 학습된 모델
    history1, test_acc1, model1 = run_model_and_save_weights(X, y, random_seed=42, save_name='first_weights.h5')
    
    # 두번째 학습된 모델
    history2, test_acc2, model2 = run_model_and_save_weights(X, y, random_seed=84, save_name='second_weights.h5', model=model1)
    if os.path.exists('first_weights.h5'):
        os.remove('first_weights.h5')
    
    # 세번째 학습된 모델
    model2.load_weights('second_weights.h5')
    history3, test_acc3, model3 = run_model_and_save_weights(X, y, random_seed=126, save_name='final_serial_weights.h5', model=model2)
    if os.path.exists('second_weights.h5'):
        os.remove('second_weights.h5')
    
    return [history1, history2, history3], [test_acc1, test_acc2, test_acc3]


def plot_aggregated_histories(histories):
    avg_train_acc = np.zeros_like(histories[0].history['accuracy'])
    avg_val_acc = np.zeros_like(histories[0].history['val_accuracy'])
    avg_train_loss = np.zeros_like(histories[0].history['loss'])
    avg_val_loss = np.zeros_like(histories[0].history['val_loss'])
    for history in histories:
        avg_train_acc += history.history['accuracy']
        avg_val_acc += history.history['val_accuracy']
        avg_train_loss += history.history['loss']
        avg_val_loss += history.history['val_loss']
    avg_train_acc /= len(histories)
    avg_val_acc /= len(histories)
    avg_train_loss /= len(histories)
    avg_val_loss /= len(histories)
    plt.figure(figsize=(14, 5))
    plt.subplot(1, 2, 1)
    plt.plot(avg_train_acc, label='Training Accuracy')
    plt.plot(avg_val_acc, label='Validation Accuracy')
    plt.title('Aggregated Training and Validation Accuracy')
    plt.xlabel('Epochs')
    plt.ylabel('Accuracy')
    plt.legend()
    plt.subplot(1, 2, 2)
    plt.plot(avg_train_loss, label='Training Loss')
    plt.plot(avg_val_loss, label='Validation Loss')
    plt.title('Aggregated Training and Validation Loss')
    plt.xlabel('Epochs')
    plt.ylabel('Loss')
    plt.legend()
    plt.tight_layout()
    plt.show()

if __name__ == "__main__":
    X, y = preprocess_data()
    X, y = clean_data_and_normalize(X, y)
    histories, test_accuracies = run_iteratively_and_aggregate(X, y)
    plot_aggregated_histories(histories)
