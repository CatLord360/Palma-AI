import numpy as np
import tensorflow as tf
import pickle
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Embedding, GlobalAveragePooling1D, Dense
from tensorflow.keras.utils import to_categorical

# ------------------------
# 1️⃣ Command Dataset (Natural Version)
# ------------------------
texts = [

    # 🔹 LIST COMMANDS
    "show my lists",
    "create a new list named groceries",
    "add milk to my groceries list",
    "remove eggs from my grocery list",
    "delete my shopping list",
    "load the homework list",
    "add new task to my task list",
    "delete completed tasks from my task list",

    # 🔹 REMINDER COMMANDS
    "set a daily reminder at 08:00 to drink water",
    "set a weekly reminder on Monday at 09:00 for team meeting",
    "set a monthly reminder on day 15 at 14:00 for pay bills",
    "set an annual reminder on 12-25 at 10:00 for Christmas",
    "delete my daily reminder at 07:00",
    "delete weekly reminder on Friday at 18:00",
    "remind me to call mom tomorrow",
    "cancel my monthly reminder for trash collection",

    # 🔹 CONTACT COMMANDS
    "create a contact for john",
    "add alice to my contacts",
    "delete bob from my contacts",
    "remove charlie from contacts",
    "update contact details for dave",
    "save new contact emily",
    "write user email john@example.com",
    "write group contact for team",
    "remove ai contact helperbot",
    "add ai contact assistantbot",

    # 🔹 DEFAULT / UNCLEAR
    "do something",
    "help me",
    "i don't know",
    "just do it",
    "random action"
]

labels_text = [

    # LIST
    "list","list","list","list","list","list","list","list",

    # REMINDER
    "reminder","reminder","reminder","reminder","reminder","reminder","reminder","reminder",

    # CONTACT
    "contact","contact","contact","contact","contact","contact","contact","contact","contact","contact",

    # DEFAULT
    "default","default","default","default","default"
]

# ------------------------
# 2️⃣ Label Encoding
# ------------------------
label_map = {"list": 0, "reminder": 1, "contact": 2, "default": 3}
labels = np.array([label_map[l] for l in labels_text])
labels = to_categorical(labels, num_classes=4)

# ------------------------
# 3️⃣ Tokenize & Pad
# ------------------------
vocab_size = 1500
max_length = 15
embedding_dim = 32

# Normalize text
texts = [t.lower() for t in texts]

tokenizer = Tokenizer(num_words=vocab_size, oov_token="<OOV>")
tokenizer.fit_on_texts(texts)

sequences = tokenizer.texts_to_sequences(texts)
padded = pad_sequences(sequences, maxlen=max_length, padding='post')

# Save tokenizer
with open("command_tokenizer.pkl", "wb") as f:
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

with open("command_classifier.tflite", "wb") as f:
    f.write(tflite_model)

print("✅ Model exported as command_classifier.tflite")
print("✅ Tokenizer saved as command_tokenizer.pkl")