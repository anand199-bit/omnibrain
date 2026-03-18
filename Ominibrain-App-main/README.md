# OmniBrain - AI Voice Assistant

An elegant Android assistant app with voice commands, app launching, and AI chatbot powered by Google Gemini 2.5 Pro.

## Features

- **Voice Recognition**: Speak naturally to interact with the assistant
- **App Launcher**: Open apps using voice commands (e.g., "Open WhatsApp", "Launch Chrome")
- **AI Chatbot**: Powered by Gemini 2.5 Pro for general knowledge conversations
- **Text-to-Speech**: Voice responses for a complete assistant experience
- **Dual Input**: Both text and voice input supported
- **Elegant UI**: 
  - Glassmorphism effects and gradient backgrounds
  - Smooth animations with spring physics
  - Pulsing microphone button with glow effects
  - Message bubbles with user/AI avatars
  - Welcome screen with animated logo
  - Beautiful settings screen with card-based layout

## Setup

### Option 1: Quick Start (API Key Pre-configured)

The API key is already configured in `local.properties`. Just build and run:

```bash
./gradlew build
./gradlew installDebug
```

### Option 2: Manual Configuration

1. **Get Gemini API Key**
   - Visit https://aistudio.google.com/app/apikey
   - Create a new API key

2. **Add to local.properties**
   ```properties
   GEMINI_API_KEY=your_api_key_here
   ```

3. **Build and Run**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

4. **Alternative: Configure in App**
   - Open the app
   - Go to Settings
   - Enter your Gemini API key
   - Start chatting!

### Important Security Note
⚠️ Never commit `local.properties` to version control. It's already in `.gitignore`.

## Permissions

The app requires:
- **RECORD_AUDIO**: For voice input
- **INTERNET**: For Gemini API calls
- **QUERY_ALL_PACKAGES**: For app launching (Android 11+)

## Usage

### Voice Commands
- "Open Chrome" - Launches Chrome browser
- "Launch WhatsApp" - Opens WhatsApp
- "Start Camera" - Opens camera app

### Chat Examples
- "What is the capital of France?"
- "Explain quantum physics"
- "Tell me a joke"

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM
- **AI**: Google Gemini 2.5 Pro
- **Speech**: Android SpeechRecognizer & TextToSpeech

## Project Structure

```
app/
├── data/
│   ├── model/          # Data models
│   └── repository/     # Gemini repository
├── service/            # Voice, TTS, App launcher services
├── ui/
│   ├── components/     # Reusable UI components
│   ├── screens/        # Main and Settings screens
│   └── theme/          # App theming
├── util/               # Utilities and constants
└── viewmodel/          # ViewModel layer
```

## License

MIT License
