# LyricsAI - A Song Lyrics Retrieval Library

![alt text](https://github.com/khaouitiabdelhakim/LyricsAI/blob/master/LyricsAI.png)

LyricsAI is a versatile library designed for Android developers to effortlessly retrieve song lyrics through web scraping. Developed by Abdelhakim Khaouiti, this library provides a seamless way to access lyrics, making it an excellent addition to music-related Android applications.

## Author

- **Author:** Abdelhakim Khaouiti
- **GitHub:** [khaouitiabdelhakim](https://github.com/khaouitiabdelhakim)


## How to Use
To use the LyricsAI library in your Android project, follow these steps:

**Step 1. Add the JitPack Repository and Dependencies**

First, add the JitPack repository to your root build.gradle file to enable the use of the LyricsAI library. Add it at the end of the repositories section:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Next, add the LyricsAI library as a dependency in your app's build.gradle file. Be sure to replace `Tag` with the specific release version you want to use:
latest version is [![](https://jitpack.io/v/khaouitiabdelhakim/LyricsAI.svg)](https://jitpack.io/#khaouitiabdelhakim/LyricsAI)

```groovy
dependencies {
    implementation 'com.github.khaouitiabdelhakim:LyricsAI:Tag'
}
```

Please note that this library uses web scraping to retrieve lyrics, which can be resource-intensive. Therefore, it's essential to perform these operations in the background using Coroutines.

**Step 2. Adding Coroutines Dependency**

To handle asynchronous operations efficiently, you should add Coroutines dependencies to your project. Add the following dependencies with the latest version:

```groovy
// For Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4'
```

**Step 3. Creating a Coroutine Scope**

To use the LyricsAI library in a Coroutine scope, you can create a CoroutineScope with a specific dispatcher:

```kotlin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// Create a CoroutineScope
val scope = CoroutineScope(Job() + Dispatchers.Main)
```

**Step 4. Retrieving Lyrics**

You can now use the CoroutineScope to retrieve lyrics in the background. Here's how to do it:

```kotlin
scope.launch {
    // To retrieve lyrics by song title
    val title = "YourSongTitle"
    val lyrics = LyricsAI.findLyricsBySongTitle(title)

    // To retrieve lyrics by both song title and artist
    val title = "YourSongTitle"
    val artist = "ArtistName"
    val lyrics = LyricsAI.findLyricsBySongTitleAndArtist(title, artist)
}
```

This approach ensures that the library's operations are performed asynchronously and won't block the main thread, offering a smoother user experience in your Android application.


Please ensure you handle network and web scraping exceptions in your application as needed.

## Features

- Retrieve song lyrics with a simple song title query or a combination of title and artist.
- Web scraping capabilities to efficiently extract lyrics.
- Ideal for integration into music-related applications, karaoke apps, and more.


## Acknowledgments

We extend our gratitude to the open-source community for their valuable contributions.

## License

```
Copyright 2024 KHAOUITI ABDELHAKIM

Licensed under the MIT License
You may obtain a copy of the License at

http://opensource.org/licenses/MIT

Unless required by applicable law or agreed to in writing, software
distributed under the MIT License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the MIT License.
```

```Fork this repository if you wish to make changes or contribute improvements.```


This library is designed to enhance your Android applications with easy access to song lyrics. Feel free to use LyricsAI in your projects while respecting its authorship. If you encounter issues, have suggestions for improvement, or want to contribute, we encourage you to get involved.



