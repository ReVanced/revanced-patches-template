## 🧩 Patches

The official Patch bundle provided by ReVanced and the community.

> Looking for the JSON variant of this? [Click here](patches.json).

### 📦 `com.garzotto.pflotsh.ecmwf_a`
<details>

| 💊 Patch | 📜 Description | 🏹 Target Version |
|:--------:|:--------------:|:-----------------:|
| `pflotsh-ecmwf-subscription-unlock` | Unlocks all subscription features. | 3.5.4 |
</details>

### 📦 `com.google.android.apps.youtube.music`
<details>

| 💊 Patch | 📜 Description | 🏹 Target Version |
|:--------:|:--------------:|:-----------------:|
| `music-video-ads` | Removes ads in the music player. | 5.22.54 |
| `codecs-unlock` | Adds more audio codec options. The new audio codecs usually result in better audio quality. | 5.22.54 |
| `exclusive-audio-playback` | Enables the option to play music without video. | 5.22.54 |
| `compact-header` | Hides the music category bar at the top of the homepage. | 5.22.54 |
| `minimized-playback-music` | Enables minimized playback on Kids music. | 5.22.54 |
| `hide-get-premium` | Removes all "Get Premium" evidences from the avatar menu. | 5.22.54 |
| `tasteBuilder-remover` | Removes the "Tell us which artists you like" card from the home screen. | 5.22.54 |
| `upgrade-button-remover` | Removes the upgrade tab from the pivot bar. | 5.22.54 |
| `music-microg-support` | Allows YouTube Music ReVanced to run without root and under a different package name. | 5.22.54 |
| `background-play` | Enables playing music in the background. | 5.22.54 |
</details>

### 📦 `com.reddit.frontpage`
<details>

| 💊 Patch | 📜 Description | 🏹 Target Version |
|:--------:|:--------------:|:-----------------:|
| `general-reddit-ads` | Removes general ads from the Reddit frontpage and subreddits. | all |
| `premium-icon-reddit` | Unlocking Premium Icons in reddit app. | all |
</details>

### 📦 `com.ss.android.ugc.trill`
<details>

| 💊 Patch | 📜 Description | 🏹 Target Version |
|:--------:|:--------------:|:-----------------:|
| `tiktok-ads` | Removes ads from TikTok. | all |
| `tiktok-download` | Remove restrictions on downloads video. | all |
| `tiktok-seekbar` | Show progress bar for all video. | all |
</details>

### 📦 `com.twitter.android`
<details>

| 💊 Patch | 📜 Description | 🏹 Target Version |
|:--------:|:--------------:|:-----------------:|
| `timeline-ads` | Removes ads from the Twitter timeline. | all |
</details>

### 📦 `de.dwd.warnapp`
<details>

| 💊 Patch | 📜 Description | 🏹 Target Version |
|:--------:|:--------------:|:-----------------:|
| `promo-code-unlock` | Disables the validation of promo code. Any code will work to unlock all features. | all |
</details>

### 📦 `com.google.android.youtube`
<details>

| 💊 Patch | 📜 Description | 🏹 Target Version |
|:--------:|:--------------:|:-----------------:|
| `general-ads` | Removes general ads. | 17.33.42 |
| `hide-infocard-suggestions` | Hides infocards in videos. | 17.33.42 |
| `video-ads` | Removes ads in the video player. | 17.33.42 |
| `downloads` | Enables downloading music and videos from YouTube. | 17.33.42 |
| `seekbar-tapping` | Enables tap-to-seek on the seekbar of the video player. | 17.33.42 |
| `swipe-controls` | Adds volume and brightness swipe controls. | 17.33.42 |
| `amoled` | Enables pure black theme. | all |
| `hide-autoplay-button` | Hides the autoplay button in the video player. | 17.33.42 |
| `premium-heading` | Shows premium branding on the home screen. | all |
| `custom-branding` | Changes the YouTube launcher icon and name to your choice (defaults to ReVanced). | all |
| `hide-cast-button` | Hides the cast button in the video player. | all |
| `disable-create-button` | Hides the create button in the navigation bar. | 17.33.42 |
| `disable-fullscreen-panels` | Disables video description and comments panel in fullscreen view. | 17.33.42 |
| `old-quality-layout` | Enables the original quality flyout menu. | 17.33.42 |
| `return-youtube-dislike` | Shows the dislike count of videos using the Return YouTube Dislike API. | 17.33.42 |
| `hide-shorts-button` | Hides the shorts button on the navigation bar. | 17.33.42 |
| `sponsorblock` | Integrate SponsorBlock. | 17.33.42 |
| `tablet-mini-player` | Enables the tablet mini player layout. | 17.33.42 |
| `hide-watermark` | Hides creator's watermarks on videos. | 17.33.42 |
| `enable-wide-searchbar` | Replaces the search icon with a wide search bar. This will hide the YouTube logo when active. | 17.33.42 |
| `always-autorepeat` | Always repeats the playing video again. | 17.33.42 |
| `client-spoof` | Spoofs the YouTube or Vanced client to prevent playback issues. | all |
| `custom-playback-speed` | Adds more video playback speed options. | 17.33.42 |
| `enable-debugging` | Enables app debugging by patching the manifest file. | all |
| `hdr-auto-brightness` | Makes the brightness of HDR videos follow the system default. | 17.33.42 |
| `microg-support` | Allows YouTube ReVanced to run without root and under a different package name with Vanced MicroG. | 17.33.42 |
| `minimized-playback` | Enables minimized and background playback. | 17.33.42 |
| `remember-video-quality` | Adds the ability to remember the video quality you chose in the video quality flyout. | 17.33.42 |
| `settings` | Adds settings for ReVanced to YouTube. | all |
| `custom-video-buffer` | Lets you change the buffers of videos. | 17.33.42 |
</details>



## 📝 JSON Format

This section explains the JSON format for the [patches.json](patches.json) file.

The file contains an array of objects, each object representing a patch. The object contains the following properties:

| key                           | description                                                                                                      |
|-------------------------------|------------------------------------------------------------------------------------------------------------------|
| `name`                        | The name of the patch.                                                                                           |
| `description`                 | The description of the patch.                                                                                    |
| `version`                     | The version of the patch.                                                                                        |
| `excluded`                    | Whether a patch is excluded by default. If `true`, the patch must never be included by default.                  |
| `dependencies`                | An array of dependencies, which are patch names.                                                                 |
| `compatiblePackages`          | An array of packages compatible with this patch.                                                                 |
| `compatiblePackages.name`     | The name of the package.                                                                                         |
| `compatiblePackages.versions` | An array of versions of the package compatible with this patch. If empty, all versions are seemingly compatible. |

Example:

```json
[
  {
    "name": "remember-video-quality",
    "description": "Adds the ability to remember the video quality you chose in the video quality flyout.",
    "version": "0.0.1",
    "excluded": false,
    "dependencies": [
      "integrations",
      "video-id-hook"
    ],
    "compatiblePackages": [
      {
        "name": "com.google.android.youtube",
        "versions": [
          "17.22.36",
          "17.24.35",
          "17.26.35",
          "17.27.39",
          "17.28.34",
          "17.29.34",
          "17.32.35"
        ]
      }
    ]
  },
  {
    "name": "client-spoof",
    "description": "Spoofs the YouTube or Vanced client to prevent playback issues.",
    "version": "0.0.1",
    "excluded": false,
    "dependencies": [],
    "compatiblePackages": [
      {
        "name": "com.google.android.youtube",
        "versions": []
      },
      {
        "name": "com.vanced.android.youtube",
        "versions": []
      }
    ]
  }
]
```