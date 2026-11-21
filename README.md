# Badword Filter

A lightweight, highly optimized chat filter for Minecraft servers. Specifically designed to be 100% compatible with both **Paper** and the multi-threaded **Folia** server software.

## ✨ Features
* **Folia Compatible:** Uses Folia's `EntityScheduler` to safely send messages without crashing async threads.
* **Smart Masking:** Replaces bad words with a mask character (default: `#`) instead of just blocking the whole message.
* **Fully Configurable:** Add or remove bad words, change the warning message, and change the mask character via `badwords.yml`.
* **Thread-Safe:** Uses `ConcurrentHashMap` and cached configs to prevent async race conditions.

## 🚀 Installation
1. Download the latest from the [Releases](link-to-your-releases) page.
2. Drop the JAR into your server's `plugins/` folder.
3. Restart your server.
4. Edit the generated `badwords.yml` inside the `plugins/Badword/` folder to customize your filter.

## 🛠️ Configuration
The plugin generates a `badwords.yml` file. You can add as many words as you want
