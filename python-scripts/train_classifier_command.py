import numpy as np
import tensorflow as tf
import pickle
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Embedding, GlobalAveragePooling1D, Dense
from tensorflow.keras.utils import to_categorical

print("🚀 Starting training for command classifier...")

# ------------------------
# 1️⃣ Dataset (Natural Commands, improved)
# ------------------------
texts = [
    # 🔹 LIST COMMANDS
    "show my lists","show all lists","create a new list","create a list named groceries",
    "make a list for shopping","load my grocery list","open my task list",
    "delete my grocery list","remove my shopping list","add milk to my grocery list",
    "add eggs to the list","remove bread from my list","delete eggs from grocery list",
    "add item to my task list","remove item from my task list",

    # 🔹 REMINDER COMMANDS
    "set a reminder","remind me at 07:00","set a daily reminder at 08:00",
    "remind me every day at 08:00","set a weekly reminder on monday at 09:00",
    "remind me every monday at 09:00","set a monthly reminder on the 15th",
    "remind me every 15th of the month","set a yearly reminder on december 25",
    "remind me every year on december 25","delete my reminder","cancel my reminder",
    "remove reminder at 07:00","delete my weekly reminder","cancel reminder for tomorrow",

    # 🔹 CONTACT COMMANDS
    "add a contact named john","save a new contact","create a contact for alice",
    "add john to my contacts","delete john from my contacts",
    "remove contact named mike","update contact details","edit contact information",
    "save email for john","add email to contact","remove email from contact",
    "create a group contact","delete group contact","add ai contact","remove ai contact",

    # 🔹 DEFAULT / UNCLEAR
    "do something","help me","i don't know","just do it",
    "random action","perform a task","execute something"
]

labels_text = (
    ["list"] * 15 +
    ["reminder"] * 15 +
    ["contact"] * 15 +
    ["default"] * 7
)

# ------------------------
# 2️⃣ Encode labels
# ------------------------
label_map = {
    "list": 0,
    "reminder": 1,
    "contact": 2,
    "default": 3
}

labels = np.array([label_map[l] for l in labels_text])
labels = to_categorical(labels, num_classes=4)

# ------------------------
# 3️⃣ Tokenize & Pad
# ------------------------
vocab_size = 2000  # increased for natural commands
max_length = 20    # slightly longer to capture full commands
embedding_dim = 32

texts = [t.lower() for t in texts]

tokenizer = Tokenizer(num_words=vocab_size, oov_token="<OOV>")
tokenizer.fit_on_texts(texts)

sequences = tokenizer.texts_to_sequences(texts)
padded = pad_sequences(sequences, maxlen=max_length, padding='post')

# Save tokenizer
with open("command_tokenizer.pkl", "wb") as f:
    pickle.dump(tokenizer, f)
print("✅ Tokenizer saved")

# ------------------------
# 4️⃣ Build Model
# ------------------------
model = Sequential([
    Embedding(vocab_size, embedding_dim, input_length=max_length),
    GlobalAveragePooling1D(),
    Dense(64, activation='relu'),  # increased capacity
    Dense(32, activation='relu'),
    Dense(4, activation='softmax')
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
model.fit(padded, labels, epochs=100, verbose=2)  # more epochs for better learning
print("✅ Training complete")

# ------------------------
# 6️⃣ Convert to TFLite
# ------------------------
converter = tf.lite.TFLiteConverter.from_keras_model(model)
converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS]
tflite_model = converter.convert()

with open("command_classifier.tflite", "wb") as f:
    f.write(tflite_model)
print("✅ TFLite model saved successfully!")
print("✅ Tokenizer saved as command_tokenizer.pkl")