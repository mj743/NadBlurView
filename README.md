# NadBlurView

**NadBlurView** is a lightweight and customizable BlurView component for Android that supports RenderEffect (API 31+) and RenderScript fallback for older devices. Designed to be modular, efficient, and easy to integrate into any Android UI.

![Version](https://img.shields.io/github/v/tag/mj743/NadBlurView?label=version&color=blue)
![Build](https://img.shields.io/github/actions/workflow/status/mj743/NadBlurView/android.yml?label=build&logo=github)
![License](https://img.shields.io/github/license/mj743/NadBlurView)
![Platform](https://img.shields.io/badge/platform-android-lightgrey)

---

## ✨ Features

- ✅ Support Android 6 to 14+
- 🎨 Real-time blur using `RenderEffect` or `RenderScript`
- 📐 Easy to configure: radius, overlay, clip
- ♻️ Performance-optimized with preDraw and canvas caching
- 🧱 Custom rounded blur views
- 🪄 Smooth integration in any layout

---

## 📦 Installation

Using **JitPack**:

### 1. Add JitPack to your root `settings.gradle`:
```groovy
dependencyResolutionManagement {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. Add NadBlurView to your module's `build.gradle`:
```groovy
implementation 'com.github.mj743:NadBlurView:1.1.0'
```

---

## 🛠 Usage

## 📐 Example Layout
Below is a complete example using all three components:
```xml
<!-- NadBlurView -->
<com.nad.blurview.NadBlurView
    android:id="@+id/nadBlurView"
    android:layout_width="0dp"
    android:layout_height="200dp"
    android:layout_margin="@dimen/size_20"
    app:nadBlur_overlayColor="#99FFFFFF"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />

<!-- NadBlurIndicator -->
<com.nad.blurview.NadBlurIndicator
    android:id="@+id/nadBlurIndicator"
    android:layout_width="150dp"
    android:layout_height="50dp"
    app:nadBlur_overlayColor="#66F00000"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />

<!-- NadBlurViewRounded -->
<com.nad.blurview.NadBlurViewRounded
    android:id="@+id/nadBlurViewRounded"
    android:layout_width="0dp"
    android:layout_height="100dp"
    app:nadBlur_cornerRadius="24dp"
    app:nadBlur_overlayColor="#880986ED"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />
```

## ⚙️ Blur Initialization (Java)

Initialize and configure all components programmatically:

```java
// Inisialisasi komponen blur
NadBlurView nadBlurView = findViewById(R.id.nadBlurView);
NadBlurViewRounded nadBlurViewRounded = findViewById(R.id.nadBlurViewRounded);
NadBlurIndicator nadBlurIndicator = findViewById(R.id.nadBlurIndicator);

// Attach NadBlurView dan NadBlurViewRounded ke root (wajib sebelum configure)
ViewGroup rootView = findViewById(android.R.id.content);
nadBlurView.attachToRoot(rootView);
nadBlurViewRounded.attachToRoot(rootView);

// Konfigurasi global menggunakan builder
BlurConfig config = new BlurConfig.Builder()
        .setBlurRoot(rootView) // sumber blur (biasanya root)
        .setBlurTarget(rootView) // target yang akan diburamkan
        .setBlurRadius(18f)
        .setOverlayColor(Color.parseColor("#99FFFFFF"))
        .setClipToOutline(true)
        .setFallbackEnabled(true)
        .build();

// Terapkan ke semua komponen
nadBlurView.configure(config);
nadBlurViewRounded.configure(config);
nadBlurIndicator.configure(config);

```

> For full examples and advanced usage, check the `mylibrary` module or upcoming documentation in the Wiki.

---

## 🙌 Contribute

Pull requests are welcome!  
If you find bugs or want to add features, feel free to open an issue or a PR.

---

## ☕ Donate

If you like this project and want to support the developer:

**[💖 Donate via PayPal](https://paypal.me/MuhamadJaelani?country.x=ID&locale.x=id_ID)**

---
## 📄 License

MIT License

Copyright (c) 2024 Muhamad Jaelani

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
