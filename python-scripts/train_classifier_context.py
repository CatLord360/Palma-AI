import numpy as np
import tensorflow as tf

print("🚀 Starting training for context classifier...")

# ------------------------
# 1️⃣ Dataset
# ------------------------
texts = [

    # 🔹 COMMAND
    "show my lists","show all lists","create a new list","create a list named groceries",
    "make a list for shopping","load my grocery list","open my task list",
    "delete my grocery list","remove my shopping list","add milk to my grocery list",
    "add eggs to the list","remove bread from my list","delete eggs from grocery list",
    "add item to my task list","remove item from my task list",

    "set a reminder","remind me at 7 pm","set a daily reminder at 8 am",
    "remind me every day at 8 am","set a weekly reminder on monday at 9 am",
    "remind me every monday at 9 am","set a monthly reminder on the 15th",
    "remind me every 15th of the month","set a yearly reminder on december 25",
    "remind me every year on december 25","delete my reminder","cancel my reminder",
    "remove reminder at 7 pm","delete my weekly reminder","cancel reminder for tomorrow",

    "add a contact named john","save a new contact","create a contact for alice",
    "add john to my contacts","delete john from my contacts",
    "remove contact named mike","update contact details","edit contact information",
    "save email for john","add email to contact","remove email from contact",
    "create a group contact","delete group contact","add ai contact","remove ai contact",

    # 🔹 ETIQUETTE
    "hi","hello","hey","good morning","good evening",
    "thank you","thanks a lot","how are you","greetings",

    # 🔹 QUERY
    "what is my username","show my email","what is my phone number",
    "tell me my address","when is my birthday","what is my gender",
    "give me my contact info","show my mobile number",

    "what is the ai name","show ai email","what is the ai contact number",
    "tell me the ai address","when was the ai created",
    "what is the ai gender","show ai mobile number",

    "tell me about cats","what do you know about dogs",
    "give me information on quantum physics",
    "what do you know about python programming",
    "show details about the recipe",
    "what was the last fact i mentioned",
    "what do you know about basketball",
    "do you remember movies we talked about",

    # 🔹 FORECAST
    "what was the weather yesterday","did it rain last night",
    "how was the weather this morning","what was the temperature earlier",
    "was it hot yesterday","did it rain last week",
    "how was the weather last monday",

    "what is the weather now","is it raining right now",
    "how is the weather today","what is the temperature outside",
    "is it hot right now","do i need an umbrella now",
    "what is the weather like",

    "will it rain tomorrow","what will the weather be like tomorrow",
    "is it going to rain later","weather forecast for next week",
    "will it be hot this afternoon","is it going to storm tonight",
    "what is the weather this weekend"
]

# ------------------------
# 2️⃣ Labels
# ------------------------
labels_text = (
    ["command"] * 45 +
    ["etiquette"] * 9 +
    ["query"] * 23 +
    ["forecast"] * 21
)

# ------------------------
# 3️⃣ Encode labels
# ------------------------
label_map = {
    "command": 0,
    "etiquette": 1,
    "query": 2,
    "forecast": 3
}

labels = np.array([label_map[l] for l in labels_text])
labels = tf.keras.utils.to_categorical(labels, num_classes=4)

# ------------------------
# 4️⃣ 🔥 IMPROVED FEATURES (CRITICAL FIX)
# ------------------------
def feature_vector(text):
    text = text.lower()
    tokens = text.split()

    return [
        len(tokens),                      # token count
        len(text),                        # char count
        int("?" in text),                 # question indicator
        int(any(w in text for w in ["add","delete","create","set","remove","load"])),  # command words
        int(any(w in text for w in ["weather","rain","temperature","forecast"])),     # forecast words
        int(any(w in text for w in ["what","who","where","when","how","tell"])),      # query words
    ]

X = np.array([feature_vector(t) for t in texts], dtype=np.float32)

# ------------------------
# 5️⃣ Model
# ------------------------
model = tf.keras.Sequential([
    tf.keras.layers.Input(shape=(6,)),   # 🔥 NOW 6 FEATURES
    tf.keras.layers.Dense(32, activation='relu'),
    tf.keras.layers.Dense(16, activation='relu'),
    tf.keras.layers.Dense(4, activation='softmax')
])

model.compile(
    optimizer='adam',
    loss='categorical_crossentropy',
    metrics=['accuracy']
)

# ------------------------
# 6️⃣ Train
# ------------------------
model.fit(X, labels, epochs=150, verbose=2)

# ------------------------
# 7️⃣ Convert to TFLite (SAFE)
# ------------------------
converter = tf.lite.TFLiteConverter.from_keras_model(model)

converter.inference_input_type = tf.float32
converter.inference_output_type = tf.float32

tflite_model = converter.convert()

with open("context_classifier.tflite", "wb") as f:
    f.write(tflite_model)

print("✅ context_classifier.tflite generated successfully!")