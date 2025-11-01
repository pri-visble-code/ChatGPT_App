# ChatGPT Android App

## Overview

This is an Android app built in Kotlin that allows users to send questions and get responses using the OpenAI GPT-3 API.

## Solution

The previous version of this app had a few limitations:

*   **No Chat History:** The app didn't save the conversation, so you couldn't see previous messages.
*   **No Image Support:** The app only supported text-based conversations.
*   **Paid API:** The app used the paid OpenAI API.

I have upgraded the app to address these issues:

*   **Chat History:** The app now displays the full conversation history in a `RecyclerView`.
*   **Image Attachments:** You can now attach images to your messages.
*   **Free and Fast AI:** The app now uses the Groq API, which is free and provides much faster responses.

## Getting Started
### Full tutorial on Youtube: https://youtu.be/bLktoOzb4R0
### Prerequisites
To run this app, you need Android Studio installed on your computer.

### Installation
- Clone this repository.
- Open the project in Android Studio.
- Build and run the app on your device or emulator.

### Testing

To run the tests, you can use the following Gradle command:

```
./gradlew test
```

This will run the `MainActivityTest.kt` test file, which verifies that the image attachment functionality is working correctly.

## Screenshots

[<img src="https://i.imgur.com/um3xa2j.png" align="left"
width="200"
    hspace="10" vspace="10">](https://i.imgur.com/um3xa2j.png)
[<img src="https://i.imgur.com/ofwR5YT.png" align="center"
width="200"
    hspace="10" vspace="10">](https://i.imgur.com/ofwR5YT.png)
[<img src="https://i.imgur.com/rsv9Em6.png" align="center"
width="200"
    hspace="10" vspace="10">](https://i.imgur.com/rsv9Em6.png)    

## Built With
- Kotlin - Programming language used.
- XML - Layout language used.
- OkHttp - HTTP client for making API calls.
- Robolectric - For testing Android functionality.

## Authors
[Shivank Verma](https://www.linkedin.com/in/shivank8/)


## License
```license
MIT License

Copyright (c) 2023 Shivank Verma

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

