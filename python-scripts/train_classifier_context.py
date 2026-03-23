import numpy as np
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout
from tensorflow.keras.utils import to_categorical
from tensorflow.keras.callbacks import EarlyStopping
import pickle

print("🚀 Starting training for context classifier...")

np.random.seed(42)
tf.random.set_seed(42)

# ------------------------
# 1️⃣ Dataset
# ------------------------
texts = []
labels_text = []

def add(samples, label):
    texts.extend(samples)
    labels_text.extend([label] * len(samples))

# COMMAND
add([
    "show my lists","create a list","add milk","remove eggs",
    "set a reminder","remind me at 7 pm","delete reminder",
    "add contact john","delete contact mike"
], "command")

# ETIQUETTE (including farewells)
add([
    "hi","hello","hey","greetings",
    "good morning","good evening",
    "thank you","thanks",
    "how are you","nice to meet you",
    "bye","goodbye","see you later","take care"
], "etiquette")

# QUERY
add([
    "what is my name","who are you","what can you do",
    "tell me about cats","what do you know about dogs",
    "what is my email","what is my phone number"
], "query")

# FORECAST
add([
    "what is the weather today","is it raining",
    "will it rain tomorrow","weather forecast",
    "is it hot today","temperature outside"
], "forecast")

# ------------------------
# 2️⃣ Encode labels
# ------------------------
label_map = {"command":0,"etiquette":1,"query":2,"forecast":3}
labels = np.array([label_map[l] for l in labels_text])
labels = to_categorical(labels, num_classes=4)

# ------------------------
# 3️⃣ Feature extraction (🔥 FIXED: 6 FEATURES ONLY)
# ------------------------
command_words = {"add","delete","create","set","remove","load","open"}
forecast_words = {"weather","rain","temperature","forecast"}
query_words = {"what","who","where","when","why","how","tell"}
etiquette_words = {"hello","hi","hey","thanks","thank","bye","goodbye","farewell","later","see","greetings","morning","afternoon","evening","night"}

def extract_features(texts):
    features = []
    for t in texts:
        t_lower = t.lower()
        tokens = t_lower.split()
        features.append([
            len(tokens),                         # token count
            len(t_lower),                         # char count
            1.0 if "?" in t_lower else 0.0,      # question mark
            1.0 if any(w in t_lower for w in command_words) else 0.0,
            1.0 if any(w in t_lower for w in forecast_words) else 0.0,
            1.0 if any(w in t_lower for w in query_words.union(etiquette_words)) else 0.0
        ])
    return np.array(features, dtype=np.float32)

input_features = extract_features(texts)

# ------------------------
# 4️⃣ Model (6 features)
# ------------------------
model = Sequential([
    Dense(64, activation='relu', input_shape=(6,)),
    Dropout(0.3),
    Dense(32, activation='relu'),
    Dense(4, activation='softmax')
])

model.compile(
    loss='categorical_crossentropy',
    optimizer='adam',
    metrics=['accuracy']
)

model.summary()

# ------------------------
# 5️⃣ Train
# ------------------------
class_counts = np.sum(labels, axis=0)
total_samples = labels.shape[0]
class_weights = {i: total_samples/(4*count) for i, count in enumerate(class_counts)}

early_stop = EarlyStopping(monitor='val_accuracy', patience=10, restore_best_weights=True)

model.fit(
    input_features,
    labels,
    epochs=200,
    verbose=2,
    validation_split=0.2,
    class_weight=class_weights,
    callbacks=[early_stop]
)

print("✅ Training complete")

# ------------------------
# 6️⃣ Convert to TFLite
# ------------------------
converter = tf.lite.TFLiteConverter.from_keras_model(model)
converter.inference_input_type = tf.float32
converter.inference_output_type = tf.float32
tflite_model = converter.convert()

with open("context_classifier.tflite", "wb") as f:
    f.write(tflite_model)

print("✅ context_classifier.tflite saved!")

# ------------------------
# 7️⃣ Save tokenizer
# ------------------------
with open("context_tokenizer.pkl", "wb") as f:
    pickle.dump(texts, f)

print("✅ Tokenizer saved (for reference)")