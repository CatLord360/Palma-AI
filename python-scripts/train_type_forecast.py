import numpy as np
import tensorflow as tf
import pickle
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Embedding, GlobalAveragePooling1D, Dense
from tensorflow.keras.utils import to_categorical

print("🚀 Starting training for forecast type classifier...")

# ------------------------
# 1️⃣ Dataset (IMPROVED)
# ------------------------
texts = [

    # 🔹 PAST WEATHER (clear past indicators)
    "what was the weather yesterday",
    "did it rain yesterday",
    "how was the weather last night",
    "what was the temperature earlier today",
    "was it hot yesterday",
    "did it rain last week",
    "what was the weather last monday",
    "how was the weather before",
    "what was the weather previously",

    # 🔹 CURRENT WEATHER (strong current indicators)
    "what is the weather now",
    "is it raining right now",
    "how is the weather today",
    "what is the temperature right now",
    "is it hot now",
    "do i need an umbrella right now",
    "what is the weather currently",
    "how is the weather at the moment",
    "is it sunny right now",

    # 🔹 FUTURE WEATHER (clear future indicators)
    "will it rain tomorrow",
    "what will the weather be tomorrow",
    "is it going to rain later",
    "weather forecast for next week",
    "will it be hot this afternoon",
    "is it going to storm tonight",
    "what is the weather this weekend",
    "will it rain next week",
    "what will the temperature be tomorrow"
]

labels_text = (
    ["past"] * 9 +
    ["current"] * 9 +
    ["future"] * 9
)

# ------------------------
# 2️⃣ Label Encoding
# ------------------------
label_map = {
    "past": 0,
    "current": 1,
    "future": 2
}

labels = np.array([label_map[l] for l in labels_text])
labels = to_categorical(labels, num_classes=3)

# ------------------------
# 3️⃣ Tokenize & Pad
# ------------------------
vocab_size = 1500
max_length = 12
embedding_dim = 32

texts = [t.lower() for t in texts]

tokenizer = Tokenizer(num_words=vocab_size, oov_token="<OOV>")
tokenizer.fit_on_texts(texts)

sequences = tokenizer.texts_to_sequences(texts)
padded = pad_sequences(sequences, maxlen=max_length, padding='post')

# 🔥 SAVE TOKENIZER (FIXED NAME)
with open("forecast_tokenizer.pkl", "wb") as f:
    pickle.dump(tokenizer, f)

print("✅ Tokenizer saved as forecast_tokenizer.pkl")

# ------------------------
# 4️⃣ Build Model
# ------------------------
model = Sequential([
    Embedding(vocab_size, embedding_dim, input_length=max_length),
    GlobalAveragePooling1D(),
    Dense(32, activation='relu'),
    Dense(16, activation='relu'),
    Dense(3, activation='softmax')  # past, current, future
])

model.compile(
    loss='categorical_crossentropy',
    optimizer='adam',
    metrics=['accuracy']
)

model.build(input_shape=(None, max_length))
model.summary()

# ------------------------
# 5️⃣ Train Model
# ------------------------
model.fit(padded, labels, epochs=120, verbose=2)

print("✅ Training complete")

# ------------------------
# 6️⃣ Convert to TFLite
# ------------------------
print("🔄 Converting to TFLite...")

converter = tf.lite.TFLiteConverter.from_keras_model(model)

# ⚠️ KEEP DEFAULT (FLOAT32)
tflite_model = converter.convert()

# 🔥 SAVE MODEL (FIXED NAME)
with open("forecast_type.tflite", "wb") as f:
    f.write(tflite_model)

print("✅ TFLite model saved as forecast_type.tflite")