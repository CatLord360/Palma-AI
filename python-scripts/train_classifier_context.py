import numpy as np
import tensorflow as tf
import pickle
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Embedding, GlobalAveragePooling1D, Dense
from tensorflow.keras.utils import to_categorical

# ------------------------
# 1️⃣ Improved Context Dataset
# ------------------------
texts = [

    # COMMAND
    "add milk to my list",
    "remind me at 7 pm",
    "delete john from contacts",
    "turn off the lights",
    "play music",
    "create a reminder",
    "set alarm for morning",
    "remove bread from my list",

    # ETIQUETTE
    "hi",
    "hello",
    "hey",
    "good morning",
    "thank you",
    "thanks a lot",
    "how are you",

    # QUERY
    "what time is it",
    "who is the president",
    "where is the nearest store",
    "how does this work",
    "what is the weather today",
    "can you tell me the news",

    # FORECAST
    "it will rain tomorrow",
    "stock prices will rise",
    "the weather will be hot",
    "traffic will be heavy",
    "temperature will drop tonight"
]

labels_text = [

    # COMMAND
    "command","command","command","command","command","command","command","command",

    # ETIQUETTE
    "etiquette","etiquette","etiquette","etiquette","etiquette","etiquette","etiquette",

    # QUERY
    "query","query","query","query","query","query",

    # FORECAST
    "forecast","forecast","forecast","forecast","forecast"
]

# ------------------------
# 2️⃣ Label Encoding
# ------------------------
label_map = {"command": 0, "etiquette": 1, "query": 2, "forecast": 3}
labels = np.array([label_map[l] for l in labels_text])
labels = to_categorical(labels, num_classes=4)

# ------------------------
# 3️⃣ Tokenize & Pad
# ------------------------
vocab_size = 1000
max_length = 12
embedding_dim = 32

texts = [t.lower() for t in texts]

tokenizer = Tokenizer(num_words=vocab_size, oov_token="<OOV>")
tokenizer.fit_on_texts(texts)

sequences = tokenizer.texts_to_sequences(texts)
padded = pad_sequences(sequences, maxlen=max_length, padding='post')

# Save tokenizer for Android
with open("context_tokenizer.pkl", "wb") as f:
    pickle.dump(tokenizer, f)

# ------------------------
# 4️⃣ Build Model
# ------------------------
model = Sequential([
    Embedding(vocab_size, embedding_dim, input_length=max_length),
    GlobalAveragePooling1D(),
    Dense(32, activation='relu'),
    Dense(16, activation='relu'),
    Dense(4, activation='softmax')
])

model.compile(
    loss='categorical_crossentropy',
    optimizer='adam',
    metrics=['accuracy']
)

# ------------------------
# 5️⃣ Train Model
# ------------------------
model.fit(padded, labels, epochs=120, verbose=2)

# ------------------------
# 6️⃣ Convert to TFLite
# ------------------------
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

with open("context_classifier.tflite", "wb") as f:
    f.write(tflite_model)

print("✅ Model exported as context_classifier.tflite")
print("✅ Tokenizer saved as context_tokenizer.pkl")