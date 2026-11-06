package com.example.happy.engine

enum class EmotionType(val emoji: String, val displayName: String) {
    JOY("😊", "Happy & Joyful"),
    SADNESS("🫂", "Feeling Down"),
    STRESS("🌿", "Stressed / Overwhelmed"),
    ANGER("🔥", "Frustrated / Angry"),
    LONELINESS("❤️‍🩹", "Lonely / Need a Friend"),
    MOTIVATION("💪", "Motivated & Focused"),
    BOREDOM("⚡", "Bored / Seeking Fun"),
    GREETING("👋", "Greeting"),
    LOVE("💛", "Warm / Loving"),
    NEUTRAL("✨", "Conversational")
}

enum class SupportedLanguage(val code: String, val label: String, val nativeName: String) {
    AUTO("auto", "Auto Detect", "आपोआप / स्वतः"),
    MARATHI("mr", "Marathi", "मराठी"),
    HINDI("hi", "Hindi", "हिंदी"),
    ENGLISH("en", "English", "English"),
    HINGLISH("hinglish", "Hinglish / Marath-lish", "हिंग्लिश / मराठी-इंग्लिश")
}
