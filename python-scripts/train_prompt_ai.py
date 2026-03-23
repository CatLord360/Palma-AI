# train_prompt_ai.py
import numpy as np
import tensorflow as tf

print("🚀 Training prompt response model...")

# ------------------------
# 1️⃣ Training data
# ------------------------
texts = [
    # GREETING
    "hello","hi","hey","greetings",
    "good morning","good afternoon","good evening","good night",

    # GRATITUDE
    "thank you","thanks","thanks a lot",

    # FAREWELL
    "bye","goodbye","see you","see you later","farewell","take care",

    # SIMPLE QUERY
    "what is your name","who are you","what can you do",
    "what are you","tell me about yourself",
    "what is this app","what do you do"
]

# ------------------------
# 2️⃣ Responses (5 outputs)
# ------------------------
responses = [
    "Hello! How can I help you?",            # 0 greeting
    "Good day! Hope you're doing well.",     # 1 good
    "You're welcome!",                       # 2 gratitude
    "Goodbye! See you later!",               # 3 farewell
    "I am your AI assistant. I can help with tasks and answer simple questions."  # 4 query
]

# ------------------------
# 3️⃣ Labels (one-hot)
# ------------------------
labels_text = [
    # greeting
    0,0,0,0,
    1,1,1,1,

    # gratitude
    2,2,2,

    # farewell
    3,3,3,3,3,3,

    # query
    4,4,4,4,4,4,4
]

labels = tf.keras.utils.to_categorical(labels_text, num_classes=5)

# ------------------------
# 4️⃣ Feature extraction
# ------------------------
def feature_vector(text):
    text = text.lower()
    tokens = text.split()

    greeting_words = ["hello","hi","hey","greetings"]
    good_words = ["morning","afternoon","evening","night"]
    gratitude_words = ["thank","thanks"]
    farewell_words = ["bye","goodbye","farewell","later","see","care"]
    query_words = ["what","who","where","when","why","how","tell"]

    return [
        len(tokens),                                     # 1 token count
        len(text),                                       # 2 char count
        int("?" in text),                                # 3 question indicator
        int(any(w in text for w in greeting_words)),    # 4 greeting flag
        int("good" in text and any(w in text for w in good_words)), # 5 good morning/evening flag
        int(any(w in text for w in gratitude_words)),   # 6 gratitude flag
        int(any(w in text for w in farewell_words)),    # 7 farewell flag
        int(any(w in text for w in query_words))        # 8 query flag
    ]

X = np.array([feature_vector(t) for t in texts], dtype=np.float32)

# ------------------------
# 5️⃣ Build model
# ------------------------
model = tf.keras.Sequential([
    tf.keras.layers.Input(shape=(8,)),  # 8 features
    tf.keras.layers.Dense(64, activation="relu"),
    tf.keras.layers.Dense(32, activation="relu"),
    tf.keras.layers.Dense(5, activation="softmax")  # 5 outputs
])

model.compile(
    optimizer="adam",
    loss="categorical_crossentropy",
    metrics=["accuracy"]
)

# ------------------------
# 6️⃣ Train
# ------------------------
model.fit(X, labels, epochs=250, verbose=2)

# ------------------------
# 7️⃣ Convert to TFLite
# ------------------------
converter = tf.lite.TFLiteConverter.from_keras_model(model)
converter.inference_input_type = tf.float32
converter.inference_output_type = tf.float32

tflite_model = converter.convert()

with open("prompt.tflite", "wb") as f:
    f.write(tflite_model)

print("✅ prompt.tflite generated successfully!")

# ------------------------
# 8️⃣ Test function
# ------------------------
def predict_response(message):
    feat = np.array([feature_vector(message)], dtype=np.float32)
    pred = model.predict(feat, verbose=0)
    idx = np.argmax(pred)
    return responses[idx]

# ------------------------
# 9️⃣ CLI test
# ------------------------
if __name__ == "__main__":
    while True:
        prompt = input("You: ").strip()
        if prompt.lower() in ["exit","quit"]:
            break
        print(predict_response(prompt))